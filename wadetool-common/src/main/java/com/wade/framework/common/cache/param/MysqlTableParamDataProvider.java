package com.wade.framework.common.cache.param;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.cache.CacheConfig;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.util.HttpHelper;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.db.util.DbUtil;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

public class MysqlTableParamDataProvider implements IParamDataProvider {
    private static final Logger log = LogManager.getLogger(MysqlTableParamDataProvider.class);
    
    @Override
    public IDataList getAllData(ParamConfigItem conf) throws Exception {
        IDataMap data = new DataHashMap();
        data.put("tableName", conf.getTableName());
        data.put("selColumns", conf.getCacheKeys());
        data.put("sortKeys", conf.getSortKeys());
        data.put("dataSrc", conf.getDataSrc());
        data.put("tableState", conf.getTableState());
        IDataList list = getList(data);
        return list;
    }
    
    @Override
    public IDataList getSelectData(ParamConfigItem conf, String[] condColumns, String[] condValues) throws Exception {
        IDataMap data = new DataHashMap();
        data.put("tableName", conf.getTableName());
        data.put("selColumns", conf.getCacheKeys());
        data.put("condColumns", condColumns);
        data.put("condValues", condValues);
        data.put("sortKeys", conf.getSortKeys());
        data.put("dataSrc", conf.getDataSrc());
        data.put("tableState", conf.getTableState());
        IDataList list = getList(data);
        return list;
    }
    
    public IDataList getList(IDataMap param) throws Exception {
        // 表名
        String tableName = param.getString("tableName");
        // 查询字段
        String selColumns = param.getString("selColumns");
        //查询的列
        String[] condColumns = (String[])param.get("condColumns");
        // 查询值
        String[] condValues = (String[])param.get("condValues");
        // 排序字段
        String sortKeys = param.getString("sortKeys");
        // 数据源，查询哪个系统的表
        String dataSrc = param.getString("dataSrc");
        // 表状态字段，用来过滤有效数据
        String tableState = param.getString("tableState");
        
        IDataList ds = new DataArrayList();
        if (StringHelper.isBlank(tableName)) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "tableName不能为空");
            return ds;
        }
        if (selColumns == null){
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
                    sqlAConds.append(" = ").append("'").append(condValue).append("' ");
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
        IDataList dsList = null;
        // 判断查询哪个系统的数据库，调用相应系统的服务查询数据
        if (StringHelper.isNonBlank(dataSrc) && "USER_CENTER_PARAM".equals(dataSrc)) {
            if (log.isDebugEnabled()) {
                log.debug("sql=:" + sql.toString());
            }
            try {
                //当调用微服务异常时，直接查询数据库
                dsList = DbUtil.queryList(sql.toString());
                log.info("MysqlTableParamDataProvider直接jdbc获取数据库时间=:" + dsList);
            }
            catch (Exception e) {
                log.error("MysqlTableParamDataProvider直接jdbc获取数据库时间异常:", e);
            }
        }
        return dsList;
    }
}
