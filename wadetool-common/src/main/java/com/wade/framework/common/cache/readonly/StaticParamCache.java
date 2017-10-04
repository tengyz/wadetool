package com.wade.framework.common.cache.readonly;

import java.util.Map;

import org.apache.log4j.Logger;

import com.wade.framework.common.cache.BaseReadOnlyCache;
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
    private static Logger log = Logger.getLogger(StaticParamCache.class);
    
    @Override
    public Map<String, Object> loadData() throws Exception {
        Map<String, Object> map = new DataHashMap();
        for (ParamConfigItem itemConf : ParamConfig.getAllParamItemConfig()) {
            if (!itemConf.isNeedLoadingAll())
                continue;
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
