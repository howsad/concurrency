package ru.sbt.concurrency.manager;

/**
 * Created by Alexander Ushakov on 28.08.2016.
 */
public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
