package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import com.gerwalex.radarplott.main.IllegalManoeverException;

import java.util.Locale;
import java.util.Objects;

public class OpponentVessel extends BaseObservable {
    public final ObservableField<Lage> lage = new ObservableField<>();
    public final String name;
    private final float dist1;
    private final Vessel me;
    private final int rwP1;
    private final int startTime;
    public ObservableField<Lage> manoever = new ObservableField<>();
    private float dist2;
    /**
     * Zeitunterschied zwischen den einzelnen Peilungen
     */
    private int minutes;
    private Vessel relativVessel;
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
        manoever.set(new Lage(lage.get(), other, minutes));
    }

    private String getFormatedTime(int minutes) {
        return String.format(Locale.getDefault(), "%02d:%02d", minutes / 60, minutes % 60);
    }

    /**
     * Liefert einen neuen Kurs zu einem Manöver, dass in {minutes} Minuten in der Zukunft liegt und zu einen CPA von
     * {cpaDistance} zu {other} führen soll.
     *
     * @param other    anderes Schiff
     * @param minutes  Zeitpunkt in Minuten, zu dem das Manöver stattfinden soll
     * @param distance Gewünschte Distanz
     * @return Neuen Kurs, wenn möglich.
     * Null in folgenden Fällen:
     * <br> - zeitpunkt liegt in der Vergangenheit
     * <br> - die Position zum Zeitpunkt ist geringer als die gewuenschte Distanz
     * <br> - der Aktuelle CPA wurde bereits passiert
     * @throws IllegalManoeverException Kursänderung wiederspricht KVR §§ 19 und ist daher nicht erlaubt
     *                                  -
     */
    @Nullable
    public Float getHeadingforCPADistance(@NonNull OpponentVessel other, int minutes, float distance)
            throws IllegalManoeverException {
        Float heading = null;
        return relativVessel.checkForValidKurs(heading);
    }

    @Bindable
    public Vessel getRelativeVessel() {
        return relativVessel;
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
        relativVessel = new Vessel(firstPosition, secondPosition, minutes);
        lage.set(new Lage(me, relativVessel));
        me.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                lage.set(new Lage(me, relativVessel));
            }
        });
    }
}
