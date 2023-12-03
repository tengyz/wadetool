package com.wade.framework.common.cache.readonly;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.AbstractReadOnlyCache;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.db.util.DbUtil;

/**
 * 表的版本号本地缓存
 * @Description 表的版本号本地缓存 
 * @ClassName   UacCacheTablesCache 
 * @Date        2018年11月26日 下午2:04:03 
 * @Author      yz.teng
 */
public class UacCacheTablesCache extends AbstractReadOnlyCache {
    private static final Logger log = LogManager.getLogger(UacCacheTablesCache.class);
    
    @Override
    public Map<String, Object> loadData() throws Exception {
        Map rtn = new HashMap();
        String sql = "SELECT TABLE_NAME, date_format(VERSION,'%Y-%c-%d %H:%i:%s') VERSION FROM TD_M_CACHE_TABLES WHERE STATE =1 ";
        IDataList ds = null;
        try {
            ds = DbUtil.queryList(sql);
            log.info("UacCacheTablesCache直接jdbc获取数据库时间=:" + ds);
        }
        catch (Exception e) {
            log.error("UacCacheTablesCache直接jdbc获取数据库时间异常:", e);
        }
        
        int i = 0;
        for (int size = ds.size(); i < size; i++) {
            IDataMap data = ds.getData(i);
            String tableName = data.getString("TABLE_NAME");
            String version = data.getString("VERSION");
            version = StringUtils.replaceChars(version, ":- ", "").substring(6, 12);
            log.info("UacCacheTablesCache表的版本号本地缓存tableName=:" + tableName);
            log.info("UacCacheTablesCache表的版本号本地缓存version=:" + version);
            rtn.put(tableName, version);
        }
        log.info("UacCacheTablesCache表的版本号本地缓存size=:" + ds.size());
        return rtn;
    }
}