package com.wade.framework.common.cache.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.cache.localcache.interfaces.IReadWriteCache;
import com.wade.framework.cache.util.ICache;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 本地读写缓存
 * @author yz.teng
 *
 */
public class LocalCache extends AbstractCache implements ICache {
    private static final Logger log = LogManager.getLogger(LocalCache.class);
    
    private IReadWriteCache cache = null;
    
    public LocalCache(String cacheName) {
        this.cacheName = cacheName;
        this.cache = CacheFactory.getReadWriteCache(cacheName);
        this.valid = (this.cache != null);
    }
    
    public void refresh() {
        this.cache.refresh();
    }
    
    private boolean containsKey(String cacheKey) {
        return cache.containsKey(cacheKey);
    }
    
    @Override
    public Object get(String cacheKey) {
        try {
            if (isEmpty() || !containsKey(cacheKey))
                return null;
            else
                return cache.get(cacheKey);
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e);
            return null;
        }
    }
    
    private boolean isEmpty() {
        return cache.isEmpty();
    }
    
    @Override
    public boolean put(String cacheKey, Object value, int secTTL) {
        try {
            cache.put(cacheKey, value);
            return true;
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e);
            return false;
        }
    }
    
    @Override
    public boolean remove(String cacheKey) {
        cache.remove(cacheKey);
        return true;
    }
    
}
