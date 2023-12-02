package com.wade.framework.cache.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JavaFuture {
    public static void main(String[] args) throws Throwable, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Callable c = new MyCallable(1 + "");
        // 执行任务并获取Future对象
        Future f = pool.submit(c);
        // 关闭线程池
        pool.shutdown();
        System.out.println("main thread is blocked。。。。。");
    }
    
}

class MyCallable implements Callable<Object> {
    private String name;
    
    MyCallable(String name) {
        this.name = name;
    }
    
    public Object call() throws Exception {
        System.out.println("线程：" + name + "开始运行");
        long time1 = System.currentTimeMillis();
        Thread.sleep(1000);
        long time2 = System.currentTimeMillis();
        System.out.println("线程：" + name + "运行耗时【" + (time2 - time1) + "】");
        return "线程：" + name + "运行耗时【" + (time2 - time1) + "】";
    }
}