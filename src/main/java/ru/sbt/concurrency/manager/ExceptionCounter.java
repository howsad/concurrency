package ru.sbt.concurrency.manager;

/**
 * Created by Alexander Ushakov on 29.08.2016.
 */
public class ExceptionCounter implements Thread.UncaughtExceptionHandler {
    private volatile int failed = 0;

    public void uncaughtException(Thread th, Throwable ex) {
        System.out.println("Uncaught exception: " + ex);
        failed++;
    }

    public int getFailed() {
        return failed;
    }
}
