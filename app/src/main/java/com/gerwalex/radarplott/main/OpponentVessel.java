package com.gerwalex.radarplott.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;

import java.util.Locale;

public class OpponentVessel extends Vessel {
    public final String name;
    private final float dist1;
    private final int rwP1;
    private final int startTime;
    private float dist2;
    private float headingRelativ;
    private Gerade2D kurslinieRelativ;
    /**
     * Abstand zwischen den Peilungen in Minuten
     */
    private int minutes;
    private Punkt2D relPosition;
    private int rwP2;
    private float speedRelativ;

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

    public float getAbstandBCR(Vessel me) {
        return me.getSecondPosition().getAbstand(getBCR(me));
    }

    public float getAbstandCPA(Vessel me) {
        return getCPA(me).getAbstand(me.getSecondPosition());
    }

    public Punkt2D getBCR(Vessel me) {
        return kurslinie.getSchnittpunkt(me.getKurslinie());
    }

    public Punkt2D getCPA(Vessel me) {
        return kurslinie.getLotpunkt(me.getSecondPosition());
    }

    /**
     * Heading
     *
     * @return heading
     */
    @Bindable
    public float getHeadingRelativ() {
        return headingRelativ;
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
    public Float getHeadingforCPADistance(@NonNull Vessel other, int minutes, float distance)
            throws IllegalManoeverException {
        Punkt2D cpa = getCPA(other);
        Float heading = null;
        if (minutes > 0 && getTimeTo(cpa) > minutes) {
            heading = 0f;
            Punkt2D futurePosition = getPosition(minutes);
            Punkt2D otherFuturePosition = other.getPosition(minutes);
            Kreis2D k = new Kreis2D(otherFuturePosition, distance);
            Punkt2D[] bp = k.getBeruehrpunkte(futurePosition);
            if (bp != null) {
                getRelPosition(other);
                Gerade2D t1 = new Gerade2D(futurePosition, bp[0]);
                Gerade2D kursAbsolutNeu = kurslinieRelativ.verschiebeParallell(bp[0]);
            }
        }
        return checkForValidKurs(heading);
    }

    public Gerade2D getKurslinieRelativ() {
        return kurslinieRelativ;
    }

    public int getMinutes() {
        return minutes;
    }

    public float getPeilungRechtweisendCPA(Vessel me) {
        return new Gerade2D(me.getSecondPosition(), getCPA(me)).getYAxisAngle();
    }

    public Punkt2D getRelPosition(@NonNull Vessel me) {
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = firstPosition.add(otherPos);
        kurslinieRelativ = new Gerade2D(relPosition, secondPosition);
        headingRelativ = kurslinieRelativ.getYAxisAngle();
        speedRelativ = (float) (relPosition.getAbstand(secondPosition) * 60.0 / (float) minutes);
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

    public float getSeitenpeilungCPA(Vessel me) {
        return getPeilungRechtweisendCPA(me) + me.getHeading();
    }

    /**
     * Speed
     *
     * @return speed
     */
    @Bindable
    public float getSpeedRelativ() {
        return speedRelativ;
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
        return headingRelativ > 90 && headingRelativ < 270;
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
        return "Vessel{name=" + name + "dist1=" + dist1 + ", dist2=" + dist2 + ", headingRelativ=" + headingRelativ +
                ", " + "minutes=" + minutes + ", relPosition=" + relPosition + ", rwP1=" + rwP1 + ", rwP2=" + rwP2 +
                ", speedRelativ=" + speedRelativ + '}' + super.toString();
    }
}
