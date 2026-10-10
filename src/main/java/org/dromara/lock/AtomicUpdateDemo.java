package org.dromara.lock;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicUpdateDemo {
    public static void main(String[] args) {
        AtomicInteger a = new AtomicInteger(10);

        // 1. 两个方法的差别：一个返回旧值，一个返回新值
        System.out.println("getAndUpdate(x+5) 返回: " + a.getAndUpdate(x -> x + 5));  // 旧值 10
        System.out.println("  现在 a = " + a.get());                                  // 15
        System.out.println("updateAndGet(x*2) 返回: " + a.updateAndGet(x -> x * 2));  // 新值 30
        System.out.println("  现在 a = " + a.get());                                  // 30

        // 2. 真正有用的场景：任意逻辑 —— "把值限制在 100 以内"
        AtomicInteger b = new AtomicInteger(0);
        b.updateAndGet(x -> Math.min(x + 60, 100));   // 0+60=60
        System.out.println("\n+60 后 = " + b.get());   // 60
        b.updateAndGet(x -> Math.min(x + 60, 100));   // 60+60=120 → 被压到 100
        System.out.println("再+60 后 = " + b.get());   // 100（因为 min 了）

        // 3. 为什么不能"先 get 再 set"？—— 两步之间有人插队
        AtomicInteger c = new AtomicInteger(0);
        c.set(c.get() + 1);      // ✗ 危险：get 和 set 是【两个】动作，中间能被插队
        c.updateAndGet(x -> x + 1);  // ✓ 安全：整个"读-算-写"被 CAS 包成一个原子操作
        System.out.println("\nc = " + c.get());   // 2
    }
}
