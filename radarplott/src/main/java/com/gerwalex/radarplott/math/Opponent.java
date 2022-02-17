package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.gerwalex.lib.math.Punkt2D;

import java.util.Locale;
import java.util.Objects;

public class Opponent extends BaseObservable {
    public final ObservableField<Lage> manoever = new ObservableField<>();
    private final float dist1;
    private final Vessel me;
    private final float rwP1;
    private final int startTime;
    public String name;
    private float dist2;
    private Lage lage;
    private float minRadarSize;
    /**
     * Zeitunterschied zwischen den einzelnen Peilungen
     */
    private int minutes;
    private float rwP2;

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param startTime           Uhrzeit in Minuten nach Mitternacht
     * @param name                Name
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public Opponent(@NonNull Vessel me, int startTime, @NonNull String name, float peilungRechtweisend,
                    double distance) {
        this.me = Objects.requireNonNull(me);
        this.name = name;
        this.startTime = startTime;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
    }

    public Opponent(@NonNull Vessel me, int startTime, @NonNull String name, float peilungRechtweisend, double distance,
                    float time, float rwP2, double distance2) {
        this(me, startTime, name, peilungRechtweisend, distance);
        setSecondSeitenpeilung((int) time, rwP2, distance2);
    }

    public float getMinutes() {
        return minutes;
    }

    public Vessel getMe() {
        return me;
    }

    public void createManoeverLage(Vessel other, int minutes) {
        manoever.set(getLage().getLage(other, minutes));
    }

    public void createManoeverLage(float abstandCPA, int minutes) {
        manoever.set(getLage().getLage(abstandCPA, minutes));
    }

    public float getDist1() {
        return dist1;
    }

    public float getDist2() {
        return dist2;
    }

    private String getFormatedTime(int minutes) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes / 60, minutes % 60);
    }

    @NonNull
    public Lage getLage() {
        return Objects.requireNonNull(lage);
    }

    public float getMinRadarSize() {
        return minRadarSize;
    }

    @Bindable
    public Vessel getRelativeVessel() {
        return getLage().getRelativVessel();
    }

    @Bindable
    public float getRwP1() {
        return rwP1;
    }

    public float getRwP2() {
        return rwP2;
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
    public final void setSecondSeitenpeilung(int time, float rwP, double distance) {
        dist2 = (float) distance;
        rwP2 = rwP;
        minutes = time - startTime;
        Punkt2D firstPosition = new Punkt2D().getPunkt2D(rwP1, dist1);
        Punkt2D secondPosition = new Punkt2D().getPunkt2D(rwP, dist2);
        Vessel relativVessel = new Vessel(firstPosition, secondPosition, minutes);
        lage = new Lage(me, relativVessel);
        createManoeverLage(me, 0);
        me.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                lage = new Lage(me, relativVessel);
                notifyPropertyChanged(BR.lage);
            }
        });
        minRadarSize = Math.max(dist1, dist2);
    }
}
