package com.gerwalex.radarplott.math;

public class Lage {
    private final Vessel absolutVessel;
    private final float abstandBCR;
    private final float abstandCPA;
    private final Punkt2D bcr;
    private final Punkt2D cpa;
    private final float distanceToCPA;
    private final float maxCPA;
    private final Vessel me;
    private final float peilungRechtweisendCPA;
    private final Punkt2D relPos;
    private final Vessel relativVessel;
    private final float timeToBCR;
    private final float timeToCPA;

    /**
     * Lage zum Beginn. Durch Peilungen stehen Kurs und Geschwindigkeit des Opponent fest. Anhand dieses relativen
     * Kurses wird der absolute(=echte) Kurs/Geschwindigkeit in Abhängigkeit des eigenen Schiffs (me) ermittelt.
     *
     * @param me            me
     * @param relativVessel relativVessel, durch Peilungen ermittelt.
     */
    public Lage(Vessel me, Vessel relativVessel) {
        this.me = me;
        this.relativVessel = relativVessel;
        relPos = relativVessel.firstPosition.add(me.getRichtungsvektor(-relativVessel.minutes));
        absolutVessel = new Vessel(relPos, relativVessel.secondPosition, relativVessel.minutes);
        cpa = me.getCPA(relativVessel);
        abstandCPA = me.getAbstand(cpa);
        timeToCPA = relativVessel.getTimeTo(cpa);
        distanceToCPA = relativVessel.getSpeed() / 60f * timeToCPA;
        peilungRechtweisendCPA = me.getPeilungRechtweisend(cpa);
        bcr = me.getBCR(relativVessel);
        abstandBCR = me.getAbstand(bcr);
        timeToBCR = relativVessel.getTimeTo(bcr);
        maxCPA = me.secondPosition.getAbstand(relativVessel.getSecondPosition());
    }

    public float getAbstandBCR() {
        return abstandBCR;
    }

    public float getAbstandCPA() {
        return abstandCPA;
    }

    public Punkt2D getBCR() {
        return bcr;
    }

    public Punkt2D getCPA() {
        return cpa;
    }

    public float getDistanceToCPA() {
        return distanceToCPA;
    }

    public float getHeadingAbsolut() {
        return absolutVessel.getHeading();
    }

    public float getHeadingRelativ() {
        return relativVessel.getHeading();
    }

    public Lage getLage(float abstandCPA, int minutes) {
        // Ermitteln der potientiellen Tangenten fur gewuenschten CPA-Abstand
        Kreis2D k = new Kreis2D(new Punkt2D(), abstandCPA);
        Punkt2D currentPos = relativVessel.getPosition(minutes);
        Punkt2D[] sp = k.getBeruehrpunkte(currentPos);
        if (sp == null) {
            // Keine Tangenten möglich
            throw new IllegalArgumentException("CPA nicht (mehr) erreichbar");
        }
        // ok, jetzt CPA auswählen. Dabei KVR §19 beachten: Ausweichen nur nach Steuerbord.
        Punkt2D cpa = me.isSteuerbord(sp[0]) ? sp[0] : sp[1];
        // Jetzt Gerade durch aktuelle Position und CPA legen...
        Kurslinie cpaGerade = new Kurslinie(currentPos, cpa);
        // ... und diese Gerade in die Startposition der relativVessel verschieben.
        Kurslinie line = new Kurslinie(absolutVessel.secondPosition, cpaGerade.getHeading());
        // Ermittlung Manoeverkurs: Jetzt den Schnittpunkte vo manoever mit neuer relativVessel ermitteln
        k = new Kreis2D(absolutVessel.firstPosition, relPos.getAbstand(relativVessel.firstPosition));
        Punkt2D[] rel = line.getSchnittpunkt(k);
        if (rel == null) {
            throw new IllegalArgumentException("CPA nicht möglich");
        }
        // auch hier wieder KVR §19 beachten: Kursänderung nur nach Steuerbord
        Punkt2D relPos = me.isSteuerbord(rel[0]) ? rel[0] : rel[1];
        // Jetzt haben wir die Daten für den neuen CPA...
        Vessel relativVessel = new Vessel(relPos, this.relativVessel.secondPosition, this.relativVessel.minutes);
        // .. allerdings muss der Weg durch die aktuelle Position laufen.
        Vessel relativVessel1 = new Vessel(currentPos, relativVessel.getHeading(), relativVessel.getSpeed());
        float heading = absolutVessel.firstPosition.getYAxisAngle(relPos);
        Vessel manoever = new Vessel(heading, me.getSpeed());
        return new Lage(manoever, relativVessel1);
    }

    /**
     * @param manoever Vessel
     * @param minutes
     * @return neue Lage
     */
    public Lage getLage(Vessel manoever, int minutes) {
        Vessel relativ = relativVessel;
        Punkt2D mp = relativ.getPosition(minutes);
        Punkt2D relPos = absolutVessel.firstPosition.add(manoever.getRichtungsvektor(relativ.minutes));
        Vessel rv = new Vessel(relPos, absolutVessel.secondPosition, relativ.minutes);
        Vessel relativVessel = new Vessel(mp, rv.getHeading(), rv.getSpeed());
        return new Lage(manoever, relativVessel);
    }

    public float getMaxCPA() {
        return maxCPA;
    }

    public double getPeilungRechtweisendCPA() {
        return peilungRechtweisendCPA;
    }

    public Punkt2D getRelPos() {
        return relPos;
    }

    public Vessel getRelativVessel() {
        return relativVessel;
    }

    public float getSpeedAbsolut() {
        return absolutVessel.getSpeed();
    }

    public float getSpeedRelativ() {
        return relativVessel.getSpeed();
    }

    public float getTimeToBCR() {
        return timeToBCR;
    }

    public float getTimeToCPA() {
        return timeToCPA;
    }
}
