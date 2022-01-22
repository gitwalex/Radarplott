package com.gerwalex.radarplott.lib;

import android.app.Application;
import android.content.res.Resources;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class App extends Application {
    public final static TaskRunner taskRunner = new TaskRunner();
    private static Resources resources;

    public static Resources getAppResources() {
        return App.resources;
    }

    /**
     * Submitted ein Runnable
     *
     * @param runInBackground das Runnable
     */
    public static void run(Runnable runInBackground) {
        taskRunner.execute(runInBackground);
    }

    /**
     * Submitted ein Callable
     *
     * @param runInBackground das Callable
     * @return ein future
     */
    public static <T> Future<T> run(Callable<T> runInBackground) {
        return taskRunner.submit(runInBackground);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }
}