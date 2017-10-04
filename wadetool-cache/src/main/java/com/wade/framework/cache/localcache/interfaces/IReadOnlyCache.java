package com.wade.framework.cache.localcache.interfaces;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 只读缓存抽象接口
 * @Description 只读缓存抽象接口 
 * @ClassName   IReadOnlyCache 
 * @Date        2015年11月28日 上午11:52:09 
 * @Author      yz.teng
 */
public abstract interface IReadOnlyCache {
    public abstract void refresh() throws Exception;
    
    public abstract Object get(String paramString) throws Exception;
    
    public abstract int size();
    
    public abstract Set<String> keySet();
    
    public abstract LinkedHashMap<Long, Integer> getRefreshHistory();
    
    public abstract Map<String, Object> loadData() throws Exception;
    
    public abstract String getClassName();
    
    public abstract void setClassName(String paramString);
}