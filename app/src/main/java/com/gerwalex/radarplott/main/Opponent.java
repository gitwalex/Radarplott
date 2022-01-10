package com.gerwalex.radarplott.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Locale;

public class Opponent {
    public final String name;
    private final float dist1;
    private final Punkt2D firstPosition;
    private final Vessel me;
    private final int rwP1;
    private final int startTime;
    private float dist2;
    /**
     * Abstand zwischen den Peilungen in Minuten
     */
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

    public Opponent(Vessel me, int time, @NonNull Character name, int peilungRechtweisend, double distance) {
        this.me = me;
        this.name = name.toString();
        startTime = time;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
        firstPosition = new Punkt2D().getPunkt2D(peilungRechtweisend, dist1);
        relativVessel = new Vessel(firstPosition, 0, 0);
    }

    public Vessel createManoever(Vessel other, int minutes) {
        Punkt2D mp = relativVessel.getPosition(minutes);
        Punkt2D mpMe = other.getPosition(this.minutes);
        Punkt2D mpRelPos = relPosition.add(mpMe);
        Gerade2D kurslinie = new Gerade2D(mpRelPos, relativVessel.secondPosition);
        kurslinie.verschiebeParallell(mp);
        return new Vessel(mp, kurslinie.getYAxisAngle(),
                mpRelPos.getAbstand(relativVessel.secondPosition) * 60 / this.minutes);
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
    public Float getHeadingforCPADistance(@NonNull Opponent other, int minutes, float distance)
            throws IllegalManoeverException {
        Float heading = null;
        return heading;
    }

    public int getMinutes() {
        return minutes;
    }

    public final Punkt2D getRelPosition() {
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = firstPosition.add(otherPos);
        Punkt2D secondPosition = relativVessel.getPosition(0);
        Gerade2D kurslinieAbsolut = new Gerade2D(relPosition, secondPosition);
        float heading = kurslinieAbsolut.getYAxisAngle();
        float speed = (float) (relPosition.getAbstand(secondPosition) * 60.0 / (float) minutes);
        relativVessel = new Vessel(secondPosition, heading, speed);
        return relPosition;
    }

    public String getSecondTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", (startTime + minutes) / 60, (startTime + minutes) % 60);
    }

    public String getStartTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", startTime / 60, startTime % 60);
    }

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
        this.minutes = time - startTime;
        Punkt2D position = new Punkt2D().getPunkt2D(rwP, dist2);
        Gerade2D kurslinie = new Gerade2D(relativVessel.getPosition(0), position);
        float heading = kurslinie.getYAxisAngle();
        float speed = (float) (firstPosition.getAbstand(position) * 60.0 / (float) minutes);
        relativVessel = new Vessel(position, heading, speed);
    }

    @NonNull
    @Override
    public String toString() {
        return "Vessel{name=" + name + "relativ" + relativVessel;
    }
}
