package com.wade.framework.common.cache;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICache;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.common.cache.param.ParamConfig;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.cache.param.data.ReadOnlyDataset;
import com.wade.framework.common.cache.readonly.StaticParamCache;
import com.wade.framework.common.cache.readonly.UacCacheTablesCache;
import com.wade.framework.common.util.DataHelper;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.crypto.MD5Util;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.Timer;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 获取静态参数工具类
 * 
 * @Description 获取静态参数工具类
 * @ClassName ParamMgr
 * @Date 2016年1月22日 下午4:56:11
 * @Author yz.teng
 */
public class ParamMgr {
    private static final Logger log = LogManager.getLogger(ParamMgr.class);
    
    private static IReadOnlyCache cacheTables = null;
    
    public static final String ACCT_KEY_PREFIX = "UAC_";
    
    public static final int CACHE_KEY_MAX_LEN = 250;
    
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
    
    public static String getStaticValue(String tableName, String[] keys, String name, String[] values, String defValue) throws Exception {
        return getStaticValue(tableName, StringHelper.join(keys, ","), name, StringHelper.join(values, ","), null);
    }
    
    public static String getStaticValue(String tableName, String keys, String name, String values, String defValue) throws Exception {
        if (StringHelper.isBlank(values))
            return "";
        IDataMap data = getData(tableName, keys, values);
        if (data == null)
            return defValue;
        return data.getString(name, defValue);
    }
    
    private static IDataList getList(String tableName, String[] cols, String[] values, boolean like) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        if (cols != null && values != null && cols.length != values.length) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, Arrays.toString(cols), Arrays.toString(values));
        }
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null)
                    return new ReadOnlyDataset(null);
            }
        }
        tableName = tableName.toUpperCase();
        ParamConfigItem itemConf = ParamConfig.getParamItemConfig(tableName);
        //不等于like，却是全量加载
        log.debug("like=:" + like);
        log.debug("isNeedLoadingAll=:" + itemConf.isNeedLoadingAll());
        if (!like && itemConf.isNeedLoadingAll()) {
            StaticParamCache apCache = CacheManager.getReadOnlyCache(StaticParamCache.class);
            try {
                if (apCache.containsTable(tableName)) {
                    IDataList ds = apCache.getList(tableName, cols, values, like);
                    if (log.isDebugEnabled()) {
                        log.debug("get param from readonly [" + tableName + "]" + Arrays.toString(cols) + Arrays.toString(values) + ":" + ds
                                + " use time:" + Long.valueOf(timer.getUseTimeInMillis()) + "ms");
                    }
                    return ds;
                }
                log.debug(new Object[] {"not match index", tableName, cols, values});
            }
            catch (Exception e) {
                log.error("从只读缓存中读取参数出错", e);
            }
        }
        ParamCacheSourceProvider provider = new ParamCacheSourceProvider(itemConf, cols, values);
        ICache cache = CacheManager.getCache("REDIS_STATICPARAM_CACHE");
        //获取分布式缓存,获取版本号
        String version = getCacheTableVersion(tableName);
        log.debug("获取分布式缓存,获取版本号 version=:" + version);
        if (version != null) {
            if (cache != null) {
                String cacheKey = getCacheKey(tableName, version, cols, values);
                if (log.isDebugEnabled()) {
                    log.debug("获取分布式缓存,获取版本号 version cacheKey=:" + cacheKey);
                }
                IDataList list = CacheUtil.get(cache, cacheKey, provider);
                if (log.isInfoEnabled()) {
                    log.info("get param from redis_cache," + cacheKey + ":" + list + ",use time:" + Long.valueOf(timer.getUseTime()));
                }
                return list;
            }
            log.debug(new Object[] {"staticparam_cache is null"});
        }
        // 最后查询数据库
        IDataList list = provider.getSource();
        if (log.isDebugEnabled()) {
            log.debug("get param from database," + tableName + Arrays.toString(cols) + Arrays.toString(values) + ":" + list + ",use time:"
                    + Long.valueOf(timer.getUseTimeInMillis()) + "ms");
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
        if (cacheTables == null) {
            synchronized (ParamMgr.class) {
                cacheTables = CacheManager.getReadOnlyCache(UacCacheTablesCache.class);
            }
        }
        String version = (String)cacheTables.get(tableName);
        if (null == version) {
            log.warn(tableName, "在UC_ST_CACHE_TABLES中未定义!");
            version = "0";
        }
        return version;
    }
    
    /**
     * 获取缓存的key
     * 
     * @param objs
     * @return
     */
    public static final String getCacheKey(Object... objs) {
        int count = objs.length;
        StringBuilder sb = new StringBuilder(count * 20);
        sb.append(ACCT_KEY_PREFIX);
        for (int i = 0; i < count; i++) {
            Object o = objs[i];
            if (o == null)
                sb.append("null");
            else
                sb.append(o.getClass().isArray() ? DataHelper.join((Object[])o) : o.toString());
        }
        if (sb.length() > CACHE_KEY_MAX_LEN) {
            return MD5Util.computeUTF(sb.toString());
        }
        return sb.toString();
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
        if (ds != null && !ds.isEmpty())
            return ds.first();
        return null;
    }
    
    public static IDataMap getCommPara(String paraCode) throws Exception {
        return getData("TD_B_COMMPARA", "PARA_CODE", paraCode);
    }
    
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

class ParamCacheSourceProvider implements ICacheSourceProvider<IDataList> {
    private ParamConfigItem conf = null;
    
    private String[] cols = null;
    
    private String[] values = null;
    
    public ParamCacheSourceProvider(ParamConfigItem conf, String[] cols, String[] values) {
        this.conf = conf;
        this.cols = cols;
        this.values = values;
    }
    
    public IDataList getSource() throws Exception {
        return conf.getParamDataProvider().getSelectData(conf, cols, values);
    }
}
