package ru.sbt.concurrency.task;

import java.util.concurrent.Callable;

/**
 * Created by Alexander Ushakov on 27.08.2016.
 */
public class Task<T> {
    private final Callable<? extends T> callable;
    private final Object lock = new Object();
    private boolean isCalculated = false;
    private boolean exceptionThrown = false;
    private T result;
    private Exception exception;

    public Task(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public T get() {
        if (isCalculated) {
            return getResult();
        }
        synchronized (lock) {
            while (!isCalculated) {
                try {
                    result = callable.call();
                } catch (Exception e) {
                    exceptionThrown = true;
                    exception = e;
                } finally {
                    isCalculated = true;
                }
            }
        }
        return getResult();
    }

    private T getResult() {
        if (exceptionThrown) {
            throw new CallException(exception);
        }
        return result;
    }

    private static class CallException extends RuntimeException {
        public CallException(Throwable cause) {
            super(cause);
        }
    }
}
