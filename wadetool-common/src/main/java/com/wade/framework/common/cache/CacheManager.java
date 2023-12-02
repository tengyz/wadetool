package com.wade.framework.common.cache;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.AbstractReadOnlyCache;
import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.cache.util.ICache;
import com.wade.framework.common.cache.impl.LocalCache;
import com.wade.framework.common.cache.impl.RedisCache;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 加载配置文件相应的缓存
 * @Description 加载配置文件相应的缓存 
 * @ClassName   CacheManager 
 * @Date        2018年11月26日 下午4:03:44 
 * @Author      yz.teng
 */
public class CacheManager {
    private static Logger log = LogManager.getLogger(CacheManager.class);
    
    private static Map<String, ICache> cacheMap = new ConcurrentHashMap<String, ICache>(20);
    
    private static Class<?>[] cacheClasses = {LocalCache.class, RedisCache.class};
    
    private static Lock lock = new ReentrantLock();
    
    public static <T extends AbstractReadOnlyCache> T getReadOnlyCache(Class<T> clazz) throws Exception {
        AbstractReadOnlyCache t = (AbstractReadOnlyCache)getBaseReadOnlyCache(clazz);
        if (t == null) {
            throw new Exception("未配置只读缓存对象:" + clazz);
        }
        return (T)t;
    }
    
    public static <T> T getBaseReadOnlyCache(Class<T> clazz) throws Exception {
        return (T)CacheFactory.getReadOnlyCache(clazz);
    }
    
    public static ICache getCache(String cacheName) {
        log.debug("CacheManager cacheName=:" + cacheName);
        log.debug("CacheManager cacheMap=:" + cacheMap);
        try {
            ICache cache = (ICache)cacheMap.get(cacheName);
            if (cache == null)
                try {
                    lock.lock();
                    cache = (ICache)cacheMap.get(cacheName);
                    if (cache != null) {
                        return cache;
                    }
                    for (int i = 0; i < cacheClasses.length; i++) {
                        Constructor constructor = cacheClasses[i].getConstructor(new Class[] {String.class});
                        cache = (ICache)constructor.newInstance(new Object[] {cacheName});
                        if (cache.isValid()) {
                            break;
                        }
                    }
                    if (!cache.isValid()) {
                        log.error("没有配置对应的缓存参数cacheName=:" + cacheName);
                        Thrower.throwException(BizExceptionEnum.ERROR_MSG, "没有配置对应的缓存参数cacheName=:" + cacheName);
                    }
                    else {
                        cacheMap.put(cacheName, cache);
                        if (log.isDebugEnabled()) {
                            log.debug("CacheManager cacheName=:" + cacheName);
                            log.debug("CacheManager cacheMap.put=:" + cacheMap);
                        }
                        return cache;
                    }
                }
                finally {
                    lock.unlock();
                }
            else
                return cache;
        }
        catch (Exception e) {
            log.error("获取缓存参数发生错误", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "获取缓存参数发生错误");
        }
        return null;
    }
    
    public static <K, V> Map<K, V> getStaticMap(String key, int initCapacity) {
        Map map = null;
        if (initCapacity <= 0)
            map = new ConcurrentHashMap();
        else
            map = new ConcurrentHashMap(initCapacity);
        return map;
    }
    
    public static <K, V> Map<K, V> getStaticMap(String key) {
        return getStaticMap(key, -1);
    }
}
