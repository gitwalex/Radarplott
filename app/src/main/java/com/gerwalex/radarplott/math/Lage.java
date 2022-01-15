package com.gerwalex.radarplott.math;

import android.util.Log;

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
        Punkt2D secondPosition = relativ.getSecondPosition();
        Punkt2D mp = relativ.getPosition(minutes);
        Punkt2D mpMe = manoever.getPosition(relativ.minutes);
        relPos = relativ.firstPosition.add(me.getRichtungsvektor(-relativ.minutes));
        Punkt2D mpRelPos = relPos.add(mpMe);
        Punkt2D mp1 = mp.add(me.getRichtungsvektor(-relativ.minutes));
        Log.d("gerwalex", ": " + mp1);
        Vektor2D kurslinie = new Vektor2D(mpRelPos, secondPosition);
        this.relativVessel =
                new Vessel(mp, kurslinie.getYAxisAngle(), mpRelPos.getAbstand(secondPosition) * 60 / minutes);
        cpa = manoever.getCPA(relativVessel);
        abstandCPA = manoever.getAbstand(cpa);
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = manoever.speed / 60f * timeToCPA;
        peilungRechtweisendCPA = manoever.getPeilungRechtweisend(cpa);
        bcr = manoever.getBCR(relativVessel);
        abstandBCR = manoever.getAbstand(bcr);
        timeToBCR = relativVessel.getTimeTo(bcr);
    }

    public Lage(Lage lage, int minutes, int heading, float speed) {
        this(lage, new Vessel(heading, speed), minutes);
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
