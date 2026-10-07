package org.dromara.thread;

public class ThreadDemo {

    // 任务本体：只描述"要干什么活"，它本身不是线程
    static class MyTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName()+"子线程: " + i);
            }
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(new MyTask()); // 把任务交给线程
        t.run();                           // 启动线程（注意是 start，不是 run）

        // main 线程自己也在干活
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName()+"main: " + i);
        }
    }
}
