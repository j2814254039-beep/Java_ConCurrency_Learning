package org.dromara.MyThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

// 故意丢一批"慢任务"，然后马上 shutdown —— 观察有多少任务被吞掉
public class MyThreadPoolTest2 {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2, 4, 10, new ArrayBlockingQueue<>(4) {
        });
        AtomicInteger done = new AtomicInteger(0);
        AtomicInteger rejected = new AtomicInteger(0);
        AtomicInteger interrupted = new AtomicInteger(0);

        // 丢 10 个任务，每个干 200ms
        for (int i = 1; i <= 10; i++) {
            final int id = i;
            try
            {
                pool.execute(() -> {
                    try {
                        Thread.sleep(200);
                        done.incrementAndGet();
                        System.out.println("任务 " + id + " 完成，累计 " + done.get());
                    } catch (InterruptedException e) {
                        interrupted.incrementAndGet();
                        System.out.println("任务"+id+"被中断");
                    }
                });
            }catch (RejectedExecutionException e){
                rejected.incrementAndGet();
                System.out.println("任务"+id+"被拒绝");
            }
        }

        Thread.sleep(50);      // 只等 50ms（任务要 200ms 才干得完）
        List<Runnable> interruptT=new ArrayList<>();
        System.out.println(">>> 现在关闭线程池");
        //pool.shutdown();
        interruptT=pool.shutdownNow();
        System.out.println(">>> 关闭后，实际完成的任务数: " + done.get() + " / 10");
        System.out.println(">>> 关闭后，剩余的任务数: " +interruptT.size());
        System.out.println(">>> 关闭时，期间中断的任务数: " + interrupted.get());
        System.out.println(">>> 执行期间，拒绝的任务数: "+ rejected.get());
    }
}
