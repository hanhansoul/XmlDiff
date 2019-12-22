package lcs;

import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by Administrator on 2019/4/22 0022.
 */
public class StringDiff {
    public static int show() {
        int b = 100;
        try {
            b = 1000;
//            int a = 8 / 0;
            return b;
        } finally {
            System.out.println("finally模块被执行");
            b = 10;
//            return b;
        }
    }

    public static void test1(){
        List<String> list = new ArrayList<>();
//        List<Object> l = list;
    }
    public static void main(String args[]) {
        System.out.println(show());
    }
}
