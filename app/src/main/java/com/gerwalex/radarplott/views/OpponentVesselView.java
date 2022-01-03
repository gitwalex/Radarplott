package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;

import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.radar.OpponentVessel;
import com.gerwalex.radarplott.radar.Vessel;

/**
 * Die Klasse EigenesSchiff bietet Funktionen zum ermitteln von bestimmten Daten der Bewegung eines
 * Schiffes.
 *
 * @author Alexander Winkler
 */
public class OpponentVesselView extends VesselView {
    private static final int thickPath = 3;
    private final OpponentVessel vessel;
    private final Path vesselPosition = new Path();
    private final Paint vesselPositionStyle = new Paint();

    public OpponentVesselView(OpponentVessel vessel, int color) {
        super(vessel, color);
        this.vessel = vessel;
        vesselPositionStyle.setAntiAlias(true);
        vesselPositionStyle.setStyle(Paint.Style.STROKE);
        vesselPositionStyle.setColor(color);
        vesselPositionStyle.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        vesselPositionStyle.setStrokeWidth(thickPath);
        //
    }

    @Override
    public void drawVessel(@NonNull Canvas canvas, @NonNull RadarBasisView radar) {
        super.drawVessel(canvas, radar);
        Vessel me = radar.getEigenesSchiff();
        if (me != null) {
            vessel.calculateRelativeValues(me);
            radar.drawLine(courseline, me.getAktPosition(), vessel.getCPA(me));
            radar.drawEndText(canvas, vessel.getCPA(me), "CPA");
        }
        Punkt2D relPos = vessel.getRelPosition();
        Punkt2D startPos = vessel.getStartPosition();
        Punkt2D aktPos = vessel.getAktPosition();
        vesselPosition.reset();
        radar.drawLine(vesselPosition, aktPos, startPos);
        radar.drawLine(vesselPosition, startPos, relPos);
        radar.drawLine(vesselPosition, relPos, aktPos);
        radar.addCircle(courseline, startPos);
        canvas.drawPath(vesselPosition, vesselPositionStyle);
        //
        canvas.drawPath(courseline, courslineStyle);
    }

    public OpponentVessel getVessel() {
        return vessel;
    }
}