package com.gerwalex.radarplott.math;

import androidx.annotation.NonNull;

import com.gerwalex.lib.math.Gerade2D;
import com.gerwalex.lib.math.Kreis2D;
import com.gerwalex.lib.math.Punkt2D;
import com.gerwalex.lib.math.Vektor2D;

public class Kurslinie {

    private final Gerade2D line;

    public Kurslinie(Punkt2D first, Punkt2D second) {
        line = new Gerade2D(first, new Vektor2D(first, second));
    }

    public Kurslinie(Punkt2D first, float heading) {
        line = new Gerade2D(first, new Punkt2D(first.getPunkt2D(heading, 6)));
    }

    public Punkt2D getCPA(@NonNull Punkt2D p) {
        return line.getLotpunkt(p);
    }

    public float getHeading() {
        return line.getYAxisAngle();
    }

    public Punkt2D getPosition(float minutes) {
        return line.getVon().add(line.getRichtungsvektor(minutes));
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

    public boolean isPunktAufGerade(@NonNull Punkt2D p) {
        return line.isPunktAufGerade(p);
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

    @NonNull
    @Override
    public String toString() {
        return "Kurslinie{" + line + '}';
    }
}
