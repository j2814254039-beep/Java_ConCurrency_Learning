package org.dromara.coop;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// 用 BlockingQueue 实现同一件事：满了自己阻塞，空了自己阻塞
public class BlockingQueueDemo {

    static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

    public static void main(String[] args) throws InterruptedException {

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 8; i++) {
                    queue.put(i);                       // 满了会自动阻塞等待
                    System.out.println("生产 " + i + "  (队列=" + queue.size() + ")");
                    Thread.sleep(200);
                }
                queue.put(-1);                          // -1 当"停产信号"
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "生产者");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int v = queue.take();               // 空了会自动阻塞等待
                    if (v == -1) {
                        break;                          // 收到停产信号 → 收工
                    }
                    System.out.println("    消费 " + v + "  (队列=" + queue.size() + ")");
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("消费者收工");
        }, "消费者");

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
