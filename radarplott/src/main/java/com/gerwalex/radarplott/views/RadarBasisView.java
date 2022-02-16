package com.gerwalex.radarplott.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.Observable;
import androidx.lifecycle.MutableLiveData;

import com.gerwalex.lib.math.Kreis2D;
import com.gerwalex.lib.math.Punkt2D;
import com.gerwalex.radarplott.R;
import com.gerwalex.radarplott.math.Lage;
import com.gerwalex.radarplott.math.OpponentVessel;
import com.gerwalex.radarplott.math.Vessel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Zeichnet ein quadratisches Radarbild.
 *
 * @author Alexander Winkler
 */
public class RadarBasisView extends View {
    public static final float RADARRINGE = 8;
    private static final int textPadding = 30;
    private static final int thickPath = 3;
    private static final int thinPath = 2;
    public final MutableLiveData<Integer> maxTime = new MutableLiveData<>(0);
    protected final Path courseline = new Path();
    protected final Paint courslineStyle = new Paint();
    private final long clickTime = -1;
    @ColorRes
    private final int[] colors;
    private final float desiredSize;
    private final float extraSmallTextSize;
    private final RadarTouchDetector gestureDetector;
    private final Paint manoeverCourselineStyle = new Paint();
    private final Path manoeverline = new Path();
    private final float markerRadius = 20f;
    private final int maxRadarRings;
    private final int ownVesselColor;
    // Variablen zum Zeichnen
    private final Paint radarLineStyle = new Paint();
    private final Path relCourseline = new Path();
    private final float smallTextSize;
    private final Path symbolPath = new Path();
    private final int textColor;
    private final Paint textStyle = new TextPaint();
    private final Paint vesselPositionStyle = new Paint();
    private boolean drawCourseline = true;
    private boolean drawCourselineText = true;
    private boolean drawPositionText = true;
    private Bitmap innerRadarRing;
    private boolean longPressed;
    private ScaleGestureDetector mScaleDetector;
    private Vessel manoverVessel;
    private Vessel me;
    /**
     * Mindestanzahl der Radarringe. Wird in @{link {@link RadarBasisView#setOpponents(List)} gesetzt.}
     */
    private float minRadarRings;
    private int minutes;
    private boolean northupOrientierung = true;
    private List<OpponentVessel> opponentVesselList = new ArrayList<>();
    private final Observable.OnPropertyChangedCallback ownVesselObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (manoverVessel != null) {
                for (OpponentVessel opponent : opponentVesselList) {
                    opponent.createManoeverLage(manoverVessel, minutes);
                }
            }
            invalidate();
        }
    };
    private Bitmap outerRadarRing;
    private float outerRadarRingRadius;
    private Kreis2D outerRing;
    private RadarObserver radarObserver;
    private RadarSize radarSize = RadarSize.Small;
    private float scaleFactor;
    private int startTime;

    public RadarBasisView(Context context) {
        this(context, null);
    }

    public RadarBasisView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.RadarView);
    }

    public RadarBasisView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        TypedArray a =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadarViewStyle, 0, R.style.RadarView);
        try {
            minRadarRings = a.getInt(R.styleable.RadarViewStyle_minRadarRings, 3);
            maxRadarRings = a.getInt(R.styleable.RadarViewStyle_maxRadarRings, 3);
            desiredSize = a.getDimension(R.styleable.RadarViewStyle_minRadarSize, 300);
        } finally {
            a.recycle();
        }
        colors = getResources().getIntArray(R.array.vesselcolors);
        ownVesselColor = ContextCompat.getColor(context, R.color.ownVesselColor);
        radarLineStyle.setTextSize(40);
        radarLineStyle.setFakeBoldText(true);
        radarLineStyle.setAntiAlias(true);
        radarLineStyle.setStyle(Paint.Style.STROKE);
        radarLineStyle.setColor(ContextCompat.getColor(context, R.color.colorRadarLinien));
        courslineStyle.setStrokeWidth(thinPath);
        courslineStyle.setAntiAlias(true);
        courslineStyle.setStyle(Paint.Style.STROKE);
        //
        vesselPositionStyle.setAntiAlias(true);
        vesselPositionStyle.setStyle(Paint.Style.STROKE);
        vesselPositionStyle.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        vesselPositionStyle.setStrokeWidth(thickPath);
        //
        manoeverCourselineStyle.setAntiAlias(true);
        manoeverCourselineStyle.setStyle(Paint.Style.STROKE);
        manoeverCourselineStyle.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
        manoeverCourselineStyle.setStrokeWidth(thickPath);
        //
        textColor = ContextCompat.getColor(context, R.color.white);
        textStyle.setColor(textColor);
        smallTextSize = getResources().getDimension(R.dimen.mediumText);
        extraSmallTextSize = getResources().getDimension(R.dimen.extraSmallText);
        textStyle.setTextSize(smallTextSize);
        textStyle.setAntiAlias(true);
        setWillNotDraw(false);
        gestureDetector = new RadarTouchDetector(context);
    }

    public void addCircle(Path path, Punkt2D pos) {
        path.addCircle(pos.x * scaleFactor, -pos.y * scaleFactor, markerRadius, Path.Direction.CW);
    }

    private void createInnerRadarRings(float radarRings) {
        if (getWidth() + getHeight() != 0) {
            textStyle.setTextSize(extraSmallTextSize);
            textStyle.setColor(textColor);
            int w = getWidth();
            int h = getHeight();
            innerRadarRing = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(innerRadarRing);
            canvas.translate(w / 2f, h / 2f);
            textStyle.setTextAlign(Paint.Align.CENTER);
            boolean drawInnerRingText = radarSize.getDrawInnerRingText();
            for (int ringSize = 1; ringSize <= radarRings; ringSize++) {
                canvas.drawCircle(0, 0, ringSize * scaleFactor, radarLineStyle);
                if (drawInnerRingText && ringSize % 2 == 0) {
                    String text = String.valueOf(ringSize);
                    canvas.drawText(text, 0, -(ringSize * scaleFactor) + extraSmallTextSize / 2, textStyle);
                    canvas.drawText(text, 0, ringSize * scaleFactor + extraSmallTextSize / 2, textStyle);
                    //
                    canvas.drawText(text, -(ringSize * scaleFactor), +extraSmallTextSize / 2, textStyle);
                    canvas.drawText(text, (ringSize * scaleFactor), +extraSmallTextSize / 2, textStyle);
                }
            }
        }
    }

    private void createOuterRadarRing(int w, int h) {
        if (w * h != 0) {
            float sektorlinienlaenge = (Math.min(w, h)) / 40f;
            float startsektorlinie2grad = outerRadarRingRadius - sektorlinienlaenge / 3;
            float endsektorlinie2grad = outerRadarRingRadius + sektorlinienlaenge / 3;
            float startsektorlinie5grad = outerRadarRingRadius - sektorlinienlaenge / 2;
            float endsektorlinie5grad = outerRadarRingRadius + sektorlinienlaenge / 2;
            textStyle.setColor(textColor);
            textStyle.setTextSize(extraSmallTextSize);
            textStyle.setTextAlign(Paint.Align.CENTER);
            Rect textRect = getTextRect(textStyle, "000");
            outerRadarRingRadius = (Math.min(w, h) - textRect.width()) / 2f - sektorlinienlaenge;
            outerRadarRing = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outerRadarRing);
            canvas.save();
            canvas.translate(w / 2f, h / 2f);
            Punkt2D mp = new Punkt2D();
            int stepsize = radarSize.getStepsize();
            for (int i = 0; i < 36; i += stepsize) {
                Punkt2D pkt = mp.getPunkt2D(i * 10, outerRadarRingRadius + sektorlinienlaenge);
                String text = "000" + i * 10;
                canvas.drawText(text.substring(text.length() - 3), pkt.x, -pkt.y + textRect.height() / 2f, textStyle);
            }
            for (int winkel = 0; winkel < 180; winkel++) {
                if (winkel % 10 == 0) {
                    canvas.drawLine(0, -outerRadarRingRadius - sektorlinienlaenge, 0,
                            outerRadarRingRadius + sektorlinienlaenge, radarLineStyle);
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
            canvas.restore();
        }
    }

    private void drawCPA(Canvas canvas, Vessel vessel, int color) {
        courseline.reset();
        courslineStyle.setColor(color);
        textStyle.setColor(color);
        textStyle.setTextSize(extraSmallTextSize);
        Punkt2D cpa = me.getCPA(vessel);
        if (me.getPeilungRechtweisend(cpa) <= 180) {
            drawLine(courseline, me.getSecondPosition(), cpa);
            textStyle.setTextAlign(Paint.Align.LEFT);
        } else {
            drawLine(courseline, cpa, me.getSecondPosition());
            textStyle.setTextAlign(Paint.Align.RIGHT);
        }
        if (drawCourselineText) {
            String text = getContext().getString(R.string.CPAFormatted, me.getAbstand(cpa));
            canvas.drawTextOnPath(text, courseline, 0, -10, textStyle);
        }
        canvas.drawPath(courseline, courslineStyle);
    }

    public void drawCenteredText(Canvas canvas, Punkt2D ankerPos, String text) {
        Rect result = getTextRect(textStyle, text);
        canvas.drawText(text, ankerPos.x * scaleFactor - result.width() / 2f,
                -ankerPos.y * scaleFactor + result.height() / 2f, textStyle);
    }

    private void drawCourseline(Canvas canvas, Vessel vessel, int color) {
        // keine Geschwindigkeit -> keine Linie
        Punkt2D dest = getEndOfKursline(vessel);
        if (dest != null) {
            courseline.reset();
            courslineStyle.setColor(color);
            Punkt2D endPos = vessel.getSecondPosition();
            if (vessel.getHeading() <= 180) {
                drawLine(courseline, endPos, dest);
            } else {
                drawLine(courseline, dest, endPos);
            }
            if (drawCourselineText) {
                textStyle.setColor(color);
                textStyle.setTextSize(extraSmallTextSize);
                textStyle.setTextAlign(Paint.Align.CENTER);
                String text = getContext().getString(R.string.kursFormatted, vessel.getHeading(), vessel.getSpeed());
                canvas.drawTextOnPath(text, courseline, 0, -10, textStyle);
                textStyle.setColor(textColor);
            }
            drawLine(courseline, me.getSecondPosition(), me.getCPA(vessel));
            canvas.drawPath(courseline, courslineStyle);
        }
    }

    public Punkt2D drawEndText(Canvas canvas, Punkt2D ankerPos, String text, float textSize) {
        textStyle.setTextSize(textSize);
        Rect result = getTextRect(textStyle, text);
        canvas.drawText(text, ankerPos.x * scaleFactor + textPadding, -ankerPos.y * scaleFactor + result.height() / 2f,
                textStyle);
        return new Punkt2D(ankerPos.x + result.width() / scaleFactor, ankerPos.y);
    }

    public void drawLine(Path path, Punkt2D from, Punkt2D to) {
        path.moveTo(from.x * scaleFactor, -from.y * scaleFactor);
        path.lineTo(to.x * scaleFactor, -to.y * scaleFactor);
    }

    public void drawLine(Path path, float fromX, float fromY, float toX, float toY) {
        path.moveTo(fromX, -fromY);
        path.lineTo(toX, -toY);
    }

    private void drawManoeverline(Canvas canvas, Vessel vessel, int color) {
        Punkt2D[] dest = getEndsOfKursline(vessel);
        if (dest != null) {
            manoeverline.reset();
            manoeverCourselineStyle.setColor(color);
            textStyle.setColor(color);
            textStyle.setTextSize(extraSmallTextSize);
            // Kurslinie zeichnen und Textausrichtung ermitteln
            if (vessel.getHeading() <= 180) {
                drawLine(manoeverline, dest[0], dest[1]);
                textStyle.setTextAlign(Paint.Align.RIGHT);
            } else {
                drawLine(manoeverline, dest[1], dest[0]);
                textStyle.setTextAlign(Paint.Align.LEFT);
            }
            if (drawCourselineText) {
                String text = getContext().getString(R.string.kursFormatted, vessel.getHeading(), vessel.getSpeed());
                canvas.drawTextOnPath(text, manoeverline, 0, -10, textStyle);
            }
            drawLine(manoeverline, me.getSecondPosition(), me.getCPA(vessel));
            canvas.drawPath(manoeverline, manoeverCourselineStyle);
        }
    }

    @SuppressLint("DefaultLocale")
    private void drawPosition(Canvas canvas, OpponentVessel opponent, int color) {
        if (me != null) {
            relCourseline.reset();
            vesselPositionStyle.setColor(color);
            Vessel vessel = opponent.getRelativeVessel();
            Lage lage = new Lage(me, opponent.getRelativeVessel());
            Punkt2D relPos = lage.getRelPos();
            Punkt2D startPos = vessel.getFirstPosition();
            Punkt2D aktPos = vessel.getSecondPosition();
            drawLine(relCourseline, relPos, aktPos);
            drawLine(relCourseline, startPos, aktPos);
            drawLine(relCourseline, startPos, relPos);
            Punkt2D nextPos = vessel.getPosition(minutes);
            if (outerRing.liegtImKreis(nextPos)) {
                Punkt2D dest = getEndOfKursline(vessel);
                assert dest != null;
                maxTime.setValue((int) Math.max(vessel.getTimeTo(dest), maxTime.getValue()));
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
        canvas.translate(ankerPos.x * scaleFactor + textPadding, -ankerPos.y * scaleFactor - smallTextSize / 2f);
        canvas.rotate(degrees);
        textView.draw(canvas);
        canvas.restore();
    }

    /**
     * Ermmittlung des Endpunktes einer Kurslinie in Fahrtrichtung  auf dem aeusseren Radarring
     *
     * @param vessel Vessel
     * @return endpunkt, null, wenn der Schnittpunkt nicht in Fahrtrichtung liegt (Dann ist Schiff ausserhalb
     * Raadarbild)
     */
    @Nullable
    protected Punkt2D getEndOfKursline(Vessel vessel) {
        Punkt2D[] sc = getEndsOfKursline(vessel);
        return sc == null ? null : sc[1];
    }

    /**
     * Berechnet die Endpunkte eine Kurslinie auf dem aeusseren Radarring.
     *
     * @param vessel Vessel
     * @return Endpunkte. Null, wenn Kurslinie ausserhalb des Radar liegt. Ansonsten in Punkt2D[0] der Punkt auf dem
     * Radarring, der entgegen der Fahrtrichtung liegt. In Punkt2D[1] der Punkt, der in Fahrtrichtung auf dem
     * Radarring liegt.
     */

    protected Punkt2D[] getEndsOfKursline(Vessel vessel) {
        Punkt2D[] result = null;
        Punkt2D[] sc = vessel.getKurslinie().getSchnittpunkt(outerRing);
        if (sc != null) {
            result = new Punkt2D[2];
            if (vessel.isPunktInFahrtrichtung(sc[0])) {
                result[0] = sc[1];
                result[1] = sc[0];
            } else {
                result[0] = sc[0];
                result[1] = sc[1];
            }
        }
        return result;
    }

    public int getStarttimeInMinutes() {
        return startTime;
    }

    private Rect getTextRect(Paint paint, String text) {
        Rect result = new Rect();
        paint.getTextBounds(text, 0, text.length(), result);
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (me != null) {
            me.removeOnPropertyChangedCallback(ownVesselObserver);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (outerRadarRing == null || innerRadarRing == null) {
            Log.d("gerwalex", "RadarView: Size too small");
            return;
        }
        symbolPath.reset();
        canvas.save();
        canvas.drawBitmap(outerRadarRing, 0, 0, radarLineStyle);
        canvas.drawBitmap(innerRadarRing, 0, 0, radarLineStyle);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        if (me != null) {
            if (drawCourseline) {
                drawCourseline(canvas, me, ownVesselColor);
            }
            if (manoverVessel != null) {
                drawCourseline(canvas, manoverVessel, ownVesselColor);
            }
        }
        for (int i = 0; i < opponentVesselList.size(); i++) {
            OpponentVessel opponent = opponentVesselList.get(i);
            int color = colors[i];
            drawCourseline(canvas, opponent.getRelativeVessel(), color);
            drawPosition(canvas, opponent, color);
            if (drawPositionText) {
                drawPositionTexte(canvas, opponent, color);
            }
            if (manoverVessel != null) {
                Lage lage = Objects.requireNonNull(opponent.manoever.get());
                drawManoeverline(canvas, lage.getRelativVessel(), color);
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
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = widthSize;
        } else {
            //Be whatever you want
            width = (int) desiredSize;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = heightSize;
        } else {
            //Be whatever you want
            height = (int) desiredSize;
        }
        int size = Math.min(width, height);
        //MUST CALL THIS
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw && h != oldh) {
            radarSize = RadarSize.getSize(w, h);
            Log.d("gerwalex", String.format("RadarView onSizeChanged: %1d, %2d", w, h));
            createOuterRadarRing(w, h);
            scaleFactor = outerRadarRingRadius / RADARRINGE;
            outerRing = new Kreis2D(new Punkt2D(), outerRadarRingRadius / scaleFactor);
            createInnerRadarRings(RADARRINGE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        boolean consumed = gestureDetector.onTouchEvent(event);
        if (!consumed) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    longPressed = false;
                    consumed = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (longPressed) {
                        int width = getWidth() / 2;
                        int height = getHeight() / 2;
                        float x = event.getX();
                        float y = event.getY();
                        Punkt2D pkt = new Punkt2D((x - width) / scaleFactor, (height - y) / scaleFactor);
                        manoverVessel = new Vessel((int) new Punkt2D().getYAxisAngle(pkt), me.getSpeed());
                        if (radarObserver != null) {
                            radarObserver.onCreateManoever(manoverVessel);
                        }
                        consumed = true;
                    }
            }
        }
        mScaleDetector.onTouchEvent(event);
        return consumed;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setCurrentTime(int minutes) {
        this.minutes = minutes;
        if (manoverVessel != null) {
            manoverVessel = new Vessel(manoverVessel.getPosition(minutes), (int) manoverVessel.getHeading(),
                    manoverVessel.getSpeed());
            if (radarObserver != null) {
                radarObserver.onCreateManoever(manoverVessel);
            }
        }
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

    public void setManoeverVessel(Vessel vessel) {
        manoverVessel = vessel;
        for (OpponentVessel opponent : opponentVesselList) {
            opponent.createManoeverLage(vessel, minutes);
        }
        invalidate();
    }

    public void setOpponents(List<OpponentVessel> opponents) {
        opponentVesselList = opponents;
        for (OpponentVessel v : opponents) {
            minRadarRings =
                    Math.max(getContext().getResources().getInteger(R.integer.minRadarRings), v.getMinRadarSize());
        }
        invalidate();
    }

    public void setOwnVessel(@NonNull Vessel vessel) {
        if (me != null) {
            me.removeOnPropertyChangedCallback(ownVesselObserver);
        }
        me = vessel;
        me.addOnPropertyChangedCallback(ownVesselObserver);
        maxTime.setValue(0);
        if (manoverVessel != null) {
            for (OpponentVessel opponent : opponentVesselList) {
                opponent.createManoeverLage(manoverVessel, minutes);
            }
        }
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

    public enum RadarSize {
        Small {
            @Override
            public int getStepsize() {
                return 9;
            }

            @Override
            public boolean drawTexte() {
                return false;
            }

            @Override
            public boolean getDrawInnerRingText() {
                return false;
            }
        }, Medium {
            @Override
            public int getStepsize() {
                return 3;
            }

            @Override
            public boolean drawTexte() {
                return true;
            }

            @Override
            public boolean getDrawInnerRingText() {
                return false;
            }
        }, Large {
            @Override
            public int getStepsize() {
                return 1;
            }

            @Override
            public boolean drawTexte() {
                return true;
            }

            @Override
            public boolean getDrawInnerRingText() {
                return true;
            }
        };

        public static RadarSize getSize(int width, int heigth) {
            int size = Math.min(width, heigth);
            if (size < 500) {
                return Small;
            }
            if (size < 900) {
                return Medium;
            }
            return Large;
        }

        public abstract boolean drawTexte();

        public abstract boolean getDrawInnerRingText();

        public abstract int getStepsize();
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

        void onCreateManoever(Vessel manoverVessel);

        default boolean onRadarClick() {
            return false;
        }

        void onVesselClick(Vessel vessel);
    }

    private class RadarTouchDetector extends GestureDetector {

        public RadarTouchDetector(Context context) {
            super(context, new SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent event) {
                    int width = getWidth() / 2;
                    int height = getHeight() / 2;
                    float x = event.getX();
                    float y = event.getY();
                    Punkt2D pkt = new Punkt2D((x - width) / scaleFactor, (height - y) / scaleFactor);
                    Kreis2D k = new Kreis2D(pkt, 40f);
                    if (radarObserver != null) {
                        for (OpponentVessel opponent : opponentVesselList) {
                            Vessel vessel = opponent.getRelativeVessel();
                            if (k.liegtImKreis(vessel.getSecondPosition())) {
                                radarObserver.onVesselClick(vessel);
                            }
                        }
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent event) {
                    longPressed = true;
                    int width = getWidth() / 2;
                    int height = getHeight() / 2;
                    float x = event.getX();
                    float y = event.getY();
                    Punkt2D pkt = new Punkt2D((x - width) / scaleFactor, (height - y) / scaleFactor);
                    manoverVessel = new Vessel((int) new Punkt2D().getYAxisAngle(pkt), me.getSpeed());
                    if (radarObserver != null) {
                        radarObserver.onCreateManoever(manoverVessel);
                    }
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (radarObserver != null) {
                        radarObserver.onRadarClick();
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Skaliert das Radarbild. Skalierung nur dann, wenn die Mindestanzahl an Radarringen sichtbar bleibt.
     */

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            float newScaleFactor = scaleFactor * factor;
            float radarRings = outerRadarRingRadius / newScaleFactor;
            if (radarRings > minRadarRings && radarRings < maxRadarRings) {
                scaleFactor = newScaleFactor;
                outerRing = new Kreis2D(new Punkt2D(), radarRings);
                createInnerRadarRings(radarRings);
            }
            invalidate();
            return true;
        }
    }
}




