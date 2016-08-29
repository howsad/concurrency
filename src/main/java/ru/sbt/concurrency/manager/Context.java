package ru.sbt.concurrency.manager;

/**
 * Created by Alexander Ushakov on 28.08.2016.
 */
public interface Context {
    int getCompletedTaskCount();

    int getFailedTaskCount();

    int getInterruptedTaskCount();

    void interrupt();

    boolean isFinished();
}