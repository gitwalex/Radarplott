package com.gerwalex.radarplott.main;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

public class OpponentVessel extends Vessel {
    private final float dist1;
    private final Character name;
    private final int rwP1;
    private float dist2;
    private float headingRelativ;
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

    public int getMinutes() {
        return minutes;
    }

    public float getPeilungRechtweisendCPA(Vessel me) {
        return new Gerade2D(me.getAktPosition(), getCPA(me)).getYAxisAngle();
    }

    public Punkt2D getRelPosition(@NonNull Vessel me) {
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = startPosition.add(otherPos);
        Gerade2D kurslinieRelativ = new Gerade2D(relPosition, aktPosition);
        headingRelativ = kurslinieRelativ.getYAxisAngle();
        speedRelativ = (float) (relPosition.getAbstand(aktPosition) * 60.0 / (float) minutes);
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
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{name=" + name + "dist1=" + dist1 + ", dist2=" + dist2 + ", headingRelativ=" + headingRelativ +
                ", " + "minutes=" + minutes + ", relPosition=" + relPosition + ", rwP1=" + rwP1 + ", rwP2=" + rwP2 +
                ", speedRelativ=" + speedRelativ + '}' + super.toString();
    }
}
