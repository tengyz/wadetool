package com.wade.framework.cache.localcache;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.cache.localcache.interfaces.IReadWriteCache;

/**
 * 本地缓存（本地只读和本地读写缓存）
 * 
 * @Description 本地缓存（本地只读和本地读写缓存）
 * @ClassName CacheFactory
 * @Date 2015年11月4日 上午10:19:31
 * @Author tengyz
 */
public final class CacheFactory {
    private static final Logger log = Logger.getLogger(CacheFactory.class);
    
    private static Map<Class, IReadOnlyCache> ROCACHES = new HashMap();
    
    private static Set<String> ROCACHE_CLAZZNAME = new HashSet();
    
    private static Set<String> ROCACHE_NEEDINIT = new HashSet();
    
    private static Map<String, IReadWriteCache> RWCACHES = new HashMap();
    
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    
    private static Scheduler scheduler;
    
    private static List<CacheXml.ReadOnlyCacheItem> readonlyCacheItems;
    
    private static List<CacheXml.ReadWriteCacheItem> readwriteCacheItems;
    
    public static IReadOnlyCache getReadOnlyCache(Class clazz) throws Exception {
        if (!ROCACHE_CLAZZNAME.contains(clazz.getName())) {
            log.error("缓存类在配置文件中未定义!" + clazz.getName());
            return null;
        }
        IReadOnlyCache cache = (IReadOnlyCache)ROCACHES.get(clazz);
        if (null == cache) {
            synchronized (clazz) {
                if ((cache = (IReadOnlyCache)ROCACHES.get(clazz)) != null) {
                    return cache;
                }
                long start = System.currentTimeMillis();
                cache = (IReadOnlyCache)clazz.newInstance();
                cache.setClassName(clazz.getName());
                cache.refresh();
                ROCACHES.put(clazz, cache);
                
                log.info("ReadOnlyCache:" + clazz.getName() + "刷新成功，加载数据量:" + cache.size() + "条，耗时:" + (System.currentTimeMillis() - start) + "毫秒");
            }
        }
        
        return cache;
    }
    
    public static final IReadWriteCache getReadWriteCache(String cacheName) {
        IReadWriteCache cache = (IReadWriteCache)RWCACHES.get(cacheName);
        return cache;
    }
    
    private static final void initReadOnlyCaches(List<CacheXml.ReadOnlyCacheItem> items) {
        for (CacheXml.ReadOnlyCacheItem item : items) {
            ROCACHE_CLAZZNAME.add(item.className);
            if (item.isInitial) {
                ROCACHE_NEEDINIT.add(item.className);
            }
            
            try {
                Class clazz = Class.forName(item.className);
                
                if (null != item.cronExpr) {
                    startSchedulerIfNotStarted();
                    
                    JobDetail jobDetail = new JobDetail("refresh_" + item.className + "_job", "CacheRefreshGroup", CacheAutoRefreshJob.class);
                    jobDetail.getJobDataMap().put("CACHE_TYPE", "READONLY_CACHE");
                    jobDetail.getJobDataMap().put("CACHE_NAME", clazz);
                    CronTrigger trigger = new CronTrigger("refresh_" + item.className + "_trigger", "d");
                    try {
                        trigger.setCronExpression(item.cronExpr);
                        scheduler.scheduleJob(jobDetail, trigger);
                    }
                    catch (ParseException e) {
                        log.error(e);
                    }
                    catch (SchedulerException e) {
                        log.error(e);
                    }
                }
            }
            catch (Exception e) {
                log.error("ReadOnlyCache配置加载出错! " + item.className, e);
            }
        }
    }
    
    private static final void initReadWriteCaches(List<CacheXml.ReadWriteCacheItem> items) {
        for (CacheXml.ReadWriteCacheItem item : items) {
            IReadWriteCache cache = new ReadWriteCache(item.maxSize);
            cache.setName(item.name);
            String name = item.name;
            RWCACHES.put(name, cache);
            
            if (null != item.cronExpr) {
                startSchedulerIfNotStarted();
                JobDetail jobDetail = new JobDetail("refresh_" + name + "_job", "CacheRefreshGroup", CacheAutoRefreshJob.class);
                jobDetail.getJobDataMap().put("CACHE_TYPE", "READWRITE_CACHE");
                jobDetail.getJobDataMap().put("CACHE_NAME", name);
                
                CronTrigger trigger = new CronTrigger("refresh_" + name + "_trigger", "d");
                try {
                    trigger.setCronExpression(item.cronExpr);
                    scheduler.scheduleJob(jobDetail, trigger);
                }
                catch (ParseException e) {
                    log.error(e);
                }
                catch (SchedulerException e) {
                    log.error(e);
                }
            }
        }
    }
    
    public static final List<Map<String, String>> listReadOnlyCache() {
        List rtn = new ArrayList();
        for (CacheXml.ReadOnlyCacheItem item : readonlyCacheItems) {
            Map map = new HashMap();
            map.put("className", item.className);
            map.put("init", String.valueOf(item.isInitial));
            map.put("cronExpr", item.cronExpr);
            rtn.add(map);
        }
        return rtn;
    }
    
    public static final List<Map<String, String>> listReadWriteCache() {
        List rtn = new ArrayList();
        for (CacheXml.ReadWriteCacheItem item : readwriteCacheItems) {
            Map map = new HashMap();
            map.put("name", item.name);
            map.put("maxSize", String.valueOf(item.maxSize));
            map.put("cronExpr", item.cronExpr);
            rtn.add(map);
        }
        return rtn;
    }
    
    public static final void init() {
        synchronized (CacheFactory.class) {
            try {
                for (String clazzName : ROCACHE_NEEDINIT) {
                    long start = System.currentTimeMillis();
                    
                    Class clazz = Class.forName(clazzName);
                    IReadOnlyCache cache = (IReadOnlyCache)clazz.newInstance();
                    cache.setClassName(clazzName);
                    cache.refresh();
                    ROCACHES.put(clazz, cache);
                    log.info("ReadOnlyCache:" + clazz.getName() + "刷新成功，加载数据量:" + cache.size() + "条，耗时:" + (System.currentTimeMillis() - start)
                            + "毫秒");
                }
            }
            catch (Exception e) {
                log.error("本地只读缓存初始化时发生错误!", e);
            }
        }
    }
    
    public static final void destroy() {
        if (null != scheduler)
            try {
                scheduler.shutdown();
            }
            catch (SchedulerException e) {
                log.error("销毁quartz调度线程池失败!", e);
            }
    }
    
    private static final void startSchedulerIfNotStarted() {
        if (null != scheduler) {
            return;
        }
        try {
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        }
        catch (SchedulerException e) {
            log.error("缓存定时刷新调度器初始化失败! " + e);
        }
    }
    
    static {
        try {
            CacheXml cacheXml = CacheXml.getInstance();
            readonlyCacheItems = cacheXml.getReadOnlyCacheItems();
            readwriteCacheItems = cacheXml.getReadWriteCacheItems();
            initReadOnlyCaches(readonlyCacheItems);
            initReadWriteCaches(readwriteCacheItems);
        }
        catch (Exception e) {
            log.error("CacheFactory 初始化异常static!" + e);
            e.printStackTrace();
        }
    }
}