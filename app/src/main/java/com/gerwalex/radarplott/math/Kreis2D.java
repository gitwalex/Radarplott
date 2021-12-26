package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Die Klasse Kreis2D bietet Funktionen zur Berechnung von Kreiswerten im 2-dimensionalen Raum
 *
 * @author Alexander Winkler
 */
public class Kreis2D {
    // Mittelpunkt und Radius des Kreise
    private final Punkt2D mittelpunkt;
    private final double radius;

    /**
     * Erstellt einen Einheitskreis mit dem Radius 1 um den Nullpunkt des Koordinatensystems
     */
    public Kreis2D() {
        /*
         * erstellt einen Einheitskreis um den Nullpunkt
         */
        this(new Punkt2D(0, 0), 1);
    }

    /**
     * Erstellt einen Kreis mit dem Mittelpunkt mittelpunkt und Radius radius
     *
     * @param mittelpunkt Mittelpunkt des Kreises
     * @param radius      Radius des Kreises
     */
    public Kreis2D(Punkt2D mittelpunkt, double radius) {
        this.mittelpunkt = new Punkt2D(mittelpunkt.x, mittelpunkt.y);
        this.radius = radius;
    }

    /**
     * Liefert den Mittelpunkt des Kreises
     *
     * @return Mittelpunkt
     */
    public final Punkt2D getMittelpunkt() {
        return mittelpunkt;
    }

    /**
     * Liefert den Radius des Kreises
     *
     * @return Radius
     */
    public final double getRadius() {
        return radius;
    }

    /**
     * Berechnet Schnittpunkt(e) zweier Kreise
     *
     * @param k Kreis, mit dem Schnittpunkte berechnet werden sollen
     * @return false, wenn es keinen Schnittpunkt gibt, ansonsten true
     */
    public final Punkt2D[] getSchnittpunkt(Kreis2D k) {
        double r1 = getRadius();
        double r2 = k.getRadius();
        double d = k.getMittelpunkt().abstand(getMittelpunkt());
        /*
         * ist der Abstand der beiden Punkte groesser als die Summe der Radien,
         * gibt es keinen Schnittpunkt.
         */
        if (d > r1 + r2) {
            return null;
        }
        /*
         * ist der Abstand der beiden Punkte geringer oder gleich der Differenz
         * der beiden Radien, liegt der eine Kreis vollstaendig im/aufanderen
         * und es gibt auch hier keinen Schnittpunkt
         */
        if (d <= Math.abs(r1 - r2)) {
            return null;
        }
        /*
         * Schnittpunkte werden berechnet, indem die beiden Kreisgleichungen
         * voneinander abgezogen werden
         */
        // Mittelpunkte merken
        double p1 = getMittelpunkt().x;
        double p2 = getMittelpunkt().y;
        double m1 = k.getMittelpunkt().x;
        double m2 = k.getMittelpunkt().y;
        // Loesung von hier: http://www.c-plusplus.de/forum/201202-full
        double dx = m1 - p1;
        double dy = m2 - p2;
        double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
        double h = (double) Math.sqrt(r1 * r1 - a * a);
        double sp1x = p1 + (a / d) * dx - (h / d) * dy; // Schnittpunkt 1
        double sp1y = p2 + (a / d) * dy + (h / d) * dx;
        double sp2x = p1 + (a / d) * dx + (h / d) * dy; // Schnittpinkt 2
        double sp2y = p2 + (a / d) * dy - (h / d) * dx;
        Punkt2D[] e = new Punkt2D[2];
        e[0] = new Punkt2D(sp1x, sp1y);
        e[1] = new Punkt2D(sp2x, sp2y);
        return e;
    }

    /**
     * Liefert die Beruehrpunkte zweier Tangenten an den {@link Kreis2D} zuruek, die durch einen
     * Bezugspunkt (bezugspunkt) gehen
     *
     * @param bezugspunkt
     * @return Array mit den Beruehrpunkten. Ist null, wenn der Bezugspunkt im Kreis liegt
     */
    public final Punkt2D[] getTangente(Punkt2D bezugspunkt) {
        double a, b, c, a1, a2, a3;
        Punkt2D[] p = new Punkt2D[2];
        /*
         * liefert die Beruehrpunkte zweier Tangenten an den Kreis zuruek, die
         * durch einen Bezugspunkt (bezugspunkt) gehen
         */
        /*
         * siehe auch
         * http://www.delphi-forum.de/topic_kreis+tangente_12873,0.html
         */
        // Ist der Abstand des Bezugspunktes zum Mittelpunkt kleiner als der
        // Radius, liegt der Bezugspunkt in oder auf dem Kreis. Dann gibt es
        // keine Tangente
        if (mittelpunkt.abstand(bezugspunkt) <= radius) {
            return p;
        }
        a = radius;
        c = (double) Math.hypot(mittelpunkt.x - bezugspunkt.x, mittelpunkt.y - bezugspunkt.y);
        b = (double) Math.sqrt(Math.pow(c, 2) - Math.pow(a, 2));
        a1 = (double) Math.atan2(bezugspunkt.x - mittelpunkt.x, bezugspunkt.y - mittelpunkt.y);
        a2 = (double) Math.asin(b / c);
        a3 = a1 - a2;
        p[0] = new Punkt2D((double) ((Math.sin(a3) * a) + mittelpunkt.x),
                (double) ((Math.cos(a3) * a) + mittelpunkt.y));
        a3 = a1 + a2;
        p[1] = new Punkt2D((double) ((Math.sin(a3) * a) + mittelpunkt.x),
                (double) ((Math.cos(a3) * a) + mittelpunkt.y));
        return p;
    }

    /**
     * Pruefung, ob ein Punkt auf dem Kreis liegt.
     *
     * @param p Punkt, der auf dem Kreis liegen soll
     * @return true: Punkt liegt auf dem Kreis
     */
    public final boolean isAufKreis(Punkt2D p) {
        double s = Math.sqrt(
                ((p.x - mittelpunkt.x) * (p.x - mittelpunkt.x)) + ((p.y - mittelpunkt.y) * (p.y - mittelpunkt.y)));
        double d = s - radius;
        return Math.round(d * 1E6) == 0;
    }

    /**
     * Prueft, ob ein Punkt im Kreis liegt
     *
     * @param p Punkt
     * @return true: Punkt liegt innerhalb des Kreises, false: Punkt liegt auf oder ausserhalb des
     * Kreises
     */
    public final boolean liegtImKreis(Punkt2D p) {
        return (p.abstand(mittelpunkt) < radius);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Kreis: Mittelpunkt: %1s, Radius %.4f", mittelpunkt.toString(),
                Math.round(radius * 1E6) / 1E6);
    }
}
