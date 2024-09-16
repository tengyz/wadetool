package com.wade.framework.common.cache.timestamp;

/**
 * CacheTimeStamp类所使用的清空缓存执行器
 * @Description CacheTimeStamp类所使用的清空缓存执行器 
 * @ClassName   ICacheClearInvoker 
 * @Date        2018年3月29日 上午11:01:57 
 * @Author      yz.teng
 */
public abstract class ICacheClearInvoker {
    
    /**
     * 清空所有被监听的缓存
     */
    public abstract void clearListeningCache() throws Exception;
}
