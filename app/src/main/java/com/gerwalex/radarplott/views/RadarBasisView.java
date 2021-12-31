package com.gerwalex.radarplott.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorRes;
import androidx.databinding.Observable;

import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.main.MainModel;
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
    private final Path ringPath = new Path();
    private final float sektorlinienlaenge = 40.0f;
    private final float textWidth;
    private final List<VesselView> vesselList = new ArrayList<>();
    Observable.OnPropertyChangedCallback ownVesselObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            invalidate();
        }
    };
    private Bitmap bm;
    private View canvasView;
    private Vessel mEigenesSchiff;
    private MainModel mModel;
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
        setWillNotDraw(false);
        colors = getResources().getIntArray(R.array.vesselcolors);
        paint.setTextSize(getPx(10));
        textWidth = paint.measureText("0000");
    }

    public void addVessel(Vessel vessel) {
        VesselView vesselView = new VesselView(vessel, colors[vesselList.size()]);
        vesselList.add(vesselView);
    }

    private void createRadarBitmap2(int w, int h) {
        Kreis2D outerRing = new Kreis2D(new Punkt2D(), sm * RADARRINGE);
        bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.translate(w / 2f, h / 2f);
        for (int i = 0; i < RADARRINGE; i++) {
            // zeichne Radarringe
            canvas.drawCircle(0, 0, sm * (i + 1), paint);
        }
        float radius = outerRing.getRadius();
        float startsektorlinie2grad = radius - sektorlinienlaenge / 3;
        float endsektorlinie2grad = radius + sektorlinienlaenge / 3;
        float startsektorlinie5grad = radius - sektorlinienlaenge / 2;
        float endsektorlinie5grad = radius + sektorlinienlaenge / 2;
        for (int winkel = 0; winkel < 180; winkel++) {
            // Festlegen Laenge der sektorlienien auf dem Aussenkreis
            // Alle 2 Grad: halbe sektorlinienlaenge
            // Alle 10 Grad: sektorlinienlaenge
            // Alle 30 Grad: Linie vom Mittelpunkt zum aeusseren sichtbaren Radarkreis
            // Berechnen der Linien - Abstand 2 Grad
            if (winkel % 10 == 0) {
                canvas.drawLine(0, -radius - sektorlinienlaenge, 0, radius + sektorlinienlaenge, paint);
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
    }

    private void drawVessels() {
        if (canvasView == null) {
            canvasView = new View(getContext()) {
                @Override
                protected void onDraw(Canvas canvas) {
                    for (int i = 0; i < vesselList.size(); i++) {
                        VesselView v = vesselList.get(i);
                        v.onDraw(canvas, RadarBasisView.this, mEigenesSchiff);
                    }
                }
            };
            canvasView.setLayoutParams(new LayoutParams(getWidth(), getHeight()));
            addView(canvasView);
            canvasView.invalidate();
        }
    }

    private int getPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mEigenesSchiff != null) {
            mEigenesSchiff.removeOnPropertyChangedCallback(ownVesselObserver);
            vesselList.remove(0);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        // Hintergrundfarbe setzen
        //        canvas.drawColor(colorRadarBackground);
        // Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
        if (!northupOrientierung && mEigenesSchiff != null) {
            canvas.rotate(mEigenesSchiff.getHeading());
        }
        canvas.drawBitmap(bm, 0, 0, paint);
        drawVessels();
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
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
        int size = Math.min(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) / 2.0f;
        sm = (int) (scale - textWidth) / RADARRINGE;
        Log.d("gerwalex", "1sm Pixel: " + sm);
        drawVessels();
        createRadarBitmap2(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            int width = getWidth() / 2;
            int height = getHeight() / 2;
            float x = event.getX();
            float y = event.getY();
            Punkt2D pkt = new Punkt2D((x - width) / sm, (height - y) / sm);
            for (VesselView vesselView : vesselList) {
                if (vesselView.isClicked(pkt)) {
                    mModel.clickedVessel.setValue(vesselView.getVessel());
                    Log.d("gerwalex", "click: : " + vesselView.getVessel());
                }
            }
            if (mEigenesSchiff != null && mEigenesSchiff.isPunktAufKurslinie(pkt)) {
            }
        }
        return mScaleDetector.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setModel(MainModel model) {
        mModel = model;
        setOwnVessel(mModel.ownVessel.getValue());
    }

    public void setOwnVessel(Vessel me) {
        if (mEigenesSchiff != null) {
            mEigenesSchiff.removeOnPropertyChangedCallback(ownVesselObserver);
            vesselList.remove(0);
        }
        mEigenesSchiff = me;
        mEigenesSchiff.addOnPropertyChangedCallback(ownVesselObserver);
        vesselList.add(0, new VesselView(me, colors[0]));
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
