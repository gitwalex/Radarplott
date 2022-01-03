package com.gerwalex.radarplott.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;

import com.gerwalex.radarplott.BuildConfig;
import com.gerwalex.radarplott.R;
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
    @ColorRes
    private final int[] colors;
    private final float markerRadius = 20f;
    // Variablen zum Zeichnen
    private final Paint radarLineStyle = new Paint();
    private final float sektorlinienlaenge = 40.0f;
    private final Path symbolPath = new Path();
    private final int textSize;
    private final Paint textStyle = new TextPaint();
    private final List<VesselView> vesselList = new ArrayList<>();
    public Observable.OnPropertyChangedCallback vesselObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            invalidate();
        }
    };
    private Bitmap bm;
    private Vessel mEigenesSchiff;
    private final Observer<Vessel> ownVesselObserver = new Observer<Vessel>() {
        @Override
        public void onChanged(Vessel vessel) {
            if (mEigenesSchiff != null) {
                mEigenesSchiff.removeOnPropertyChangedCallback(vesselObserver);
                vesselList.remove(0);
            }
            mEigenesSchiff = vessel;
            mEigenesSchiff.addOnPropertyChangedCallback(vesselObserver);
            VesselView mEigenesSchiffView = new VesselView(mEigenesSchiff, getEigenesSchiffColor());
            vesselList.add(0, mEigenesSchiffView);
            invalidate();
        }
    };
    private OnVesselClickListener mOnClickListener;
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
        radarLineStyle.setTextSize(40);
        radarLineStyle.setFakeBoldText(true);
        radarLineStyle.setAntiAlias(true);
        radarLineStyle.setStyle(Paint.Style.STROKE);
        radarLineStyle.setColor(getResources().getColor(R.color.colorRadarLinien));
        colors = getResources().getIntArray(R.array.vesselcolors);
        textStyle.setColor(getResources().getColor(R.color.white));
        textSize = getResources().getDimensionPixelSize(R.dimen.smallText);
        textStyle.setTextSize(textSize);
        textStyle.setAntiAlias(true);
        setWillNotDraw(false);
    }

    public void addCircle(Path path, Punkt2D pos) {
        path.addCircle(pos.x * sm, -pos.y * sm, markerRadius, Path.Direction.CW);
    }

    public void addSymbol(Symbols symbol, Path path, Punkt2D mittelpunkt) {
        symbol.addSymbol(path, mittelpunkt, sm);
    }

    public void addVessel(OpponentVessel vessel) {
        OpponentVesselView opponentVesselView = new OpponentVesselView(vessel, colors[vesselList.size()]);
        vesselList.add(opponentVesselView);
    }

    private void createRadarBitmap2(int w, int h) {
        int textWidth = getTextRect(textStyle, "000").width();
        sm = (int) (scale - textWidth * 2) / RADARRINGE;
        outerRing = new Kreis2D(new Punkt2D(), RADARRINGE);
        bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.translate(w / 2f, h / 2f);
        for (int i = 0; i < RADARRINGE; i++) {
            // zeichne Radarringe
            canvas.drawCircle(0, 0, sm * (i + 1), radarLineStyle);
        }
        float radius = outerRing.getRadius() * sm;
        Punkt2D mp = outerRing.getMittelpunkt();
        for (int i = 0; i < 36; i++) {
            Punkt2D pkt = mp.getPunkt2D(i * 10, radius + sektorlinienlaenge + textSize);
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
                canvas.drawLine(0, -radius - sektorlinienlaenge, 0, radius + sektorlinienlaenge, radarLineStyle);
            } else {
                if (winkel % 5 == 0) {
                    canvas.drawLine(0, startsektorlinie5grad, 0, endsektorlinie5grad, radarLineStyle);
                    canvas.drawLine(0, -startsektorlinie5grad, 0, -endsektorlinie5grad, radarLineStyle);
                } else {
                    canvas.drawLine(0, startsektorlinie2grad, 0, endsektorlinie2grad, radarLineStyle);
                    canvas.drawLine(0, -startsektorlinie2grad, 0, -endsektorlinie2grad, radarLineStyle);
                }
            }
            canvas.rotate(1);
        }
    }

    public void drawCenteredText(Canvas canvas, Punkt2D ankerPos, String text) {
        Rect result = getTextRect(textStyle, text);
        canvas.drawText(text, ankerPos.x * sm - result.width() / 2f, -ankerPos.y * sm + result.height() / 2f,
                textStyle);
    }

    public void drawEndText(Canvas canvas, Punkt2D ankerPos, String text) {
        Rect result = getTextRect(textStyle, text);
        canvas.drawText(text, ankerPos.x * sm, -ankerPos.y * sm + result.height() / 2f, textStyle);
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

    /**
     * Hilfsmethode zum berechnen des Endpunktes einer Kurslinie auuf einem Radarring
     *
     * @param vessel Vessel
     * @return endpunkt, null, wenn der Schnittpunkt nicht in Fahrtrichtung liegt (Dann ist Schiff ausserhalb
     * Raadarbild)
     */
    @Nullable
    protected Punkt2D getEndOfKurslinie(Vessel vessel) {
        Punkt2D[] sc = vessel.getKurslinie().getSchnittpunkt(outerRing);
        if (sc != null) {
            return vessel.isPunktInFahrtrichtung(sc[0]) ? sc[0] : sc[1];
        }
        return null;
    }

    private int getPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
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
        if (mEigenesSchiff != null) {
            mEigenesSchiff.removeOnPropertyChangedCallback(vesselObserver);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        symbolPath.reset();
        canvas.save();
        if (!northupOrientierung && mEigenesSchiff != null) {
            canvas.rotate(getEigenesSchiff().getHeading());
        }
        canvas.drawBitmap(bm, 0, 0, radarLineStyle);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        for (VesselView vv : vesselList) {
            vv.drawVessel(canvas, this);
        }
        canvas.drawPath(symbolPath, textStyle);
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
        int desiredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
        setMeasuredDimension(size, size);
        if (getPaddingLeft() + getPaddingRight() + getPaddingTop() + getPaddingBottom() != 0) {
            Log.w("gerwalex", "RadarView: Padding ignored ");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scale = Math.min(h, w) / 2.0f;
        createRadarBitmap2(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if (MotionEvent.ACTION_DOWN == event.getAction() && mOnClickListener != null) {
            int width = getWidth() / 2;
            int height = getHeight() / 2;
            float x = event.getX();
            float y = event.getY();
            Punkt2D pkt = new Punkt2D((x - width) / sm, (height - y) / sm);
            Kreis2D k = new Kreis2D(pkt, 40f);
            for (VesselView vesssel : vesselList) {
                if (k.liegtImKreis(vesssel.getVessel().getAktPosition())) {
                    mOnClickListener.onVesselClick(mEigenesSchiff);
                }
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

    public void setOnVessselClickListener(OnVesselClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOwnVessel(Vessel vessel) {
        if (mEigenesSchiff != null) {
            mEigenesSchiff.removeOnPropertyChangedCallback(vesselObserver);
            vesselList.remove(0);
        }
        mEigenesSchiff = vessel;
        mEigenesSchiff.addOnPropertyChangedCallback(vesselObserver);
        VesselView mEigenesSchiffView = new VesselView(mEigenesSchiff, getEigenesSchiffColor());
        vesselList.add(0, mEigenesSchiffView);
        invalidate();
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

    public interface OnVesselClickListener {
        void onVesselClick(Vessel vessel);
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
