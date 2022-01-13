package com.gerwalex.radarplott.math;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Lage extends BaseObservable {
    private final Punkt2D bcr;
    private final Punkt2D cpa;
    private final Vessel me;
    private final Vessel other;

    public Lage(Vessel me, Vessel other) {
        this.me = me;
        this.other = other;
        cpa = me.getCPA(other);
        bcr = me.getBCR(other);
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
        int dauer = (int) other.getTimeTo(cpa);
        return me.speed / 60f * dauer;
    }

    @Bindable
    public float getHeadingAbsolut() {
        return other.heading;
    }

    @Bindable
    public double getPeilungRechtweisendCPA() {
        return me.getPeilungRechtweisend(cpa);
    }

    @Bindable
    public float getSpeedAbsolut() {
        return other.speed;
    }

    @Bindable
    public float getSpeedRelativ() {
        return other.speed;
    }

    @Bindable
    public float getTimeToBCR() {
        return other.getTimeTo(bcr);
    }

    @Bindable
    public float getTimeToCPA() {
        return other.getTimeTo(cpa);
    }
}
