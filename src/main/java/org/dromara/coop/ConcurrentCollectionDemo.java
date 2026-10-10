package org.dromara.coop;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// 对比：普通集合 vs 并发集合，在"遍历时修改"下的表现
public class ConcurrentCollectionDemo {
    public static void main(String[] args) {
        // ① LinkedList：遍历时删除 → 炸
        List<Integer> linked = new LinkedList<>(List.of(1, 2, 3, 4, 5, 6));
        try {
            for (int x : linked) {
                if (x % 2 == 0) {
                    linked.remove(Integer.valueOf(x));   // 遍历的同时改集合
                }
            }
            System.out.println("LinkedList: 没报错，剩下 " + linked);
        } catch (Exception e) {
            System.out.println("LinkedList: 抛了 " + e.getClass().getSimpleName() + " ← 就是你踩的那个");
        }

        // ② CopyOnWriteArrayList：同样的操作，不炸
        List<Integer> cow = new CopyOnWriteArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        for (int x : cow) {
            if (x % 2 == 0) {
                cow.remove(Integer.valueOf(x));
            }
        }
        System.out.println("CopyOnWriteArrayList: 没报错，剩下 " + cow);
    }
}
