package com.gerwalex.radarplott.views;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
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
    private static final int thickPath = 5;
    private static final int thinPath = 2;
    private final Paint absolutKursLine = new Paint();
    private final Path absolutPath = new Path();
    private final float markerRadius = 20f;
    private final Paint relativKursLine;
    private final Path relativPath = new Path();
    private final Punkt2D startPos, endPos, relPos;
    private final Path startpath = new Path();
    private final Paint textPaint = new Paint();
    private final Vessel vessel;

    public VesselView(Vessel vessel, int color) {
        this.vessel = vessel;
        startPos = vessel.getFirstPosition();
        endPos = vessel.getSecondPosition();
        relPos = vessel.getRelPosition(); // null, wenn eigenes Schiff
        absolutKursLine.setStrokeWidth(relPos == null ? thickPath : thinPath);
        absolutKursLine.setAntiAlias(true);
        absolutKursLine.setStyle(Paint.Style.STROKE);
        absolutKursLine.setColor(color);
        relativKursLine = new Paint(absolutKursLine);
        relativKursLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        relativKursLine.setStrokeWidth(thickPath);
        textPaint.setColor(color);
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
    private Punkt2D getEndOfKurslinie(Gerade2D kurslinie, Kreis2D kreis) {
        Punkt2D[] sc = kurslinie.getSchnittpunkt(kreis);
        if (sc != null) {
            return vessel.isPunktInFahrtrichtung(sc[0]) ? sc[0] : sc[1];
        }
        return null;
    }

    public void onDraw(Canvas canvas, RadarBasisView radar) {
        float sm = radar.getSMSizeInPixel();
        Kreis2D aussenkreis = radar.getRadarAussenkreis();
        Punkt2D dest = getEndOfKurslinie(vessel.getKurslinieRelativ(), aussenkreis);
        if (dest == null) {
            // Schiff ist ausserhalb des Radars
            return;
        }
        relativPath.reset();
        relativPath.moveTo(-endPos.x * sm, endPos.y * sm);
        relativPath.lineTo(-dest.x * sm, dest.y * sm);
        relativPath.addCircle(-endPos.x * sm, endPos.y * sm, markerRadius, Path.Direction.CW);
        if (relPos != null) {
            // Gegner
            startpath.reset();
            startpath.moveTo(-endPos.x * sm, endPos.y * sm);
            startpath.lineTo(-startPos.x * sm, startPos.y * sm);
            startpath.lineTo(-relPos.x * sm, relPos.y * sm);
            relativPath.addCircle(-startPos.x * sm, startPos.y * sm, markerRadius, Path.Direction.CW);
            Symbols.ownMove.addSymbol(relativPath, relPos, startPos, sm);
            Symbols.realMove.addSymbol(relativPath, relPos, endPos, sm);
            Symbols.relativeMove.addSymbol(relativPath, startPos, endPos, sm);
            dest = getEndOfKurslinie(vessel.getKurslinieAbsolut(), aussenkreis);
            if (dest != null) {
                absolutPath.moveTo(-relPos.x * sm, relPos.y * sm);
                absolutPath.lineTo(-dest.x * sm, dest.y * sm);
                canvas.drawPath(absolutPath, relativKursLine);
            }
            canvas.drawPath(startpath, relativKursLine);
        }
        canvas.drawPath(relativPath, absolutKursLine);
    }

    public enum Symbols {
        ownMove {
            @Override
            public void addSymbol(Path path, Punkt2D from, Punkt2D to, float sm) {
                Punkt2D mp = from.getMittelpunkt(to);
                path.addCircle(-mp.x * sm, mp.y * sm, radius, Path.Direction.CW);
            }
        }, realMove {
            @Override
            public void addSymbol(Path path, Punkt2D from, Punkt2D to, float sm) {
                Punkt2D mp = from.getMittelpunkt(to);
                path.addCircle(-mp.x * sm, mp.y * sm, radius, Path.Direction.CW);
            }
        }, relativeMove {
            @Override
            public void addSymbol(Path path, Punkt2D from, Punkt2D to, float sm) {
                Punkt2D mp = from.getMittelpunkt(to);
                path.addCircle(-mp.x * sm, mp.y * sm, radius, Path.Direction.CW);
            }
        };
        private static final int radius = 15;

        public abstract void addSymbol(Path path, Punkt2D from, Punkt2D to, float sm);
    }
}