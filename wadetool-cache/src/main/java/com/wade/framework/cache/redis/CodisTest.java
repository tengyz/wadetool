package com.wade.framework.cache.redis;

import java.util.Date;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;

public class CodisTest {
    
    public static void main(String[] args) {
        System.out.println("jedisPool");
        JedisResourcePool jedisPool = RoundRobinJedisPool.create()
                .curatorClient("10.236.0.84:2181,10.236.0.85:2181,10.236.0.86:2181", 3000)
                .zkProxyDir("/zk/codis/db_codis-demo2/proxy")
                .build();
        
        System.out.println("jedisPool2");
        final Jedis jedis = jedisPool.getResource();
        
        System.out.println("----程序开始运行----");
        Date date1 = new Date();
        
        for (int i = 0; i < 10; i++) {
            new Runnable() {
                public void run() {
                    try {
                        //发送操作
                        jedis.set("foo", "bar");
                        //                        String value = jedis.get("foo");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.run();
        }
        
        Date date2 = new Date();
        System.out.println("----程序结束运行----，程序运行时间【" + (date2.getTime() - date1.getTime()) + "毫秒】");
    }
    
}
