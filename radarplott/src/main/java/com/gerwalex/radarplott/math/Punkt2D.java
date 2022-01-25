package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Die Klasse Point2D beschreibt einen Punkt in einem zwei-dimensionalen Koordinatensystem
 */
public class Punkt2D {
    public final float x;
    public final float y;

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
    public Punkt2D(float x, float y) {
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

    public Punkt2D add(Vektor2D v) {
        return new Punkt2D(x + v.x, y + v.y);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Punkt2D punkt2D = (Punkt2D) o;
        return Math.abs(punkt2D.x - x) * 1E4f < 1 && Math.abs(punkt2D.y - y) * 1E4f < 1;
    }

    /**
     * Liefert den Abstand eines uebergebenen Punktes zum Punkt zurueck
     *
     * @param p Punkt, zu dem der Abstand berechnet werden soll
     * @return Abstand der beiden Punkte
     */
    public final float getAbstand(Punkt2D p) {
        /*
         * Anwendung Satz des Phytagoras
         */
        float a = (p.x - x) * (p.x - x);
        float b = (p.y - y) * (p.y - y);
        return (float) Math.sqrt(a + b);
    }

    /**
     * Liefert den Mittelpunkt dieses und eines anderen Punkt2D.
     *
     * @param other ein anderer Punkt2D
     * @return Mittelpunkt.
     */
    public Punkt2D getMittelpunkt(@NonNull Punkt2D other) {
        return new Punkt2D((x + other.x) / 2, (y + other.y) / 2);
    }

    /**
     * Gibt einen Punkt zurueck, der in einem bestimmten Abstand (distanz) und einem bestimmten
     * Winkel (winkel) zum aktuellen Punkt liegt.
     *
     * @param winkel  Winkel in Radiant, zu dem sich der neuen Punkt zum aktuellen Punkt liegt
     * @param distanz Distanz des neuen Punktes zum aktuellen Punkt
     * @return neuer Punkt
     */
    public final Punkt2D getPunkt2D(float winkel, float distanz) {
        return new Punkt2D((float) (x + distanz * Math.sin(Math.toRadians(winkel))),
                (float) (y + distanz * Math.cos(Math.toRadians(winkel))));
    }

    /**
     * Ermittelt den Winkel zwischen dem Vektor zwischen dem Punkt2D (von) und dem übergebenen Punkt2D pkt (nach).
     *
     * @param pkt Punkt
     * @return Winkel zur Y-Achse
     */
    public float getYAxisAngle(Punkt2D pkt) {
        return new Vektor2D(this, pkt).getYAxisAngle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[x:%.4f | y:%.4f]", x, y);
    }
}
