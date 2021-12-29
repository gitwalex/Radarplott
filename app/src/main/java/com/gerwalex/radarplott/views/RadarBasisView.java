package com.gerwalex.radarplott.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.ColorRes;
import androidx.databinding.Observable;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.radar.Vessel;

import java.util.ArrayList;
import java.util.List;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class RadarBasisView extends FrameLayout {
    public final static int RADARRINGE = 8;
    @ColorRes
    private final int[] colors;
    // Variablen zum Zeichnen
    private final Paint paint = new Paint();
    private final float sektorlinienlaenge = 40.0f;
    private final List<VesselView> vesselList = new ArrayList<>();
    private int colorRadarBackground;
    private Vessel mEigenesSchiff;
    private ScaleGestureDetector mScaleDetector;
    private boolean northupOrientierung = true;
    private float scale;
    private int sm;

    public RadarBasisView(Context context) {
        this(context, null);
    }

    public RadarBasisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarBasisView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.colorRadarLinien));
        colorRadarBackground = getResources().getColor(R.color.white);
        setWillNotDraw(false);
        colors = getResources().getIntArray(R.array.vesselcolors);
    }

    public void addVessel(Vessel vessel) {
        if (vesselList.size() == 0) {
            mEigenesSchiff = vessel;
        }
        VesselView vesselView = new VesselView(vessel, colors[vesselList.size()]);
        vesselList.add(vesselView);
        vessel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                invalidate();
            }
        });
    }

    public Kreis2D getRadarAussenkreis() {
        return new Kreis2D(new Punkt2D(), RADARRINGE);
    }

    public int getSMSizeInPixel() {
        return sm;
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        if (result < desiredSize) {
            Log.d("RadarView", "The view is too small, the content might get cut");
        }
        return result;
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
        if (!northupOrientierung && mEigenesSchiff != null) {
            canvas.rotate(mEigenesSchiff.getHeading());
        }
        for (int i = 0; i < RADARRINGE; i++) {
            // zeichne Radarringe
            canvas.drawCircle(0, 0, sm * (i + 1), paint);
        }
        int innerScale = sm * RADARRINGE;
        for (int winkel = 0; winkel < 180; winkel++) {
            // Festlegen Laenge der sektorlienien auf dem Aussenkreis
            // Alle 2 Grad: halbe sektorlinienlaenge
            // Alle 10 Grad: sektorlinienlaenge
            // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
            // Berechnen der Linien - Abstand 2 Grad
            float startsektorlinie2grad = innerScale - sektorlinienlaenge / 3;
            float endsektorlinie2grad = innerScale + sektorlinienlaenge / 3;
            float startsektorlinie5grad = innerScale - sektorlinienlaenge / 2;
            float endsektorlinie5grad = innerScale + sektorlinienlaenge / 2;
            if (winkel % 10 == 0) {
                canvas.drawLine(0, -innerScale - sektorlinienlaenge, 0, innerScale + sektorlinienlaenge, paint);
            } else {
                if (winkel % 5 == 0) {
                    canvas.drawLine(0, startsektorlinie5grad, 0, endsektorlinie5grad, paint);
                    canvas.drawLine(0, -startsektorlinie5grad, 0, -endsektorlinie5grad, paint);
                } else {
                    canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, paint);
                    canvas.drawLine(0, -startsektorlinie2grad, 0, -endsektorlinie2grad, paint);
                }
            }
            canvas.rotate(1);
        }
        for (int i = 0; i < vesselList.size(); i++) {
            canvas.save();
            VesselView v = vesselList.get(i);
            v.onDraw(canvas, this);
            canvas.restore();
        }
        canvas.restore();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("Chart onMeasure w", MeasureSpec.toString(widthMeasureSpec));
        Log.v("Chart onMeasure h", MeasureSpec.toString(heightMeasureSpec));
        int textSize = (int) paint.measureText(" 000 ");
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight() - textSize;
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom() - textSize;
        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) / 2.0f;
        sm = (int) (scale / RADARRINGE);
        Log.d("gerwalex", "1sm Pixel: " + sm);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            Punkt2D pkt = new Punkt2D(event.getX(), event.getY());
            if (mEigenesSchiff != null && mEigenesSchiff.isPunktAufKurslinie(pkt)) {
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
            Log.d("gerwalex", "scale: " + detector.getScaleFactor());
            scale *= detector.getScaleFactor();
            scale = Math.max(Math.min(getWidth() / 2, getHeight() / 2), scale);
            invalidate();
            return true;
        }
    }
}
