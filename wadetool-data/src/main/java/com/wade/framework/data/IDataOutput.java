package com.wade.framework.data;

import java.io.Serializable;

/**
 * 返回对象
 * @author yizuteng
 */
public abstract interface IDataOutput extends Serializable {
    public abstract IDataList getData();
    
    public abstract IDataMap getHead();
    
    public abstract long getDataCount();
    
    public abstract String toString();
}