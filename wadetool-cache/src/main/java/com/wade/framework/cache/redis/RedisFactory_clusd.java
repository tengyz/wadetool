package com.wade.framework.cache.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

/**
 * redis工具类
 * @Description redis工具类 
 * @ClassName   RedisFactory 
 * @Date        2018年4月19日 上午1:18:15 
 * @Author      yz.teng
 */
public class RedisFactory_clusd {
    private static final Logger log = LogManager.getLogger(RedisFactory_clusd.class);
    
    private static JedisPool jedisPool = null;
    
    private static JedisCluster jedisCluster = null;
    
    public static Properties getJedisProperties() {
        Properties config = new Properties();
        InputStream is = null;
        try {
            is = RedisFactory_clusd.class.getClassLoader().getResourceAsStream("application.properties");
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
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setLifo(getProperty(props, "lifo", false));
        jpc.setMaxTotal(getProperty(props, "maxTotal", 500));
        jpc.setMinIdle(getProperty(props, "minIdle", 20));
        jpc.setMaxIdle(getProperty(props, "maxIdle", 200));
        jpc.setMaxWaitMillis(getProperty(props, "maxWaitMillis", 3000));
        jpc.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
        Integer maxRedirections = getProperty(props, "maxRedirections", 20);
        String address = getProperty(props, "address", "127.0.0.1:6379");
        
        String[] arr = address.split(",");
        if (arr != null && arr.length > 1) {
            Set<HostAndPort> set = new HashSet<HostAndPort>();
            for (String server : arr) {
                String[] tmp = server.split(":");
                if (tmp.length >= 2) {
                    set.add(new HostAndPort(tmp[0], Integer.parseInt(tmp[1])));
                }
            }
            jedisCluster = new JedisCluster(set, timeout, maxRedirections, jpc);
            log.info("BinaryJedisCluster连接初始化！！！");
        }
        
        //        // 数据库链接池配置
        //        JedisPoolConfig config = new JedisPoolConfig();
        //        config.setMaxTotal(1000);
        //        config.setMaxIdle(50);
        //        config.setMinIdle(20);
        //        config.setMaxWaitMillis(6 * 1000);
        //        config.setTestOnBorrow(true);
        //        // Redis集群的节点集合
        //        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7000));
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7001));
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7002));
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7003));
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7004));
        //        jedisClusterNodes.add(new HostAndPort("101.114.111.123", 7005));
        //        // 节点，超时时间，最多重定向次数，链接池
        //        jedisCluster = new JedisCluster(jedisClusterNodes, 2000, 100, config);
        if (log.isInfoEnabled()) {
            log.info("Redis连接初始化成功！！！");
        }
    }
    
    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (jedisCluster == null)
            createJedisPool();
    }
    
    /**
     * 获取Jedis实例
     * @return 返回Jedis实例
     */
    public static JedisCluster getJedis() {
        try {
            if (jedisCluster != null) {
                return jedisCluster;
            }
            else {
                poolInit();
                return jedisCluster;
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
     * @throws IOException 
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    private static void close(BinaryJedisCluster jedis) {
        if (jedis != null) {
            try {
                //                jedis.close();
            }
            catch (Exception e) {
                log.error("jedis.close" + jedis + e.fillInStackTrace());
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * 释放资源
     * @param jedis
     * @throws IOException 
     * @Date        2018年5月12日 下午7:30:10 
     * @Author      yz.teng
     */
    public static void close() throws IOException {
        if (jedisCluster != null) {
            //jedisCluster.close();
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
    
    public static void set(String key, Object value) {
        JedisCluster jedis = null;
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
        JedisCluster jedis = null;
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
        JedisCluster jedis = null;
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
    
    public static void del(String key) {
        JedisCluster jedis = null;
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
        JedisCluster jedis = null;
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
        JedisCluster jedis = null;
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
        JedisCluster jedis = null;
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
