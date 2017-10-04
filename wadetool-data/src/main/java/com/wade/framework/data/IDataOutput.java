package com.wade.framework.data;

import java.io.Serializable;

public abstract interface IDataOutput extends Serializable {
    public abstract IDataList getData();
    
    public abstract IDataMap getHead();
    
    public abstract long getDataCount();
    
    public abstract String toString();
}