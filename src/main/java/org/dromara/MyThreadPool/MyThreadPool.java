package org.dromara.MyThreadPool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//1. start() vs run()               —— 线程没真开
//2. count++ 非原子                  —— 丢数据
//3. 工作内存 vs 主内存 / volatile    —— 死循环看不见
//4. ArrayList 并发增删 → CME        —— 快照 + 锁
//5. isOn=false 了 worker 看不见      —— volatile
//6. 空转 while()                   —— 你踩了【五次】
//        7. 死等的 shutdown                 —— 毒丸/中断被吞
//8. 循环上界 runningThreads.get()   —— 每轮重算，数量不够
//9. 假 CAS（get读两次/检查在外/没重试）—— 三连坑
//10. 锁漏一半                      —— 拷贝忘加锁（又一次）

public class MyThreadPool{
    int coreThreads;
    int maximumThreads;
    long keepAliveTime;
    volatile boolean isOn=true;
    List<Thread> threads;
    AtomicInteger runningThreads=new AtomicInteger(0);//不可以用list.size来替代，因为size访问的位置都会需要加锁，会导致更麻烦
    BlockingQueue<Runnable> queue;
    private static final Runnable stop = ()->{};
    public MyThreadPool(int coreThreads,int maxThreads,long keepAliveTime,BlockingQueue<Runnable> queue){
        this.coreThreads = coreThreads;
        this.maximumThreads = maxThreads;
        this.keepAliveTime = keepAliveTime;
        threads = new LinkedList<>();
        this.queue=queue;
    }
    public Runnable MyThreadTask(){
        return ()-> {
            while (isOn) {
                boolean isTmp=runningThreads.get()>coreThreads;
                try {
                    Runnable task;
                    if(isTmp){
                         task= queue.poll(keepAliveTime, TimeUnit.SECONDS);
                    }else{
                        task= queue.take();
                    }
                    if(task==null||task==stop){break;}
                    task.run();
                } catch (InterruptedException e) {
                }
            }
            synchronized (threads) {
                threads.remove(Thread.currentThread());
            }
            runningThreads.decrementAndGet();
        };
    }
    public boolean addThread(int limit){
        while(true) {
            int c = runningThreads.get();
            if (c >= limit) return false;
            if (runningThreads.compareAndSet(c, c + 1)) {//失败有两种情况，1、值改变但是未超限2、值改变同时超限了，因此需要重复循环判断
                Thread thread = new Thread(MyThreadTask());
                synchronized (threads) {
                    threads.add(thread);
                }
                thread.start();
                return true;
            }
        }
    }
    public void execute(Runnable task) throws InterruptedException{
        if(addThread(coreThreads)) {
            queue.put(task);
        }else if(queue.offer(task)) {
        }else if(addThread(maximumThreads)) {
            queue.put(task);
        }else{
            throw new RejectedExecutionException();
        }
    }
    public List<Runnable> shutdownNow() throws InterruptedException{
        System.out.println("完成剩余任务中~");
        isOn=false;//interrupt可能被线程执行的程序吃掉，isOn保底
        List<Thread> tmp;
        synchronized (threads){
             tmp= new LinkedList<>(threads);
        }
        for(Thread thread:tmp){
            thread.interrupt();
            thread.join();
        }
        List<Runnable> runnables=new ArrayList<>();
        queue.drainTo(runnables);
        System.out.println("线程池关闭完成");
        return runnables;
    }
    public void shutdown() throws InterruptedException{
        System.out.println("完成剩余任务中~");
        List<Thread> tmp;
        synchronized (threads) {
            tmp = new LinkedList<>(threads);//防止直接遍历threads导致多线程异常
        }
        //runningThreads.get()直接放到循环内会导致循环的上限一直变化，导致stop的任务数量不够
        for(int i=0;i<tmp.size();i++){
            queue.put(stop);
        }
        for(Thread t : tmp) t.join();
        System.out.println("线程池关闭完成");
    }
}
