package org.dromara.share;

public class CounterBrokenFixed {
    static int count = 0;

    // 每个线程的活儿：把 count 加 10000 次
    static class Adder implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                synchronized (Adder.class) {
                    count++;
                }
            }
        }
    }
        public static void main(String[] args) throws InterruptedException {
            Thread t1 = new Thread(new Adder());
            Thread t2 = new Thread(new Adder());

            t1.start();
            t2.start();

            // join：等这条线程彻底跑完，再往下走
            t1.join();
            t2.join();

            System.out.println("期望结果: 20000");
            System.out.println("实际结果: " + count);
        }
    }
