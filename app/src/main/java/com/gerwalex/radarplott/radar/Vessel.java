package com.gerwalex.radarplott.radar;

import androidx.annotation.NonNull;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

public class Vessel {
    private double dist1, dist2;
    private double heading;
    private double headingRelativ;
    private Gerade2D kurslinie;
    private Gerade2D kurslinieRelativ;
    private Punkt2D peilPunkt1, peilPunkt2;
    private int rwP1, rwP2;
    private double speed;
    private double speedRelativ;

    public Vessel(double heading, double speed) {
        this.heading = heading;
        this.speed = speed;
        peilPunkt2 = new Punkt2D(0, 0);
        peilPunkt1 = peilPunkt2.getPunkt2D(heading, speed);
        kurslinie = new Gerade2D(peilPunkt1, peilPunkt2);
    }

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public Vessel(int peilungRechtweisend, double distance) {
        dist1 = distance;
        rwP1 = peilungRechtweisend;
        peilPunkt1 = new Punkt2D().getPunkt2D(peilungRechtweisend, distance);
    }

    public Punkt2D getFirstPosition() {
        return peilPunkt1;
    }

    /**
     * Heading
     *
     * @return heading
     */
    public double getHeading() {
        return heading;
    }

    public double getHeadingRelativ() {
        return headingRelativ;
    }

    public Lage getLage(Vessel me) {
        return new Lage(this, me);
    }

    public Punkt2D getPosition(int minutes) {
        return peilPunkt2.getPunkt2D(heading, speed * minutes / 60.0);
    }

    public Punkt2D getSecondPosition() {
        return peilPunkt2;
    }

    /**
     * Speed
     *
     * @return speed
     */

    public double getSpeed() {
        return speed;
    }

    public double getSpeedRelativ() {
        return speedRelativ;
    }

    public double getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinieRelativ.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        double abstand = peilPunkt2.abstand(p);
        return abstand / speedRelativ * 60.0;
    }

    /**
     * Sett die zweite Seitenpeilung. Dabei werden dann Geschwindigkeit und Kurs (neu) berechnet
     *
     * @param minutes            Zeitunterschied zwischen zwei Peilungen in Minuten
     * @param radarSeitenPeilung zweite Rechtweisende Peilung
     * @param distance           distance bei der zweiten Peilung
     */
    public Lage setSecondSeitenpeilung(int minutes, int radarSeitenPeilung, double distance, Vessel me) {
        dist2 = distance;
        rwP2 = radarSeitenPeilung;
        peilPunkt2 = new Punkt2D().getPunkt2D(radarSeitenPeilung, distance);
        kurslinieRelativ = new Gerade2D(peilPunkt1, peilPunkt2);
        headingRelativ = kurslinieRelativ.getYAxisAngle();
        speedRelativ = peilPunkt2.abstand(peilPunkt1) * 60.0 / (double) minutes;
        Punkt2D otherPos = me.getPosition(-minutes);
        Punkt2D relPos1 = peilPunkt1.add(otherPos);
        kurslinie = new Gerade2D(relPos1, peilPunkt2);
        heading = kurslinie.getYAxisAngle();
        speed = relPos1.abstand(peilPunkt2) * 60.0 / (double) minutes;
        return getLage(me);
    }

    public static class Lage {
        /**
         * BCR - Bow Crossing Point (Passierpunkt Kurslinie)
         */
        public final Punkt2D bcr;
        /**
         * BCR Abstand
         */

        public final double bcrDistance;
        /**
         * CPA - ClosestPointOfApproach
         */
        public final Punkt2D cpa;
        /**
         * CPA Abstand
         */
        public final double cpaDistance;
        /**
         * Radarseitenpeilung zum CPA
         */
        public final double pCPA;
        /**
         * Seitenpeilung zum CPA
         */
        public final double sCPA;
        /**
         * Time to BCR
         */
        public final double tBCR;
        /**
         * Time to CPA
         */
        public final double tCPA;
        private final Vessel me, gegner;

        public Lage(Vessel gegner, Vessel me) {
            this.gegner = gegner;
            this.me = me;
            cpa = gegner.kurslinieRelativ.getLotpunkt(me.peilPunkt2);
            cpaDistance = cpa.abstand(me.peilPunkt2);
            pCPA = new Gerade2D(me.peilPunkt2, cpa).getYAxisAngle();
            sCPA = (pCPA + me.getHeading());
            tCPA = gegner.getTimeTo(cpa);
            bcr = gegner.kurslinieRelativ.getSchnittpunkt(me.kurslinie);
            bcrDistance = me.peilPunkt2.abstand(bcr);
            tBCR = gegner.getTimeTo(bcr);
        }

        public Vessel getVessel() {
            return gegner;
        }

        public void manoever(int minutes) {
            Punkt2D manoeverposition = gegner.getPosition(minutes);
        }
    }
}
