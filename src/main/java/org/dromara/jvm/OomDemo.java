package org.dromara.jvm;

import java.util.ArrayList;
import java.util.List;

// 不停往堆里塞对象 → 撑爆"所有线程共享的堆" → OutOfMemoryError
public class OomDemo {

    public static void main(String[] args) {
        List<byte[]> list = new ArrayList<>();
        int count = 0;
        try {
            while (true) {
                list.add(new byte[1024 * 1024]);   // 每次塞 1MB
                count++;
            }
        } catch (OutOfMemoryError e) {
            list.clear();   // 先释放堆，才有空间去拼字符串打印
            System.out.println("堆撑爆了！一共塞了 " + count + " MB");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
        }
    }
}
