package org.dromara.jvm;

import java.util.ArrayList;
import java.util.List;

// 亲眼看见 GC：造垃圾 → 看堆占用 → 手动触发 GC → 看堆降下来
public class GcDemo {

    static void printUsed(String tag) {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        System.out.println(tag + " 已用堆内存: " + used + " MB");
    }

    public static void main(String[] args) throws InterruptedException {
        printUsed("【开始】");

        // 1. 造一批"短命垃圾"：循环里 new，每轮上一轮的 list 就没人要了
        for (int i = 0; i < 5; i++) {
            List<byte[]> garbage = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                garbage.add(new byte[1024 * 1024]);   // 每个 1MB
            }
            printUsed("第 " + (i + 1) + " 轮造完垃圾");
        }

        // 2. 手动"求"JVM 做一次 GC（只是建议，JVM 未必立刻听）
        System.gc();
        Thread.sleep(500);          // 给 GC 一点时间
        printUsed("【请求 GC 之后】");

        // 3. 保留一批"长命大对象"，看 GC 能不能收回它
        List<byte[]> alive = new ArrayList<>();
        for (int j = 0; j < 50; j++) {
            alive.add(new byte[1024 * 1024]);
        }
        printUsed("【持有 50MB 活对象】");
        System.gc();
        Thread.sleep(500);
        printUsed("【GC 后：活对象该留还是留】");
    }
}
