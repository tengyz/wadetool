package com.wade.framework.cache.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 本地缓存工具类
 * 
 * @Description 本地缓存工具类
 * @ClassName CacheUtil
 * @Date 2016年1月22日 下午4:56:11
 * @Author yz.teng
 */
public class CacheUtil {
    private static final Logger log = LogManager.getLogger(CacheUtil.class);
    
    public static final int objectSize(Object obj) {
        int size = 0;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            size = bytes.length;
        }
        catch (IOException e) {
            log.error("计算对象所占内存大小发生错误！", e);
        }
        finally {
            try {
                oos.close();
            }
            catch (IOException e) {
                log.error("关闭ObjectOutputStream时发生错误！", e);
            }
        }
        return size;
    }
    
    public static <V> V get(ICache cache, String cacheKey, ICacheSourceProvider<V> provider) throws Exception {
        return get(cache, cacheKey, 0, provider);// 缓存一天时间
    }
    
    public static <V> V get(ICache cache, String cacheKey, int secTTL, ICacheSourceProvider<V> provider) throws Exception {
        Object obj = cache.get(cacheKey);
        if (obj != null) {
            if ("<-- IMPOSSIBLE_VALUE -->".equals(obj)) {
                return null;
            }
            return (V)obj;
        }
        Object v = null;
        try {
            cache.lock();// 加锁
            obj = cache.get(cacheKey);
            if (obj != null) {
                if (!"<-- IMPOSSIBLE_VALUE -->".equals(obj))
                    v = obj;
            }
            else if (provider != null) {
                v = provider.getSource();
                
                if (v != null)
                    cache.put(cacheKey, v, secTTL);
                else
                    cache.put(cacheKey, "<-- IMPOSSIBLE_VALUE -->", secTTL);
            }
        }
        finally {
            cache.unlock();// 释放锁
        }
        return (V)v;
    }
    
    public static <K, V> V get(Map<K, V> cache, K cacheKey, ICacheSourceProvider<V> provider) throws Exception {
        Object value = null;
        if ((cache != null) && (cache.containsKey(cacheKey))) {
            value = cache.get(cacheKey);
        }
        else if (provider != null) {
            value = provider.getSource();
            
            if ((cache != null) && (value != null)) {
                cache.put(cacheKey, (V)value);
            }
        }
        return (V)value;
    }
    
}
