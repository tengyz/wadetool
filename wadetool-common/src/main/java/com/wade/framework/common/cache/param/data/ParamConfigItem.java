package com.wade.framework.common.cache.param.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.wade.framework.common.cache.param.IParamDataProvider;
import com.wade.framework.common.cache.param.MysqlTableParamDataProvider;
import com.wade.framework.common.util.InstanceManager;

public class ParamConfigItem {
    
    public static final String DEFAULT_DATA_SRC = "cen2";
    
    private String tableName = null;
    
    private String primaryKeys = null;
    
    private String[] indexArr = null;
    
    private String cacheKeys = null;
    
    private boolean needLoadingAll = false;
    
    /**
     * 暂时不用
     */
    private String eparchyKey = null;
    
    private String sortKeys = null;
    
    /**
     * 数据源名称
     */
    private String dataSrc = null;
    
    /**
     * 表的状态字段和条件
     */
    private String tableState = null;
    
    private Map<String, String> attrMap = null;
    
    public String getTableState() {
        return tableState;
    }
    
    private transient IParamDataProvider paramDataProvider = null;
    
    public ParamConfigItem(String tableName) {
        this.tableName = tableName;
        this.paramDataProvider = new MysqlTableParamDataProvider();
    }
    
    /**
     * 配置文件的
     * @param node
     */
    public ParamConfigItem(Element node) {
        this.tableName = node.attributeValue("tableName");
        this.primaryKeys = node.attributeValue("primaryKeys");
        this.cacheKeys = node.attributeValue("cacheKeys");
        this.eparchyKey = node.attributeValue("eparchyKey");
        this.sortKeys = node.attributeValue("sortKeys");
        this.dataSrc = node.attributeValue("dataSrc");
        this.tableState = node.attributeValue("tableState");
        
        String indexes = node.attributeValue("indexes");
        
        if (indexes != null) {
            this.indexArr = indexes.split("\\|");
        }
        if (this.primaryKeys != null) {
            this.needLoadingAll = true;
        }
        
        String provider = node.attributeValue("dataProvider");
        if (provider == null)
            this.paramDataProvider = new MysqlTableParamDataProvider();
        else {
            this.paramDataProvider = InstanceManager.newInstance(provider, IParamDataProvider.class, null);
        }
        
        attrMap = new HashMap<String, String>();
        for (Object attrObj : node.attributes()) {
            Attribute attr = (Attribute)attrObj;
            attrMap.put(attr.getName(), attr.getValue());
        }
    }
    
    /**
     * map入参走的是数据库配置的
     * @param data
     * @throws Exception
     */
    public ParamConfigItem(Map<String, String> data) throws Exception {
        this.tableName = ((String)data.get("TABLE_NAME"));
        this.primaryKeys = ((String)data.get("PRIMARY_COLUMNS"));
        this.cacheKeys = ((String)data.get("CACHE_COLUMNS"));
        this.eparchyKey = "";
        this.sortKeys = ((String)data.get("SORT_COLUMNS"));
        this.dataSrc = (data.containsKey("DATA_SRC") ? (String)data.get("DATA_SRC") : DEFAULT_DATA_SRC);
        this.needLoadingAll = "Y".equals(data.get("NEED_LOAD_ALL"));
        String indexes = (String)data.get("INDEXES");
        
        if (indexes != null) {
            this.indexArr = indexes.split("\\|");
        }
        String provider = (String)data.get("DATA_PROVIDER");
        if (provider == null) {
            this.paramDataProvider = new MysqlTableParamDataProvider();
        }
        else {
            this.paramDataProvider = ((IParamDataProvider)InstanceManager.newInstance(provider, IParamDataProvider.class, null));
        }
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public IParamDataProvider getParamDataProvider() {
        return paramDataProvider;
    }
    
    public String getPrimaryKeys() {
        return primaryKeys;
    }
    
    public String[] getIndexArr() {
        return indexArr;
    }
    
    public String getCacheKeys() {
        return cacheKeys;
    }
    
    public boolean isNeedLoadingAll() {
        return needLoadingAll;
    }
    
    public String getEparchyKey() {
        return eparchyKey;
    }
    
    public String getSortKeys() {
        return sortKeys;
    }
    
    public String getAttribute(String key) {
        return attrMap.get(key);
    }
    
    public String getDataSrc() {
        return dataSrc == null ? DEFAULT_DATA_SRC : dataSrc;
    }
    
    @Override
    public String toString() {
        return "ParamConfigItem:{" + "tableName=" + this.getTableName() + ", " + "primaryKeys=" + this.getPrimaryKeys() + ", " + "indexes="
                + Arrays.toString(this.indexArr) + ", " + "cacheKeys=" + this.cacheKeys + ", " + "needLoadingAll=" + this.needLoadingAll + ", "
                + "eparchyKey=" + this.eparchyKey + "," + "dataSrc=" + this.dataSrc + "," + "tableState=" + this.tableState + "," + "sortKeys="
                + this.sortKeys + "}";
    }
}