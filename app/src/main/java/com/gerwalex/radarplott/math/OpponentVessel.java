package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import com.gerwalex.radarplott.main.IllegalManoeverException;

import java.util.Locale;

public class OpponentVessel extends BaseObservable {
    public final String name;
    private final float dist1;
    private final Vessel me;
    private final int rwP1;
    private final int startTime;
    private Vessel absolutVessel;
    private Punkt2D bcr;
    private Punkt2D bcrManoever;
    private Punkt2D cpa;
    private Punkt2D cpaManoever;
    private float dist2;
    private Vessel manoeverVessel;
    private int minutes;
    private Punkt2D relPosition;
    private Vessel relativVessel;
    private int rwP2;

    /**
     * Erstellt ein Schiff aus Seitenpeilung. Schiff hat Geschwindigkeit 0 und Kurs 0
     *
     * @param me
     * @param time                Uhrzeit in Minuten nach Mitternacht
     * @param name                Name
     * @param peilungRechtweisend Rechtweisende Peilung
     * @param distance            distance bei Peilung
     */

    public OpponentVessel(@NonNull Vessel me, int time, @NonNull Character name, int peilungRechtweisend,
                          double distance) {
        this.name = name.toString();
        me.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                notifyChange();
            }
        });
        this.me = me;
        startTime = time;
        dist1 = (float) distance;
        rwP1 = peilungRechtweisend;
    }

    public Vessel createManoever(Vessel me, int minutes) {
        Punkt2D secondPosition = relativVessel.getSecondPosition();
        Punkt2D mp = relativVessel.getPosition(minutes);
        Punkt2D mpMe = me.getPosition(this.minutes);
        Punkt2D mpRelPos = relPosition.add(mpMe);
        Vektor2D kurslinie = new Vektor2D(mpRelPos, secondPosition);
        manoeverVessel =
                new Vessel(mp, kurslinie.getYAxisAngle(), mpRelPos.getAbstand(secondPosition) * 60 / this.minutes);
        cpaManoever = me.getCPA(manoeverVessel);
        bcrManoever = me.getBCR(manoeverVessel);
        notifyChange();
        return manoeverVessel;
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
    public float getAbstandManoeverBCR() {
        return me.getAbstand(bcrManoever);
    }

    @Bindable
    public float getAbstandManoeverCPA() {
        return me.getAbstand(cpaManoever);
    }

    @Bindable
    public float getDistanceToCPA() {
        int dauer = (int) relativVessel.getTimeTo(cpa);
        return me.speed / 60f * dauer;
    }

    @Bindable
    public float getDistanceToManoeverCPA() {
        return 0;
        //        int dauer = (int) manoeverVessel.getTimeTo(cpaManoever);
        //        return me.speed / 60f * dauer;
    }

    @Bindable
    public float getHeadingAbsolut() {
        return absolutVessel.heading;
    }

    @Bindable
    public float getHeadingManoever() {
        return manoeverVessel.heading;
    }

    @Bindable
    public float getHeadingRelativ() {
        return relativVessel.heading;
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

    public Vessel getManoever() {
        return manoeverVessel;
    }

    @Bindable
    public int getMinutes() {
        return minutes;
    }

    @Bindable
    public double getPeilungRechtweisendCPA() {
        return me.getPeilungRechtweisend(cpa);
    }

    @Bindable
    public double getPeilungRechtweisendManoeverCPA() {
        return me.getPeilungRechtweisend(cpaManoever);
    }

    public final Punkt2D getRelPosition() {
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = relativVessel.firstPosition.add(otherPos);
        absolutVessel = new Vessel(relPosition, relativVessel.secondPosition, minutes);
        return relPosition;
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
    public float getSpeedAbsolut() {
        return absolutVessel.speed;
    }

    @Bindable
    public float getSpeedManoever() {
        return manoeverVessel.speed;
    }

    @Bindable
    public float getSpeedRelativ() {
        return relativVessel.speed;
    }

    @Bindable
    public String getStartTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", startTime / 60, startTime % 60);
    }

    @Bindable
    public int getTime() {
        return startTime + minutes;
    }

    @Bindable
    public float getTimeToBCR() {
        return relativVessel.getTimeTo(bcr);
    }

    @Bindable
    public float getTimeToCPA() {
        return relativVessel.getTimeTo(cpa);
    }

    @Bindable
    public float getTimeToManoeverBCR() {
        return manoeverVessel.getTimeTo(bcrManoever);
    }

    @Bindable
    public float getTimeToManoeverCPA() {
        return 0;
        //        return manoeverVessel.getTimeTo(cpaManoever);
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
        relativVessel = new Vessel(firstPosition, secondPosition, time - startTime);
        cpa = me.getCPA(relativVessel);
        bcr = me.getBCR(relativVessel);
        Punkt2D otherPos = me.getPosition(-minutes);
        relPosition = relativVessel.firstPosition.add(otherPos);
        absolutVessel = new Vessel(relPosition, relativVessel.secondPosition, minutes);
    }
}
