package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

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
        Punkt2D dest = radar.getEndOfKurslinie(vessel);
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

    public Vessel getVessel() {
        return vessel;
    }

    @Override
    public String toString() {
        return "VesselView{" + "vessel=" + vessel + '}';
    }
}