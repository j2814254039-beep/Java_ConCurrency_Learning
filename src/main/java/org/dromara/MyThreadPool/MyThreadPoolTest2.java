package org.dromara.MyThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

// 故意丢一批"慢任务"，然后马上 shutdown —— 观察有多少任务被吞掉
public class MyThreadPoolTest2 {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2);
        AtomicInteger done = new AtomicInteger(0);

        // 丢 10 个任务，每个干 200ms
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            pool.execute(() -> {
                try { Thread.sleep(200); } catch (InterruptedException e) { }
                done.incrementAndGet();
                System.out.println("任务 " + id + " 完成，累计 " + done.get());
            });
        }

        Thread.sleep(50);      // 只等 50ms（任务要 200ms 才干得完）
        System.out.println(">>> 现在关闭线程池");
        pool.shutdown();

        System.out.println(">>> 关闭后，实际完成的任务数: " + done.get() + " / 10");
    }
}
