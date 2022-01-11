package com.gerwalex.radarplott.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.ObservableInt;

import com.gerwalex.radarplott.BuildConfig;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.math.Kreis2D;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Punkt2D;
import com.gerwalex.radarplott.math.Vessel;
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
    private static final float sektorlinienlaenge = 40.0f;
    private static final int textPadding = 30;
    private static final int thickPath = 3;
    private static final int thinPath = 2;
    protected final Path courseline = new Path();
    protected final Paint courslineStyle = new Paint();
    private final long clickTime = -1;
    @ColorRes
    private final int[] colors;
    private final float extraSmallTextSize;
    private final GestureDetector gestureDetector;
    private final float markerRadius = 20f;
    private final int ownVesselColor;
    // Variablen zum Zeichnen
    private final Paint radarLineStyle = new Paint();
    private final Path relCourseline = new Path();
    private final float smallTextSize;
    private final Path symbolPath = new Path();
    private final int textColor;
    private final Paint textStyle = new TextPaint();
    private final List<OpponentVessel> vesselList = new ArrayList<>();
    private final Paint vesselPositionStyle = new Paint();
    public ObservableInt maxTime = new ObservableInt(0);
    private Bitmap bm;
    private boolean drawCourseline = true;
    private boolean drawCourselineText = true;
    private boolean drawPositionText = true;
    private boolean longPressed;
    private Vessel mManoeverVessel;
    private ScaleGestureDetector mScaleDetector;
    private Vessel me;
    private int minutes;
    private boolean northupOrientierung = true;
    private Kreis2D outerRing;
    private RadarObserver radarObserver;
    private float scale;
    private int sm;
    private int startTime;

    public RadarBasisView(Context context) {
        this(context, null);
    }

    public RadarBasisView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarBasisView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        colors = getResources().getIntArray(R.array.vesselcolors);
        ownVesselColor = getResources().getColor(R.color.ownVesselColor);
        radarLineStyle.setTextSize(40);
        radarLineStyle.setFakeBoldText(true);
        radarLineStyle.setAntiAlias(true);
        radarLineStyle.setStyle(Paint.Style.STROKE);
        radarLineStyle.setColor(getResources().getColor(R.color.colorRadarLinien));
        courslineStyle.setStrokeWidth(thinPath);
        courslineStyle.setAntiAlias(true);
        courslineStyle.setStyle(Paint.Style.STROKE);
        vesselPositionStyle.setAntiAlias(true);
        vesselPositionStyle.setStyle(Paint.Style.STROKE);
        vesselPositionStyle.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        vesselPositionStyle.setStrokeWidth(thickPath);
        //
        textColor = getResources().getColor(R.color.white);
        textStyle.setColor(textColor);
        smallTextSize = getResources().getDimension(R.dimen.mediumText);
        extraSmallTextSize = getResources().getDimension(R.dimen.extraSmallText);
        textStyle.setTextSize(smallTextSize);
        textStyle.setAntiAlias(true);
        setWillNotDraw(false);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                int width = getWidth() / 2;
                int height = getHeight() / 2;
                float x = e.getX();
                float y = e.getY();
                Punkt2D pkt = new Punkt2D((x - width), (height - y));
                Kreis2D k = new Kreis2D(pkt, 40f);
                if (radarObserver != null) {
                    for (OpponentVessel opponent : vesselList) {
                        Vessel vessel = opponent.getRelativeVessel();
                        if (k.liegtImKreis(vessel.getSecondPosition())) {
                            radarObserver.onVesselClick(vessel);
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Snackbar.make(RadarBasisView.this, pkt.toString(), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                longPressed = true;
                int width = getWidth() / 2;
                int height = getHeight() / 2;
                float x = e.getX();
                float y = e.getY();
                Punkt2D pkt = new Punkt2D((x - width) / sm, (height - y) / sm);
                float angle = new Punkt2D().getYAxisAngle(pkt);
                mManoeverVessel = new Vessel((int) angle, me.getSpeed());
                invalidate();
            }
        });
    }

    public void addCircle(Path path, Punkt2D pos) {
        path.addCircle(pos.x * sm, -pos.y * sm, markerRadius, Path.Direction.CW);
    }

    public void addOpponent(OpponentVessel vessel) {
        vesselList.add(vessel);
        startTime = Math.min(vessel.getTime(), 24 * 60);
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
            Punkt2D pkt = mp.getPunkt2D(i * 10, radius + sektorlinienlaenge + smallTextSize);
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

    private void drawCourseline(Canvas canvas, Vessel vessel, int color) {
        Punkt2D dest = getEndOfKurslinie(vessel);
        if (dest != null) {
            courseline.reset();
            courslineStyle.setColor(color);
            Punkt2D endPos = vessel.getSecondPosition();
            if (vessel.getHeading() <= 180) {
                drawLine(courseline, endPos, dest);
            } else {
                drawLine(courseline, dest, endPos);
            }
            Punkt2D cpa = me.getCPA(vessel);
            Punkt2D pos = me.getSecondPosition();
            if (!pos.equals(cpa) && vessel.isPunktInFahrtrichtung(cpa)) {
                drawLine(courseline, me.getSecondPosition(), cpa);
            }
            if (drawCourselineText) {
                textStyle.setColor(color);
                textStyle.setTextSize(extraSmallTextSize);
                textStyle.setTextAlign(Paint.Align.CENTER);
                String text = getContext().getString(R.string.kursFormatted, vessel.getHeading(), vessel.getSpeed());
                canvas.drawTextOnPath(text, courseline, 0, -10, textStyle);
                textStyle.setColor(textColor);
            }
            canvas.drawPath(courseline, courslineStyle);
        }
    }

    public Punkt2D drawEndText(Canvas canvas, Punkt2D ankerPos, String text, float textSize) {
        textStyle.setTextSize(textSize);
        Rect result = getTextRect(textStyle, text);
        canvas.drawText(text, ankerPos.x * sm + textPadding, -ankerPos.y * sm + result.height() / 2f, textStyle);
        return new Punkt2D(ankerPos.x + result.width() / (float) sm, ankerPos.y);
    }

    public void drawLine(Path path, Punkt2D from, Punkt2D to) {
        path.moveTo(from.x * sm, -from.y * sm);
        path.lineTo(to.x * sm, -to.y * sm);
    }

    @SuppressLint("DefaultLocale")
    private void drawPosition(Canvas canvas, OpponentVessel opponent, int color) {
        if (me != null) {
            relCourseline.reset();
            vesselPositionStyle.setColor(color);
            Vessel vessel = opponent.getRelativeVessel();
            Punkt2D relPos = opponent.getRelPosition();
            Punkt2D startPos = vessel.getFirstPosition();
            Punkt2D aktPos = vessel.getSecondPosition();
            drawLine(relCourseline, relPos, aktPos);
            drawLine(relCourseline, startPos, aktPos);
            drawLine(relCourseline, startPos, relPos);
            Punkt2D nextPos = vessel.getPosition(minutes);
            if (outerRing.liegtImKreis(nextPos)) {
                Punkt2D dest = getEndOfKurslinie(vessel);
                assert dest != null;
                maxTime.set((int) Math.max(vessel.getTimeTo(dest), maxTime.get()));
                addCircle(relCourseline, nextPos);
                if (minutes != 0) {
                    int time = opponent.getTime() + minutes;
                    drawTextView(canvas, nextPos,
                            new SpannableString(String.format("%1s %02d:%02d", opponent.name, time / 60, time % 60)));
                }
            }
            canvas.drawPath(relCourseline, vesselPositionStyle);
            canvas.drawPath(courseline, courslineStyle);
        }
    }

    private void drawPositionTexte(Canvas canvas, OpponentVessel opponent, int color) {
        textStyle.setColor(color);
        textStyle.setTextSize(extraSmallTextSize);
        Vessel vessel = opponent.getRelativeVessel();
        Punkt2D startPos = vessel.getFirstPosition();
        Punkt2D aktPos = vessel.getSecondPosition();
        Punkt2D cpa = me.getCPA(vessel);
        String text = String.format("%1s %2s", opponent.name, opponent.getStartTime());
        drawTextView(canvas, startPos, new SpannableString(text));
        text = String.format("%1s %2s", opponent.name, opponent.getSecondTime());
        drawTextView(canvas, aktPos, new SpannableString(text));
        drawTextView(canvas, cpa,
                new SpannableString(getContext().getString(R.string.CPAFormatted, me.getAbstand(cpa))));
    }

    public void drawTextView(Canvas canvas, Punkt2D ankerPos, Spannable text) {
        drawTextView(canvas, ankerPos, 0, text);
    }

    public void drawTextView(Canvas canvas, Punkt2D ankerPos, int degrees, Spannable text) {
        canvas.save();
        TextView textView = new TextView(getContext());
        TextViewCompat.setTextAppearance(textView, R.style.RawApp_TextAppearance_ExtraSmall);
        textView.setTextColor(textStyle.getColor());
        textView.layout(0, 0, 600, 500);
        textView.setText(text);
        canvas.translate(ankerPos.x * sm + textPadding, -ankerPos.y * sm - smallTextSize / 2f);
        canvas.rotate(degrees);
        textView.draw(canvas);
        canvas.restore();
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

    public int getStarttimeInMinutes() {
        return startTime;
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
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        symbolPath.reset();
        canvas.save();
        canvas.drawBitmap(bm, 0, 0, radarLineStyle);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        if (me != null) {
            if (drawCourseline) {
                drawCourseline(canvas, me, ownVesselColor);
            }
            if (mManoeverVessel != null && mManoeverVessel.getHeading() != me.getHeading()) {
                drawCourseline(canvas, mManoeverVessel, ownVesselColor);
            }
        }
        for (int i = 0; i < vesselList.size(); i++) {
            OpponentVessel vessel = vesselList.get(i);
            int color = colors[i];
            drawCourseline(canvas, vessel.getRelativeVessel(), color);
            drawPosition(canvas, vessel, color);
            if (drawPositionText) {
                drawPositionTexte(canvas, vessel, color);
            }
            if (mManoeverVessel != null) {
                Vessel v = vessel.createManoever(mManoeverVessel, minutes);
                drawCourseline(canvas, v, color);
            }
        }
        canvas.drawPath(symbolPath, textStyle);
        canvas.restore();
        if (!northupOrientierung && me != null) {
            canvas.rotate(me.getHeading());
        }
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
        boolean consumed = gestureDetector.onTouchEvent(event);
        if (!consumed) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_MOVE && longPressed) {
                int width = getWidth() / 2;
                int height = getHeight() / 2;
                float x = event.getX();
                float y = event.getY();
                Punkt2D pkt = new Punkt2D((x - width) / sm, (height - y) / sm);
                int angle = (int) new Punkt2D().getYAxisAngle(pkt);
                mManoeverVessel = new Vessel(angle, me.getSpeed());
                if (radarObserver != null) {
                    radarObserver.onHeadingChanged(me, angle, minutes);
                }
                invalidate();
                consumed = true;
            } else if (action == MotionEvent.ACTION_UP) {
                longPressed = false;
            }
        }
        return consumed || super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setCurrentTime(int minutes) {
        this.minutes = minutes;
        invalidate();
    }

    public void setDrawCourseline(boolean draw) {
        drawCourseline = draw;
    }

    public void setDrawCourselineTexte(boolean draw) {
        drawCourselineText = draw;
        invalidate();
    }

    public void setDrawPositionTexte(boolean draw) {
        drawPositionText = draw;
        invalidate();
    }

    public void setOwnVessel(Vessel vessel) {
        me = vessel;
        maxTime.set(0);
        invalidate();
    }

    public void setRadarObserver(RadarObserver listener) {
        radarObserver = listener;
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

    public interface RadarObserver {
        void onHeadingChanged(Vessel me, int heading, int minutes);

        void onSpeedChanged(Vessel me, int speed, int minutes);

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
