package org.dromara.lock;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicGetAndUpdateDemo {
    public static void main(String[] args) {
        // 两个对象，初始值一样、函数一样 —— 唯一的区别就是调用哪个方法
        AtomicInteger a = new AtomicInteger(10);
        AtomicInteger b = new AtomicInteger(10);

        int r1 = a.getAndUpdate(x -> x + 1);    // 旧值
        int r2 = b.updateAndGet(x -> x + 1);    // 新值

        System.out.println("getAndUpdate 返回: " + r1 + "   (a 现在 = " + a.get() + ")   ← 返回【旧值】10");
        System.out.println("updateAndGet 返回: " + r2 + "   (b 现在 = " + b.get() + ")   ← 返回【新值】11");
        System.out.println("\nprev(旧) 和 next(新) 是同一个数吗? " + (r1 == r2));
    }
}
