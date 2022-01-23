package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.gerwalex.radarplott.main.IllegalManoeverException;

import java.util.Objects;

public class Vessel extends BaseObservable {
    protected final float minutes;
    protected final Punkt2D secondPosition;
    protected Punkt2D firstPosition;
    private float heading;
    private Kurslinie kurslinie;
    private float speed;

    public Vessel(Punkt2D start, Kurslinie kurslinie, float speed) {
        secondPosition = start;
        this.kurslinie = kurslinie;
        this.speed = speed;
        minutes = 6;
        heading = kurslinie.getYAxisAngle();
        firstPosition = getPosition(-minutes);
    }

    public Vessel(Punkt2D position, float heading, float speed) {
        this(position, heading, speed, 6);
    }

    public Vessel(float heading, float speed) {
        this(new Punkt2D(), heading, speed);
    }

    public Vessel(Punkt2D position, Vektor2D richtung, int minutes) {
        this.minutes = minutes;
        this.firstPosition = position.add(richtung.negate());
        this.secondPosition = position;
        kurslinie = new Kurslinie(firstPosition, secondPosition);
        heading = kurslinie.getYAxisAngle();
        speed = (float) (firstPosition.getAbstand(secondPosition) * 60.0 / (float) minutes);
    }

    public Vessel(Punkt2D firstPosition, Punkt2D secondPosition, float minutes) {
        this.minutes = minutes;
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        kurslinie = new Kurslinie(firstPosition, secondPosition);
        heading = kurslinie.getYAxisAngle();
        speed = (float) (firstPosition.getAbstand(secondPosition) * 60.0 / minutes);
    }

    public Vessel(Punkt2D position, float heading, float speed, int minutes) {
        this(position.getPunkt2D(heading, -(speed / minutes)), position, minutes);
        this.heading = heading % 360;
        this.speed = speed;
    }

    public final Punkt2D addRichtungsvektor(Punkt2D pkt, int minutes) {
        return pkt.add(kurslinie.getRichtungsvektor(minutes / 60f * speed));
    }

    public float checkForValidKurs(Float newKurs) throws IllegalManoeverException {
        if (newKurs != null) {
            return newKurs;
        }
        throw new IllegalManoeverException("Kurs nicht erlaubt.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vessel vessel = (Vessel) o;
        return firstPosition.equals(vessel.firstPosition) && secondPosition.equals(vessel.secondPosition) &&
                vessel.heading == heading && vessel.speed == speed;
    }

    public float getAbstand(Punkt2D pkt) {
        float abstand = secondPosition.getAbstand(pkt);
        return (!isPunktAufKurslinie(pkt) || isPunktInFahrtrichtung(pkt)) ? abstand : -abstand;
    }

    public Punkt2D getBCR(Vessel other) {
        return kurslinie.getSchnittpunkt(other.getKurslinie());
    }

    /**
     * Liefert den CPA zwischen Vessel und Other.
     *
     * @param other Vessel
     * @return CPA
     */
    public Punkt2D getCPA(Vessel other) {
        return other.getKurslinie().getLotpunkt(secondPosition);
    }

    @Bindable
    public final Punkt2D getFirstPosition() {
        return firstPosition;
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public final float getHeading() {
        return heading;
    }

    @Bindable
    public final Kurslinie getKurslinie() {
        return kurslinie;
    }

    @Bindable
    public int getHeadingFormatted() {
        return Math.round((heading + 0.5f) * 10) / 10;
    }

    /**
     * Liefert die Position nach Minuten
     *
     * @param minutes Minuten
     * @return Position auf Kurslinie
     */
    public final Punkt2D getPosition(float minutes) {
        return secondPosition.getPunkt2D(heading, (float) (speed * minutes / 60.0));
    }

    public float getPeilungRechtweisend(Punkt2D pkt) {
        if (secondPosition.equals(pkt)) {
            throw new IllegalArgumentException("Punkte dürfen nicht identisch sein");
        }
        return secondPosition.getYAxisAngle(pkt);
    }

    /**
     * Liefert den Richtungsvektor. Länge des Vektor ist die Strecke, die in minutes zurückgelegt wird.
     *
     * @param minutes Minuten
     * @return Richtungsvektor
     */
    public final Vektor2D getRichtungsvektor(float minutes) {
        return kurslinie.getRichtungsvektor(minutes / 60f * speed);
    }

    public final float getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinie.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie:" + p);
        }
        float timeToP = (float) (secondPosition.getAbstand(p) / speed * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    @Bindable
    public final Punkt2D getSecondPosition() {
        return secondPosition;
    }

    public float getSeitenPeilung(Punkt2D pkt) {
        return (getPeilungRechtweisend(pkt) + 360 - heading) % 360;
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public final float getSpeed() {
        return speed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(heading, kurslinie, kurslinie, speed, firstPosition, secondPosition);
    }

    /**
     * Eine Steuerbordpeilung liegt immer zwischen 0 und 180 Grad
     *
     * @param pkt Punkt
     * @return true, wennn pkt in Steuerbord
     */
    public boolean isSteuerbord(Punkt2D pkt) {
        float plg = getSeitenPeilung(pkt);
        return plg >= 0 && plg < 180;
    }

    /**
     * Berechnet die Zeit bis ein bestimmter Abstand zu einem anderen Schiff erreicht ist
     *
     * @param abstand abstand in sm
     * @return null, wenn dieser Abstand nicht erreichbar ist, ansonsten ein Array mit einem oder zwei Punkten. Dabei
     * ist in float[0] die kuerzere, in float[1] die gleiche (wenn es nur einen Punkt gibt) oder die groessere Zeit
     * enthalten.
     */
    public float[] getTimeToAbstand(Vessel other, float abstand) {
        float[] time = null;
        Kreis2D k = new Kreis2D(secondPosition, abstand);
        Punkt2D[] sc = other.kurslinie.getSchnittpunkt(k);
        if (sc != null) {
            time = new float[2];
            float t1 = getTimeTo(sc[0]);
            float t2 = getTimeTo(sc[1]);
            if (t1 > t2) {
                time[0] = t2;
                time[1] = t1;
            } else {
                time[0] = t1;
                time[1] = t2;
            }
        }
        return time;
    }

    @Bindable
    public final void setHeading(float heading) {
        if (this.heading != heading) {
            this.heading = heading;
            firstPosition = secondPosition.getPunkt2D(this.heading % 360, -(speed / 6f));
            kurslinie = new Kurslinie(firstPosition, secondPosition);
            notifyPropertyChanged(BR.heading);
        }
    }

    /**
     * Ein Entgegenkommer wird zwischen 90 und 270 Grad gepeilt
     *
     * @param other   Entgegenkommer
     * @param minutes Zeitpunkt der Position
     * @return true, wennn Entgegenkommer
     */
    public boolean isEntgegenkommer(Vessel other, int minutes) {
        float plg = getSeitenPeilung(other.getPosition(minutes));
        return plg > 270 || plg < 90;
    }

    /**
     * Prueft, ob ein Punkt auf der EigenesSchiff liegt. Toleranz ist 1E6f.
     *
     * @param p Punkt
     * @return true, wenn der Punkt auf der EigenesSchiff liegt, ansonsten false.
     */
    public final boolean isPunktAufKurslinie(@NonNull Punkt2D p) {
        return Math.round(kurslinie.getAbstand(p.x, p.y) * 1E4f) < 1;
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p zu pruefender Punkt
     * @return true, wenn der Punkt in Fahrtrichtung liegt. Sonst false.
     */
    public final boolean isPunktInFahrtrichtung(Punkt2D p) {
        float plg = getSeitenPeilung(p);
        return plg > 270 || plg < 90;
    }

    @Bindable
    public final void setSpeed(float speed) {
        if (this.speed != speed) {
            this.speed = speed;
            firstPosition = secondPosition.getPunkt2D(this.heading, -(speed / 6f));
            kurslinie = new Kurslinie(firstPosition, secondPosition);
            notifyPropertyChanged(BR.speed);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{ heading=" + heading + " speed=" + speed + ",startPosition=" + firstPosition + ", aktPosition=" +
                secondPosition + ", kurslinie=" + kurslinie + "}";
    }
}


