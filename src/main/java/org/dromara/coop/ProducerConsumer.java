package org.dromara.coop;

import java.util.LinkedList;
import java.util.Queue;

// 手动用 wait/notify 实现生产者-消费者
public class ProducerConsumer {

    static final int CAPACITY = 3;              // 队列最大容量
    static final Queue<Integer> queue = new LinkedList<>();
    static volatile boolean done = false;       // 生产者是否已停产

    static class Producer implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i <= 8; i++) {
                synchronized (queue) {
                    // 队列满了 → 睡着等消费者来取
                    while (queue.size() == CAPACITY) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    queue.offer(i);
                    System.out.println("生产 " + i + "  (队列=" + queue.size() + ")");
                    queue.notifyAll();          // 叫醒可能在等的消费者
                }
                sleep(200);
            }
            // 生产完毕，通知消费者别再等了
            synchronized (queue) {
                done = true;
                queue.notifyAll();
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    // 队列空了 → 睡着等生产者来喂
                    while (queue.isEmpty() && !done) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    if (queue.isEmpty() && done) {
                        break;                  // 停产且取空了 → 收工
                    }
                    int v = queue.poll();
                    System.out.println("    消费 " + v + "  (队列=" + queue.size() + ")");
                    queue.notifyAll();          // 叫醒可能在等的生产者
                }
                sleep(300);
            }
            System.out.println("消费者收工");
        }
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) {
        new Thread(new Producer(), "生产者").start();
        new Thread(new Consumer(), "消费者").start();
    }
}
