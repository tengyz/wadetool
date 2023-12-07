package com.wade.framework.common.cache;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.common.cache.param.ParamConfig;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.cache.param.data.ReadOnlyDataset;
import com.wade.framework.common.cache.readonly.StaticParamCache;
import com.wade.framework.common.cache.readonly.UacCacheTablesCache;
import com.wade.framework.common.util.DataHelper;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.Timer;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 获取静态参数工具类
 * source:配置的aicache会走aicache,可通过aicache刷新缓存，dataType:CacheConfig.xml中的 server_code
 * 不配置走的分布式缓存,没个表的缓存数据在redis上key为:STATIC_PARAM_TAB_VERSION_表名
 * 查询条件列需要走上BS_PARAM_TABLES表配置的主键和索引才能命中缓存
 * 否则在aicache缓存中找不到，这样刷新缓存需要删除redis，STATIC_PARAM_TAB_VERSION_表名
 * 的key，在使用ParamMgr获取缓存的时候一定要按表bs_param_tables配置的主键和索引列查找
 * 否则会出现缓存无法刷新的情况
 * @Description 获取静态参数工具类
 * @ClassName ParamMgr
 * @Date 2016年1月22日 下午4:56:11
 * @Author yz.teng
 */
public class ParamMgr {
    private static final Logger log = LogManager.getLogger(ParamMgr.class);
    
    private static IReadOnlyCache cacheTables = null;
    
    private static final String IMPOSSIBLE_VALUE = "<-- IMPOSSIBLE_VALUE -->";
    
    private static final boolean REDIS_DISABLED = "false".equals(CacheConfig.STATICPARAM_DISABLED);
    
    /**
     * 类加载时执行
     */
    static {
        try {
            cacheTables = CacheFactory.getReadOnlyCache(UacCacheTablesCache.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取静态参数表TD_M_STATIC
     * @param typeId
     * @param dataId
     * @return
     * @throws Exception
     */
    public static final String getStaticValue(String typeId, String dataId) throws Exception {
        return getStaticValue("TD_M_STATIC", new String[] {"TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[] {typeId, dataId});
    }
    
    /**
     * 查询字典缓存
     * @param tableName 表名
     * @param keys 查询key主键
     * @param name 要查询结果字段
     * @param values 对应key的变量参数
     * @return String
     * @throws Exception
     * @Date        2017年6月11日 下午8:41:15 
     * @Author      yz.teng
     */
    public static String getStaticValue(String tableName, String keys, String name, String values) throws Exception {
        return getStaticValue(tableName, keys, name, values, null);
    }
    
    public static String getStaticValue(String tableName, String keys, String name, String[] value) throws Exception {
        return getStaticValue(tableName, keys, name, StringHelper.join(value, ","));
    }
    
    public static String getStaticValue(String tableName, String[] key, String name, String[] value) throws Exception {
        return getStaticValue(tableName, key, name, value, null);
    }
    
    /**
     * 先查询分布式缓存，再查询本地缓存
     * @param tableName
     * @param keys
     * @param name
     * @param values
     * @param defValue
     * @return
     * @throws Exception
     */
    public static String getStaticValue(String tableName, String[] keys, String name, String[] values, String defValue) throws Exception {
        ParamConfigItem itemConf = ParamConfig.getParamItemConfig(tableName);
        String version = null;
        version = getCacheTableVersion(tableName);
        if (null == version) {
            //在UOP_CEN1.CACHE_TABLES中未定义!
            throw new NullPointerException(new StringBuilder().append(tableName).append("在CACHE_TABLES中未定义!").toString());
        }
        String valueName = null;
        String cacheKey = CacheKeyCreater.getCacheKey(itemConf.getDataSrc(), tableName, version, name, "T", keys, values);
        if (log.isDebugEnabled()) {
            log.debug("===getStaticValue===version=:" + version + ",cacheKey=:" + cacheKey);
        }
        ICache cache = CacheManager.getCache("REDIS_STATICPARAM_CACHE");
        Object retValue = cache.get(cacheKey);
        if (null != retValue) {
            String rtn = (String)retValue;
            if (IMPOSSIBLE_VALUE.equals(rtn)) {
                valueName = defValue;
            }
            else {
                valueName = ((String)retValue);
            }
        }
        else {
            IDataMap data = getData(tableName, keys, values);
            if (data != null) {
                String v = data.getString(name);
                if (null == v) {
                    cache.put(cacheKey, IMPOSSIBLE_VALUE);
                    valueName = defValue;
                }
                else {
                    cache.put(cacheKey, v);
                    valueName = v;
                }
            }
            else {
                cache.put(cacheKey, IMPOSSIBLE_VALUE);
                valueName = defValue;
            }
        }
        if (log.isDebugEnabled()) {
            log.info("======getStaticValue===" + tableName + keys + name + values + valueName);
        }
        return valueName;
    }
    
    public static String getStaticValue(String tableName, String keys, String name, String values, String defValue) throws Exception {
        if (StringHelper.isBlank(values)) {
            return "";
        }
        IDataMap data = getData(tableName, keys, values);
        if (data == null) {
            return defValue;
        }
        return data.getString(name, defValue);
    }
    
    private static IDataList getList(String tableName, String[] cols, String[] values, boolean like) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        if (cols != null && values != null && cols.length != values.length) {
            Thrower.throwException(BizExceptionEnum.WADE_COMP_CACHE_NOTMATCH, Arrays.toString(cols), Arrays.toString(values));
        }
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null) {
                    return new ReadOnlyDataset(null);
                }
            }
        }
        //表名称转大写
        tableName = tableName.toUpperCase();
        ParamConfigItem itemConf = ParamConfig.getParamItemConfig(tableName);
        //不等于like，却是全量加载
        if (log.isDebugEnabled()) {
            log.debug("like=:" + like);
            log.debug("isNeedLoadingAll=:" + itemConf.isNeedLoadingAll());
        }
        if (!like && itemConf.isNeedLoadingAll()) {
            StaticParamCache apCache = CacheManager.getReadOnlyCache(StaticParamCache.class);
            try {
                if (apCache.containsTable(tableName)) {
                    IDataList ds = apCache.getList(tableName, cols, values, like);
                    if (log.isDebugEnabled()) {
                        log.debug("get param from readonly [" + tableName + "]" + cols + values + ":" + ds + "use time:"
                                + Long.valueOf(timer.getUseTimeInMillis()) + " ms");
                    }
                    return ds;
                }
                log.warn(new Object[] {"not match index", tableName, cols, values});
            }
            catch (Exception e) {
                log.warn("从只读缓存中读取参数出错", e.getMessage());
            }
        }
        //等于like
        com.wade.framework.common.cache.param.ParamCacheSourceProvider provider = new com.wade.framework.common.cache.param.ParamCacheSourceProvider(
                itemConf, cols, values);
        ICache cache = null;
        //本地读写缓存，在配置文件上配置readwrite，把需要配置的表名称加上
        cache = CacheManager.getCache(tableName);
        if (cache != null) {
            String cacheKey = CacheKeyCreater.getCacheKey(cols, values);
            IDataList list = CacheUtil.get(cache, cacheKey, provider);
            if (log.isDebugEnabled()) {
                log.info("get param from cache [" + tableName + "] " + cols + values + ":" + list + "use time:"
                        + Long.valueOf(timer.getUseTimeInMillis()) + " ms");
            }
            return list;
        }
        //获取redis分布式缓存,获取版本号
        String version = getCacheTableVersion(tableName);
        if (version != null) {
            //初始化redis客户端
            cache = CacheManager.getCache("REDIS_STATICPARAM_CACHE");
            if (cache != null) {
                String cacheKey = CacheKeyCreater.getCacheKey(tableName, version, cols, values);
                if (log.isDebugEnabled()) {
                    log.debug("获取分布式缓存 cacheKey=:" + cacheKey);
                }
                IDataList list = CacheUtil.get(cache, cacheKey, provider);
                if (log.isDebugEnabled()) {
                    log.debug("get param from redis_staticparam_cache" + cacheKey + ":" + list + "use time:"
                            + Long.valueOf(timer.getUseTimeInMillis()) + " ms");
                }
                return list;
            }
            if (log.isDebugEnabled()) {
                log.debug("static redis_staticparam_cache is null");
            }
        }
        //如果前面条件不满足，最后查询数据库
        IDataList list = provider.getSource();
        if (log.isDebugEnabled()) {
            log.debug("get param from database" + tableName + cols + values + ":" + list + "use time:" + Long.valueOf(timer.getUseTimeInMillis())
                    + " ms");
        }
        return list;
    }
    
    /**
     * 获取版本号
     * @param tableName
     * @return
     * @throws Exception
     * @Date        2018年11月26日 下午3:18:22 
     * @Author      yz.teng
     */
    public static String getCacheTableVersion(String tableName) throws Exception {
        String version = null;
        if (cacheTables == null) {
            synchronized (ParamMgr.class) {
                cacheTables = CacheFactory.getReadOnlyCache(UacCacheTablesCache.class);
            }
        }
        version = (String)cacheTables.get(tableName);
        if (null == version) {
            ////UOP_PARAM数据库下面,在UOP_PARAM.CACHE_TABLES中未定义!
            log.warn(tableName, "在CACHE_TABLES中未定义!");
            version = "0";
        }
        if (log.isDebugEnabled()) {
            log.debug("获取版本号getCacheTableVersion version=:" + version + ",tableName=:" + tableName);
        }
        return version;
    }
    
    public static IDataList getListLike(String tableName, String[] cols, String[] values) throws Exception {
        return getList(tableName, cols, values, true);
    }
    
    public static IDataList getListLike(String tableName, String column, String value) throws Exception {
        String[] valArr = StringHelper.isNonBlank(value) ? value.split(DataHelper.STR_SEPARATOR) : null;
        return getListLike(tableName, column, valArr);
    }
    
    public static IDataList getListLike(String tableName, String column, String[] values) throws Exception {
        String[] colArr = StringHelper.isNonBlank(column) ? column.split(DataHelper.STR_SEPARATOR) : null;
        return getListLike(tableName, colArr, values);
    }
    
    /**
     * 根据typeId 获取静态参数表list
     * @param typeId
     * @return
     * @throws Exception
     */
    public static final IDataList getStaticList(String typeId) throws Exception {
        return getList("TD_M_STATIC", new String[] {"TYPE_ID"}, new String[] {typeId});
    }
    
    /**
     * 根据typeId，pDataId 获取静态参数表list
     * @param typeId
     * @param pDataId
     * @return
     * @throws Exception
     */
    public static final IDataList getStaticListByParent(String typeId, String pDataId) throws Exception {
        return getList("TD_M_STATIC", new String[] {"TYPE_ID", "PDATA_ID"}, new String[] {typeId, pDataId});
    }
    
    public static IDataList getList(String tableName, String[] cols, String[] values) throws Exception {
        return getList(tableName, cols, values, false);
    }
    
    public static IDataList getList(String tableName, String column, String value) throws Exception {
        String[] valArr = StringHelper.isNonBlank(value) ? value.split(DataHelper.STR_SEPARATOR) : null;
        return getList(tableName, column, valArr);
    }
    
    public static IDataList getList(String tableName, String column, String[] values) throws Exception {
        String[] colArr = StringHelper.isNonBlank(column) ? column.split(DataHelper.STR_SEPARATOR) : null;
        return getList(tableName, colArr, values);
    }
    
    public static IDataMap getData(String tableName, String col, String value) throws Exception {
        String[] valArr = StringHelper.isNonBlank(value) ? value.split(DataHelper.STR_SEPARATOR) : null;
        return getData(tableName, col, valArr);
    }
    
    public static IDataMap getData(String tableName, String cols, String[] values) throws Exception {
        String[] colArr = StringHelper.isNonBlank(cols) ? cols.split(DataHelper.STR_SEPARATOR) : null;
        return getData(tableName, colArr, values);
    }
    
    public static IDataMap getData(String tableName, String[] cols, String[] values) throws Exception {
        IDataList ds = getList(tableName, cols, values);
        if (ds != null && !ds.isEmpty()) {
            return ds.first();
        }
        return null;
    }
    
    public static IDataMap getCommPara(String paraCode) throws Exception {
        return getData("TD_B_COMMPARA", "PARA_CODE", paraCode);
    }
    
    //    public static IDataset getListByCodeCode(String tableName, String sqlRef, IData data) throws Exception {
    //        data.put("_TABLE_NAME", tableName);
    //        data.put("_SQL_REF", sqlRef);
    //        
    //        ParamConfigItem item = ParamConfig.getParamItemConfig(tableName);
    //        data.put("_DATA_SOURCE", item.getDataSrc());
    //        IDataset ds = ServiceCaller.callList("AC_AcctParam_GetListByCodeCode", data);
    //        return ds;
    //    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // System.out.println(getList("TD_B_COMMPARA", new String[]{"PARA_CODE", "EPARCHY_CODE"}, new String[]{"123",
        // "0731"}, false));
        // Thread.sleep(5000);
        // System.out.println(getList("TD_B_COMMPARA", new String[]{"PARA_CODE", "EPARCHY_CODE"}, new String[]{"123",
        // "0731"}, false));
        // Thread.sleep(20000);
        // System.out.println(getList("TD_B_COMMPARA", "PARA_CODE", "0731"));
        // System.out.println(getList("ST_AREA", "AREA_CODE", "0731"));
        // System.out.println(getList("ST_AREA", "AREA_CODE", "0731"));
        
        // System.out.println(getListLike("TD_B_COMMPARA", "PARA_CODE", "1%"));
        // System.out.println(getListLike("TD_B_COMMPARA", "PARA_CODE", "1%"));
        // System.out.println(getListLike("TD_B_COMMPARA", "PARA_CODE", "1%"));
        // System.out.println(getList("TD_M_DEPART", "DEPART_ID", "DEPART_ID"));
    }
    
}
