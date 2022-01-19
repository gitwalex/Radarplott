package com.gerwalex.radarplott.lib;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunner extends ThreadPoolExecutor {
    //Queue for all the Tasks
    private static final int CORE_POOL_SIZE = 0;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int MAX_POOL_SIZE = 500;
    private int running;

    public TaskRunner() {
        super(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new MeasurableThread(r);
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        Log.d("TaskRunner", "Tasks running: " + --running);
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            t.printStackTrace();
        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        running++;
    }

    public String getStatistics() {
        int largestPoolSize = getLargestPoolSize();
        long completed = getCompletedTaskCount();
        long tasks = getTaskCount();
        return String
                .format("LargestPoolSize: %1s, completedTasks: %2s, taskCount: %3s", largestPoolSize, completed, tasks);
    }
}
