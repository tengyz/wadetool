package com.wade.framework.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Map 继承类(接口),IData：继承了Map接口的所有方法
 * @Description Map 实现类,注：获取值转换成原生数据类型时，如果获取的为空或空串，会自动赋默认值，如getInt获取的值为空或空串时会转为0
 * @ClassName   IData 
 * @Date        2016年1月20日 下午2:36:10 
 * @Author      yz.teng
 */
public abstract interface IDataMap extends Map<String, Object>, Serializable {
    public abstract boolean isNoN(String paramString);
    
    /**
     * 获取列名数组
     * @return
     */
    public abstract String[] getNames();
    
    /**
     * 获取指定列名的字符型数据值，如果值为空，返回空对象，不会自动转为空串
     * @param paramString
     * @return
     */
    public abstract String getString(String paramString);
    
    /**
     * 根据key获取值，如果获取不到就把默认值返回
     * @param paramString1 key
     * @param paramString2 默认值
     * @return
     */
    public abstract String getString(String paramString1, String paramString2);
    
    /**
     * 获取指定列名的布尔型数据值，如果值为空或为空串，取false
     * @param paramString
     * @return
     */
    public abstract boolean getBoolean(String paramString);
    
    /**
     * 获取指定列名的布尔数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramBoolean
     * @return
     */
    public abstract boolean getBoolean(String paramString, boolean paramBoolean);
    
    /**
     * 获取指定列名的整型数据值，如果值为空或为空串，取0
     * @param paramString
     * @return
     */
    public abstract int getInt(String paramString);
    
    /**
     * 定列名的整型数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramInt
     * @return
     */
    public abstract int getInt(String paramString, int paramInt);
    
    /**
     * 获取long类型的
     * @param paramString
     * @return
     * @Date        2017年5月26日 下午3:39:41 
     * @Author      yz.teng
     */
    public abstract long getLong(String paramString);
    
    /**
     * 定列名的long数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramLong
     * @return
     * @Date        2017年5月26日 下午3:40:31 
     * @Author      yz.teng
     */
    public abstract long getLong(String paramString, long paramLong);
    
    /**
     * 获取指定列名的浮点型数据值，如果值为空或为空串，取0
     * @param paramString
     * @return
     */
    public abstract double getDouble(String paramString);
    
    /**
     * 获取指定列名的浮点数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramDouble
     * @return
     */
    public abstract double getDouble(String paramString, double paramDouble);
    
    /**
     * ;获取指定列名的IDataset数据值
     * @param paramString
     * @return
     * @Date        2017年5月26日 下午3:45:48 
     * @Author      yz.teng
     */
    public abstract IDataList getDataset(String paramString);
    
    /**
     * ;获取指定列名的IDataset数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramIDataset
     * @return
     * @Date        2017年5月26日 下午3:45:54 
     * @Author      yz.teng
     */
    public abstract IDataList getDataset(String paramString, IDataList paramIDataset);
    
    /**
     * 获取指定列名的IData数据值
     * @param paramString
     * @return
     * @Date        2017年5月26日 下午3:45:14 
     * @Author      yz.teng
     */
    public abstract IDataMap getData(String paramString);
    
    /**
     * 获取指定列名的IData数据值，如果值为空或为空串，取defaultValue默认值
     * @param paramString
     * @param paramIData
     * @return
     * @Date        2017年5月26日 下午3:45:23 
     * @Author      yz.teng
     */
    public abstract IDataMap getData(String paramString, IDataMap paramIData);
    
    public abstract IDataMap subData(String paramString) throws Exception;
    
    public abstract IDataMap subData(String paramString, boolean paramBoolean) throws Exception;
    
    /**
     * 打印数据
     * @return
     * @Date        2017年5月26日 下午3:41:15 
     * @Author      yz.teng
     */
    public abstract String toString();
    
}