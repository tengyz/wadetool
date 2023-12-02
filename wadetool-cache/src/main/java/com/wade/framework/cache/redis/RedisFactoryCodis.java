package com.wade.framework.cache.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

/**
 * Codis redis工具类
 * @Description Codis redis工具类 
 * @ClassName   RedisFactoryCodis 
 * @Date        2018年4月19日 上午1:18:15 
 * @Author      yz.teng
 */
public class RedisFactoryCodis {
    private static final Logger log = LogManager.getLogger(RedisFactoryCodis.class);
    
    private static JedisPool jedisPool = null;
    
    /**
     * 初始化锁
     */
    private static ReentrantLock INSTANCE_INIT_LOCL = new ReentrantLock(false);
    
    public static Properties getJedisProperties() {
        Properties config = new Properties();
        InputStream is = null;
        try {
            is = RedisFactoryCodis.class.getClassLoader().getResourceAsStream("application.properties");
            config.load(is);
        }
        catch (IOException e) {
            log.error("读取文件失败application.properties", e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    log.error("读取文件失败application.properties", e);
                }
            }
        }
        return config;
    }
    
    public static void init() {
        getInstance();
    }
    
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
    
    private static JedisPool getInstance() {
        if (jedisPool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        if (jedisPool == null) {
                            log.info("开始getInstance连接初始化！！！");
                            Properties getProps = getJedisProperties();
                            Properties props = getProviderProperties(getProps);
                            Integer timeout = getProperty(props, "timeout", 2000);
                            Integer database = getProperty(props, "database", 1);
                            String password = getProperty(props, "password", null);
                            String clientName = getProperty(props, "clientName", null);
                            JedisPoolConfig poolConfig = new JedisPoolConfig();
                            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
                            long minEvictableIdleTimeMillis = getProperty(props, "minEvictableIdleTimeMillis", 1000);
                            poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
                            //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
                            long timeBetweenEvictionRunsMillis = getProperty(props, "timeBetweenEvictionRunsMillis", 10);
                            poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
                            //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
                            int numTestsPerEvictionRun = getProperty(props, "numTestsPerEvictionRun", 10);
                            poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
                            //是否启用后进先出, 默认true
                            poolConfig.setLifo(getProperty(props, "lifo", false));
                            //设置最大连接数
                            poolConfig.setMaxTotal(getProperty(props, "maxTotal", 500));
                            //设置最大空闲数
                            poolConfig.setMaxIdle(getProperty(props, "maxIdle", 200));
                            //设置最小空闲数
                            poolConfig.setMinIdle(getProperty(props, "minIdle", 20));
                            //设置最大阻塞时间，记住是毫秒数milliseconds
                            poolConfig.setMaxWaitMillis(getProperty(props, "maxWaitMillis", 3000));
                            //Idle时进行连接扫描
                            poolConfig.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
                            //在获取连接的时候检查有效性, 默认false
                            poolConfig.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
                            //在空闲时检查有效性, 默认false
                            poolConfig.setTestOnReturn(getProperty(props, "testOnReturn", false));
                            String address = getProperty(props, "address", "127.0.0.1:6379");
                            String[] arr = address.split(",");
                            if (arr != null && arr.length > 1) {
                                log.error("redis初始化address格式不正确!!!,只能填写一个ip");
                            }
                            else {
                                String[] server = arr[0].split(":");
                                log.info(">>>>>>>>>>> JedisPool init server=:" + server);
                                String host = server[0];
                                Integer port = Integer.parseInt(server[1]);
                                if (port != null && timeout != null && password != null && database != null && database != -1 && clientName != null)
                                    jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database, clientName);
                                else if (port != null && timeout != null && password != null && database != null && database != -1)
                                    jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
                                else if (port != null && timeout != null && password != null)
                                    jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
                                else if (port != null && timeout != null)
                                    jedisPool = new JedisPool(poolConfig, host, port, timeout);
                                else
                                    jedisPool = new JedisPool(poolConfig, host, port);
                            }
                            if (log.isInfoEnabled()) {
                                log.info("Redis连接初始化成功！！！");
                            }
                            if (jedisPool == null) {
                                throw new NullPointerException(">>>>>>>>>>> jedisPool is null.");
                            }
                        }
                    }
                    finally {
                        INSTANCE_INIT_LOCL.unlock();
                    }
                }
            }
            catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (jedisPool == null) {
            throw new NullPointerException(">>>>>>>>>>> jedisCodis jedisPool is null.");
        }
        
        return jedisPool;
        
    }
    
    /**
     * 释放资源
     * @param jedis
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    private static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
    /**
     * 释放资源
     * @param jedis
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    public static void close() {
        if (jedisPool != null) {
            try {
                jedisPool.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private final static Properties getProviderProperties(Properties props) {
        Properties new_props = new Properties();
        Enumeration<Object> keys = props.keys();
        String prefix = "redis.";
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith(prefix)) {
                new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
            }
        }
        return new_props;
    }
    
    private static String getProperty(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key, defaultValue);
        return value == null ? value : value.trim();
    }
    
    private static int getProperty(Properties props, String key, int defaultValue) {
        try {
            String value = props.getProperty(key);
            return value == null ? defaultValue : Integer.parseInt(value.trim());
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    private static boolean getProperty(Properties props, String key, boolean defaultValue) {
        return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
    }
    
    // ------------------------ serialize and unserialize ------------------------
    private static byte[] keyToBytes(String key) {
        return SafeEncoder.encode(key);
    }
    
    private static byte[] valueToBytes(Object object) {
        return com.wade.framework.cache.redis.Serialize.serialize(object);
    }
    
    private static <T> T valueFromBytes(byte[] bytes) {
        if (bytes != null)
            return com.wade.framework.cache.redis.Serialize.deserialize(bytes);
        return null;
    }
    
    // ------------------------ jedis util ------------------------
    /**
     * 存储简单的字符串或者是Object 因为jedis没有分装直接存储Object的方法，所以在存储对象需斟酌下
     * 存储对象的字段是不是非常多而且是不是每个字段都用到，如果是的话那建议直接存储对象，
     * 否则建议用集合的方式存储，因为redis可以针对集合进行日常的操作很方便而且还可以节省空间
     */
    public static void setObject(String key, Object value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(keyToBytes(key), valueToBytes(value));
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,setObject error:", e);
        }
        finally {
            close(jedis);
        }
    }
    
    public static void set(String key, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,set error:", e);
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void setexObject(String key, int seconds, Object value) {
        Jedis jedis = getJedis();
        try {
            if (value == null) {
                jedis.del(keyToBytes(key));
            }
            jedis.setex(keyToBytes(key), seconds, valueToBytes(value));
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,setexObject error:", e);
        }
        finally {
            close(jedis);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getObject(String key) {
        T value = null;
        Jedis jedis = getJedis();
        try {
            value = (T)valueFromBytes(jedis.get(keyToBytes(key)));
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,getObject error:", e);
        }
        finally {
            close(jedis);
        }
        return value;
    }
    
    /**
     * Get String
     * @param key
     * @return
     */
    public static String get(String key) {
        String value = null;
        Jedis jedis = getJedis();
        try {
            value = jedis.get(key);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,get error:", e);
        }
        finally {
            close(jedis);
        }
        return value;
    }
    
    public static void delObject(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(keyToBytes(key));
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,delObject error:", e);
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void del(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,del error:", e);
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void expireObject(String key, int timeOut) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(keyToBytes(key), timeOut);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,expireObject error:", e);
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void expire(String key, int timeOut) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, timeOut);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,expire error:", e);
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static Long decr(String key) {
        Long value = null;
        Jedis jedis = getJedis();
        try {
            value = jedis.decr(key);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,decr error:", e);
        }
        finally {
            close(jedis);
        }
        return value;
    }
    
    public static Long incr(String key) {
        Long value = null;
        Jedis jedis = getJedis();
        try {
            value = jedis.incr(key);
        }
        catch (Exception e) {
            log.error("RedisFactory redis异常,incr error:", e);
        }
        finally {
            close(jedis);
        }
        return value;
    }
    
    public static void main(String[] args) {
        System.out.println(keyToBytes("aaaa"));
        System.out.println(valueToBytes("bbbb"));
    }
    
}
