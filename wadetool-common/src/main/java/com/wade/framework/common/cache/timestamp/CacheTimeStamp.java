package com.wade.framework.common.cache.timestamp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.beetl.sql.core.SQLManager;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;
import com.wade.framework.spring.SpringContextsUtil;

/**
 * 专用于前台缓存的时间戳控制 缓存的时间戳配置在TD_B_COMMPARA表中 PARA_CODE为时间戳的KEY 通过getInstance(key) 可以获取一个缓存时间戳控制类的实例 PARA_CODE1为刷新时间戳的频率
 * PARA_DATE7 为时间戳 如果比原来的新 则需要更新缓存 注意：由定时刷新时会先判断这个时间窜，如果不是最新的不做刷新操作
 * 
 * @author yz.teng
 * 
 */
public class CacheTimeStamp {
    private static Logger log = Logger.getLogger(CacheTimeStamp.class);
    
    public static final String CACHE_TIMESTAMP = "WT_CACHE_REFRESH_SWITCH";
    
    private String cacheTimestamp = null;
    
    private String timestampKey = CACHE_TIMESTAMP;
    
    private List<ICacheClearInvoker> clearInvokers = new ArrayList<ICacheClearInvoker>();
    
    //查询数据库
    private static SQLManager getService = null;
    
    private CacheTimeStamp(String timestampKey) {
        this.timestampKey = timestampKey;
        try {
            this.cacheTimestamp = getTimestamp(timestampKey);
        }
        catch (Exception e) {
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
                return false;
            }
            if (null == cacheTimestamp || "".equals(cacheTimestamp)) {
                return false;
            }
            if (timestamp.compareTo(cacheTimestamp) > 0) {
                cacheTimestamp = timestamp;
                log.info("....needReFreshCache()...判断需要更新本地缓存，开始更新本地缓存...");
                return true;
            }
            return false;
        }
        catch (Exception e) {
            log.error("needReFreshCache异常！！！", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "needReFreshCache异常！！！");
        }
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
        // 获得字典表时间 wt_pub_commpara 的key=CACHE_REFRESH_SWITCH;
        String getTimestamp = null;
        try {
            if (null == getService) {
                getService = (SQLManager)SpringContextsUtil.getBean("sqlManager");
            }
            IDataList getList = null;
            // getList=getService.queryList(new SQLReady(
            //"select t.PARA_CODE1 TIMESTAMP_CODE from RP_PUB_COMMPARA t where t.param_code='RP_CACHE_REFRESH_SWITCH'"));
            IDataMap data = getList.first();
            getTimestamp = data.getString("TIMESTAMP_CODE");
        }
        catch (Exception e) {
            log.error("getTimestamp获取开关时间异常！！！", e);
            //Thrower.throwException(BaseExceptionType.ERROR_MSG, e, "getTimestamp获取开关时间异常！！！");
            getTimestamp = null;
        }
        log.info("===获取缓存是否刷新开关数据=getTimestamp=:" + getTimestamp);
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
