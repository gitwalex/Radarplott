package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Die Klasse Point2D beschreibt einen Punkt in einem zwei-dimensionalen Koordinatensystem
 */
public class Punkt2D {
    public final double x;
    public final double y;

    /**
     * Erstellt einen Punkt im Nullpunkt eines 2D-Koordinatensystems
     */
    public Punkt2D() {
        /*
         * Nullpunkt
         */
        this(0, 0);
    }

    /**
     * Konstruktor, der einen Punkt in einem Koordinatensystem erstellt.
     *
     * @param x Punkt auf der X-Achse
     * @param y Punkt auf der Y-Achse
     */
    public Punkt2D(double x, double y) {
        /*
         * Legt einen Punkt mit den Koordinaten (x , y) an
         */
        this.x = x;
        this.y = y;
    }

    /**
     * Konstruktor, der aus einem Punkt einen Punkt in einem Koordinatensystem erstellt.
     *
     * @param p Point2D
     */
    public Punkt2D(Punkt2D p) {
        /*
         * Legt einen Punkt mit den Koordinaten (x , y) an
         */
        this(p.x, p.y);
    }

    /**
     * Liefert den Abstand eines uebergebenen Punktes zum Punkt zurueck
     *
     * @param p Punkt, zu dem der Abstand berechnet werden soll
     * @return Abstand der beiden Punkte
     */
    public final double abstand(Punkt2D p) {
        /*
         * Anwendung Satz des Phytagoras
         */
        double a = (p.x - x) * (p.x - x);
        double b = (p.y - y) * (p.y - y);
        return (double) Math.sqrt(a + b);
    }

    /**
     * addiert einen Punkt2D
     *
     * @param p Punkt2d
     * @return Punkt2D
     */
    public Punkt2D add(Punkt2D p) {
        return new Punkt2D(x + p.x, y + p.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Punkt2D other = (Punkt2D) obj;
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x) &&
                Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
    }

    /**
     * Gibt einen Punkt zurueck, der in einem bestimmten Abstand (distanz) und einem bestimmten
     * Winkel (winkel) zum aktuellen Punkt liegt.
     *
     * @param winkel  Winkel in Radiant, zu dem sich der neuen Punkt zum aktuellen Punkt liegt
     * @param distanz Distanz des neuen Punktes zum aktuellen Punkt
     * @return neuer Punkt
     */
    public final Punkt2D getPunkt2D(double winkel, double distanz) {
        return new Punkt2D((double) (x + distanz * Math.sin(Math.toRadians(winkel))),
                (double) (y + distanz * Math.cos(Math.toRadians(winkel))));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[x:%.4f | y:%.4f]", x, y);
    }
}
