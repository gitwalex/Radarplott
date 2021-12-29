package com.gerwalex.radarplott.math;
/**
 * @author alex
 */

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Die Klasse Vektor2D beschreibt einen Vektor in einem zweidimensionalen Koordinatensystem. Ein
 * Vektor hat eine Laenge, einen Zielpunkt sowie eine Richtung. Der Startpunkt liegt immer im
 * Nullpunkt.
 */
public class Vektor2D {
    /**
     *
     */
    private final Punkt2D endpunkt;

    /**
     * Erstellt einen Vektor mit Ursprung im Nullpunkt, Zielpunkt ist der Uebergebene Point2D
     *
     * @param p Zielpunkt des Vektors
     */
    public Vektor2D(Punkt2D p) {
        endpunkt = p;
    }

    /**
     * Erstellt einen Nullvektor, Start- und Zielpunkt liegen im Nullpunkt des Koordinatensystems
     */
    public Vektor2D() {
        this(new Punkt2D(0, 0));
    }

    /**
     * Erstellt einen neuen Vektor anhand zweier Punkte.
     *
     * @param von  Startpunkt
     * @param nach Endpunkt
     */
    public Vektor2D(Punkt2D von, Punkt2D nach) {
        /*
         * Erstellt einen neuen Vektor2D durch Subtraktion
         */
        this(new Punkt2D(nach.x - von.x, nach.y - von.y));
    }

    /**
     * Addiert einen Vektor
     *
     * @param v Vektor, zu dem dieser Vektor addiert werden soll
     * @return Das Ergebnis der Addition
     */
    public final Vektor2D add(Vektor2D v) {
        return new Vektor2D(new Punkt2D(endpunkt.x + v.getEndpunkt().x, endpunkt.y + v.getEndpunkt().y));
    }

    @Override
    public boolean equals(Object objekt) {
        if (objekt == this) {
            return true;
        }
        if (!(objekt instanceof Vektor2D)) {
            return false;
        }
        Punkt2D p1 = getEinheitsvektor().getEndpunkt();
        Vektor2D v = (Vektor2D) objekt;
        Punkt2D p2 = v.getEinheitsvektor().getEndpunkt();
        return (p1.equals(p2));
    }

    /**
     * Erstellt einen neuen Vektor mit gleicher Richtung, aber der Laenge 1 (Einheitsvektor)
     *
     * @return Vektor mit der Laenge eins und der gleichen Richtung
     */
    public final Vektor2D getEinheitsvektor() {
        float l = (float) length();
        return new Vektor2D(new Punkt2D(endpunkt.x / l, endpunkt.y / l));
    }

    /**
     * Endpunkt des Vektors
     *
     * @return Endpunkt des Vektors
     */
    public final Punkt2D getEndpunkt() {
        return endpunkt;
    }

    /**
     * Berechnet das Skalarprodukt zweier Vektoren
     *
     * @param v Vektor, mit dem das Skalarprodukt berechnet wird
     * @return das Skalarprodukt
     */
    public final float getSkalar(Vektor2D v) {
        return v.getEndpunkt().x * this.getEndpunkt().x + v.getEndpunkt().y * this.getEndpunkt().y;
    }

    /**
     * Liefert den Winkel zwischen zwei Vektoren zurueck
     *
     * @param v Vektor, der zur Winkelberechnung benutzt werden soll
     * @return float Winkel in Degrees
     */
    public final float getWinkel(Vektor2D v) {
        return (float) Math.toDegrees(Math.acos(getSkalar(v) / (length() * v.length())));
    }

    /**
     * Ermittelt den Winkel zwischen der Y-Achse und dem Vektor in Radiant.
     *
     * @return Winkel zwischen Vektor und Y-Achse in 360-Grad-Darstellung
     */
    public final float getYAxisAngle() {
        // Achtung - Besonderheit atan2: Koordinaten muessen vertauscht
        // angegeben werden
        return (float) ((450 - Math.toDegrees(Math.atan2(endpunkt.y, endpunkt.x))) % 360);
    }

    /**
     * Laenge des Vektors
     *
     * @return laenge des Vektors
     */
    public final float length() {
        /*
         * liefert Laenge des Vektors zurueck (Satz des Phytagoras)
         */
        return (float) Math.hypot(endpunkt.x, endpunkt.y);
    }

    /**
     * Dreht die Richtung eines Vektors um 180 Grad
     *
     * @return ein neuer Vektor mit umgedrehter Richtung.
     */
    public final Vektor2D negate() {
        /*
         * Richtung des Vektoren umdrehen
         */
        return new Vektor2D(new Punkt2D(-endpunkt.x, -endpunkt.y));
    }

    /**
     * Subtrahiert einen Vektor
     *
     * @param v Vektor , der vom aktuellen Vektor subtrahiert werden soll
     * @return Das Ergebnis der Subtraktion
     */
    public final Vektor2D subtract(Vektor2D v) {
        return new Vektor2D(new Punkt2D(endpunkt.x - v.getEndpunkt().x, endpunkt.y - v.getEndpunkt().y));
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Vektor2D: %1s mit Laenge %.4f", endpunkt.toString(), length());
    }
}
