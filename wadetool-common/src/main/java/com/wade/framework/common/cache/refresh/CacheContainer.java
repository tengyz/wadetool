package com.wade.framework.common.cache.refresh;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.common.cache.ICache;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;

/**
 * 缓存刷新，提供给前台或者后端进行手动刷新
 * @author yz.teng
 */
public class CacheContainer {
    
    public static final Boolean IS_PRODUCTION = false;
    
    private static final Map<String, IReadOnlyCache> readOnlyCacheMap = new ConcurrentHashMap<String, IReadOnlyCache>();
    
    private static final Map<String, Map<?, ?>> jvmCacheMap = new ConcurrentHashMap<String, Map<?, ?>>();
    
    private static final Map<String, ICache> readWriteCacheMap = new ConcurrentHashMap<String, ICache>();
    
    private static final Map<String, CacheType> cacheTypeMap = new ConcurrentHashMap<String, CacheType>();
    
    private static enum CacheType {
        READ_ONLY, READ_WRITE, JVM_MAP
    }
    
    public static void registerCache(String key, IReadOnlyCache cache) {
        if (IS_PRODUCTION) {
            return;
        }
        if (readOnlyCacheMap.containsKey(key)) {
            return;
        }
        readOnlyCacheMap.put(key, cache);
        cacheTypeMap.put(key, CacheType.READ_ONLY);
    }
    
    public static void registerCache(String key, ICache cache) {
        if (IS_PRODUCTION) {
            return;
        }
        readWriteCacheMap.put(key, cache);
        cacheTypeMap.put(key, CacheType.READ_WRITE);
    }
    
    public static void registerCache(String key, Map<?, ?> cache) {
        if (IS_PRODUCTION) {
            return;
        }
        jvmCacheMap.put(key, cache);
        cacheTypeMap.put(key, CacheType.JVM_MAP);
    }
    
    public static void clearAll() throws Exception {
        if (IS_PRODUCTION) {
            return;
        }
        clearReadOnlyCache();
        clearReadWriteCache();
        clearJvmMapCache();
    }
    
    public static void clearReadOnlyCache() throws Exception {
        if (IS_PRODUCTION) {
            return;
        }
        for (Entry<String, IReadOnlyCache> entry : readOnlyCacheMap.entrySet()) {
            entry.getValue().refresh();
        }
    }
    
    public static void clearReadWriteCache() {
        if (IS_PRODUCTION) {
            return;
        }
        for (Entry<String, ICache> entry : readWriteCacheMap.entrySet()) {
            entry.getValue().refresh();
        }
    }
    
    public static void clearJvmMapCache() {
        if (IS_PRODUCTION) {
            return;
        }
        for (Entry<String, Map<?, ?>> entry : jvmCacheMap.entrySet()) {
            entry.getValue().clear();
        }
    }
    
    public static void clear(String[] keys) throws Exception {
        if (IS_PRODUCTION) {
            return;
        }
        for (String key : keys) {
            if ("ALL".equals(key)) {
                clearAll();
                return;
            }
            clear(key);
        }
    }
    
    public static void clear(String key) throws Exception {
        if (IS_PRODUCTION) {
            return;
        }
        CacheType ct = cacheTypeMap.get(key);
        if (ct == null) {
            return;
        }
        switch (ct) {
            case READ_ONLY:
                readOnlyCacheMap.get(key).refresh();
                break;
            case READ_WRITE:
                readWriteCacheMap.get(key).refresh();
                break;
            case JVM_MAP:
                jvmCacheMap.get(key).clear();
                break;
        }
    }
    
    private static void registerSystemCache() {
        if (IS_PRODUCTION) {
            return;
        }
        try {
            Field field = CacheFactory.class.getDeclaredField("ROCACHES");
            field.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<Class<?>, IReadOnlyCache> obj = (Map<Class<?>, IReadOnlyCache>)field.get(null);
            for (Entry<Class<?>, IReadOnlyCache> entry : obj.entrySet()) {
                registerCache(entry.getKey().getName(), entry.getValue());
            }
        }
        catch (Exception e) {
            
        }
    }
    
    public static IDataList getCacheList() {
        IDataList list = new DataArrayList();
        registerSystemCache();
        
        for (Entry<String, IReadOnlyCache> entry : readOnlyCacheMap.entrySet()) {
            IDataMap m = new DataHashMap();
            m.put("CACHE_NAME", entry.getKey());
            m.put("CACHE_CLAZZ", entry.getValue().getClass().getName());
            m.put("CACHE_TYPE", CacheType.READ_ONLY);
            list.add(m);
        }
        
        for (Entry<String, ICache> entry : readWriteCacheMap.entrySet()) {
            IDataMap m = new DataHashMap();
            m.put("CACHE_NAME", entry.getKey());
            m.put("CACHE_CLAZZ", entry.getValue().getClass().getName());
            m.put("CACHE_TYPE", CacheType.READ_WRITE);
            list.add(m);
        }
        
        for (Entry<String, Map<?, ?>> entry : jvmCacheMap.entrySet()) {
            IDataMap m = new DataHashMap();
            m.put("CACHE_NAME", entry.getKey());
            m.put("CACHE_CLAZZ", entry.getValue().getClass().getName());
            m.put("CACHE_TYPE", CacheType.JVM_MAP);
            list.add(m);
        }
        return list;
    }
    
}
