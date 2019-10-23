package com.lock.zookeeper.service;

import com.lock.zookeeper.lock.ZookeeperLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class UserLockService  implements  Runnable{

    private Logger logger = LoggerFactory.getLogger(UserLockService.class);

    // 自增长序列
    private static int i = 0;
    // 同时并发的线程数
    private static final int NUM = 10;
    // 按照线程数初始化倒计数器,倒计数器
    private static CountDownLatch cdl = new CountDownLatch(NUM);
    private Lock lock = new ZookeeperLock();


    @Override
    public void run() {
        try {
            // 等待其他线程初始化
            cdl.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 创建订单
        demo();
    }

    public  void  demo(){
        String code = null;
        //准备获取锁
        lock.lock();
        try {
            code = getCode();
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            //完成业务逻辑以后释放锁
            lock.unlock();
        }
        // ……do something
        logger.info("insert into DB使用id：=======================>" + code);
    }


    // 按照“年-月-日-小时-分钟-秒-自增长序列”的规则生成订单编号
    public String getCode() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(now) + ++i;
    }

    public static void main(String[] args) {
        // 按照线程数初始化倒计数器,倒计数器
        for (int i = 1; i <= NUM; i++) {
            // 按照线程数迭代实例化线程
            new Thread(new UserLockService()).start();
            // 创建一个线程，倒计数器减1
            cdl.countDown();
        }
    }
}
