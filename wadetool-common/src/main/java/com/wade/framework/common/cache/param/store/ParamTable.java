package com.wade.framework.common.cache.param.store;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.common.cache.param.ParamConfig;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.common.cache.param.data.ReadOnlyDataset;
import com.wade.framework.common.util.DataHelper;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 参数表数据结构，用于在只读缓存中保存参数表中的数据
 * <p>
 * 在只读缓存初始化时读取只读缓存
 * 
 * @author tengyz 2016-5-10 下午07:03:03
 */
public class ParamTable implements Serializable {
    
    private static final long serialVersionUID = 6053908101123034071L;
    
    /**
     * 日志操作对象
     */
    private static final Logger log = Logger.getLogger(ParamTable.class);
    
    /**
     * 表名
     */
    private String tableName = null;
    
    /**
     * 索引列表，主键也作为一个索引保存，以索引类形式存在
     */
    private ParamTableIndex[] indexes = null;
    
    /**
     * 表的全量数据，用于无条件的查询，可用于参数下拉框
     */
    private IDataList srcDatas = null;
    
    /**
     * 索引对应的数据缓存
     */
    private Map<String, IDataList>[] indexDatas = null;
    
    /**
     * 构造函数
     * 
     * @param tableName 表名
     * @param primaryKeys 主键列的集合，各列名称以逗号“,”分割
     * @param indexes 索引列的集合，各索引以“|”分割，每个索引内各列以逗号“,”分割
     * @param eparchyKey 地州字段的列名
     */
    public ParamTable(ParamConfigItem itemConf) {
        this.tableName = itemConf.getTableName();
        if (StringHelper.isBlank(tableName) || StringHelper.isBlank(itemConf.getPrimaryKeys())) {
            log.warn("buildParamTableFailed:" + tableName + itemConf.getPrimaryKeys());
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "paramtables.xml:表名和主键没有配置");
        }
        
        String[] indexArr = itemConf.getIndexArr();
        if (indexArr != null) {
            this.indexes = new ParamTableIndex[indexArr.length + 1];
            int i = 1;
            for (String idxStr : indexArr) {
                this.indexes[i] = new ParamTableIndex(idxStr, itemConf.getEparchyKey());
                this.indexes[i].setId(i);
                i++;
            }
        }
        else {
            this.indexes = new ParamTableIndex[1];
        }
        this.indexes[0] = new ParamTableIndex(itemConf.getPrimaryKeys(), itemConf.getPrimaryKeys());
    }
    
    /**
     * 加载数据函数，根据构造时传入的表名、主键列集合和索引集合组装参数表数据<br>
     * 根据表名查询全量数据，根据索引配置组装每个索引对应的数据
     * 
     * @throws Exception
     */
    public void loadData() throws Exception {
        IDataMap data = new DataHashMap();
        data.put("TABLE_NAME", tableName);
        ParamConfigItem item = ParamConfig.getParamItemConfig(tableName);
        srcDatas = item.getParamDataProvider().getAllData(item);
        if (log.isDebugEnabled()) {
            log.debug("loadData==加载参数表[" + tableName + "]数据成功，共加载数据" + srcDatas.size() + "条");
        }
        int indexCount = indexes.length;
        
        @SuppressWarnings("unchecked")
        Map<String, IDataList>[] map = new Map[indexCount];
        indexDatas = map;
        
        for (int j = 0; j < indexCount; j++) {
            long s = System.nanoTime();
            indexDatas[j] = new HashMap<String, IDataList>();
            ParamTableIndex tIdx = indexes[j];
            for (int i = 0; i < srcDatas.size(); i++) {
                IDataMap d = srcDatas.getData(i);
                String key = DataHelper.getJoinedValueByCodes(d, tIdx.getColumns());
                CacheUtil.get(indexDatas[j], key, new ICacheSourceProvider<IDataList>() {
                    @Override
                    public IDataList getSource() throws Exception {
                        return new DataArrayList();
                    }
                }).add(d);
            }
            if (log.isDebugEnabled()) {
                log.debug("构建索引数据成功:" + tableName + "-" + Arrays.toString(tIdx.getColumns()) + "共构建数据" + indexDatas[j].size() + "条" + "，耗时:"
                        + (System.nanoTime() - s));
            }
        }
        long s = System.nanoTime();
        int count = 0;
        for (int i = 0; i < indexCount; i++) {
            for (Entry<String, IDataList> entry : indexDatas[i].entrySet()) {
                entry.setValue(new ReadOnlyDataset(entry.getValue()));
                count++;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("组装只读List完毕，耗时：" + (System.nanoTime() - s) + "，共转换：" + count + "条数据");
        }
    }
    
    /**
     * 获取参数数据函数，根据传入的列集合和数据集合获取对应的数据列表
     * 
     * @param cols 列集合，需符合索引配置，否则将抛出不匹配异常
     * @param values 列对应的值集合
     * @return 索引对应的表数据
     * @throws Exception
     */
    public IDataList getList(String[] cols, String[] values, boolean like) throws Exception {
        String[] value = null;
        ParamTableIndex idx = null;
        
        if (cols == null || values == null)
            return srcDatas;
        int idxIndex = 0;
        for (; idxIndex < indexes.length; idxIndex++) {
            idx = indexes[idxIndex];
            value = idx.getValues(cols, values);
            if (value != null) {
                break;
            }
        }
        if (value == null) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, tableName + Arrays.toString(cols) + "异常");
        }
        
        if (!like) {
            for (int i = 0; i < value.length; i++) {
                IDataList ds = indexDatas[idxIndex].get(value[i]);
                if (ds != null)
                    return ds;
            }
        }
        return null;
    }
    
}
