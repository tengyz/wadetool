package com.wade.framework.common.cache.readonly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

import com.wade.framework.common.cache.BaseReadOnlyCache;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.data.IDataList;
import com.wade.framework.file.config.XMLConfig;

/**
 * 读取缓存表的缓存配置数据
 * @Description 读取缓存表的缓存配置数据 
 * @ClassName   ParamTablesCache 
 * @Date        2016年12月24日 上午12:51:54 
 * @Author      yz.teng
 */
public class ParamTablesCache extends BaseReadOnlyCache {
    private static final Logger log = LogManager.getLogger(ParamTablesCache.class);
    
    //现在不走xml配置 直接走表配置的
    private static boolean FROM_DB = true;
    
    private static final String PARAM_TABLE_FILE = "paramtables.xml";
    
    private static final String COLLECTION_KEY = "__ALL_PARAM_TABLES";
    
    @Override
    public Map<String, Object> loadData() throws Exception {
        Map map = new HashMap();
        List list = new ArrayList();
        if (FROM_DB) {
            //获取缓存配置表ST_PARAM_TABLES
            IDataList getList = (IDataList)loadTableData("td_m_param_tables");
            for (int i = 0; i < getList.size(); i++) {
                Map data = (Map)getList.get(i);
                ParamConfigItem item = new ParamConfigItem(data);
                map.put(data.get("TABLE_NAME"), item);
                list.add(item);
            }
        }
        else {
            Element root = XMLConfig.getRoot(PARAM_TABLE_FILE);
            Iterator iter = root.elementIterator("table");
            while (iter.hasNext()) {
                Element tableNode = (Element)iter.next();
                ParamConfigItem item = new ParamConfigItem(tableNode);
                map.put(item.getTableName(), item);
                list.add(item);
            }
        }
        map.put(COLLECTION_KEY, list);
        return map;
    }
    
    public ParamConfigItem getParamItemConfig(String tableName) {
        return (ParamConfigItem)get(tableName);
    }
    
    public List<ParamConfigItem> getAllParamItemConfigList() {
        return (List)get(COLLECTION_KEY);
    }
}
