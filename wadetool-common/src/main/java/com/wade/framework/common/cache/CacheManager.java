package com.wade.framework.common.cache;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.common.cache.impl.LocalCache;
import com.wade.framework.common.cache.impl.RedisCache;
import com.wade.framework.common.cache.refresh.CacheContainer;

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
    
    @SuppressWarnings("unchecked")
    public static <T extends BaseReadOnlyCache> T getReadOnlyCache(Class<T> clazz) throws Exception {
        T t = (T)CacheFactory.getReadOnlyCache(clazz);
        CacheContainer.registerCache(clazz.getName(), t);
        return t;
    }
    
    public static ICache getCache(final String cacheName) {
        ICache cache = CacheUtil.get(cacheMap, cacheName, new ICacheSourceProvider<ICache>() {
            
            @Override
            public ICache getSource() throws Exception {
                ICache cache = null;
                for (int i = 0; i < cacheClasses.length; i++) {
                    Constructor<?> constructor = cacheClasses[i].getConstructor(String.class);
                    cache = (ICache)constructor.newInstance(cacheName);
                    if (cache.isValid()) {
                        break;
                    }
                }
                
                if (!cache.isValid()) {
                    log.info("获取缓存对象【", cacheName, "】失败，请确认是否有缓存配置!");
                }
                else {
                    CacheContainer.registerCache(cacheName, cache);
                    log.debug("获取缓存对象【", cacheName, "】成功，实例化缓存类", cache.getClass());
                }
                return cache;
            }
        });
        if (cache != null && cache.isValid()) {
            log.debug("获取缓存对象【", cacheName, "】成功:", (cache != null ? cache.getClass() : null));
            return cache;
        }
        return null;
        
    }
    
    public static <K, V> Map<K, V> getStaticMap(String key) {
        Map<K, V> map = new ConcurrentHashMap<K, V>();
        CacheContainer.registerCache(key, map);
        return map;
    }
}
