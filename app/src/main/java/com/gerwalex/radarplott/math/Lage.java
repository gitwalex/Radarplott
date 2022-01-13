package com.gerwalex.radarplott.math;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Lage extends BaseObservable {
    public final Vessel me;
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
        this.me = manoever;
        cpa = me.getCPA(relativVessel);
        bcr = me.getBCR(relativVessel);
    }

    public Lage(Vessel me, Vessel relativVessel, int minutes, int heading, float speed) {
        this.me = me;
        Vessel manoever = new Vessel(heading, speed);
        Punkt2D secondPosition = relativVessel.getSecondPosition();
        Punkt2D mp = relativVessel.getPosition(minutes);
        Punkt2D mpMe = manoever.getPosition(relativVessel.minutes);
        Punkt2D otherPos = me.getPosition(-relativVessel.minutes);
        relPos = relativVessel.firstPosition.add(otherPos);
        Punkt2D mpRelPos = relPos.add(mpMe);
        Vektor2D kurslinie = new Vektor2D(mpRelPos, secondPosition);
        this.absolutVessel = new Vessel(relPos, relativVessel.secondPosition, relativVessel.minutes);
        this.relativVessel = new Vessel(mp, kurslinie.getYAxisAngle(),
                mpRelPos.getAbstand(secondPosition) * 60 / relativVessel.minutes);
        cpa = manoever.getCPA(relativVessel);
        bcr = manoever.getBCR(relativVessel);
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
        bcr = me.getBCR(relativVessel);
    }

    @Bindable
    public float getAbstandBCR() {
        return me.getAbstand(bcr);
    }

    @Bindable
    public float getAbstandCPA() {
        return me.getAbstand(cpa);
    }

    @Bindable
    public float getDistanceToCPA() {
        int dauer = (int) relativVessel.getTimeTo(cpa);
        return me.speed / 60f * dauer;
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
        return me.getPeilungRechtweisend(cpa);
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
        return relativVessel.getTimeTo(bcr);
    }

    @Bindable
    public float getTimeToCPA() {
        return relativVessel.getTimeTo(cpa);
    }
}
