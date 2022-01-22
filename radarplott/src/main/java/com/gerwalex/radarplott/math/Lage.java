package com.gerwalex.radarplott.math;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Lage extends BaseObservable {
    public final float abstandBCR;
    public final float abstandCPA;
    public final float distanceToCPA;
    public final float peilungRechtweisendCPA;
    public final float timeToBCR;
    public final float timeToCPA;
    private final Vessel absolutVessel;
    private final Punkt2D bcr;
    private final Punkt2D cpa;
    private final Punkt2D relPos;
    private final Vessel relativVessel;

    /**
     * Lage zum Beginn. Durch Peilungen stehen Kurs und Geschwindigkeit des Opponent fest. Anhand dieses relativen
     * Kurses wird der absolute(=echte) Kurs/Geschwindigkeit in Abhängigkeit des eigenen Schiffs (me) ermittelt.
     *
     * @param me            me
     * @param relativVessel relativVessel, durch Peilungen ermittelt.
     */

    public Lage(Lage lage, Vessel manoever, int minutes) {
        this.absolutVessel = lage.absolutVessel;
        Vessel relativ = lage.relativVessel;
        Punkt2D mp = relativ.getPosition(minutes);
        relPos = absolutVessel.firstPosition.add(manoever.getRichtungsvektor(relativ.minutes));
        Vessel rv = new Vessel(relPos, absolutVessel.secondPosition, relativ.minutes);
        this.relativVessel = new Vessel(mp, rv.getHeading(), rv.getSpeed());
        cpa = manoever.getCPA(relativVessel);
        abstandCPA = Math.abs(manoever.getAbstand(cpa));
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = manoever.getSpeed() / 60f * timeToCPA;
        peilungRechtweisendCPA = manoever.getPeilungRechtweisend(cpa);
        bcr = manoever.getBCR(relativVessel);
        abstandBCR = manoever.getAbstand(bcr);
        timeToBCR = relativVessel.getTimeTo(bcr);
    }

    /**
     * Lage zum Beginn. Durch Peilungen stehen Kurs und Geschwindigkeit des Opponent fest. Anhand dieses relativen
     * Kurses wird der absolute(=echte) Kurs/Geschwindigkeit in Abhängigkeit des eigenen Schiffs (me) ermittelt.
     *
     * @param me            me
     * @param relativVessel relativVessel, durch Peilungen ermittelt.
     */
    public Lage(Vessel me, Vessel relativVessel) {
        this.relativVessel = relativVessel;
        relPos = relativVessel.firstPosition.add(me.getRichtungsvektor(-relativVessel.minutes));
        absolutVessel = new Vessel(relPos, relativVessel.secondPosition, relativVessel.minutes);
        cpa = me.getCPA(relativVessel);
        abstandCPA = me.getAbstand(cpa);
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = relativVessel.getSpeed() / 60f * timeToCPA;
        peilungRechtweisendCPA = me.getPeilungRechtweisend(cpa);
        bcr = me.getBCR(relativVessel);
        abstandBCR = me.getAbstand(bcr);
        timeToBCR = relativVessel.getTimeTo(bcr);
    }

    @Bindable
    public float getAbstandBCR() {
        return abstandBCR;
    }

    @Bindable
    public float getAbstandCPA() {
        return abstandCPA;
    }

    @Bindable
    public float getDistanceToCPA() {
        return distanceToCPA;
    }

    @Bindable
    public float getHeadingAbsolut() {
        return absolutVessel.getHeading();
    }

    @Bindable
    public float getHeadingRelativ() {
        return relativVessel.getHeading();
    }

    @Bindable
    public double getPeilungRechtweisendCPA() {
        return peilungRechtweisendCPA;
    }

    public Punkt2D getRelPos() {
        return relPos;
    }

    public Vessel getRelativVessel() {
        return relativVessel;
    }

    @Bindable
    public float getSpeedAbsolut() {
        return absolutVessel.getSpeed();
    }

    @Bindable
    public float getSpeedRelativ() {
        return relativVessel.getSpeed();
    }

    @Bindable
    public float getTimeToBCR() {
        return timeToBCR;
    }

    @Bindable
    public float getTimeToCPA() {
        return timeToCPA;
    }
}
