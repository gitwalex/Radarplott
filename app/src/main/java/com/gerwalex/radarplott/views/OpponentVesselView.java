package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.gerwalex.radarplott.math.Kreis2D;
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
    private static final int thickPath = 5;
    private final Paint ownVesselPaint = new Paint();
    private final Path ownVesselPath = new Path();
    private final OpponentVessel vessel;
    private final Paint vesselPaint = new Paint();
    private final Path vesselPath = new Path();

    public OpponentVesselView(OpponentVessel vessel, int color) {
        super(vessel, color);
        this.vessel = vessel;
        vesselPaint.setAntiAlias(true);
        vesselPaint.setStyle(Paint.Style.STROKE);
        vesselPaint.setColor(color);
        vesselPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        vesselPaint.setStrokeWidth(thickPath);
        //
        ownVesselPaint.setAntiAlias(true);
        ownVesselPaint.setStyle(Paint.Style.STROKE);
        ownVesselPaint.setStrokeWidth(thickPath);
    }

    @Override
    public void drawVessel(Canvas canvas, RadarBasisView radar) {
        super.drawVessel(canvas, radar);
        Kreis2D aussenkreis = radar.getRadarAussenkreis();
        Punkt2D dest = getEndOfKurslinie(vessel.getKurslinieRelativ(), aussenkreis);
        if (dest != null) {
            Punkt2D relPos = vessel.getRelPosition(); // null, wenn eigenes Schiff
            Punkt2D startPos = vessel.getStartPosition();
            Punkt2D aktPos = vessel.getAktPosition();
            vesselPath.reset();
            radar.drawLine(vesselPath, aktPos, startPos);
            radar.drawLine(vesselPath, startPos, relPos);
            radar.drawLine(vesselPath, relPos, dest);
            radar.addCircle(vesselPath, startPos);
            radar.addSymbol(RadarBasisView.Symbols.ownMove, relPos.getMittelpunkt(startPos));
            radar.addSymbol(RadarBasisView.Symbols.realMove, relPos.getMittelpunkt(aktPos));
            radar.addSymbol(RadarBasisView.Symbols.relativeMove, startPos.getMittelpunkt(aktPos));
            canvas.drawPath(vesselPath, vesselPaint);
            //
            Vessel me = radar.getEigenesSchiff();
            ownVesselPath.reset();
            radar.drawLine(ownVesselPath, me.getAktPosition(), vessel.getCPA(me));
            ownVesselPaint.setColor(radar.getEigenesSchiffColor());
            canvas.drawPath(ownVesselPath, ownVesselPaint);
            radar.drawEndText(canvas, vessel.getCPA(me), "CPA");
        }
    }

    public OpponentVessel getVessel() {
        return vessel;
    }
}