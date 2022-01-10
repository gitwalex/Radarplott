package com.gerwalex.radarplott.main;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Objects;

public class Vessel extends BaseObservable {
    protected Punkt2D firstPosition, secondPosition;
    protected float heading;
    protected Gerade2D kurslinie;
    protected float speed;

    protected Vessel() {
        firstPosition = secondPosition = new Punkt2D();
    }

    public Vessel(Punkt2D position, float heading, float speed) {
        this.heading = heading % 360;
        this.speed = speed;
        secondPosition = position;
        firstPosition = secondPosition.getPunkt2D(this.heading, -(speed / 6f));
        kurslinie = new Gerade2D(firstPosition, secondPosition);
    }

    public Vessel(int heading, float speed) {
        this(new Punkt2D(), heading, speed);
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

    public Punkt2D getCPA(Vessel other) {
        return other.getKurslinie().getLotpunkt(secondPosition);
    }

    public final Punkt2D getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(Punkt2D firstPosition) {
        this.firstPosition = firstPosition;
        kurslinie = new Gerade2D(firstPosition, secondPosition);
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

    public float getPeilungRechtweisend(Punkt2D pkt) {
        return new Gerade2D(secondPosition, pkt).getYAxisAngle();
    }

    public int getHeadingFormatted() {
        return Math.round((heading + 0.5f) * 10) / 10;
    }

    public final Gerade2D getKurslinie() {
        return kurslinie;
    }

    public float getSeitenPeilung(Punkt2D pkt) {
        return (getPeilungRechtweisend(pkt) + 360 - heading) % 360;
    }

    public float getPeilungRechtweisendCPA(OpponentVessel other) {
        return new Gerade2D(secondPosition, getCPA(other)).getYAxisAngle();
    }

    /**
     * Liefert die Position nach Minuten
     *
     * @param minutes Minuten
     * @return Position auf Kurslinie
     */
    public final Punkt2D getPosition(int minutes) {
        return secondPosition.getPunkt2D(heading, (float) (speed * minutes / 60.0));
    }

    public final Punkt2D getSecondPosition() {
        return secondPosition;
    }

    public final float getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinie.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (secondPosition.getAbstand(p) / speed * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
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
     * Berechnet die Zeit bis ein bestimmter Abstand zu einem anderen Schiff erreicht ist
     *
     * @param abstand abstand in sm
     * @return null, wenn dieser Abstand nicht erreichbar ist, ansonsten ein Array mit einem oder zwei Punkten. Dabei
     * ist in float[0] die kuerzere, in float[1] die gleiche (wenn es nur einen Punkt gibt) oder die groessere Zeit
     * enthalten.
     */
    public float[] getTimeToAbstand(OpponentVessel other, float abstand) {
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

    @Override
    public int hashCode() {
        return Objects.hash(heading, kurslinie, kurslinie, speed, firstPosition, secondPosition);
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
    public final void setHeading(float heading) {
        if (this.heading != heading) {
            this.heading = heading;
            firstPosition = secondPosition.getPunkt2D(this.heading % 360, -(speed / 6f));
            kurslinie = new Gerade2D(firstPosition, secondPosition);
            notifyChange();
        }
    }

    @Bindable
    public final void setSpeed(float speed) {
        if (this.speed != speed) {
            this.speed = speed;
            firstPosition = secondPosition.getPunkt2D(this.heading, -(speed / 6f));
            kurslinie = new Gerade2D(firstPosition, secondPosition);
            notifyChange();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{ heading=" + heading + " speed=" + speed + ",startPosition=" + firstPosition + ", aktPosition=" +
                secondPosition + ", kurslinie=" + kurslinie + "}";
    }
}


