package com.lock.zookeeper.service;

import java.util.concurrent.CountDownLatch;

public class LockTest {


    public static void main(String[] args) {
        int NUM = 10;
        // 按照线程数初始化倒计数器,倒计数器
         CountDownLatch cdl = new CountDownLatch(NUM);
        for (int i = 1; i <= NUM; i++) {
            // 按照线程数迭代实例化线程
            new Thread(new UserLockService()).start();
            // 创建一个线程，倒计数器减1
            cdl.countDown();
        }
    }
}
