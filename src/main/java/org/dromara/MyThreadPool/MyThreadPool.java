package org.dromara.MyThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadPool{
    int poolSize;
    int maximumPoolSize;
    int maximumQueueSize;
    volatile boolean isOn=true;
    List<Thread> threads;
    AtomicInteger runningThreads=new AtomicInteger(0);
    BlockingQueue<Runnable> queue;
    private static final Runnable stop = ()->{};
    public MyThreadPool(int poolSize){
        this.poolSize = poolSize;
        maximumPoolSize = poolSize*2;
        maximumQueueSize = maximumPoolSize*2;
        threads = new ArrayList<>();
        queue=new ArrayBlockingQueue<>(maximumQueueSize);
        for(int i=0;i<poolSize;i++){
            Thread t=new Thread(MyThreadTask());
            threads.add(t);
            t.start();
        }
    }
    public Runnable MyThreadTask(){
        return ()-> {
            runningThreads.incrementAndGet();
            while (isOn) {
                try {
                    Runnable task = queue.take();
                    if(task==stop){break;}
                    task.run();
                } catch (InterruptedException e) {
                }
            }
            runningThreads.decrementAndGet();
        };
    }
    public void execute(Runnable task) throws InterruptedException{
        if(queue.size()==maximumQueueSize){
            System.out.println("任务满了，拒绝该次任务");
            return;
        }
        if(queue.size()>0&&runningThreads.get()==threads.size()&&threads.size()<maximumPoolSize){
            Thread t=new Thread(MyThreadTask());
            threads.add(t);
            t.start();
        }
        queue.put(task);
    }
    public void shutdownNow() throws InterruptedException{
        System.out.println("完成剩余任务中~");
        isOn=false;//interrupt可能被线程执行的程序吃掉，isOn保底
        for(Thread t:threads){
            t.interrupt();
            t.join();
        }
        System.out.println("线程池关闭完成");
    }
    public void shutdown() throws InterruptedException{
        System.out.println("完成剩余任务中~");
        for(int i=0;i<threads.size();i++){
            queue.put(stop);
        }
        for(Thread t : threads) t.join();
        System.out.println("线程池关闭完成");
    }
}
