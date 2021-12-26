package com.gerwalex.radarplott.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.radar.Vessel;

import java.util.ArrayList;
import java.util.List;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class ViewRadarBasisBild extends FrameLayout {
    public final static int RADARRINGE = 9;
    public static float scale;
    // Variablen zum Zeichnen
    private final Paint paint = new Paint();
    private final float sektorlinienlaenge = 40.0f;
    private final List<VesselView> vesselList = new ArrayList<>();
    private int colorRadarBackground;
    private Vessel mEigenesSchiff;
    private float mHeadUpRotation;
    private ScaleGestureDetector mScaleDetector;
    private boolean northupOrientierung = true;

    public ViewRadarBasisBild(Context context) {
        this(context, null);
    }

    public ViewRadarBasisBild(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewRadarBasisBild(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.colorRadarLinien));
        colorRadarBackground = getResources().getColor(R.color.white);
        setWillNotDraw(false);
    }

    public void addVessel(Vessel vessel) {
        VesselView view = new VesselView(getContext(), vessel);
        vesselList.add(view);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        canvas.translate(width / 2, height / 2);
        // Hintergrundfarbe setzen
        canvas.drawColor(colorRadarBackground);
        // Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
        if (!northupOrientierung) {
            canvas.rotate(mHeadUpRotation);
        }
        for (int i = 1; i <= RADARRINGE; i++) {
            float innerScale = scale * i / RADARRINGE;
            canvas.drawCircle(0, 0, innerScale, paint);
        }
        for (int winkel = 0; winkel < 180; winkel += 2) {
            for (int i = 1; i <= RADARRINGE; i++) {
                float innerScale = scale * i / RADARRINGE;
                float startsektorlinie2grad = innerScale - sektorlinienlaenge / 2;
                float endsektorlinie2grad = innerScale + sektorlinienlaenge / 2;
                float startsektorlinie10grad = innerScale - sektorlinienlaenge;
                float endsektorlinie10grad = innerScale + sektorlinienlaenge;
                // Festlegen Laenge der sektorlienien auf dem Aussenkreis
                // Alle 2 Grad: halbe sektorlinienlaenge
                // Alle 10 Grad: sektorlinienlaenge
                // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
                // Berechnen der Linien - Abstand 2 Grad
                if (winkel % 30 == 0) {
                    canvas.drawLine(0, -innerScale, 0, innerScale, paint);
                } else {
                    if (winkel % 10 == 0) {
                        canvas.drawLine(0, startsektorlinie10grad, 0, endsektorlinie10grad, paint);
                        canvas.drawLine(0, -startsektorlinie10grad, 0, -endsektorlinie10grad, paint);
                    } else {
                        canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, paint);
                        canvas.drawLine(0, -startsektorlinie2grad, 0, -endsektorlinie2grad, paint);
                    }
                }
            }
            canvas.rotate(2);
        }
        for (VesselView v : vesselList) {
            v.onDraw(canvas);
        }
        canvas.restore();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) * 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mEigenesSchiff != null) {
            }
        }
        return mScaleDetector.onTouchEvent(event);
    }

    public boolean toggleNorthUpOrientierung() {
        northupOrientierung = !northupOrientierung;
        invalidate();
        return northupOrientierung;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(Math.min(getWidth() / 2, getHeight() / 2), scale);
            invalidate();
            return true;
        }
    }
}
