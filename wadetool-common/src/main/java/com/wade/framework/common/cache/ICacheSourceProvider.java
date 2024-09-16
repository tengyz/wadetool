package com.wade.framework.common.cache;

/**
 * 缓存数据源接口类
 * @author tengs
 * @param <T>
 */
public interface ICacheSourceProvider<T> {
    
    public T getSource() throws Exception;
    
}
