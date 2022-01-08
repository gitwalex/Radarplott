package com.gerwalex.radarplott.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Locale;

public class OpponentVessel extends Vessel {
    public final String name;
    private final float dist1;
    private final int rwP1;
    private final int startTime;
    private float dist2;
    private float headingAbsolut;
    private Vessel manoeverVessel;
    private Vessel me;
    /**
     * Abstand zwischen den Peilungen in Minuten
     */
    private int minutes;
    private Punkt2D relPosition;
    private int rwP2;
    private float speedAbsolut;

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
        setFirstPosition(new Punkt2D().getPunkt2D(peilungRechtweisend, dist1));
    }

    public Vessel createManoever(Vessel manoever, int minutes) {
        Punkt2D mp = getPosition(minutes);
        Punkt2D mpMe = manoever.getPosition(this.minutes);
        Punkt2D mpRelPos = relPosition.add(mpMe);
        Gerade2D kurslinie = new Gerade2D(mpRelPos, secondPosition);
        kurslinie.verschiebeParallell(mp);
        manoeverVessel =
                new Vessel(mp, kurslinie.getYAxisAngle(), mpRelPos.getAbstand(secondPosition) * 60 / this.minutes);
        return manoeverVessel;
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public float getHeadingAbsolut() {
        return headingAbsolut;
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
        return checkForValidKurs(heading);
    }

    public int getMinutes() {
        return minutes;
    }

    public final Punkt2D getRelPosition(@NonNull Vessel me) {
        this.me = me;
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = firstPosition.add(otherPos);
        Gerade2D kurslinieAbsolut = new Gerade2D(relPosition, secondPosition);
        headingAbsolut = kurslinieAbsolut.getYAxisAngle();
        speedAbsolut = (float) (relPosition.getAbstand(secondPosition) * 60.0 / (float) minutes);
        return relPosition;
    }

    public final float getRelativTimeTo(@NonNull Punkt2D p) {
        if (!kurslinie.isPunktAufGerade(p)) {
            throw new IllegalArgumentException("Punkt nicht auf Kurslinie");
        }
        float timeToP = (float) (secondPosition.getAbstand(p) / speed * 60.0);
        return isPunktInFahrtrichtung(p) ? timeToP : -timeToP;
    }

    public String getSecondTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", (startTime + minutes) / 60, (startTime + minutes) % 60);
    }

    public float getSeitenpeilungCPA(OpponentVessel me) {
        return getPeilungRechtweisendCPA(me) + me.getHeading();
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public float getSpeedAbsolut() {
        return speedAbsolut;
    }

    public String getStartTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", startTime / 60, startTime % 60);
    }

    public int getTime() {
        return startTime + minutes;
    }

    /**
     * Ein Entgegenkommer fährt mit einem relativen Kurs zwischen 90 und 270 Grad
     *
     * @return true, wennn relativer Kurs zum anderen Schiff zwischen 90 und 270 Grad ist.
     */
    public boolean isEntgegenkommer(Vessel me) {
        getRelPosition(me);
        return headingAbsolut > 90 && headingAbsolut < 270;
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
        this.minutes = time - startTime;
        secondPosition = new Punkt2D().getPunkt2D(rwP, dist2);
        kurslinie = new Gerade2D(firstPosition, secondPosition);
        heading = kurslinie.getYAxisAngle();
        speed = (float) (firstPosition.getAbstand(secondPosition) * 60.0 / (float) minutes);
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{name=" + name + "dist1=" + dist1 + ", dist2=" + dist2 + ", headingRelativ=" + headingAbsolut +
                ", " + "minutes=" + minutes + ", relPosition=" + relPosition + ", rwP1=" + rwP1 + ", rwP2=" + rwP2 +
                ", speedRelativ=" + speedAbsolut + '}' + super.toString();
    }
}
