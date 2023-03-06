package com.wade.framework.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.common.util.base.BaseDataHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.data.impl.Pagination;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 数据处理转换工具类
 * 
 * @Description 数据处理转换工具类(数据工具类DataHelper,IData中字段判空,IData、IDataset中金额字段格式化,IDataset过滤操作)
 * 数据类型之间的转换，比如int转string
 * @ClassName DataHelper
 * @Date 2015年11月4日 上午10:19:31
 * @Author yz.teng
 */
public class DataHelper {
    private static final Logger log = LogManager.getLogger(DataHelper.class);
    
    /**
     * 逗号","
     */
    public static final String STR_SEPARATOR = ",";
    
    // 默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
    
    private static final Map<String, DecimalFormat> decimalFormatMap = new ConcurrentHashMap<String, DecimalFormat>();
    
    /**
     * 提供精确的加法运算
     * 
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double addForDou(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 提供精确的加法运算
     * 
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static String addForDou(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }
    
    /**
     * 提供精确的减法运算
     * 
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double subForDou(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
    
    public static double subForDou(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }
    
    /**
     * 提供精确的乘法运算
     * 
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mulForDou(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    /**
     * 提供（相对）精确的除法运算，当放生除不尽的情况时，精确到小数点以后10位，以后的四舍五入
     * 
     * @param v1 被除数
     * @param v2 除数
     * @return 两个除数的商
     */
    public static double divForDou(double v1, double v2) {
        return divForDou(v1, v2, DEF_DIV_SCALE);
    }
    
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入
     * 
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示需要精确到小数点以后几位
     * @return 两个参数的商
     */
    public static double divForDou(double v1, double v2, int scale) {
        if (scale < 0) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 提供精确的小数位四舍五入
     * 
     * @param v 需要四舍五入的数位
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        // 注意不可直接使用double，否则可能出现负数舍入不正确
        BigDecimal b = new BigDecimal(Double.toString(v));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * 提供精确的小数位四舍五入
     * 
     * @param String v 需要四舍五入的数位
     * @param int scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String round(String v, int scale) {
        // 注意不可直接使用double，否则可能出现负数舍入不正确
        BigDecimal b = new BigDecimal(v);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
    }
    
    /**
     * @remark 针对Idataset返回一条记录,获取字段值，若结果集为空则返回null
     * @fieldName 需要获取得key名称
     * @return String
     * @throws Exception
     */
    
    public static String getStrOfDataset(IDataList ds, String fieldName) throws Exception {
        if (ds == null || ds.isEmpty())
            return null;
        return ds.first().getString(fieldName);
    }
    
    /**
     * 从结果集中的第一条记录中获取整形字段值，若结果集为空则抛出异常
     * 
     * @param ds
     * @param fieldName
     * @return int
     * @throws Exception
     */
    public static int getIntOfDataset(IDataList ds, String fieldName) throws Exception {
        if (ds == null || ds.size() == 0)
            Thrower.throwException(BizExceptionEnum.RESULT_EMPTY);
        return ds.getData(0).getInt(fieldName);
    }
    
    /**
     * 根据字段名和字段的值筛选符合条件的记录
     * 
     * @param source
     * @param key
     * @param value
     * @return
     */
    public static IDataList filter(IDataList source, String key, String value) {
        return filter(source, new String[] {key}, new String[] {value});
    }
    
    /**
     * 根据字段名和字段的值筛选符合条件的记录
     * 
     * @param source
     * @param keys
     * @param values
     * @return
     * @author
     */
    public static IDataList filter(IDataList source, String[] keys, String[] values) {
        return filterWithType(source, keys, values, true);
    }
    
    private static IDataList filterWithType(IDataList source, String[] keys, String[] values, boolean filterEquals) {
        if (keys.length != values.length) {
            Thrower.throwException(BizExceptionEnum.INPUTPARAMS_NOT_MATCH);
        }
        
        int length = keys.length;
        
        if (length == 0)
            return source;
        
        IDataList result = new DataArrayList();
        Iterator<Object> it = source.iterator();
        
        OUTER_LABEL: while (it.hasNext()) {
            IDataMap one = (IDataMap)it.next();
            
            for (int i = 0; i < length; i++) {
                if (values[i].equals(one.getString(keys[i])) != filterEquals)
                    continue OUTER_LABEL;
            }
            
            result.add(one);
        }
        
        return result;
    }
    
    /**
     * 根据字段名和字段的值筛选不符合条件的记录
     * 
     * @param source
     * @param key
     * @param value
     * @return
     */
    public static IDataList unequalsFilter(IDataList source, String key, String value) {
        return unequalsFilter(source, new String[] {key}, new String[] {value});
    }
    
    /**
     * 根据字段名和字段的值筛选不符合条件的记录
     * 
     * @param source
     * @param keys
     * @param values
     * @return
     * @author
     */
    public static IDataList unequalsFilter(IDataList source, String[] keys, String[] values) {
        return filterWithType(source, keys, values, false);
    }
    
    /**
     * 从结果集中选择字段名对应的字段组成新的结果集
     * 
     * @param source
     * @param keys
     * @return IDataset
     */
    public static IDataList select(IDataList source, String keys) throws Exception {
        IDataList result = new DataArrayList();
        
        String[] keyArray = keys.split(",");
        
        OUTER_LABEL: for (int i = 0; i < source.size(); i++) {
            IDataMap item = source.getData(i);
            
            IDataMap newItem = new DataHashMap();
            for (String key : keyArray) {
                if (item.containsKey(key))
                    newItem.put(key, item.get(key));
                else
                    continue OUTER_LABEL;
            }
            
            result.add(newItem);
        }
        
        return result;
    }
    
    /**
     * 查找.从IDataset中查找字段名为key，其值为value的IData
     * 
     * @param source
     * @param key
     * @param value
     * @return IData
     */
    public static IDataMap locate(IDataList source, String key, String value) {
        return locate(source, new String[] {key}, new String[] {value});
    }
    
    /**
     * 查找 从IDataset中查找字段名为key，其值为value的IData
     * 
     * @param source
     * @param keys
     * @param values
     * @return IData
     */
    public static IDataMap locate(IDataList source, String[] keys, String[] values) {
        if (keys.length != values.length) {
            Thrower.throwException(BizExceptionEnum.INPUTPARAMS_NOT_MATCH);
        }
        
        int length = keys.length;
        
        if (length == 0)
            return (IDataMap)source.get(0);
        
        Iterator<Object> it = source.iterator();
        
        OUTER_LABEL: while (it.hasNext()) {
            IDataMap one = (IDataMap)it.next();
            
            for (int i = 0; i < length; i++) {
                if (!values[i].equals(one.getString(keys[i])))
                    continue OUTER_LABEL;
            }
            
            return one;
        }
        
        return null;
    }
    
    /**
     * 按照指定的key把idataset转化成HashMap
     * 
     * @param ds 的对象必须为IData
     * @param key
     * @return IData
     * @author
     */
    public static IDataMap convert2Map(IDataList ds, String key) throws Exception {
        return convert2Map(ds, new String[] {key});
    }
    
    /**
     * 按照指定的key把idataset转化成HashMap
     * 
     * @param ds 的对象必须为IData
     * @param key
     * @return IData
     * @author
     */
    public static IDataMap convert2Map(IDataList ds, String[] keyArr) throws Exception {
        IDataMap map = new DataHashMap();
        for (int i = 0; i < ds.size(); ++i) {
            IDataMap data = ds.getData(i);
            String value = getJoinedValueByCodes(data, keyArr);
            map.put(value, data);
        }
        return map;
    }
    
    /**
     * 按照指定的key把idataset转化成HashMap
     * 
     * @param ds
     *            的对象必须为IData
     * @param key
     * @return
     * @author wang.dapeng
     */
    static public HashMap convert2HashMap(IDataList ds, String key) {
        HashMap map = new HashMap();
        for (int i = 0; i < ds.size(); ++i) {
            IDataMap data = (IDataMap)ds.get(i);
            map.put(data.getString(key), data);
        }
        return map;
    }
    
    /**
     * 从IDataset里面找key对应的内容，如果和value相同，就返回目前这个IData
     * 
     * @param datas
     * @param key
     * @param value
     * @return
     * @author chenjw
     */
    public static IDataMap getTheData(IDataList datas, String key, String value) {
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            if (data.containsKey(key) && data.getString(key).equals(value)) {
                return data;
            }
        }
        return null;
    }
    
    /**
     * 在IDataset中删除键为key，值为空的元素
     * 
     * @param datas IDataset数据对象
     * @param key
     * @author
     */
    public static void removeEmptyElement(IDataList datas, String key) throws Exception {
        if (datas == null || datas.size() == 0)
            return;
        for (int i = 0; i < datas.size(); i++) {
            IDataMap data = datas.getData(i);
            if (null == data.getString(key)) {
                datas.remove(i--);
            }
        }
    }
    
    /**
     * 返回列表中指定的 fromIndex（包括 ）和 toIndex（不包括）之间的部分视图。（如果 fromIndex 和 toIndex 相等，则返回的列表为空）。
     * 
     * @param source IDataset数据对象
     * @param fromIndex
     * @param toIndex
     * @return IDataset
     */
    public static IDataList subList(IDataList source, int fromIndex, int toIndex) {
        if (fromIndex == toIndex)
            return null;
        
        if (fromIndex < 0 || toIndex > source.size() || fromIndex > toIndex)
            throw new IndexOutOfBoundsException("fromIndx:" + fromIndex + ",size:" + source.size() + ",toIndex:" + toIndex);
        
        IDataList result = new DataArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(source.get(i));
        }
        
        return result;
    }
    
    /**
     * 从集合中获得code到name的映射，适用于将code转为name的时候 以STR_SEPARATOR分割 支持多个关键字对应一个名字 若多个key 建议使用getStrByCodes从data中获取keys的String
     * 
     * @param ds code与name对应的集合
     * @param codeKey 集合中code的Keys 以","分隔
     * @param nameKey 集合中name的Key
     * @return IData
     */
    public static IDataMap getCodeNameMap(IDataList ds, String codeKeys, String nameKey) {
        IDataMap data = new DataHashMap();
        IDataMap perData = null;
        
        String[] codeKeyArr = codeKeys.split(STR_SEPARATOR);
        if (ds != null && !ds.isEmpty()) {
            for (int i = 0; i < ds.size(); i++) {
                perData = ds.getData(i);
                String key = getJoinedValueByCodes(perData, codeKeyArr);
                data.put(key, perData.get(nameKey));
            }
        }
        return data;
    }
    
    public static String join(String... valueArr) {
        return StringUtils.join(valueArr, STR_SEPARATOR);
    }
    
    public static String join(Object[] valueArr) {
        return StringUtils.join(valueArr, STR_SEPARATOR);
    }
    
    public static String join(String[] valueArr, String separator) {
        return StringUtils.join(valueArr, separator);
    }
    
    /**
     * 从IData中获取keys所对应的values的String形式 其value以STR_SEPARATOR分割 配合 getCodeNameMap 来使用
     * 
     * @param data IData数据对象
     * @param codeKeys
     * @return String
     */
    public static String getJoinedValueByCodes(IDataMap data, String codeKeys) {
        String[] codeKeyArr = codeKeys.split(STR_SEPARATOR);
        return getJoinedValueByCodes(data, codeKeyArr);
    }
    
    public static String getJoinedValueByCodes(IDataMap data, String[] codeKeyArr) {
        StringBuilder strBuilder = new StringBuilder(20);
        for (int j = 0; j < codeKeyArr.length; j++) {
            if (j > 0)
                strBuilder.append(STR_SEPARATOR);
            
            strBuilder.append(data.getString(codeKeyArr[j]));
        }
        
        return strBuilder.toString();
    }
    
    /**
     * 从IDataset里面找key对应的内容，如果和value相同，就返回包涵这个IData的新的IDataset
     * 
     * @param datas
     * @param key
     * @param value
     * @return
     * @author lif
     */
    public static IDataList getTheDataset(IDataList datas, String key, String value) {
        IDataList dataset = new DataArrayList();
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            if (data.containsKey(key) && data.getString(key).equals(value)) {
                dataset.add(data);
            }
        }
        return dataset;
    }
    
    /**
     * 从IDataset里面找key对应的内容，如果和value相同，就返回目前这个IData里面 column对应的那列值
     * 
     * @param datas
     * @param key
     * @param value
     * @param column
     * @return
     */
    public static String getTheDataValue(IDataList datas, String key, String value, String column) {
        
        IDataMap data = getTheData(datas, key, value);
        return data == null ? "" : data.getString(column);
    }
    
    /**
     * 分析数据中的所有出现的key对应value，组成一个不重复的List
     * 
     * @param datas
     * @param key
     * @return List
     * @throws Exception
     */
    public static Set<String> getDistinctValues(IDataList datas, String key) {
        Set<String> set = new HashSet<String>();
        for (int i = 0, size = datas.size(); i < size; i++) {
            IDataMap data = datas.getData(i);
            if (!StringHelper.isEmpty(data, key)) {
                set.add(data.getString(key));
            }
        }
        return set;
    }
    
    /**
     * 从datas里面取出指定列的数据，形成一个IDataset，如果没有该值，就是用默认值
     * 
     * @param datas
     * @param keys
     * @return
     * @throws Exception
     * @author chenjw
     */
    public static IDataList spliceIDataset(IDataList datas, String[] keys, String defaultValue) throws Exception {
        IDataList outParams = new DataArrayList();
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            IDataMap outParam = new DataHashMap();
            for (int i = 0; i < keys.length; i++) {
                DataHelper.setParam(outParam, keys[i], data, defaultValue);
            }
            outParams.add(outParam);
        }
        return outParams;
    }
    
    /**
     * 将datas中含有特定键值对作为一个新的IDataset返回
     * 
     * @param datas
     * @param keys
     */
    public static IDataList cloneIDataset(IDataList datas, String... keys) throws Exception {
        IDataList out = datas.getClass().newInstance();
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            IDataMap outParam = new DataHashMap();
            for (int i = 0; i < keys.length; i++) {
                DataHelper.setParam(outParam, keys[i], data);
            }
            out.add(outParam);
        }
        return out;
    }
    
    /**
     * 从datas中取出键值对中，键为key的IData，加入到一个新的IDataset中并返回
     * 
     * @param datas
     * @param key
     * @return
     * @throws Exception
     */
    public static IDataList spliceIDataset(IDataList datas, String key) throws Exception {
        IDataList outParams = new DataArrayList();
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            IDataMap outParam = new DataHashMap();
            if (!StringHelper.isEmpty(data, key)) {
                DataHelper.setParam(outParam, key, data);
                outParams.add(outParam);
            }
        }
        return outParams;
    }
    
    /**
     * 对ds中指定的若干列有则修改（将其对应的值格式化0.00后做修改）无则新增（对象中增加columns和""键值对）
     * 
     * @param ds
     * @param columns
     */
    public static void div100DatasetSpecial(IDataList ds, String[] columns) {
        for (int i = 0; i < ds.size(); i++) {
            IDataMap tmpData = (IDataMap)ds.get(i);
            for (int j = 0; j < columns.length; j++) {
                if (null == tmpData.getString(columns[j]) || tmpData.getString(columns[j]).equals("")) {
                    tmpData.put(columns[j], "");
                }
                else
                    tmpData.put(columns[j], div100(tmpData.getString(columns[j])));
            }
        }
    }
    
    /**
     * 从datas里面，把key对应的内容为null的那几行记录删除
     * 
     * @param datas
     * @param key
     */
    public static void removeBlankObj(IDataList datas, String key) {
        for (int i = 0; i < datas.size(); i++) {
            IDataMap data = (IDataMap)datas.get(i);
            if (!data.containsKey(key) || data.getString(key) == null) {
                datas.remove(data);
            }
        }
    }
    
    /**
     * 从datas里面取出指定列的数据和要进行拼串的列的数据，形成指定的列名，形成一个IDataset
     * 
     * @param datas
     * @param keys 指定要拼串的列
     * @param coloumns 指定的列
     * @param coloumnname 指定的列名
     * @return
     * @throws Exception
     */
    
    public static IDataList addIData(IDataList datas, String[] keys, String[] coloumns, String coloumnname) throws Exception {
        IDataList outParams = new DataArrayList();
        
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            StringBuilder descdata = new StringBuilder("");
            IDataMap data = (IDataMap)iter.next();
            IDataMap outParam = new DataHashMap();
            for (int i = 0; i < keys.length; i++) {
                descdata.append(descdata + data.getString(keys[i]) + " ");
            }
            outParam.put(coloumnname, descdata.substring(0, descdata.length() - 1));
            
            for (int j = 0; j < coloumns.length; j++) {
                DataHelper.setParam(outParam, coloumns[j], data);
                
            }
            outParams.add(outParam);
        }
        return outParams;
    }
    
    /**
     * 从IDataset里面找出所有与key对应的内容并且和value相同，形成一个IDataset
     * 
     * @param datas
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public static IDataList spliceIData(IDataList datas, String key, String value) throws Exception {
        IDataList outParams = new DataArrayList();
        for (Iterator<Object> iter = datas.iterator(); iter.hasNext();) {
            IDataMap data = (IDataMap)iter.next();
            IDataMap outParam = new DataHashMap();
            if (data.getString(key).equals(value)) {
                outParam.putAll(data);
                outParams.add(outParam);
            }
            
        }
        return outParams;
    }
    
    /**
     * 如果目标IData里面没有该键值或为null或为"",则抛出异常
     * 
     * @param srcData
     * @param key
     * @return
     * @throws Exception
     */
    public static String getStrEx(IDataMap srcData, String key) throws Exception {
        
        if (!srcData.containsKey(key) || srcData.getString(key) == null) {
            Thrower.throwException(BizExceptionEnum.INPUTPAMAMS_NOT_FOUND, key);
        }
        return srcData.getString(key).trim();
    }
    
    /**
     * 如果目标IData里面没有该键值或为null或为"",则抛出异常
     * 
     * @param srcData
     * @param key
     * @return
     * @throws Exception
     */
    public static int getIntEx(IDataMap srcData, String key) throws Exception {
        return toInt(getStrEx(srcData, key));
    }
    
    /**
     * 如果目标IData里面没有该键值或为null或为"",则抛出异常
     * 
     * @param srcData
     * @param key
     * @return
     * @throws Exception
     */
    public static double getDoubleEx(IDataMap srcData, String key) throws Exception {
        return toDouble(getStrEx(srcData, key));
    }
    
    /**
     * 如果目标IData里面没有该键值或为null或为"",则设默认值
     * 
     * @param descParam
     * @param key
     * @param defaultValue
     * @throws Exception
     */
    public static void setParam(IDataMap descParam, String key, String defaultValue) throws Exception {
        
        if (!descParam.containsKey(key) || descParam.getString(key) == null || descParam.getString(key).trim().equals("")) {
            descParam.put(key, defaultValue.trim());
        }
    }
    
    /**
     * 将srcData中提取本业务相关的参数,没有则使用默认值
     * 
     * @param inparam
     * @param key
     * @param defaultValue
     * @param objdata
     * @throws Exception
     */
    public static void setParam(IDataMap descParam, String key, IDataMap srcData, String defaultValue) throws Exception {
        if (srcData.containsKey(key) && srcData.getString(key) != null && !srcData.getString(key).trim().equals("")) {
            descParam.put(key, srcData.get(key));
        }
        else {
            descParam.put(key, defaultValue);
        }
    }
    
    /**
     * 将srcData中提取本业务相关的参数,没有则不设置(!针对数值性)
     * 
     * @param srcData
     * @param key
     * @param inparam
     * @throws Exception
     */
    public static void setParam(IDataMap descParam, String key, IDataMap srcData) throws Exception {
        if (srcData.containsKey(key)) {
            descParam.put(key, srcData.get(key));
        }
    }
    
    /**
     * 将srcData中提取本业务相关的参数,没有则抛出异常
     * 
     * @param srcData
     * @param key
     * @param inparam
     * @throws Exception
     */
    public static void setParamEx(IDataMap descParam, String key, IDataMap srcData) throws Exception {
        if (srcData.containsKey(key)) {
            descParam.put(key, srcData.get(key));
        }
        else {
            Thrower.throwException(BizExceptionEnum.INPUTPAMAMS_NOT_FOUND, key.toString());
        }
    }
    
    /**
     * 将输入value除以100
     * 
     * @param value
     * @return
     */
    public static String div100(double value) {
        return format2Centi(value / 100);
    }
    
    /**
     * 将输入value除以1000
     * 
     * @param value
     * @return
     */
    public static String div1000(double value) {
        return div1000(value, 3);
    }
    
    /**
     * 将输入value除以1000，四舍五入保留scale位小数
     * 
     * @param value
     * @param scale
     * @return
     */
    public static String div1000(double value, int scale) {
        return formatDecimal(value / 1000, scale);
    }
    
    /**
     * 将输入字符串除以100并格式化为0.00
     * 
     * @param value
     * @return
     */
    public static String div100(String value) {
        return value == null ? "0.00" : format2Centi(div100(toDouble(value)));
    }
    
    /**
     * 将输入字符串除以100并格式化为0.0
     * 
     * @param value
     * @return
     */
    public static String div1000(String value) {
        return div1000(value, 3);
    }
    
    /**
     * 将输入字符串除以1000并格式化为0.0
     * 
     * @param value
     * @param scale
     * @return
     */
    public static String div1000(String value, int scale) {
        return div1000(value == null ? 0.0 : toDouble(value), scale);
    }
    
    /**
     * 对ds中指定的若干列进行除100的金额转换
     * 
     * @param ds
     * @param columns
     * @author skywalker
     */
    public static void div100(IDataList ds, String[] columns) {
        for (int i = 0; i < ds.size(); i++) {
            IDataMap tmpData = (IDataMap)ds.get(i);
            for (int j = 0; j < columns.length; j++) {
                tmpData.put(columns[j], div100(tmpData.getString(columns[j])));
            }
        }
    }
    
    /**
     * 对ds中指定的若干列进行除1000的金额转换
     * 
     * @param ds
     * @param columns
     */
    public static void div1000(IDataList ds, String... columns) {
        div1000(ds, 3, columns);
    }
    
    /**
     * 对ds中指定的若干列进行除1000的金额转换
     * 
     * @param ds
     * @param scale
     * @param columns
     */
    public static void div1000(IDataList ds, int scale, String... columns) {
        for (int i = 0; i < ds.size(); i++) {
            IDataMap data = ds.getData(i);
            div1000(data, scale, columns);
        }
    }
    
    /**
     * 对Data中指定的若干列进行除1000的金额转换
     * 
     * @param ds
     * @param scale
     * @param columns
     */
    public static void div1000(IDataMap data, int scale, String... columns) {
        for (int j = 0; j < columns.length; j++) {
            data.put(columns[j], div1000(data.getString(columns[j]), scale));
        }
    }
    
    /**
     * 将输入value乘以100并取整
     * 
     * @param value
     * @return
     */
    public static String mult100(double value) {
        return mult(new BigDecimal(value), 100);
    }
    
    /**
     * 将输入value乘以100并取整
     * 
     * @param value
     * @return
     */
    public static String mult1000(double value) {
        return mult(new BigDecimal(value), 1000);
    }
    
    /**
     * 将输入value乘以multiValue并取整
     * 
     * @param value
     * @return
     */
    private static String mult(BigDecimal b1, int multiValue) {
        
        BigDecimal b2 = new BigDecimal(multiValue);
        BigDecimal r = b1.multiply(b2);
        String result = r.toString();
        if (result.indexOf(".") != -1) {
            return result.substring(0, result.indexOf("."));
        }
        return result;
    }
    
    /**
     * 将输入value乘以100并取整
     * 
     * @param value
     * @return
     */
    public static String mult100(String value) {
        return mult(new BigDecimal(value), 100);
    }
    
    /**
     * 将输入value乘以1000并取整
     * 
     * @param value
     * @return
     */
    public static String mult1000(String value) {
        return mult(new BigDecimal(value), 1000);
    }
    
    private static void mult(IDataMap data, int multiValue, String... keyArr) {
        if (keyArr == null || keyArr.length == 0)
            return;
        for (int i = 0; i < keyArr.length; i++) {
            data.put(keyArr[i], mult(new BigDecimal(data.getString(keyArr[i])), multiValue));
        }
    }
    
    /**
     * 将data里面指定的值乘以100
     * 
     * @param data
     * @param keyArr
     */
    public static void mult100(IDataMap data, String... keyArr) {
        mult(data, 100, keyArr);
    }
    
    /**
     * 将data里面指定的值乘以1000
     * 
     * @param data
     * @param keyArr
     */
    public static void mult1000(IDataMap data, String... keyArr) {
        mult(data, 1000, keyArr);
    }
    
    private static void mult(IDataList ds, int multiValue, String... keyArr) {
        if (ds == null || ds.size() == 0)
            return;
        for (int i = 0; i < ds.size(); i++) {
            mult(ds.getData(i), multiValue);
        }
    }
    
    /**
     * 对ds中指定的若干列进行乘100的金额转换
     * 
     * @param ds
     * @param columns
     * @author skywalker
     */
    public static void mult100(IDataList ds, String... columns) {
        mult(ds, 100, columns);
    }
    
    /**
     * 对ds中指定的若干列进行乘1000的金额转换
     * 
     * @param ds
     * @param columns
     * @author skywalker
     */
    public static void mult1000(IDataList ds, String... columns) {
        mult(ds, 1000, columns);
    }
    
    /**
     * 将data里面指定的值除以100
     * 
     * @param data
     * @param key
     */
    public static void div100(IDataMap data, String key) {
        if (key == null || key.equals(""))
            key = "0";
        data.put(key, div100(data.getString(key)));
    }
    
    /**
     * 将data里面指定的值除以100
     * 
     * @param data
     * @param keys
     */
    public static void div100(IDataMap data, String[] keys) {
        if (keys.length < 1)
            return;
        
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            div100(data, key);
        }
    }
    
    /**
     * 在data里面的某个内容后面加上个 String。 例如 data里面有个值是 cjw, 我们在它后面加上个'123',就可以用这个了，变为cjw123
     * 
     * @param data
     * @param key
     * @param c
     * @throws AmException
     * @author chenjw
     */
    public static void addString(IDataMap data, String key, String c) throws Exception {
        if (key == null || key.equals("")) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "addString() 时, key为 null 或 ''");
        }
        if (c == null || c.equals("")) {
            return;
        }
        data.put(key, data.getString(key) + c);
    }
    
    // --------------------------------- 类型转换
    /**
     * String 转化成 int
     * 
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        String value = obj.toString().trim();
        if (value.length() == 0)
            return 0;
        else
            return Integer.parseInt(value);
    }
    
    /**
     * String 转化成 long 并进行格式化
     * 
     * @param obj
     * @return
     */
    public static long toLong(Object obj) {
        if (obj == null) {
            return 0;
        }
        String value = obj.toString().trim();
        if (value.length() == 0)
            return 0;
        else
            return Long.parseLong(value);
    }
    
    /**
     * String 转化成 double 并进行格式化
     * 
     * @param obj
     * @return
     */
    public static double toDouble(Object obj) {
        String value;
        if (obj == null)
            return 0.00;
        value = obj.toString().trim();
        if (value.length() == 0)
            return 0.00;
        else
            return Double.parseDouble(value);
        
    }
    
    /**
     * int 转化成 String
     * 
     * @param value
     * @return
     */
    public static String toStr(int value) {
        return Integer.toString(value);
    }
    
    /**
     * 数字的String 格式化0.00后输出
     * @param obj
     * @return
     * @Date        2017年6月1日 下午4:35:00 
     * @Author      yz.teng
     */
    public static String toFormat(Object obj) {
        double value = toDouble(obj);
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }
    
    /**
     * 数字的String 格式化0.000后输出
     * @param obj
     * @return
     * @Date        2017年6月1日 下午4:35:08 
     * @Author      yz.teng
     */
    public static String toFormat2(Object obj) {
        double value = toDouble(obj);
        DecimalFormat format = new DecimalFormat("0.000");
        return format.format(value);
    }
    
    /**
     * double 转化成 String
     * 
     * @param value
     * @return
     */
    public static String toStr(double value) {
        return format2Centi(value);
    }
    
    /**
     * double 转化成 String 根据需要根是化格式化成0.00
     * @param value
     * @param bFormat
     * @return
     * @Date        2017年6月1日 下午4:34:50 
     * @Author      yz.teng
     */
    public static String toStr(double value, boolean bFormat) {
        if (bFormat)
            return toStr(value);
        else
            return Double.toString(value);
    }
    
    public static void format2Centi(IDataMap data, String... keys) {
        for (String key : keys) {
            data.put(key, format2Centi(data.get(key)));
        }
    }
    
    public static void format2Centi(IDataList ds, String... keys) {
        for (Object data : ds) {
            format2Centi((IDataMap)data, keys);
        }
    }
    
    /**
     * 根据基数以及单位数组格式化数字
     * 
     * @param value 需要格式化的数字
     * @param units 单位数组
     * @param radix 基数，时间60，流量1024
     * @param fillAllUnit 是否显示全部单位
     * @param unitLen 每个单位数字的长度
     * @return
     * @throws Exception
     */
    public static String formatNumByRadix(long value, String[] units, int radix, boolean fillAllUnit, int unitLen) throws Exception {
        DecimalFormat format = null;
        final int digitLen = unitLen < 0 ? 0 : unitLen;
        
        String key = "formatNumByRadix_" + unitLen;
        format = CacheUtil.get(decimalFormatMap, key, new ICacheSourceProvider<DecimalFormat>() {
            @Override
            public DecimalFormat getSource() throws Exception {
                if (digitLen > 0) {
                    StringBuilder pat = new StringBuilder(digitLen);
                    for (int i = 0; i < digitLen; i++)
                        pat.append('0');
                    
                    return new DecimalFormat(pat.toString());
                }
                else {
                    return new DecimalFormat();
                }
            }
        });
        
        String newValue = "";
        int j = 0;
        for (; j < units.length - 1; j++) {
            
            if (!fillAllUnit && value == 0)
                break;
            String perU = format.format(value % radix);
            newValue = perU + units[j] + newValue;
            value = value / radix;
        }
        if (value > 0 || fillAllUnit) {
            newValue = format.format(value) + units[units.length - 1] + newValue;
        }
        return newValue;
    }
    
    /**
     * double 转化成 String 并格式化成0.00
     * 
     * @param value
     * @return
     */
    public static String format2Centi(double value) {
        return format2Centi(Double.toString(value));
    }
    
    /**
     * double 转化成 String 并格式化成0.000
     * 
     * @param value
     * @return
     */
    public static String format2Milli(double value) {
        return format2Milli(Double.toString(value));
    }
    
    /**
     * 数字的String 格式化0.00后输出
     * 
     * @param obj
     * @return
     */
    public static String format2Centi(Object obj) {
        return formatDecimal(obj, 2);
    }
    
    /**
     * 数字的String 格式化0.000后输出
     * 
     * @param obj
     * @return
     */
    public static String format2Milli(Object obj) {
        return formatDecimal(obj, 3);
    }
    
    /**
     * 数字的String 格式化0.000后输出
     * 
     * @param obj
     * @return
     */
    public static String formatDecimal(Object obj, final int decimalLen) {
        double value = toDouble(obj);
        DecimalFormat format = null;
        String key = "formatDecimal_" + decimalLen;
        try {
            format = CacheUtil.get(decimalFormatMap, key, new ICacheSourceProvider<DecimalFormat>() {
                @Override
                public DecimalFormat getSource() throws Exception {
                    StringBuilder formatStr = new StringBuilder(5);
                    if (decimalLen <= 0)
                        formatStr.append("#0");
                    else {
                        formatStr.append("#0.");
                        for (int i = 0; i < decimalLen; i++) {
                            formatStr.append('0');
                        }
                    }
                    return new DecimalFormat(formatStr.toString());
                }
            });
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "获取Double格式化工具出错", e);
            format = new DecimalFormat("#0.00");
        }
        
        return format.format(value);
    }
    
    /**
     * 根据列名 添加一行合计列
     * 
     * @param ds
     * @param columns
     * @param col 这个2个是写中文 合计 2字用的
     * @param name
     */
    public static void addSumCol(IDataList ds, String[] columns, String col, String name) {
        IDataMap tmpData2 = new DataHashMap();
        tmpData2.put(col, name);
        for (int i = 0; i < columns.length; i++) {
            
            String fee = "0";
            for (int j = 0; j < ds.size(); j++) {
                IDataMap tmpData = (IDataMap)ds.get(j);
                fee = addForDou(fee, tmpData.get(columns[i]).toString());
            }
            tmpData2.put(columns[i], fee);
        }
        ds.add(tmpData2);
    }
    
    public static void to100(IDataMap data, String... keys) {
        for (String key : keys) {
            String value = data.getString(key);
            data.put(key, to100(value));
        }
    }
    
    /**
     * 厘转换单位为分
     * 
     * @param value
     */
    public static String to100(Object value) {
        String v = value.toString();
        if ("0".equals(v))
            return v;
        int val = Integer.parseInt(v);
        int i = val > 0 ? (val + 5) / 10 : (val - 5) / 10;
        return Integer.toString(i);
    }
    
    /**
     * 分转换单位为厘
     * 
     * @param value
     */
    public static String to1000(Object value) {
        String v = value.toString();
        if ("0".equals(v))
            return v;
        return v + "0";
    }
    
    /**
     * 从IDataset中获取第index位置的Data，若没有则新增并add到IDataset中
     * 
     * @param outDatas
     * @param index
     * @return
     * @throws Exception
     */
    public static IDataMap getDataFromIDataset(IDataList outDatas, int index) {
        return getDataFromIDataset(outDatas, index, null);
    }
    
    /**
     * 分页
     * @param ids
     * @param pg
     * @return
     * @throws Exception
     * @Date        2017年11月5日 下午1:54:06 
     * @Author      yz.teng
     */
    public static IDataList getOnePageData(IDataList ids, Pagination pg) throws Exception {
        try {
            
            if (pg != null)
                pg.setCount(ids != null ? ids.size() : 0);
            if (ids == null || pg == null || pg.getPageSize() >= ids.size())
                return ids;
            
            int pageSize = pg.getPageSize(); // 每页显示行数
            int currPage = pg.getCurrent(); // 目标页数
            int dataSize = ids.size(); // 数据总量
            int start = (currPage - 1) * pageSize; // 起始位置:从0开始
            
            if (currPage - 1 > dataSize / pageSize) {
                start = 0;
                currPage = 1;
            } // 考虑到越界的情况，从第一页开始
            IDataList ret = new DataArrayList();
            if (dataSize / pageSize < currPage) // 最后一页的情况
                pageSize = dataSize % pageSize;
            for (int i = 0; i < pageSize; i++) {
                Object d = ids.get(start + i);
                ret.add(d);
            }
            return ret;
        }
        catch (Exception e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "数据分页时发生错误！", e);
            return null;
        }
    }
    
    /**
     * 
     * @param outDatas
     * @param index
     * @param initData
     * @return
     * @throws Exception
     */
    public static IDataMap getDataFromIDataset(IDataList outDatas, int index, IDataMap initData) {
        IDataMap data = null;
        if (index >= outDatas.size()) {
            data = new DataHashMap();
            outDatas.add(data);
        }
        else {
            data = outDatas.getData(index);
        }
        if (initData != null && !initData.isEmpty())
            data.putAll(initData);
        return data;
    }
    
    public static IDataList newDataset(String key, String value) {
        IDataMap data = new DataHashMap();
        data.put(key, value);
        return newDataset(data);
    }
    
    public static IDataList newDataset(IDataMap data) {
        IDataList ds = new DataArrayList();
        ds.add(data);
        return ds;
    }
    
    /**
     * 对IData中设置X_RESULTCODE和X_RESULTINFO字段
     * 
     * @param outData
     * @param resultCode
     * @param resultInfo
     */
    public static void setResultInfo(IDataMap outData, int resultCode, String resultInfo) {
        outData.put("X_RESULTCODE", "" + resultCode);
        outData.put("X_RESULTINFO", resultInfo);
    }
    
    /**
     * 对IDataset中设置X_RESULTCODE和X_RESULTINFO字段
     * 
     * @param outDatas
     * @param resultCode
     * @param resultInfo
     * @throws Exception
     */
    public static void setResultInfo(IDataList outDatas, int resultCode, String resultInfo) throws Exception {
        setResultInfo(getDataFromIDataset(outDatas, 0), resultCode, resultInfo);
        
    }
    
    /**
     * IData中金额相加
     * 
     * @param srcData 相加后存放金额的IData
     * @param addData 相加的IData
     * @param keyArr 金额字段集合
     */
    public static void addFeeColumn(IDataMap srcData, IDataMap addData, String... keyArr) {
        for (String key : keyArr) {
            long addFee = addData.getLong(key, 0);
            addFeeColumn(srcData, addFee, key);
        }
    }
    
    /**
     * IData中金额相加
     * 
     * @param srcData
     * @param addFee
     * @param key
     */
    public static void addFeeColumn(IDataMap srcData, long addFee, String key) {
        long srcFee = srcData.getLong(key, 0);
        srcData.put(key, srcFee + addFee);
    }
    
    public static final void sort(IDataList data, String key, int keyType) {
        BaseDataHelper.sort(data, key, keyType);
    }
    
    public static final void sort(IDataList data, String key, int keyType, int order) {
        BaseDataHelper.sort(data, key, keyType, order);
    }
    
    public static final void sort(IDataList data, String key1, int keyType1, String key2, int keyType2) {
        BaseDataHelper.sort(data, key1, keyType1, key2, keyType2);
    }
    
    public static final void sort(IDataList data, String key1, int keyType1, int order1, String key2, int keyType2, int order2) {
        BaseDataHelper.sort(data, key1, keyType1, order1, key2, keyType2, order2);
    }
    
    public static final IDataList filter(IDataList source, String filter) throws Exception {
        return BaseDataHelper.filter(source, filter);
    }
    
    public static final IDataList distinct(IDataList source, String fieldNames, String token) throws Exception {
        return BaseDataHelper.distinct(source, fieldNames, token);
    }
    
    // ------------------------------------ IDataMap、IDataList和map list转换
    /**
     * map转IData
     * 
     * @param IDataMap
     * @return
     */
    public static IDataMap trans2IData(Map<?, ?> map) {
        IDataMap data = new DataHashMap();
        for (Map.Entry entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            
            if (value != null) {
                if ((value instanceof IDataMap))
                    data.put(key, (IDataMap)value);
                else if ((value instanceof IDataList))
                    data.put(key, (IDataList)value);
                else if ((value instanceof Map))
                    data.put(key, trans2IData((Map)value));
                else if ((value instanceof List))
                    data.put(key, trans2IDataset((List)value));
                else
                    data.put(key, value);
            }
        }
        return data;
    }
    
    /**
     * list转IDataset
     * 
     * @param IDataList
     * @return
     */
    public static IDataList trans2IDataset(List<?> list) {
        IDataList ds = new DataArrayList();
        
        for (int i = 0; i < list.size(); i++) {
            Map o = ListHelper.getMap(list, i);
            ds.add(trans2IData(o));
        }
        return ds;
    }
    
    /**
     * data转Map
     * 
     * @param Map
     * @return
     */
    public static Map<String, Object> trans2Map(IDataMap data) {
        Map map = new HashMap();
        for (String name : data.getNames()) {
            Object obj = data.get(name);
            if (obj == null)
                map.put(name, null);
            else if ((obj instanceof String))
                map.put(name, obj);
            else if ((obj instanceof IDataMap))
                map.put(name, trans2Map((IDataMap)obj));
            else if ((obj instanceof Map))
                map.put(name, (Map)obj);
            else if ((obj instanceof IDataList))
                map.put(name, trans2List((IDataList)obj));
            else if ((obj instanceof List))
                map.put(name, (List)obj);
            else {
                map.put(name, obj.toString());
            }
        }
        return map;
    }
    
    /**
     * IDataset转List
     * 
     * @param List
     * @return
     */
    public static List<Map<String, Object>> trans2List(IDataList ds) {
        List list = new ArrayList();
        for (int i = 0; i < ds.size(); i++) {
            Map o = trans2Map(ds.getData(i));
            list.add(o);
        }
        return list;
    }
    
    /**
     * 计算比例
     *  
     * @param divisor
     * @param dividend
     * @return String
     * @author fantasy 
     * @date 2013-10-9
     */
    public static String divideNumber(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return "";
        }
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0))) {
            return "0";
        }
        BigDecimal c = a.divide(b, 2, BigDecimal.ROUND_DOWN);
        return c.toString();
    }
    
    /**
     * 转换为BigDecimal
     *  
     * @param o
     * @return BigDecimal
     * @author fantasy 
     * @date 2013-8-27
     */
    public static BigDecimal toBig(Object o) {
        if (o == null || o.toString().equals("") || o.toString().equals("NaN")) {
            return new BigDecimal(0);
        }
        return new BigDecimal(o.toString());
    }
    
    /**
     * 计算百分比
     *  
     * @param divisor
     * @param dividend
     * @return String
     * @author fantasy 
     * @date 2013-8-27
     */
    public static String getPercent(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return "";
        }
        NumberFormat percent = NumberFormat.getPercentInstance();
        //建立百分比格式化引用   
        percent.setMaximumFractionDigits(2);
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0)) || a.equals(toBig(0.0)) || b.equals(toBig(0.0))) {
            return "0.00%";
        }
        BigDecimal c = a.divide(b, 4, BigDecimal.ROUND_DOWN);
        return percent.format(c);
    }
    
    public static boolean isEmpty(List args) {
        if ((args == null) || (args.size() == 0)) {
            return true;
        }
        return false;
    }
    
    public static boolean isEmpty(String[] args) {
        if ((args == null) || (args.length == 0)) {
            return true;
        }
        return false;
    }
    
    public static boolean isEmpty(Object[] args) {
        if ((args == null) || (args.length == 0)) {
            return true;
        }
        return false;
    }
    
    public static boolean isNotEmpty(Object[] args) {
        return !isEmpty(args);
    }
    
    public static boolean isNotEmpty(List args) {
        return !isEmpty(args);
    }
    
    /**
     * 测试方法
     * 
     * @param args
     */
    public static void main(String args[]) {
        //        System.out.println(DataHelper.round(11.25, 1));
        
        //        IDataset list = new DatasetList();
        //        for (int i = 0; i < 500; i++) {
        //            IData data = new DataMap();
        //            data.put("" + i, i);
        //            list.add(data);
        //        }
        //        System.out.println("======比转换list map性能========");
        //        Timer timer = new Timer();
        //        List<Map<String, Object>> getList = trans2List(list);
        //        System.out.println("useTime=:" + Long.valueOf(timer.getUseTimeInMillis()) + "ms");
        //        
        //        Timer timer2 = new Timer();
        //        IDataset getDataList = trans2IDataset(getList);
        //        System.out.println("useTime2=:" + Long.valueOf(timer2.getUseTimeInMillis()) + "ms");
        //        System.out.println("======比转换json性能========");
        //        Timer timer3 = new Timer();
        //        String getJsonFast = com.alibaba.fastjson.JSON.toJSONString(getDataList);
        //        System.out.println("useTime3=:" + Long.valueOf(timer3.getUseTimeInMillis()) + "ms");
        //        
        //        Timer timer4 = new Timer();
        //        String getJsonJack = net.sf.json.JSONArray.fromObject(getDataList).toString();
        //        System.out.println("useTime4=:" + Long.valueOf(timer4.getUseTimeInMillis()) + "ms");
        //        
        //        System.out.println("======比转换String 转datalist性能========");
        //        Timer timer5 = new Timer();
        //        com.alibaba.fastjson.JSONArray.toJavaObject(com.alibaba.fastjson.JSONArray.parseArray(getJsonJack), List.class);
        //        System.out.println("useTime5=:" + Long.valueOf(timer5.getUseTimeInMillis()) + "ms");
        //        
        //        Timer timer6 = new Timer();
        //        IDataset list3 = new DatasetList(getJsonFast);
        //        System.out.println("useTime6=:" + Long.valueOf(timer6.getUseTimeInMillis()) + "ms");
        
    }
}