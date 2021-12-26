package com.gerwalex.radarplott.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.radar.Vessel;

/**
 * Die Klasse EigenesSchiff bietet Funktionen zum ermitteln von bestimmten Daten der Bewegung eines
 * Schiffes.
 *
 * @author Alexander Winkler
 */
public class VesselView {
    private final Path destpath = new Path();
    /* Startposition, aktuelle Position */
    private final Paint dottedLine;
    private final Paint normalLine = new Paint();
    private final Path positionPath = new Path();
    private final Path startpath = new Path();
    private Punkt2D endPos;
    private Punkt2D startPos;
    private Vessel vessel;

    public VesselView(Context context, Vessel vessel) {
        this.vessel = vessel;
        startPos = vessel.getFirstPosition();
        endPos = vessel.getSecondPosition();
        Resources res = context.getResources();
        int zeichnelagefett = 5;
        int zeichnelagenormal = 3;
        normalLine.setStrokeWidth(zeichnelagenormal);
        normalLine.setAntiAlias(true);
        normalLine.setStyle(Paint.Style.STROKE);
        normalLine.setColor(res.getColor(R.color.green));
        normalLine.setStrokeWidth(zeichnelagefett);
        dottedLine = new Paint(normalLine);
        dottedLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
    }

    public void onDraw(Canvas canvas) {
        float scale = ViewRadarBasisBild.scale;
        canvas.save();
        startpath.reset();
        destpath.reset();
        startpath.moveTo(0, 0);
        startpath.lineTo((float) -startPos.x * scale, (float) startPos.y * scale);
        destpath.moveTo(0, 0);
        destpath.lineTo((float) -endPos.x * scale, (float) endPos.y * scale);
        canvas.drawPath(startpath, dottedLine);
        canvas.drawPath(destpath, normalLine);
        positionPath.reset();
        positionPath.addCircle(0, 0, 40f, Path.Direction.CW);
        canvas.drawPath(positionPath, normalLine);
        canvas.restore();
    }
}