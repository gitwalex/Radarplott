package com.gerwalex.radarplott.math;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Lage extends BaseObservable {
    private final Punkt2D bcr;
    private final Punkt2D cpa;
    private final Vessel me;
    private final Vessel originalVessel;
    private final Vessel other;
    private final Punkt2D relPos;

    public static Lage getLage(Vessel me, Vessel relativeVessel) {
        return new Lage(me, relativeVessel);
    }

    public Lage(Vessel me, Vessel relativVessel) {
        this.me = me;
        this.originalVessel = relativVessel;
        Punkt2D otherPos = me.getPosition(-relativVessel.minutes);
        relPos = relativVessel.firstPosition.add(otherPos);
        other = new Vessel(relPos, relativVessel.secondPosition, relativVessel.minutes);
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
        int dauer = (int) originalVessel.getTimeTo(cpa);
        return me.speed / 60f * dauer;
    }

    @Bindable
    public float getHeadingAbsolut() {
        return other.heading;
    }

    @Bindable
    public float getHeadingRelativ() {
        return originalVessel.heading;
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
        return other.speed;
    }

    @Bindable
    public float getSpeedRelativ() {
        return originalVessel.speed;
    }

    @Bindable
    public float getTimeToBCR() {
        return originalVessel.getTimeTo(bcr);
    }

    @Bindable
    public float getTimeToCPA() {
        return originalVessel.getTimeTo(cpa);
    }
}
