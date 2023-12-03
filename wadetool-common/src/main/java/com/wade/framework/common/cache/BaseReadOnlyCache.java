package com.wade.framework.common.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.AbstractReadOnlyCache;
import com.wade.framework.common.cache.timestamp.CacheTimeStamp;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.db.util.DbUtil;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 基础
 * @author yizuteng
 */
public abstract class BaseReadOnlyCache extends AbstractReadOnlyCache {
    private final static Logger log = LogManager.getLogger(BaseReadOnlyCache.class);
    
    protected static boolean NEED_TIMESTAMP = "true".equalsIgnoreCase("true");
    
    protected CacheTimeStamp timestamp = null;
    
    @Override
    public void refresh() throws Exception {
        try {
            if (!NEED_TIMESTAMP) {
                super.refresh();
            }
            else {
                if (getTimestampCode() == null) {
                    super.refresh();
                }
                else {
                    if (this.timestamp == null) {
                        super.refresh();
                        this.timestamp = CacheTimeStamp.getInstance(getTimestampCode());
                    }
                    else if (this.timestamp.needReFreshCache()) {
                        log.info("....refresh()...开始刷新本地缓存 this.timestamp.needReFreshCache() start=:" + this.timestamp.needReFreshCache());
                        super.refresh();
                        log.info("....refresh()...开始刷新本地缓存 this.timestamp.needReFreshCache() end=:" + this.timestamp.needReFreshCache());
                    }
                }
                
            }
        }
        catch (Exception e) {
            log.error("【BaseReadOnlyCache】刷新缓存" + this.getClass() + "失败", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "【BaseReadOnlyCache】刷新缓存失败");
        }
    }
    
    public IDataMap getData(String key) {
        return (IDataMap)get(key);
    }
    
    /**
     * 时间戳PARA_CODE，如果返回null则不使用时间戳控制
     * 设置了这个参数时，定时刷新时会根据这个时间字符串进行比较，如果不变就不刷新缓存
     * @return
     */
    public String getTimestampCode() {
        return CacheTimeStamp.CACHE_TIMESTAMP;
    }
    
    protected IDataList loadTableData(String tableName) throws Exception {
        return loadTableData(tableName, null);
    }
    
    protected IDataList loadTableData(String tableName, String sortKeys) throws Exception {
        IDataMap param = new DataHashMap();
        param.put("tableName", tableName);
        param.put("selColumns", "*");
        param.put("sortKeys", sortKeys);
        IDataList ds = getList(param);
        return ds;
    }
    
    public IDataList getList(IDataMap param) throws Exception {
        /**
         * 表名
         */
        String tableName = param.getString("tableName");
        /**
         * 查询字段
         */
        String selColumns = param.getString("selColumns");
        String[] condColumns = (String[])param.get("condColumns");
        /**
         * 查询值
         */
        String[] condValues = (String[])param.get("condValues");
        /**
         * 排序字段
         */
        String sortKeys = param.getString("sortKeys");
        /**
         * 数据源，查询哪个系统的表
         */
        String dataSrc = param.getString("dataSrc");
        /**
         * 表状态字段，用来过滤有效数据
         */
        String tableState = param.getString("tableState");
        
        IDataList ds = new DataArrayList();
        if (StringHelper.isBlank(tableName)) {
            log.error("tableName不能为空!" + tableName);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "tableName不能为空!" + tableName);
            return ds;
        }
        if (selColumns == null) {
            selColumns = "*";
        }
        
        StringBuilder sqlAConds = new StringBuilder();
        StringBuilder sql = new StringBuilder().append("SELECT ").append(selColumns).append(" FROM ").append(tableName).append(" A WHERE 1 = 1 ");
        int i = 0;
        if (condColumns != null && condValues != null && condColumns.length == condValues.length) {
            for (String col : condColumns) {
                String condValue = condValues[i];
                sqlAConds.append(" AND A.").append(col);
                if (condValue.indexOf('%') > 0) {
                    sqlAConds.append(" like  ").append(condValue).append(" ");
                }
                else {
                    sqlAConds.append(" = ").append(condValue).append(" ");
                }
                i++;
            }
            sql.append(sqlAConds);
        }
        // 状态字段过滤有效的数据
        if (StringHelper.isNonBlank(tableState)) {
            sql.append(" and ").append(tableState).append(" ");
        }
        
        if (StringHelper.isNonBlank(sortKeys)) {
            sql.append(" order by ").append(sortKeys);
        }
        //调用查询数据库
        try {
            //直接查询数据库
            ds = DbUtil.queryList(sql.toString());
            log.info("BaseReadOnlyCache直接jdbc获取数据库时间=:" + ds);
        }
        catch (Exception e) {
            log.error("BaseReadOnlyCache直接jdbc获取数据库时间异常:", e);
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, "BaseReadOnlyCache直接jdbc获取数据库时间异常！！！");
        }
        return ds;
    }
    
}
