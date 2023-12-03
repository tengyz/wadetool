package com.wade.framework.common.cache;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * @author yizuteng
 */
public class CacheUtil {
    private static Logger log = LogManager.getLogger(CacheUtil.class);
    
    public static <V> V get(ICache cache, String cacheKey, ICacheSourceProvider<V> provider) {
        return get(cache, cacheKey, 0, provider);
    }
    
    public static <V> V get(ICache cache, String cacheKey, int secTTL, ICacheSourceProvider<V> provider) {
        V value = null;
        long s = System.nanoTime();
        boolean hasCache = true;
        try {
            @SuppressWarnings("unchecked")
            V obj = cache == null ? null : (V)cache.get(cacheKey);
            value = obj;
            if (value == null) {
                hasCache = false;
                if (provider != null) {
                    value = provider.getSource();
                    
                    if (cache != null) {
                        cache.put(cacheKey, value, secTTL);
                    }
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.WADE_COMP_CACHE_LOAD, e);
        }
        log.debug("hit cache from cache[",
                cache,
                "] with key:",
                cacheKey,
                "value:",
                value,
                "use Time: ",
                System.nanoTime() - s,
                "cached:",
                hasCache,
                "secTTL:",
                secTTL);
        
        return value;
    }
    
    public static <K, V> V get(Map<K, V> cache, K cacheKey, ICacheSourceProvider<V> provider) {
        V value = null;
        try {
            if (cache != null && cache.containsKey(cacheKey)) {
                return (V)cache.get(cacheKey);
            }
            else {
                if (provider == null) {
                    return null;
                }
                
                value = provider.getSource();
                
                if (cache != null && value != null) {
                    cache.put(cacheKey, value);
                }
                return value;
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.WADE_COMP_CACHE_LOAD, e);
        }
        return value;
    }
    
}
