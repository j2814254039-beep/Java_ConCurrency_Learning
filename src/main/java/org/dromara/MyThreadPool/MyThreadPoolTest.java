package org.dromara.MyThreadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

// 测试你的第一版线程池
public class MyThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(2, 4, 10, new ArrayBlockingQueue<>(4) {
        });

        for (int i = 1; i <= 10; i++) {
            final int id = i;
            try
            {
                pool.execute(() -> {
                    try {
                        Thread.sleep(200);
                        System.out.println("任务 " + id );
                    } catch (InterruptedException e) {
                        System.out.println("任务"+id+"被中断");
                    }
                });
            }catch (RejectedExecutionException e){
                System.out.println("任务"+id+"被拒绝");
            }
        }

        Thread.sleep(500);   // 给任务一点执行时间
        pool.shutdownNow();     // 关闭
        System.out.println("main 结束");
    }
}
