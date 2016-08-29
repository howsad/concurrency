package ru.sbt.concurrency.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Ushakov on 28.08.2016.
 */
public class ExecutionManagerImpl implements ExecutionManager {
    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        Thread callbackThread = new Thread(callback);
        List<Thread> threads = new ArrayList<>();
        ExceptionCounter h = new ExceptionCounter();
        for (Runnable task : tasks) {
            Thread t = new Thread(task);
            threads.add(t);
            t.setUncaughtExceptionHandler(h);
            t.start();
        }
        ContextImpl context = new ContextImpl(threads, h);
        int nTasks = tasks.length;
        runCallbackThread(callback, context, nTasks);
        return context;
    }

    private void runCallbackThread(Runnable callback, Context context, int nTasks) {
        new Thread(() -> {
            while (context.getCompletedTaskCount() != nTasks) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            callback.run();
        }).start();
    }
}
