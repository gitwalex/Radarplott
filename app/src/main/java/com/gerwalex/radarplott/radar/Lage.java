package com.gerwalex.radarplott.radar;

import androidx.annotation.NonNull;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

public class Lage {
    public final Punkt2D aktPosition;
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
    public final float headingRelativ;
    /**
     * Radarseitenpeilung zum CPA
     */
    public final float pCPA;
    /**
     * Seitenpeilung zum CPA
     */
    public final float sCPA;
    public final float speedRelativ;
    /**
     * Time to BCR
     */
    public final float tBCR;
    /**
     * Time to CPA
     */
    public final float tCPA;
    private final OpponentVessel gegner;
    private final Gerade2D kurslinieRelativ;
    private final Vessel me;

    public Lage(OpponentVessel gegner, Vessel me, int minutes) {
        this.gegner = gegner;
        this.me = me;
        aktPosition = gegner.getPosition(minutes);
        kurslinieRelativ = gegner.getKurslinieRelativ();
        headingRelativ = gegner.getHeadingRelativ();
        speedRelativ = gegner.getSpeedRelativ();
        cpa = kurslinieRelativ.getLotpunkt(me.getAktPosition());
        cpaDistance = cpa.getAbstand(me.getAktPosition());
        pCPA = new Gerade2D(me.getAktPosition(), cpa).getYAxisAngle();
        sCPA = (pCPA + me.getHeading());
        tCPA = getTimeTo(cpa);
        bcr = kurslinieRelativ.getSchnittpunkt(me.getKurslinie());
        bcrDistance = me.getAktPosition().getAbstand(bcr);
        tBCR = getTimeTo(bcr);
    }

    public Gerade2D getKurslinieRelativ() {
        return kurslinieRelativ;
    }

    public float getTimeTo(@NonNull Punkt2D p) {
        if (!kurslinieRelativ.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (aktPosition.getAbstand(p) / speedRelativ * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    public OpponentVessel getVessel() {
        return gegner;
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
}

