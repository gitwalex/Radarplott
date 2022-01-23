package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import java.util.Locale;
import java.util.Objects;

public class OpponentVessel extends BaseObservable {
    public final ObservableField<Lage> manoever = new ObservableField<>();
    public final String name;
    private final float dist1;
    private final Vessel me;
    private final int rwP1;
    private final int startTime;
    private Lage lage;
    private float dist2;
    /**
     * Zeitunterschied zwischen den einzelnen Peilungen
     */
    private int minutes;
    private int rwP2;

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param startTime           Uhrzeit in Minuten nach Mitternacht
     * @param name                Name
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public OpponentVessel(@NonNull Vessel me, int startTime, @NonNull Character name, int peilungRechtweisend,
                          double distance) {
        this.me = Objects.requireNonNull(me);
        this.name = name.toString();
        this.startTime = startTime;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
    }

    public void createManoeverLage(Vessel other, int minutes) {
        manoever.set(new Lage(lage, other, minutes));
    }

    public void createManoeverLage(float abstandCPA, int minutes) {
        manoever.set(new Lage(lage, abstandCPA, minutes));
    }

    private String getFormatedTime(int minutes) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes / 60, minutes % 60);
    }

    public Lage getLage() {
        return lage;
    }

    @Bindable
    public Vessel getRelativeVessel() {
        return Objects.requireNonNull(lage).getRelativVessel();
    }

    @Bindable
    public String getSecondTime() {
        return getFormatedTime(startTime + minutes);
    }

    @Bindable
    public String getStartTime() {
        return getFormatedTime(startTime);
    }

    @Bindable
    public int getTime() {
        return startTime + minutes;
    }

    /**
     * Sett die zweite Seitenpeilung. Dabei werden dann Geschwindigkeit und Kurs (neu) berechnet
     *
     * @param time     zweite Uhrzeit in Minuten nach Mitternacht
     * @param rwP      zweite Rechtweisende Peilung
     * @param distance distance bei der zweiten Peilung
     */
    public void setSecondSeitenpeilung(int time, int rwP, double distance) {
        dist2 = (float) distance;
        rwP2 = rwP;
        minutes = time - startTime;
        Punkt2D firstPosition = new Punkt2D().getPunkt2D(rwP1, dist1);
        Punkt2D secondPosition = new Punkt2D().getPunkt2D(rwP, dist2);
        Vessel relativVessel = new Vessel(firstPosition, secondPosition, minutes);
        lage = new Lage(me, relativVessel);
        me.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                lage = new Lage(me, relativVessel);
            }
        });
    }
}
