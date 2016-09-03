package ru.sbt.concurrency.threadpool;

/**
 * Created by Alexander Ushakov on 02.09.2016.
 */

public class Main {
    public static void main(String[] args) {
        ThreadPool pool = new ScalableThreadPool(3, 5);


        for (int i = 0; i < 100; i++) {
            int count = i;
            pool.execute(() -> {
/*                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                System.out.println(Thread.currentThread().getName() + " " + count);
            });
        }
        System.out.println("finished task init");

/*        pool.execute(() -> System.out.println(1));
        pool.execute(() -> System.out.println(2));
        pool.execute(() -> System.out.println(3));
        pool.execute(() -> System.out.println(4));*/

        pool.start();
    }
}
