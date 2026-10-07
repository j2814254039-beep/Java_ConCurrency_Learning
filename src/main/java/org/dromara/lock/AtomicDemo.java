package org.dromara.lock;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {

    // 原子整数：内部靠 CAS 保证每次自增都是原子的
    static AtomicInteger count = new AtomicInteger(0);

    static class Adder implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                count.incrementAndGet();   // 相当于原子的 count++
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Adder());
        Thread t2 = new Thread(new Adder());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("期望结果: 20000");
        System.out.println("实际结果: " + count.get());
    }
}
