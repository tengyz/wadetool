package com.wade.framework.cache.localcache.interfaces;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 本地读写抽象接口
 * 
 * @Description 本地读写抽象接口
 * @ClassName IReadWriteCache
 * @Date 2015年11月28日 上午11:52:34
 * @Author yz.teng
 */
public abstract interface IReadWriteCache {
    public abstract void refresh();
    
    public abstract Object get(String paramString) throws Exception;
    
    public abstract Object put(String paramString, Object paramObject) throws Exception;
    
    public abstract boolean containsKey(String paramString);
    
    public abstract boolean isEmpty();
    
    public abstract Set<String> keySet();
    
    public abstract Object remove(String paramString);
    
    public abstract int size() throws Exception;
    
    public abstract LinkedHashMap<Long, Integer> getRefreshHistory();
    
    public abstract String getName();
    
    public abstract void setName(String paramString);
}