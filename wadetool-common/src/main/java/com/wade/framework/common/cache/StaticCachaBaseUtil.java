package com.wade.framework.common.cache;

import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.crypto.MD5Util;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.IDataOutput;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.data.impl.DataInput;

/**
 * 未使用的
 * @author tengyz
 *
 */
public final class StaticCachaBaseUtil {
    public static final String keySeperator = "^^";
    
    private static IReadOnlyCache localCache = null;
    
    public static final IDataList getList(String visit, String table_name, String key, String name) throws Exception {
        return paramList(visit, "cen1", table_name, key, name, new String[0], new String[0]);
    }
    
    private static final IDataList paramList(String visit, String datasource, String table_name, String key, String name, String[] columns,
            String[] values) throws Exception {
        IDataList retValue = null;
        
        String version = (String)localCache.get(table_name);
        
        StringBuilder sbuff = new StringBuilder(100);
        sbuff.append(datasource).append(table_name).append(version).append(name).append(key);
        
        if (null != columns) {
            for (String col : columns)
                sbuff.append(col);
        }
        
        if (null != values) {
            for (String val : values)
                sbuff.append(val);
        }
        
        String cacheKey = null;
        if (sbuff.length() > 250)
            //cacheKey = MD5Util.hexdigest(cacheKey);
            cacheKey = MD5Util.compute(cacheKey);
        else {
            cacheKey = sbuff.toString();
        }
        
        //        IMemCache cache = MemCacheFactory.getCache("staticparam_cache");
        //        if (null != cache) {
        //            retValue = (IDataset)cache.get(cacheKey);
        //            if (null != retValue) {
        //                return retValue;
        //            }
        //            
        //        }
        
        IDataMap param = new DataHashMap();
        param.put("DATA_SOURCE", datasource);
        param.put("TABLE_NAME", table_name);
        param.put("KEY", key);
        param.put("NAME", name);
        param.put("COLUMNS", columns);
        param.put("VALUES", values);
        
        DataInput input = new DataInput();
        //    if (null != visit) {
        //      input.getHead().putAll(visit.getAll());
        //    }
        
        input.setData(param);
        //查询数据库list
        //    IDataOutput output = ServiceFactory.call("SYS_Static_GetList", input);
        IDataOutput output = null;
        retValue = output.getData();
        //        if (null != cache) {
        //            cache.set(cacheKey, retValue);
        //        }
        
        return retValue;
    }
    
    public static final String getStaticValueDataSource(String visit, String datasource, String table_name, String[] keys, String name,
            String[] values, String defValue) throws Exception {
        return paramTranslate(visit, datasource, table_name, keys, name, values, defValue);
    }
    
    private static final String paramTranslate(String visit, String datasource, String table_name, String[] keys, String name, String[] values,
            String defValue) throws Exception {
        String version = (String)localCache.get(table_name);
        
        StringBuilder sbuff = new StringBuilder(100);
        sbuff.append(datasource).append(table_name).append(version).append(name);
        
        for (String key : keys)
            sbuff.append(key);
        for (String val : values)
            sbuff.append(val);
        
        String cacheKey = sbuff.toString();
        if (cacheKey.length() > 250) {
            cacheKey = MD5Util.compute(cacheKey);
        }
        
        //        IMemCache cache = MemCacheFactory.getCache("staticparam_cache");
        //        Object retValue = cache.get(cacheKey);
        //        if (null != retValue) {
        //            String rtn = (String)retValue;
        //            if (rtn.equals("<-- IMPOSSIBLE_VALUE -->")) {
        //                return defValue;
        //            }
        //            return (String)retValue;
        //        }
        //        
        //        IData param = new DataMap();
        //        param.put("DATA_SOURCE", datasource);
        //        param.put("TABLE_NAME", table_name);
        //        param.put("NAME", name);
        //        param.put("KEYS", keys);
        //        param.put("VALUES", values);
        //        
        //        DataInput input = new DataInput();
        //        input.setData(param);
        //        //查询数据库单个的
        //        //IDataOutput output = ServiceFactory.call("SYS_Static_GetStaticValue", input);
        //        IDataOutput output = null;
        //        IDataset ds = output.getData();
        //        
        //        if ((null != ds) && (ds.size() > 0)) {
        //            String value = ds.first().getString("VALUE");
        //            if (null == value) {
        //                cache.set(cacheKey, "<-- IMPOSSIBLE_VALUE -->");
        //                return defValue;
        //            }
        //            cache.set(cacheKey, value);
        //            return value;
        //        }
        //        
        //        cache.set(cacheKey, "<-- IMPOSSIBLE_VALUE -->");
        
        return defValue;
    }
    
    static {
        try {
            //加载cache_table表数据
            //localCache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
            localCache = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}