package com.wade.framework.cache.localcache;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.cache.localcache.interfaces.IReadWriteCache;

/**
 * 本地缓存定时自动刷新
 * @Description 本地缓存定时自动刷新 
 * @ClassName   CacheAutoRefreshJob 
 * @Date        2015年11月28日 上午11:47:48 
 * @Author      yizu.teng
 */
public class CacheAutoRefreshJob implements Job {
    private static final transient Logger log = Logger.getLogger(CacheAutoRefreshJob.class);
    
    public static final String CACHE_NAME = "CACHE_NAME";
    
    public static final String CACHE_TYPE = "CACHE_TYPE";
    
    public static final String READONLY_CACHE = "READONLY_CACHE";
    
    public static final String READWRITE_CACHE = "READWRITE_CACHE";
    
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        JobDataMap map = ctx.getJobDetail().getJobDataMap();
        String cacheType = map.getString(CACHE_TYPE);
        if (READONLY_CACHE.equals(cacheType)) {
            Class clazz = (Class)map.get(CACHE_NAME);
            try {
                IReadOnlyCache cache = CacheFactory.getReadOnlyCache(clazz);
                int oldSize = cache.size();
                if (null != cache) {
                    cache.refresh();
                    int newSize = cache.size();
                    log.info("本地只读缓存自动刷新成功! " + clazz.getName() + ",刷新前:" + oldSize + "条数据，刷新后:" + newSize + "条数据。");
                }
            }
            catch (Exception e) {
                log.error("本地只读缓存自动刷新失败! " + clazz.getName() + e);
            }
        }
        else if (READWRITE_CACHE.equals(cacheType)) {
            String cachename = map.getString(CACHE_NAME);
            IReadWriteCache cache = CacheFactory.getReadWriteCache(cachename);
            cache.refresh();
            
            log.info("本地读写缓存自动刷新成功! " + cachename);
        }
    }
}