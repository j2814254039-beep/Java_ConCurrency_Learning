package org.dromara.jvm;

// 一个"活体标本"：几条不同状态的线程，故意活 60 秒供 jstack 解剖
public class JstackDemo {

    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        // 线程 A：睡着（TIMED_WAITING）
        new Thread(() -> {
            try { Thread.sleep(60_000); } catch (InterruptedException e) { }
        }, "线程A-睡觉中").start();

        // 线程 B：死等锁（BLOCKED）—— 先占住锁，谁也不放
        Thread holder = new Thread(() -> {
            synchronized (lock) {
                try { Thread.sleep(60_000); } catch (InterruptedException e) { }
            }
        }, "线程B-占着锁");
        holder.start();
        Thread.sleep(300);   // 等 B 先把锁拿到

        // 线程 C：想拿锁但拿不到（BLOCKED，排队等）
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("拿到了");
            }
        }, "线程C-等锁中").start();

        // 线程 D：无限自旋，烧 CPU（RUNNABLE）
        new Thread(() -> {
            long x = 0;
            while (true) { x++; }
        }, "线程D-疯狂空转").start();

        System.out.println("标本就绪，主线程也睡一会儿……");
        Thread.sleep(60_000);
    }
}
