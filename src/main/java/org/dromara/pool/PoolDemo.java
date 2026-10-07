package org.dromara.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolDemo {

    public static void main(String[] args) {
        // 核心参数：2 个常驻线程，最多 4 个，队列最多放 2 个任务
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,                              // 核心线程数
                4,                              // 最大线程数
                10, TimeUnit.SECONDS,           // 空闲线程最多活 10 秒
                new ArrayBlockingQueue<>(2)     // 任务队列，最多排队 2 个
        );

        // 丢 10 个任务进去
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            try {
                pool.execute(() -> {
                    System.out.println("任务 " + id + " 由 " + Thread.currentThread().getName() + " 执行");
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                });
            } catch (Exception e) {
                System.out.println("任务 " + id + " 被拒绝了: " + e.getClass().getSimpleName());
            }
        }

        pool.shutdown();   // 干完手里的活就关，不再接新任务
    }
}
