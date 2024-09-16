package com.wade.framework.cache.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.SafeEncoder;

/**
 * redis工具类
 * @Description redis工具类 
 * @ClassName   RedisFactory 
 * @Date        2018年4月19日 上午1:18:15 
 * @Author      yz.teng
 */
public class RedisFactorySentinel {
    private static final Logger log = LogManager.getLogger(RedisFactorySentinel.class);
    
    private static JedisSentinelPool redisSentinelJedisPool = null;
    
    public static Properties getJedisProperties() {
        Properties config = new Properties();
        InputStream is = null;
        try {
            is = RedisFactorySentinel.class.getClassLoader().getResourceAsStream("application.properties");
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
    
    static {
        createJedisPool();
    }
    
    /**
     * 创建连接池
     *
     */
    private static void createJedisPool() {
        Properties getProps = getJedisProperties();
        Properties props = getProviderProperties(getProps);
        Integer timeout = getProperty(props, "timeout", 2000);
        Integer database = getProperty(props, "database", 1);
        String mymaster = getProperty(props, "master", "mymaster");
        String password = getProperty(props, "password", null);
        JedisPoolConfig jpc = new JedisPoolConfig();
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        long minEvictableIdleTimeMillis = getProperty(props, "minEvictableIdleTimeMillis", 1000);
        jpc.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        long timeBetweenEvictionRunsMillis = getProperty(props, "timeBetweenEvictionRunsMillis", 10);
        jpc.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        int numTestsPerEvictionRun = getProperty(props, "numTestsPerEvictionRun", 10);
        jpc.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        //是否启用后进先出, 默认true
        jpc.setLifo(getProperty(props, "lifo", false));
        //设置最大连接数
        jpc.setMaxTotal(getProperty(props, "maxTotal", 500));
        //设置最大空闲数
        jpc.setMaxIdle(getProperty(props, "maxIdle", 200));
        //设置最小空闲数
        jpc.setMinIdle(getProperty(props, "minIdle", 20));
        //设置最大阻塞时间，记住是毫秒数milliseconds
        jpc.setMaxWaitMillis(getProperty(props, "maxWaitMillis", 3000));
        //Idle时进行连接扫描
        jpc.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
        //在获取连接的时候检查有效性, 默认false
        jpc.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
        //在空闲时检查有效性, 默认false
        jpc.setTestOnReturn(getProperty(props, "testOnReturn", false));
        jpc.setTestOnCreate(getProperty(props, "testOnCreate", false));
        
        String address = getProperty(props, "address", "127.0.0.1:6379");
        String[] arr = address.split(",");
        if (arr != null && arr.length > 1) {
            Set<String> set = new HashSet<String>();
            for (String server : arr) {
                log.info("server=：" + server + "\r\n");
                set.add(server);
            }
            if (mymaster != null && timeout != null && password != null && database != null && database != -1) {
                redisSentinelJedisPool = new JedisSentinelPool(mymaster, set, jpc, timeout, password, database);
                log.info("1,Redis连接初始化成功！！！");
            }
            else if (mymaster != null && timeout != null && password != null) {
                redisSentinelJedisPool = new JedisSentinelPool(mymaster, set, jpc, timeout, password);
                log.info("2,Redis连接初始化成功！！！");
            }
            else {
                redisSentinelJedisPool = new JedisSentinelPool(mymaster, set, jpc, timeout);
                log.info("3,Redis连接初始化成功！！！");
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Redis连接初始化成功！！！");
        }
    }
    
    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (redisSentinelJedisPool == null){
            createJedisPool();
        }
    }
    
    /**
     * 获取Jedis实例
     * @return 返回Jedis实例
     */
    public static Jedis getJedis() {
        try {
            if (redisSentinelJedisPool != null) {
                return redisSentinelJedisPool.getResource();
            }
            else {
                poolInit();
                return redisSentinelJedisPool.getResource();
            }
        }
        catch (Exception e) {
            log.error("getResource error");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 释放资源
     * @param jedis
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    private static void close(Jedis jedis) {
        if (jedis != null) {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    /**
     * 释放资源
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    public static void close() {
        if (redisSentinelJedisPool != null) {
            redisSentinelJedisPool.close();
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
    
    private static byte[] keyToBytes(String key) {
        return RedisSafeEncoder.encode(key);
    }
    
    private static byte[] valueToBytes(Object object) {
        return com.wade.framework.cache.redis.Serialize.serialize(object);
    }
    
    private static <T> T valueFromBytes(byte[] bytes) {
        if (bytes != null) {
            return com.wade.framework.cache.redis.Serialize.deserialize(bytes);
        }
        return null;
    }
    
    public static void set(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(keyToBytes(key), valueToBytes(value));
        }
        catch (Exception e) {
            log.error("set error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }

    public static void setObject(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.set(keyToBytes(key), valueToBytes(value));
        }
        catch (Exception e) {
            log.error("set error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void setex(String key, int seconds, Object value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (value == null) {
                jedis.del(keyToBytes(key));
            }
            jedis.setex(keyToBytes(key), seconds, valueToBytes(value));
        }
        catch (Exception e) {
            log.error("setex error:" + jedis + e.fillInStackTrace());
        }
        finally {
            close(jedis);
        }
    }

    public static void setexObject(String key, int seconds, Object value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (value == null) {
                jedis.del(keyToBytes(key));
            }
            jedis.setex(keyToBytes(key), seconds, valueToBytes(value));
        }
        catch (Exception e) {
            log.error("setex error:" + jedis + e.fillInStackTrace());
        }
        finally {
            close(jedis);
        }
    }

    
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Jedis jedis = null;
        T value = null;
        try {
            jedis = getJedis();
            value = (T)valueFromBytes(jedis.get(keyToBytes(key)));
        }
        catch (Exception e) {
            log.error("get error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
        return value;
    }

    public static Object getObject(String key) {
        Jedis jedis = null;
        Object value = null;
        try {
            jedis = getJedis();
            value = (Object)valueFromBytes(jedis.get(keyToBytes(key)));
        }
        catch (Exception e) {
            log.error("getObject error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
        return value;
    }
    
    public static void del(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(keyToBytes(key));
        }
        catch (Exception e) {
            log.error("del error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void expire(String key, int timeOut) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.expire(keyToBytes(key), timeOut);
        }
        catch (Exception e) {
            log.error("expire error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void decr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.decr(keyToBytes(key));
        }
        catch (Exception e) {
            log.error("decr error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void incr(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.incr(keyToBytes(key));
        }
        catch (Exception e) {
            log.error("incr error" + jedis + e.fillInStackTrace());
            e.printStackTrace();
        }
        finally {
            close(jedis);
        }
    }
    
    public static void main(String[] args) {
        System.out.println(keyToBytes("aaaa"));
        System.out.println(valueToBytes("bbbb"));
    }
    
}
