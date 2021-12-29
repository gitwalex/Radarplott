package com.gerwalex.radarplott.radar;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

public class Vessel extends BaseObservable {
    public final Character name;
    private float dist1, dist2;
    private float heading;
    private float headingRelativ = -1;
    private Gerade2D kurslinieAbsolut;
    private Gerade2D kurslinieRelativ;
    private int rwP1, rwP2;
    private float speed;
    private float speedRelativ = -1;
    private Punkt2D startPosition, aktPosition, relPosition;

    public Vessel(int heading, float speed) {
        name = null; // Kein Name fuer eigenes Schiff
        headingRelativ = this.heading = heading % 360;
        speedRelativ = this.speed = speed;
        aktPosition = new Punkt2D(0, 0);
        startPosition = aktPosition.getPunkt2D(this.heading, -(speed / 6f));
        kurslinieRelativ = kurslinieAbsolut = new Gerade2D(startPosition, aktPosition);
    }

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public Vessel(Character name, int peilungRechtweisend, double distance) {
        this.name = name;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
        startPosition = new Punkt2D().getPunkt2D(peilungRechtweisend, dist1);
    }

    public Punkt2D getFirstPosition() {
        return startPosition;
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public float getHeading() {
        return heading;
    }

    @Bindable
    public void setHeading(float heading) {
        if (this.heading != heading) {
            this.heading = heading % 360;
            aktPosition = new Punkt2D(0, 0);
            startPosition = aktPosition.getPunkt2D(this.heading, -(speed / 6f));
            kurslinieRelativ = kurslinieAbsolut = new Gerade2D(startPosition, aktPosition);
            notifyPropertyChanged(BR.heading);
        }
    }

    public float getHeadingRelativ() {
        return headingRelativ;
    }

    public Gerade2D getKurslinieAbsolut() {
        return kurslinieAbsolut;
    }

    public Gerade2D getKurslinieRelativ() {
        return kurslinieRelativ;
    }

    public Lage getLage(Vessel me) {
        return new Lage(this, me);
    }

    public Punkt2D getPosition(int minutes) {
        return aktPosition.getPunkt2D(heading, (float) (speed * minutes / 60.0));
    }

    public Punkt2D getRelPosition() {
        return relPosition;
    }

    public Punkt2D getSecondPosition() {
        return aktPosition;
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public float getSpeed() {
        return speed;
    }

    @Bindable
    public void setSpeed(float speed) {
        if (this.speed != speed) {
            this.speed = speed;
            aktPosition = new Punkt2D(0, 0);
            startPosition = aktPosition.getPunkt2D(this.heading, -(speed / 6f));
            kurslinieRelativ = kurslinieAbsolut = new Gerade2D(startPosition, aktPosition);
            notifyPropertyChanged(BR.speed);
        }
    }

    public float getSpeedRelativ() {
        return speedRelativ;
    }

    public float getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinieRelativ.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (aktPosition.abstand(p) / speedRelativ * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    /**
     * Prueft, ob ein Punkt auf der EigenesSchiff liegt. Toleranz ist 1E6.
     *
     * @param p Punkt
     * @return true, wenn der Punkt auf der EigenesSchiff liegt, ansonsten false.
     */
    public boolean isPunktAufKurslinie(@NonNull Punkt2D p) {
        return Math.round(kurslinieRelativ.getAbstand(p.x, p.y) * 1E6) == 0;
    }

    /**
     * Prueft, ob ein Punkt in Fahrtrichtung liegt.
     *
     * @param p zu pruefender Punkt
     * @return true, wenn der Punkt in Fahrtrichtung liegt. Sonst false.
     */
    public boolean isPunktInFahrtrichtung(Punkt2D p) {
        if (!isPunktAufKurslinie(p)) {
            return false;
        }
        double wegx = kurslinieRelativ.getRichtungsvektor().getEndpunkt().x;
        double wegy = kurslinieRelativ.getRichtungsvektor().getEndpunkt().y;
        double lambda;
        if (wegx != 0) {
            lambda = (p.x - aktPosition.x) / wegx;
        } else {
            lambda = (p.y - aktPosition.y) / wegy;
        }
        return lambda > 0;
    }

    /**
     * Sett die zweite Seitenpeilung. Dabei werden dann Geschwindigkeit und Kurs (neu) berechnet
     *
     * @param minutes            Zeitunterschied zwischen zwei Peilungen in Minuten
     * @param radarSeitenPeilung zweite Rechtweisende Peilung
     * @param distance           distance bei der zweiten Peilung
     */
    public Lage setSecondSeitenpeilung(int minutes, int radarSeitenPeilung, double distance, Vessel me) {
        dist2 = (float) distance;
        rwP2 = radarSeitenPeilung;
        aktPosition = new Punkt2D().getPunkt2D(radarSeitenPeilung, dist2);
        kurslinieRelativ = new Gerade2D(startPosition, aktPosition);
        headingRelativ = kurslinieRelativ.getYAxisAngle();
        speedRelativ = (float) (aktPosition.abstand(startPosition) * 60.0 / (float) minutes);
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = startPosition.add(otherPos);
        kurslinieAbsolut = new Gerade2D(relPosition, aktPosition);
        heading = kurslinieAbsolut.getYAxisAngle();
        speed = (float) (relPosition.abstand(aktPosition) * 60.0 / (float) minutes);
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

        public final float bcrDistance;
        /**
         * CPA - ClosestPointOfApproach
         */
        public final Punkt2D cpa;
        /**
         * CPA Abstand
         */
        public final float cpaDistance;
        /**
         * Radarseitenpeilung zum CPA
         */
        public final float pCPA;
        /**
         * Seitenpeilung zum CPA
         */
        public final float sCPA;
        /**
         * Time to BCR
         */
        public final float tBCR;
        /**
         * Time to CPA
         */
        public final float tCPA;
        private final Vessel me, gegner;

        public Lage(Vessel gegner, Vessel me) {
            this.gegner = gegner;
            this.me = me;
            cpa = gegner.kurslinieRelativ.getLotpunkt(me.aktPosition);
            cpaDistance = cpa.abstand(me.aktPosition);
            pCPA = new Gerade2D(me.aktPosition, cpa).getYAxisAngle();
            sCPA = (pCPA + me.getHeading());
            tCPA = gegner.getTimeTo(cpa);
            bcr = gegner.kurslinieRelativ.getSchnittpunkt(me.kurslinieRelativ);
            bcrDistance = me.aktPosition.abstand(bcr);
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
