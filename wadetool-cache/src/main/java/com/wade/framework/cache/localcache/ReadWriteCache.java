package com.wade.framework.cache.localcache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.interfaces.IReadWriteCache;

/**
 * 本地读写缓存
 * 
 * @Description 本地读写缓存
 * @ClassName ReadWriteCache
 * @Date 2015年11月28日 上午11:39:42
 * @Author yz.teng
 */
public class ReadWriteCache implements IReadWriteCache {
    private static final Logger log = LogManager.getLogger(ReadWriteCache.class);
    
    private LinkedHashMap<Long, Integer> refreshHistory = new LinkedHashMap();
    
    private ConcurrentLRUMap<String, Object> cache;
    
    private String name;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ReadWriteCache(int maxSize) {
        this.cache = new ConcurrentLRUMap(maxSize);
    }
    
    public void refresh() {
        log.info("读写缓存刷新! refresh start: " + getName());
        updateRefreshHistory();
        this.cache.clear();
        log.info("读写缓存刷新! refresh end: " + getName());
    }
    
    public Object get(String key) throws Exception {
        return this.cache.get(key);
    }
    
    public Object put(String key, Object value) throws Exception {
        return this.cache.put(key, value);
    }
    
    public int size() throws Exception {
        return this.cache.size();
    }
    
    public boolean containsKey(String key) {
        return this.cache.containsKey(key);
    }
    
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    public Set<String> keySet() {
        return this.cache.keySet();
    }
    
    public Object remove(String key) {
        return this.cache.remove(key);
    }
    
    private final void updateRefreshHistory() {
        this.refreshHistory.put(Long.valueOf(System.currentTimeMillis()), Integer.valueOf(this.cache.size()));
        if (this.refreshHistory.size() > 10) {
            Iterator i = this.refreshHistory.keySet().iterator();
            i.next();
            i.remove();
        }
    }
    
    public LinkedHashMap<Long, Integer> getRefreshHistory() {
        return this.refreshHistory;
    }
}