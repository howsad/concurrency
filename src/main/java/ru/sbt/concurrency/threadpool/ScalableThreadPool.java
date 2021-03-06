package ru.sbt.concurrency.threadpool;

/**
 * Created by Alexander Ushakov on 01.09.2016.
 */

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class ScalableThreadPool implements ThreadPool {
    private final Queue<Runnable> tasks = new ArrayDeque<>();
    private final int min;
    private final int max;
    private final Object lock = new Object();
    private final AtomicInteger nThreads = new AtomicInteger();

    public ScalableThreadPool(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void start() {
        for (int i = 0; i < Math.min(Math.max(min, tasks.size()), max); i++) {
            newWorker();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (lock) {
            tasks.add(runnable);
            lock.notify();
        }
    }

    private void newWorker() {
        if (nThreads.get() < max) {
            nThreads.incrementAndGet();
            new Worker().start();
        }
    }

    public class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task;
                synchronized (lock) {
                    while (tasks.isEmpty()) {
                        if (nThreads.get() > min) {
                            nThreads.decrementAndGet();
                            return;
                        }
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    task = tasks.poll();
                }
                try {
                    newWorker();
                    task.run();
                } catch (Throwable t) {
                    nThreads.decrementAndGet();
                    synchronized (lock) {
                        if (!tasks.isEmpty()) {
                            newWorker();
                        }
                    }
                    throw t;
                }
            }
        }
    }
}