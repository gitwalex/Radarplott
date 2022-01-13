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

    public Lage(Lage lage, int minutes, int heading, float speed) {
        this.absolutVessel = lage.absolutVessel;
        Vessel manoever = new Vessel(heading, speed);
        Punkt2D secondPosition = lage.relativVessel.getSecondPosition();
        Punkt2D mp = lage.relativVessel.getPosition(minutes);
        Punkt2D mpMe = manoever.getPosition(lage.relativVessel.minutes);
        Punkt2D otherPos = lage.me.getPosition(-lage.relativVessel.minutes);
        relPos = lage.relativVessel.firstPosition.add(otherPos);
        Punkt2D mpRelPos = relPos.add(mpMe);
        Vektor2D kurslinie = new Vektor2D(mpRelPos, secondPosition);
        relativVessel = new Vessel(mp, kurslinie.getYAxisAngle(),
                mpRelPos.getAbstand(secondPosition) * 60 / lage.relativVessel.minutes);
        this.me = lage.me;
        cpa = manoever.getCPA(relativVessel);
        abstandCPA = manoever.getAbstand(cpa);
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = manoever.speed / 60f * timeToCPA;
        peilungRechtweisendCPA = manoever.getPeilungRechtweisend(cpa);
        bcr = manoever.getBCR(relativVessel);
        abstandBCR = manoever.getAbstand(bcr);
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
        Punkt2D otherPos = me.getPosition(-relativVessel.minutes);
        relPos = relativVessel.firstPosition.add(otherPos);
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
