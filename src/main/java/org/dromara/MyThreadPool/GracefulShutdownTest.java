package org.dromara.MyThreadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

// 专门验证 shutdown()（优雅关闭）
public class GracefulShutdownTest {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2, 4, 10, new ArrayBlockingQueue<>(4));
        AtomicInteger done = new AtomicInteger(0);
        AtomicInteger rejected = new AtomicInteger(0);

        for (int i = 1; i <= 10; i++) {
            final int id = i;
            try {
                pool.execute(() -> {
                    try {
                        Thread.sleep(200);
                        done.incrementAndGet();
                        System.out.println("任务 " + id + " 完成，累计 " + done.get());
                    } catch (InterruptedException e) {
                        System.out.println("任务 " + id + " 被中断");
                    }
                });
            } catch (RejectedExecutionException e) {
                rejected.incrementAndGet();
                System.out.println("任务 " + id + " 被拒绝");
            }
        }

        Thread.sleep(50);
        System.out.println(">>> 现在 shutdown（优雅关闭）");
        pool.shutdown();
        System.out.println(">>> 关闭完成。完成=" + done.get() + " 拒绝=" + rejected.get()
                + " 合计=" + (done.get() + rejected.get()));
    }
}
