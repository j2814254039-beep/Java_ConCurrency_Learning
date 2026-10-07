package org.dromara.share;

public class VisibilityDemo {

    // 关键：加 volatile 之前，worker 永远看不到 false
    static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {

        Thread worker = new Thread(() -> {
            long count = 0;
            // worker 一直转，直到 running 变成 false
            while (running) {
                count++;
            }
            System.out.println("worker 退出了，转了 " + count + " 圈");
        }, "worker");

        worker.start();

        Thread.sleep(1000);              // 让 worker 先转 1 秒
        System.out.println("main: 把 running 改成 false");
        running = false;                 // main 想喊停

        worker.join(3000);               // 最多等 worker 3 秒
        System.out.println("3 秒后，worker 还活着吗? " + worker.isAlive());
        System.out.println("主程序结束");
    }
}
