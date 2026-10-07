package org.dromara.jvm;

// 无限递归 → 撑爆"线程私有的栈" → StackOverflowError
public class StackOverflowDemo {

    static int depth = 0;

    static void recurse() {
        depth++;
        recurse();          // 自己调自己，永不返回
    }

    public static void main(String[] args) {
        try {
            recurse();
        } catch (StackOverflowError e) {
            System.out.println("炸了！递归深度大约: " + depth);
            System.out.println("异常类型: " + e.getClass().getName());
        }
    }
}
