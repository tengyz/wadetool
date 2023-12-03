package com.wade.framework.common.cache;

public interface ICacheSourceProvider<T> {
    
    public T getSource() throws Exception;
    
}
