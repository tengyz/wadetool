package com.wade.framework.cache.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

/**
 * redis工具类(用于session共享缓存)从节点
 * @Description redis工具类(用于session共享缓存)
 * @ClassName   RedisFactorySessionSlave1 
 * @Date        2018年4月19日 上午1:18:15 
 * @Author      yz.teng
 */
public class RedisFactorySessionSlave1 {
    private static final Logger log = LogManager.getLogger(RedisFactorySessionSlave1.class);
    
    private static JedisResourcePool jedisPool = null;
    
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
    
    /**
     * 获取Jedis实例
     * @return 返回Jedis实例
     */
    public static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            }
            else {
                init();
                return jedisPool.getResource();
            }
        }
        catch (Exception e) {
            log.error("getResource error");
            e.printStackTrace();
            return null;
        }
    }
    
    private static JedisResourcePool getInstance() {
        if (jedisPool == null) {
            try {
                if (INSTANCE_INIT_LOCL.tryLock(2, TimeUnit.SECONDS)) {
                    try {
                        if (jedisPool == null) {
                            log.info("开始getInstance连接初始化！！！");
                            Properties getProps = getJedisProperties();
                            Properties props = getProviderProperties(getProps);
                            Integer timeout = getProperty(props, "timeout", 2000);
                            String address = getProperty(props, "address", "127.0.0.1:6379");
                            String zkProxyDir = getProperty(props, "zkproxydir", null);
                            String zkProxyAuth = getProperty(props, "zkproxyauth", null);
                            String password = getProperty(props, "password", null);
                            
                            //账号和密码
                            if (password != null && zkProxyAuth != null) {
                                RetryPolicy retryPolicy = new RetryNTimes(Integer.MAX_VALUE, 2000);
                                String scheme = "digest";
                                byte[] auth = zkProxyAuth.getBytes();
                                CuratorFramework clientinit = CuratorFrameworkFactory.builder()
                                        .authorization(scheme, auth)
                                        .connectString(address)
                                        .retryPolicy(retryPolicy)
                                        .sessionTimeoutMs(2000)
                                        .build();
                                jedisPool = RoundRobinJedisPool.create()
                                        .curatorClient(clientinit, true)
                                        .zkProxyDir(zkProxyDir)
                                        .password(password)
                                        .build();
                            }
                            else {
                                jedisPool = RoundRobinJedisPool.create().curatorClient(address, timeout).zkProxyDir(zkProxyDir).build();
                            }
                            if (log.isInfoEnabled()) {
                                log.info("JedisResourcePool连接初始化成功！！！");
                            }
                            if (jedisPool == null) {
                                throw new NullPointerException(">>>>>>>>>>> JedisResourcePool is null.");
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
            throw new NullPointerException(">>>>>>>>>>> jedisCodis JedisResourcePool is null.");
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
        String prefix = "redis.session.slave.";
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
            log.error("RedisFactorySessionSlave1 redis异常,setObject error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,set error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,setexObject error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,getObject error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,get error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,delObject error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,del error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,expireObject error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,expire error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,decr error:", e);
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
            log.error("RedisFactorySessionSlave1 redis异常,incr error:", e);
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
