package org.dromara.MyThreadPool;

// 测试你的第一版线程池
public class MyThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(3);

        for (int i = 1; i <= 10; i++) {
            final int id = i;
            pool.execute(() -> {
                System.out.println("任务 " + id + " 由 " + Thread.currentThread().getName() + " 执行");
            });
        }

        Thread.sleep(500);   // 给任务一点执行时间
        pool.shutdownNow();     // 关闭
        System.out.println("main 结束");
    }
}
