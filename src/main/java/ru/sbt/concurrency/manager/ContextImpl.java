package ru.sbt.concurrency.manager;

import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.TERMINATED;

/**
 * Created by Alexander Ushakov on 28.08.2016.
 */
public class ContextImpl implements Context {
    private final Thread callback;
    private final List<Thread> newThreads;
    private final ExceptionCounter h;
    private final Object lock = new Object();
    private final int total;
    private volatile int completed = 0;
    private volatile int interrupted = 0;

    public ContextImpl(Thread callback, List<Thread> newThreads, ExceptionCounter h) {
        this.callback = callback;
        this.newThreads = newThreads;
        this.h = h;
        total = newThreads.size();
    }

    @Override
    public int getCompletedTaskCount() {
        checkState();
        return completed;
    }

    private void checkState() {
        synchronized (lock) {
            Iterator<Thread> i = newThreads.iterator();
            while (i.hasNext()) {
                Thread.State state = i.next().getState();
                if (!state.equals(NEW)) {
                    i.remove();
                    if (state.equals(TERMINATED)) {
                        completed++;
                    }
                }
            }
        }
    }

    @Override
    public int getFailedTaskCount() {
        return h.getFailed();
    }

    @Override
    public int getInterruptedTaskCount() {
        return interrupted;
    }

    @Override
    public void interrupt() {
        checkState();
        synchronized (lock) {
            for (Thread t : newThreads) {
                interrupted++;
                t.interrupt();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (isFinished()) {
            callback.start();
        }
        super.finalize();
    }

    @Override
    public boolean isFinished() {
        return total == getCompletedTaskCount() && getFailedTaskCount() == 0;
    }
}
