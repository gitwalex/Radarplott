package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gerwalex.radarplott.math.Gerade2D;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.radar.Vessel;

/**
 * Die Klasse EigenesSchiff bietet Funktionen zum ermitteln von bestimmten Daten der Bewegung eines
 * Schiffes.
 *
 * @author Alexander Winkler
 */
public class VesselView {
    private static final int thinPath = 2;
    protected final Path courseline = new Path();
    protected final Paint courslineStyle = new Paint();
    private final Vessel vessel;

    public VesselView(Vessel vessel, int color) {
        this.vessel = vessel;
        courslineStyle.setStrokeWidth(thinPath);
        courslineStyle.setAntiAlias(true);
        courslineStyle.setStyle(Paint.Style.STROKE);
        courslineStyle.setColor(color);
    }

    public void drawVessel(@NonNull Canvas canvas, @NonNull RadarBasisView radar) {
        Kreis2D aussenkreis = radar.getRadarAussenkreis();
        Punkt2D dest = getEndOfKurslinie(vessel.getKurslinie(), aussenkreis);
        if (dest == null) {
            // Schiff ist ausserhalb des Radars
            return;
        }
        Punkt2D endPos = vessel.getAktPosition();
        courseline.reset();
        radar.drawLine(courseline, endPos, dest);
        radar.addCircle(courseline, endPos);
        canvas.drawPath(courseline, courslineStyle);
    }

    /**
     * Hilfsmethode zum berechnen des Endpunktes einer Kurslinie auuf einem Radarring
     *
     * @param kurslinie Kurslinie
     * @param kreis     Radarring
     * @return endpunkt, null, wenn der Schnittpunkt nicht in Fahrtrichtung liegt (Dann ist Schiff ausserhalb
     * Raadarbild)
     */
    @Nullable
    protected Punkt2D getEndOfKurslinie(Gerade2D kurslinie, Kreis2D kreis) {
        Punkt2D[] sc = kurslinie.getSchnittpunkt(kreis);
        if (sc != null) {
            return vessel.isPunktInFahrtrichtung(sc[0]) ? sc[0] : sc[1];
        }
        return null;
    }

    public Vessel getVessel() {
        return vessel;
    }

    @Override
    public String toString() {
        return "VesselView{" + "vessel=" + vessel + '}';
    }
}