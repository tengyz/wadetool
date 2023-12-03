package com.wade.framework.data;

import java.io.Serializable;
import java.util.List;

/**
 * List 继承类（接口）,IDataset：继承了List接口的所有方法
 * @Description List 实现类
 * @ClassName   IDataset 
 * @Date        2016年1月20日 下午2:36:10 
 * @Author      yz.teng
 */
public abstract interface IDataList extends List<Object>, Serializable {
    public static final int ORDER_ASCEND = 0;
    
    public static final int ORDER_DESCEND = 1;
    
    public static final int TYPE_STRING = 2;
    
    public static final int TYPE_INTEGER = 3;
    
    public static final int TYPE_DOUBLE = 4;
    
    public static final int MAX_RECORD = 2000;

    /**
     * 获取列名数组
     * @return
     */
    public abstract String[] getNames();
    
    /**
     * 获取一行数据(IData)
     */
    public abstract Object get(int paramInt);
    
    /**
     * 获取一行数据(IData)中指定列名的数据值
     * @param paramInt
     * @param paramString
     * @return
     */
    public abstract Object get(int paramInt, String paramString);
    
    /**
     * 获取一行数据(IData)中指定列名的数据值，如果值为空，取defaultValue默认值
     * @param paramInt
     * @param paramString
     * @param paramObject
     * @return
     */
    public abstract Object get(int paramInt, String paramString, Object paramObject);
    
    /**
     * 获取一行IData结构的数据
     * @param paramInt
     * @return
     * @Date        2017年5月26日 下午3:49:07 
     * @Author      yz.teng
     */
    public abstract IDataMap getData(int paramInt);
    
    /**
     * 获取一行IDataset结构的数据
     * @param paramInt
     * @return
     * @Date        2017年5月26日 下午3:49:12 
     * @Author      yz.teng
     */
    public abstract IDataList getDataset(int paramInt);
    
    /**
     * 获取第一行
     * @return
     * @Date        2017年5月26日 下午3:49:59 
     * @Author      yz.teng
     */
    public abstract IDataMap first();
    
    /**
     * 将IDataset转成IData，data的每个名字放置一个List，这种方式适用于传递多行数据给后台调用，如前台拼传获取IDataset，然后转成IData后callTuxedoSvc
     * @return
     */
    public abstract IDataMap toData();
}