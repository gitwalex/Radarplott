package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

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
    private final Paint coursline = new Paint();
    private final Path futureCourseline = new Path();
    private final Vessel vessel;

    public VesselView(Vessel vessel, int color) {
        this.vessel = vessel;
        coursline.setStrokeWidth(thinPath);
        coursline.setAntiAlias(true);
        coursline.setStyle(Paint.Style.STROKE);
        coursline.setColor(color);
    }

    public void drawVessel(Canvas canvas, RadarBasisView radar) {
        Kreis2D aussenkreis = radar.getRadarAussenkreis();
        Punkt2D dest = getEndOfKurslinie(vessel.getKurslinie(), aussenkreis);
        if (dest == null) {
            // Schiff ist ausserhalb des Radars
            return;
        }
        Punkt2D endPos = vessel.getAktPosition();
        futureCourseline.reset();
        radar.drawLine(futureCourseline, endPos, dest);
        radar.addCircle(futureCourseline, endPos);
        canvas.drawPath(futureCourseline, coursline);
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