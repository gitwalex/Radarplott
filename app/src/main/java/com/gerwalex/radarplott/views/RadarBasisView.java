package com.gerwalex.radarplott.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.ColorRes;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;

import com.gerwalex.radarplott.BuildConfig;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.main.MainModel;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.radar.OpponentVessel;
import com.gerwalex.radarplott.radar.Vessel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class RadarBasisView extends FrameLayout {
    public final static int RADARRINGE = 8;
    private final Paint blackPaint = new Paint();
    @ColorRes
    private final int[] colors;
    // Variablen zum Zeichnen
    private final Paint linienPaint = new Paint();
    private final float markerRadius = 20f;
    private final float sektorlinienlaenge = 40.0f;
    private final Path symbolPath = new Path();
    private final List<OpponentVesselView> vesselList = new ArrayList<>();
    public Observable.OnPropertyChangedCallback vesselObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            invalidate();
        }
    };
    private Bitmap bm;
    private Vessel mEigenesSchiff;
    private VesselView mEigenesSchiffView;
    private final Observer<Vessel> ownVesselObserver = new Observer<Vessel>() {
        @Override
        public void onChanged(Vessel vessel) {
            if (mEigenesSchiff != null) {
                mEigenesSchiff.removeOnPropertyChangedCallback(vesselObserver);
            }
            mEigenesSchiff = vessel;
            mEigenesSchiff.addOnPropertyChangedCallback(vesselObserver);
            mEigenesSchiffView = new VesselView(mEigenesSchiff, getEigenesSchiffColor());
            invalidate();
        }
    };
    private MainModel mModel;
    private ScaleGestureDetector mScaleDetector;
    private boolean northupOrientierung = true;
    private Kreis2D outerRing;
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
        linienPaint.setTextSize(40);
        linienPaint.setFakeBoldText(true);
        linienPaint.setAntiAlias(true);
        linienPaint.setStyle(Paint.Style.STROKE);
        linienPaint.setColor(getResources().getColor(R.color.colorRadarLinien));
        colors = getResources().getIntArray(R.array.vesselcolors);
        blackPaint.setColor(getResources().getColor(R.color.black));
        blackPaint.setTextSize(30f);
        blackPaint.setAntiAlias(true);
        blackPaint.setStyle(Paint.Style.STROKE);
        setWillNotDraw(false);
    }

    public void addCircle(Path path, Punkt2D pos) {
        path.addCircle(pos.x * sm, -pos.y * sm, markerRadius, Path.Direction.CW);
    }

    public void addSymbol(Symbols symbol, Punkt2D mittelpunkt) {
        symbol.addSymbol(symbolPath, mittelpunkt, sm);
    }

    public void addVessel(OpponentVessel vessel) {
        OpponentVesselView opponentVesselView = new OpponentVesselView(vessel, colors[vesselList.size()]);
        vesselList.add(opponentVesselView);
    }

    private void createRadarBitmap2(int w, int h) {
        int textWidth = getTextRect(blackPaint, "000").width();
        sm = (int) (scale - textWidth * 2) / RADARRINGE;
        outerRing = new Kreis2D(new Punkt2D(), sm * RADARRINGE);
        bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.translate(w / 2f, h / 2f);
        for (int i = 0; i < RADARRINGE; i++) {
            // zeichne Radarringe
            canvas.drawCircle(0, 0, sm * (i + 1), linienPaint);
        }
        float radius = outerRing.getRadius();
        Punkt2D mp = outerRing.getMittelpunkt();
        for (int i = 0; i < 36; i++) {
            Punkt2D pkt = mp.getPunkt2D(i * 10, radius + sektorlinienlaenge);
            String text = "000" + i * 10;
            drawCenteredText(canvas, new Punkt2D(pkt.x / sm, pkt.y / sm), text.substring(text.length() - 3));
        }
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
                canvas.drawLine(0, -radius - sektorlinienlaenge, 0, radius + sektorlinienlaenge, linienPaint);
            } else {
                if (winkel % 5 == 0) {
                    canvas.drawLine(0, startsektorlinie5grad, 0, endsektorlinie5grad, linienPaint);
                    canvas.drawLine(0, -startsektorlinie5grad, 0, -endsektorlinie5grad, linienPaint);
                } else {
                    canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, linienPaint);
                    canvas.drawLine(0, -startsektorlinie2grad, 0, -endsektorlinie2grad, linienPaint);
                }
            }
            canvas.rotate(1);
        }
    }

    public void drawCenteredText(Canvas canvas, Punkt2D ankerPos, String text) {
        Rect result = getTextRect(blackPaint, text);
        canvas.drawText(text, ankerPos.x * sm - result.width() / 2f, -ankerPos.y * sm + result.height() / 2f,
                blackPaint);
    }

    public void drawEndText(Canvas canvas, Punkt2D ankerPos, String text) {
        Rect result = getTextRect(blackPaint, text);
        canvas.drawText(text, ankerPos.x * sm, -ankerPos.y * sm + result.height() / 2f, blackPaint);
    }

    public void drawLine(Path path, Punkt2D from, Punkt2D to) {
        path.moveTo(from.x * sm, -from.y * sm);
        path.lineTo(to.x * sm, -to.y * sm);
    }

    public Vessel getEigenesSchiff() {
        return mEigenesSchiff;
    }

    public int getEigenesSchiffColor() {
        return colors[0];
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

    private Rect getTextRect(Paint paint, String text) {
        Rect result = new Rect();
        paint.getTextBounds(text, 0, text.length(), result);
        return result;
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
        if (mModel != null) {
            mModel.ownVessel.removeObserver(ownVesselObserver);
        }
        if (mEigenesSchiff != null) {
            mEigenesSchiff.removeOnPropertyChangedCallback(vesselObserver);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        symbolPath.reset();
        canvas.save();
        // Hintergrundfarbe setzen
        //        canvas.drawColor(colorRadarBackground);
        // Mittelpunkt des Radars liegt in der Mitte des Bildes (Canvas).
        if (!northupOrientierung && mEigenesSchiff != null) {
            canvas.rotate(getEigenesSchiff().getHeading());
        }
        canvas.drawBitmap(bm, 0, 0, linienPaint);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        if (mEigenesSchiffView != null) {
            mEigenesSchiffView.drawVessel(canvas, this);
        }
        for (OpponentVesselView vv : vesselList) {
            vv.drawVessel(canvas, this);
        }
        canvas.drawPath(symbolPath, blackPaint);
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
            Kreis2D k = new Kreis2D(pkt, 40f);
            for (OpponentVesselView opponentVesselView : vesselList) {
                if (k.liegtImKreis(opponentVesselView.getVessel().getAktPosition())) {
                }
            }
            if (mEigenesSchiff != null && k.liegtImKreis(getEigenesSchiff().getAktPosition())) {
                mModel.clickedVessel.setValue(mEigenesSchiff);
            }
            if (BuildConfig.DEBUG) {
                Snackbar.make(this, pkt.toString(), Snackbar.LENGTH_SHORT).show();
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
        mModel.ownVessel.observeForever(ownVesselObserver);
    }

    public boolean toggleNorthUpOrientierung() {
        northupOrientierung = !northupOrientierung;
        invalidate();
        return northupOrientierung;
    }

    public enum Symbols {
        ownMove {
            @Override
            public void addSymbol(Path path, Punkt2D mp, float sm) {
                path.addCircle(mp.x * sm, -mp.y * sm, radius, Path.Direction.CW);
            }
        }, realMove {
            @Override
            public void addSymbol(Path path, Punkt2D mp, float sm) {
                path.addCircle(mp.x * sm, -mp.y * sm, radius, Path.Direction.CW);
            }
        }, relativeMove {
            @Override
            public void addSymbol(Path path, Punkt2D mp, float sm) {
                path.addCircle(mp.x * sm, -mp.y * sm, radius, Path.Direction.CW);
            }
        };
        private static final int radius = 15;

        public abstract void addSymbol(Path path, Punkt2D mp, float sm);
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
