package com.wade.framework.common.cache.readonly;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.cache.BaseReadOnlyCache;
import com.wade.framework.common.cache.ParamMgr;
import com.wade.framework.common.cache.param.ParamConfig;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.cache.param.store.ParamTable;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.impl.DataHashMap;

/**
 * 本地只读缓存
 * @Description 批量的加载需要缓存的表数据 
 * @ClassName   StaticParamCache 
 * @Date        2016年5月28日 下午7:55:38 
 * @Author      yz.teng
 */
public class StaticParamCache extends BaseReadOnlyCache {
    private static final Logger log = LogManager.getLogger(StaticParamCache.class);
    
    private static Map<String, String> tableVersion = new ConcurrentHashMap<String, String>();
    
    @Override
    public Map<String, Object> loadData() throws Exception {
        Map<String, Object> map = new DataHashMap();
        for (ParamConfigItem itemConf : ParamConfig.getAllParamItemConfig()) {
            if (!itemConf.isNeedLoadingAll()) {
                continue;
            }
            String tableName = itemConf.getTableName();
            String oldVersion = tableVersion.get(tableName);
            String newVersion = ParamMgr.getCacheTableVersion(tableName);
            if (oldVersion != null && oldVersion.equals(newVersion)) {
                log.info("StaticParamCache need not reload table...", tableName);
                map.put(tableName, get(tableName));
                continue;
            }
            log.info("StaticParamCache reload table...", tableName, oldVersion, newVersion);
            tableVersion.put(tableName, newVersion);
            
            ParamTable pt = new ParamTable(itemConf);
            pt.loadData();
            map.put(itemConf.getTableName(), pt);
        }
        return map;
    }
    
    public IDataList getList(String tableName, String[] cols, String[] values, boolean like) throws Exception {
        ParamTable pt = (ParamTable)get(tableName);
        if (pt == null) {
            return null;
        }
        IDataList getList = pt.getList(cols, values, like);
        return getList;
    }
    
    public boolean containsTable(String tableName) throws Exception {
        return get(tableName) != null;
    }
}
