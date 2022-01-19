package com.gerwalex.radarplott.math;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Lage extends BaseObservable {
    public final float abstandBCR;
    public final float abstandCPA;
    public final float distanceToCPA;
    public final Vessel me;
    public final float peilungRechtweisendCPA;
    public final float timeToBCR;
    public final float timeToCPA;
    private final Vessel absolutVessel;
    private final Punkt2D bcr;
    private final Punkt2D cpa;
    private final Punkt2D relPos;
    private final Vessel relativVessel;

    public Lage(Lage lage, Vessel manoever, int minutes) {
        this.me = lage.me;
        this.absolutVessel = lage.absolutVessel;
        Vessel relativ = lage.relativVessel;
        Punkt2D mp = relativ.getPosition(minutes);
        relPos = absolutVessel.firstPosition.add(manoever.getRichtungsvektor(minutes + relativ.minutes));
        this.relativVessel = new Vessel(relPos, mp, relativ.minutes);
        cpa = manoever.getCPA(relativVessel);
        abstandCPA = Math.abs(manoever.getAbstand(cpa));
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = manoever.speed / 60f * timeToCPA;
        peilungRechtweisendCPA = manoever.getPeilungRechtweisend(cpa);
        bcr = manoever.getBCR(relativVessel);
        abstandBCR = Math.abs(manoever.getAbstand(bcr));
        timeToBCR = relativVessel.getTimeTo(bcr);
    }


    /**
     * Lage zum Beginn
     *
     * @param me            me
     * @param relativVessel relativVessel
     */
    public Lage(Vessel me, Vessel relativVessel) {
        this.me = me;
        this.relativVessel = relativVessel;
        relPos = relativVessel.firstPosition.add(me.getRichtungsvektor(-relativVessel.minutes));
        absolutVessel = new Vessel(relPos, relativVessel.secondPosition, relativVessel.minutes);
        cpa = me.getCPA(relativVessel);
        abstandCPA = me.getAbstand(cpa);
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = relativVessel.speed / 60f * timeToCPA;
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
        return absolutVessel.heading;
    }

    @Bindable
    public float getHeadingRelativ() {
        return relativVessel.heading;
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
        return absolutVessel.speed;
    }

    @Bindable
    public float getSpeedRelativ() {
        return relativVessel.speed;
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
