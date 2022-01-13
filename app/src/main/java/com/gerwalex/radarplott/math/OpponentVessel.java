package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.main.IllegalManoeverException;

import java.util.Locale;

public class OpponentVessel extends BaseObservable {
    public final String name;
    private final float dist1;
    private final int rwP1;
    private final int startTime;
    private float dist2;
    private int minutes;
    private Punkt2D relPosition;
    private Vessel relativVessel;
    private int rwP2;

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param time                Uhrzeit in Minuten nach Mitternacht
     * @param name                Name
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public OpponentVessel(int time, @NonNull Character name, int peilungRechtweisend, double distance) {
        this.name = name.toString();
        startTime = time;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
    }

    public Vessel createManoever(Vessel me, Vessel manoever, int minutes) {
        Punkt2D secondPosition = relativVessel.getSecondPosition();
        Punkt2D mp = relativVessel.getPosition(minutes);
        Punkt2D mpMe = manoever.getPosition(relativVessel.minutes);
        Punkt2D otherPos = me.getPosition(-relativVessel.minutes);
        relPosition = relativVessel.firstPosition.add(otherPos);
        Punkt2D mpRelPos = relPosition.add(mpMe);
        Vektor2D kurslinie = new Vektor2D(mpRelPos, secondPosition);
        Vessel manoeverVessel =
                new Vessel(mp, kurslinie.getYAxisAngle(), mpRelPos.getAbstand(secondPosition) * 60 / this.minutes);
        return manoeverVessel;
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

    public Lage getLage(Vessel me) {
        return new Lage(me, relativVessel);
    }

    public Lage getManoever(Vessel me, int minutes, int heading, float speed) {
        return new Lage(getLage(me), minutes, heading, speed);
    }

    @Bindable
    public Vessel getRelativeVessel() {
        return relativVessel;
    }

    @Bindable
    public String getSecondTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", (startTime + minutes) / 60, (startTime + minutes) % 60);
    }

    @Bindable
    public String getStartTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", startTime / 60, startTime % 60);
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
    }
}
