package com.wade.framework.common.cache.timestamp;

/**
 * CacheTimeStamp类所使用的清空缓存执行器
 * @author Lin
 *
 */
public abstract class ICacheClearInvoker {
    
    /**
     * 清空所有被监听的缓存
     */
    public abstract void clearListeningCache() throws Exception;
    
}
