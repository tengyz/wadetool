package com.wade.framework.cache.util;

public interface ICacheSourceProvider<T> {
    
    public T getSource() throws Exception;
    
}
