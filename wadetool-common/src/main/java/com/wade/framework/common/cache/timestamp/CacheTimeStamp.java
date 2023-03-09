package com.wade.framework.common.cache.timestamp;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.cache.CacheConfig;
import com.wade.framework.common.util.HttpHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.db.util.DbUtil;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 专用于前台缓存的时间戳控制 缓存的时间戳配置在TD_B_COMMPARA表中 PARA_CODE为时间戳的KEY 通过getInstance(key) 可以获取一个缓存时间戳控制类的实例 PARA_CODE1为刷新时间戳的频率
 * PARA_DATE7 为时间戳 如果比原来的新 则需要更新缓存 注意：由定时刷新时会先判断这个时间窜，如果不是最新的不做刷新操作
 * 
 * @author yz.teng
 * 
 */
public class CacheTimeStamp {
    private static final Logger log = LogManager.getLogger(CacheTimeStamp.class);
    
    public static final String CACHE_TIMESTAMP = "WT_CACHE_REFRESH_SWITCH";
    
    private String cacheTimestamp = null;
    
    private String timestampKey = CACHE_TIMESTAMP;
    
    private List<ICacheClearInvoker> clearInvokers = new ArrayList<ICacheClearInvoker>();
    
    private CacheTimeStamp(String timestampKey) {
        this.timestampKey = timestampKey;
        try {
            this.cacheTimestamp = getTimestamp(timestampKey);
        }
        catch (Exception e) {
            log.error("CacheTimeStamp异常！", e);
            this.cacheTimestamp = "";
        }
    }
    
    /**
     * 获取默认的时间戳对象，时间戳配置名称为CACHE_TIMESTAMP
     * 
     * @return
     */
    public static CacheTimeStamp getInstance() {
        return getInstance(CACHE_TIMESTAMP);
    }
    
    /**
     * 根据时间戳缓存配置的名字来获取对应的时间戳控制对象
     * 
     * @param cacheKeyName
     * @return
     */
    public static CacheTimeStamp getInstance(String cacheKeyName) {
        CacheTimeStamp timestamp = new CacheTimeStamp(cacheKeyName);
        return timestamp;
    }
    
    /**
     * 根据时间戳配置返回是否需要更新缓存
     * 
     * @return
     */
    public synchronized boolean needReFreshCache() {
        try {
            String timestamp = getTimestamp(timestampKey);
            if (null == timestamp || "".equals(timestamp)) {
                log.info("needReFreshCache()判断需要更新本地缓存：false");
                return false;
            }
            if (null == cacheTimestamp || "".equals(cacheTimestamp)) {
                log.info("needReFreshCache()判断需要更新本地缓存：false");
                return false;
            }
            if (timestamp.compareTo(cacheTimestamp) > 0) {
                cacheTimestamp = timestamp;
                log.info("needReFreshCache()判断需要更新本地缓存：true");
                return true;
            }
            log.info("needReFreshCache()判断需要更新本地缓存：false");
            return false;
        }
        catch (Exception e) {
            log.error("needReFreshCache异常！！！", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "needReFreshCache异常！！！");
        }
        log.info("needReFreshCache()判断需要更新本地缓存：false");
        return false;
    }
    
    /**
     * 判断是否需要刷新时间戳，如果需要刷新时间戳则会自动执行缓存清空执行器
     * 
     * @return
     */
    public synchronized boolean checkCacheTimestamp() {
        boolean needRefresh = false;
        try {
            needRefresh = needReFreshCache();
            if (needRefresh) {
                for (int i = 0, size = clearInvokers.size(); i < size; i++) {
                    ICacheClearInvoker invoker = clearInvokers.get(i);
                    invoker.clearListeningCache();
                }
            }
        }
        catch (Exception e) {
            log.error("checkCacheTimestamp", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "checkCacheTimestamp异常！！！");
        }
        return needRefresh;
    }
    
    private String getTimestamp(String timestampKey) throws Exception {
        //获得字典表时间 TD_M_STATIC 的key=LOCAL_CACHE_REFRESH_SWITCH;
        String getTimestamp = null;
        try {
            IDataList ds = null;
            String sql = "select t.DATA_NAME  from TD_M_STATIC  t where t.TYPE_ID='LOCAL_CACHE_REFRESH_SWITCH' AND  VALID_FLAG='2'";
            try {
                //调用微服务查询数据库获取序列
                IDataMap param = new DataHashMap();
                param.put("sql", sql);
                String url = CacheConfig.GATEWAY_ADDR + "/common/queryList";
                String getList = HttpHelper.requestService(url, param.toString());
                IDataMap getIData = new DataHashMap(getList);
                String getString=getIData.getString("data");
                ds = new DataArrayList(getString);
            }
            catch (Exception e) {
                log.error("CacheTimeStamp调用微服务查询数据库获取序列异常！", e);
                try {
                    //当调用微服务异常时，直接查询数据库
                    DbUtil db = new DbUtil();
                    ds = db.queryList(sql);
                    log.info("CacheTimeStamp当调用微服务异常时，直接jdbc获取数据库时间=:" + ds);
                }
                catch (Exception e2) {
                    log.error("CacheTimeStamp直接jdbc获取数据库时间异常:", e);
                    Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "CacheTimeStamp直接jdbc获取数据库时间异常！！！");
                }
            }
            IDataMap data = ds.first();
            getTimestamp = data.getString("DATA_NAME");
        }
        catch (Exception e) {
            log.error("getTimestamp获取开关时间异常！！！", e);
            getTimestamp = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("===获取缓存是否刷新开关数据=getTimestamp=:" + getTimestamp);
        }
        return getTimestamp;
    }
    
    public void addCacheClearInvoker(ICacheClearInvoker listener) {
        clearInvokers.add(listener);
    }
    
    public void addListeningCache(Object... objs) {
        addCacheClearInvoker(new DefaultCacheClearInvoker(objs));
    }
    
    public String toString() {
        String str = "{" + "cacheTimestamp = " + cacheTimestamp + ";" + "cacheKey = " + timestampKey + "}";
        return str;
    }
}
