package com.gerwalex.radarplott.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

public class SuperSubSpan extends ReplacementSpan {
    private static TextPaint getSuperSubPaint(Paint src) {
        TextPaint paint = new TextPaint(src);
        paint.setTextSize(src.getTextSize() / 2.5f);
        return paint;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     Paint paint) {
        text = text.subSequence(start, end);
        String[] parts = text.toString().split(",");
        Paint p = getSuperSubPaint(paint);
        float width1 = p.measureText(parts[0]);
        float width2 = p.measureText(parts[1]);
        float maxWidth = Math.max(width1, width2);
        canvas.drawText(parts[0], x + (maxWidth - width1), y - (bottom - top) / 3f, p);
        canvas.drawText(parts[1], x + (maxWidth - width2), y + (bottom - top) / 10f, p);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        text = text.subSequence(start, end);
        String[] parts = text.toString().split(",");
        Paint p = getSuperSubPaint(paint);
        return (int) Math.max(p.measureText(parts[0]), p.measureText(parts[1]));
    }
}