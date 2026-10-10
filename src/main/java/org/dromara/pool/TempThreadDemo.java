package org.dromara.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 验证：临时线程是"跑一次就死"，还是"闲够了才死"？
public class TempThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        // 核心 1、最大 3、队列容量 1、空闲存活 2 秒
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1, 3, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));

        System.out.println("=== 第 1 波：3 个任务，逼它扩出临时线程 ===");
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            pool.execute(() -> {
                System.out.println("任务 " + id + " 由 " + Thread.currentThread().getName() + " 执行");
                try { Thread.sleep(500); } catch (InterruptedException e) { }
            });
        }
        Thread.sleep(800);
        System.out.println("第 1 波结束后，线程数 = " + pool.getPoolSize());

        System.out.println("\n=== 立刻再来 1 个任务（临时线程还没闲够 2 秒）===");
        pool.execute(() -> System.out.println("任务 4 由 " + Thread.currentThread().getName() + " 执行"));
        Thread.sleep(500);
        System.out.println("此时线程数 = " + pool.getPoolSize() + "  ← 临时线程被复用了，还没死");

        System.out.println("\n=== 什么都不干，等 3 秒（超过 keepAliveTime）===");
        Thread.sleep(3000);
        System.out.println("闲置 3 秒后，线程数 = " + pool.getPoolSize() + "  ← 临时线程自己死了");

        pool.shutdown();
    }
}
