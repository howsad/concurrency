package ru.sbt.concurrency.pingpong;

import static java.lang.Boolean.*;

/**
 * Created by Alexander Ushakov on 13.09.2016.
 */
public class PingPong {
    private volatile boolean isPingTurn = true;
    private ThreadLocal<Boolean> isPing = new ThreadLocal<>();

    public static void main(String[] args) {
        PingPong pingPong = new PingPong();
        pingPong.runPing();
        pingPong.runPong();
    }

    private void runPing() {
        new Thread(() -> {
            isPing.set(TRUE);
            while (true) {
                synchronized (this) {
                    while (!isPingTurn) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("PING");
                    isPingTurn = false;
                    this.notify();
                }
            }
        }).start();
    }

    private void runPong() {
        new Thread(() -> {
            isPing.set(FALSE);
            while (true) {
                synchronized (this) {
                    while (isPingTurn) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("PONG");
                    isPingTurn = true;
                    this.notify();
                }
            }
        }).start();
    }
}
