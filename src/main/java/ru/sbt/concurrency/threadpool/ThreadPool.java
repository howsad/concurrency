package ru.sbt.concurrency.threadpool;

/**
 * Created by Alexander Ushakov on 01.09.2016.
 */

public interface ThreadPool {
    void start();
    void execute(Runnable runnable);
}