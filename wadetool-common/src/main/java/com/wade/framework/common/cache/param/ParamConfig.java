package com.wade.framework.common.cache.param;

import java.util.List;
import java.util.Map;

import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.common.cache.CacheManager;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.cache.readonly.ParamTablesCache;

/**
 * 本地缓存表配置xml
 * @author tengyz
 *
 */

public class ParamConfig {
    
    private static Map<String, ParamConfigItem> paramItemConfs = CacheManager.getStaticMap(ParamConfig.class.getName());
    
    public static ParamConfigItem getParamItemConfig(String paramKey) throws Exception {
        final String tableName = paramKey.toUpperCase();
        ParamTablesCache roCache = (ParamTablesCache)CacheManager.getReadOnlyCache(ParamTablesCache.class);
        ParamConfigItem item = roCache.getParamItemConfig(tableName);
        if (item != null) {
            return item;
        }
        return (ParamConfigItem)CacheUtil.get(paramItemConfs, tableName, new ICacheSourceProvider() {
            public ParamConfigItem getSource() throws Exception {
                return new ParamConfigItem(tableName);
            }
        });
    }
    
    public static List<ParamConfigItem> getAllParamItemConfig() throws Exception {
        ParamTablesCache roCache = (ParamTablesCache)CacheManager.getReadOnlyCache(ParamTablesCache.class);
        return roCache.getAllParamItemConfigList();
    }
}