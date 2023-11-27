package com.wade.framework.cache.localcache;

import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 本地读写抽象类
 *
 * @Description 本地读写抽象类
 * @ClassName AbstractReadOnlyCache
 * @Date 2015年11月28日 上午11:52:58
 * @Author yz.teng
 */
public abstract class AbstractReadOnlyCache implements IReadOnlyCache {
    private static final Logger log = LogManager.getLogger(AbstractReadOnlyCache.class);

    private Map<String, Object> cache;

    private String className;

    private LinkedHashMap<Long, Integer> refreshHistory = new LinkedHashMap();

    public AbstractReadOnlyCache() {
    }

    public AbstractReadOnlyCache(Map<String, Object> map) {
        this.cache = map;
        updateRefreshHistory();
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public synchronized void refresh() throws Exception {
        log.info("只读缓存刷新! className:" + getClassName() + "=======start");
        Map newCache = loadData();
        Map oldCache = this.cache;
        this.cache = newCache;
        updateRefreshHistory();
        if (null != oldCache) {
            oldCache.clear();
            oldCache = null;
        }
        log.info("只读缓存刷新! className:" + getClassName() + "=======end");
    }

    public Object get(String key) {
        return this.cache.get(key);
    }


    public int size() {
        return this.cache.size();
    }

    
    public Set<String> keySet() {
        return this.cache.keySet();
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

    public abstract Map<String, Object> loadData() throws Exception;
}