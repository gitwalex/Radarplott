package com.gerwalex.radarplott.lib;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Ein Thread, der die Zeit der Ausführung des runnable misst.
 */
public class MeasurableThread extends Thread {
    private static final long MAXRUNNINGSECONDS = 1;
    private static final Random random = new Random(System.currentTimeMillis());
    public final long id;
    public long laufzeit;
    public String name;
    public long startzeit;

    public MeasurableThread(@NonNull Runnable c) {
        super(c);
        id = random.nextInt(1000000);
        String clzname = c.getClass().getSimpleName();
        String[] split = clzname.split("\\$");
        if (split.length > 4) {
            name = split[3];
        } else {
            name = clzname;
        }
    }

    @Override
    public void run() {
        startzeit = System.currentTimeMillis();
        super.run();
        if (!"Worker".equals(name)) {
            laufzeit = System.currentTimeMillis() - startzeit;
            @SuppressLint("DefaultLocale") String msg =
                    String.format("Task beendet, class %1$s (ID: %2$d, %3$d ms)", name, id, laufzeit);
            Log.d("TaskRunner", msg);
            if (TimeUnit.MILLISECONDS.toSeconds(laufzeit) > MAXRUNNINGSECONDS) {
                // Laufzeit länger 1 Sekunden -- loggen
                Log.d("gerwalex", msg);
            }
        }
    }
}

