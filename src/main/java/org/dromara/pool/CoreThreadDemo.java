package org.dromara.pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 验证：JDK 的"核心线程"是【一开始就开好】的，还是【任务来了才开】的？
public class CoreThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        // 核心 5 条、最大 5 条
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                5, 5, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        System.out.println("刚 new 出来，没提交任何任务   当前线程数 = " + pool.getPoolSize());

        pool.execute(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) { }
        });
        System.out.println("提交 1 个任务之后             当前线程数 = " + pool.getPoolSize());

        Thread.sleep(1000);
        System.out.println("任务干完、线程闲置 1 秒后      当前线程数 = " + pool.getPoolSize());

        pool.shutdown();
    }
}
