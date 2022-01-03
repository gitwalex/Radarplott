package com.gerwalex.radarplott.radar;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Objects;

public class OpponentVessel extends Vessel {
    private final float dist1;
    private final int rwP1;
    private float dist2;
    private float headingRelativ;
    private Gerade2D kurslinieRelativ;
    /**
     * Abstand zwischen den Peilungen in Minuten
     */
    private int minutes;
    private Punkt2D relPosition;
    private int rwP2;
    private float speedRelativ;

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public OpponentVessel(Character name, int peilungRechtweisend, double distance) {
        this.name = name;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
        setStartPosition(new Punkt2D().getPunkt2D(peilungRechtweisend, dist1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OpponentVessel vessel = (OpponentVessel) o;
        return Float.compare(vessel.dist1, dist1) == 0 && Float.compare(vessel.dist2, dist2) == 0 &&
                Float.compare(vessel.headingRelativ, headingRelativ) == 0 && minutes == vessel.minutes &&
                rwP1 == vessel.rwP1 && rwP2 == vessel.rwP2 && Float.compare(vessel.speedRelativ, speedRelativ) == 0 &&
                Objects.equals(kurslinieRelativ, vessel.kurslinieRelativ) &&
                Objects.equals(relPosition, vessel.relPosition);
    }

    public float getAbstandBCR(Vessel me) {
        return me.getAktPosition().getAbstand(getBCR(me));
    }

    public float getAbstandCPA(Vessel me) {
        return getCPA(me).getAbstand(me.getAktPosition());
    }

    public Punkt2D getBCR(Vessel me) {
        return kurslinie.getSchnittpunkt(me.getKurslinie());
    }

    public Punkt2D getCPA(Vessel me) {
        return kurslinie.getLotpunkt(me.getAktPosition());
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public float getHeadingRelativ() {
        return headingRelativ;
    }

    public Gerade2D getKurslinieRelativ() {
        return kurslinieRelativ;
    }

    public int getMinutes() {
        return minutes;
    }

    public float getPeilungRechtweisendCPA(Vessel me) {
        return new Gerade2D(me.getAktPosition(), getCPA(me)).getYAxisAngle();
    }

    public Punkt2D getRelPosition() {
        return relPosition;
    }

    public final float getRelativTimeTo(@NonNull Punkt2D p) {
        if (!kurslinie.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (aktPosition.getAbstand(p) / speed * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    public float getSeitenpeilungCPA(Vessel me) {
        return getPeilungRechtweisendCPA(me) + me.getHeading();
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public float getSpeedRelativ() {
        return speedRelativ;
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(), dist1, dist2, headingRelativ, kurslinieRelativ, minutes, relPosition, rwP1,
                        rwP2, speedRelativ);
    }

    /**
     * Prueft, ob ein Punkt auf der EigenesSchiff liegt. Toleranz ist 1E6.
     *
     * @param p Punkt
     * @return true, wenn der Punkt auf der EigenesSchiff liegt, ansonsten false.
     */
    public final boolean isPunktAufKurslinieRelativ(@NonNull Punkt2D p) {
        return Math.round(kurslinieRelativ.getAbstand(p.x, p.y) * 1E6) == 0;
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p zu pruefender Punkt
     * @return true, wenn der Punkt in Fahrtrichtung liegt. Sonst false.
     */
    public final boolean isPunktInFahrtrichtungRelativ(Punkt2D p) {
        if (!isPunktAufKurslinieRelativ(p)) {
            return false;
        }
        Punkt2D rv = kurslinieRelativ.getRichtungsvektor().getEndpunkt();
        double lambda;
        if (rv.x != 0) {
            lambda = (p.x - aktPosition.x) / rv.x;
        } else {
            lambda = (p.y - aktPosition.y) / rv.y;
        }
        return lambda > 0;
    }

    public void calculateRelativeValues(Vessel me) {
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = startPosition.add(otherPos);
        kurslinieRelativ = new Gerade2D(relPosition, aktPosition);
        headingRelativ = kurslinieRelativ.getYAxisAngle();
        speedRelativ = (float) (relPosition.getAbstand(aktPosition) * 60.0 / (float) minutes);
    }

    /**
     * /**
     * Sett die zweite Seitenpeilung. Dabei werden dann Geschwindigkeit und Kurs (neu) berechnet
     *
     * @param minutes  Zeitunterschied zwischen zwei Peilungen in Minuten
     * @param rwP      zweite Rechtweisende Peilung
     * @param distance distance bei der zweiten Peilung
     */
    public void setSecondSeitenpeilung(int minutes, int rwP, double distance, Vessel me) {
        dist2 = (float) distance;
        rwP2 = rwP;
        this.minutes = minutes;
        aktPosition = new Punkt2D().getPunkt2D(rwP, dist2);
        kurslinie = new Gerade2D(startPosition, aktPosition);
        heading = kurslinie.getYAxisAngle();
        speed = (float) (startPosition.getAbstand(aktPosition) * 60.0 / (float) minutes);
        calculateRelativeValues(me);
        me.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                calculateRelativeValues(me);
            }
        });
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{" + "dist1=" + dist1 + ", dist2=" + dist2 + ", headingRelativ=" + headingRelativ +
                ", kurslinieRelativ=" + kurslinieRelativ + ", minutes=" + minutes + ", relPosition=" + relPosition +
                ", rwP1=" + rwP1 + ", rwP2=" + rwP2 + ", speedRelativ=" + speedRelativ + '}' + super.toString();
    }
}
