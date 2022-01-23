package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;

public class Kurslinie {

    private final Punkt2D first;
    private final float heading;
    private final float speed;
    private Gerade2D line;

    public Kurslinie(Punkt2D first, Punkt2D second) {
        this(first, new Vektor2D(first, second));
    }

    public Kurslinie(float heading, float speed) {
        this(new Punkt2D(), heading, speed);
    }

    public Kurslinie(Punkt2D first, float heading, float speed) {
        this(first, heading, speed, 6);
    }

    public Kurslinie(Punkt2D first, float heading, float speed, float distance) {
        this(first, new Punkt2D(first.getPunkt2D(heading, distance)));
    }

    public Kurslinie(Punkt2D first, Vektor2D richtungsvektor) {
        if (!richtungsvektor.equals(new Vektor2D())) {
            line = new Gerade2D(first, richtungsvektor);
        }
        this.first = first;
        heading = line.getYAxisAngle();
        this.speed = speed;
    }

    public Punkt2D getCPA(@NonNull Punkt2D p) {
        return line.getLotpunkt(p);
    }

    public Vektor2D getRichtungsvektor() {
        return line.getRichtungsvektor();
    }

    public Vektor2D getRichtungsvektor(float v) {
        return line.getRichtungsvektor(v);
    }

    public Punkt2D[] getSchnittpunkt(@NonNull Kreis2D k) {
        return line.getSchnittpunkt(k);
    }

    public Punkt2D getSchnittpunkt(Kurslinie kurslinie) {
        return line.getSchnittpunkt(kurslinie.line);
    }

    public float getYAxisAngle() {
        return line == null ? 0 : line.getYAxisAngle();
    }

    public boolean isPunktAufGerade(@NonNull Punkt2D p) {
        return line == null ? p.equals(first) : line.isPunktAufGerade(p);
    }

    /**
     * Prueft, ob ein Punkt auf der Kurslinie liegt. Toleranz ist 1E6f.
     *
     * @param p Punkt
     * @return true, wenn der Punkt auf der Kurslinie liegt, ansonsten false.
     */
    public final boolean isPunktAufKurslinie(@NonNull Punkt2D p) {
        return Math.round(line.getAbstand(p.x, p.y) * 1E4f) < 1;
    }
}
