package com.wade.framework.data;

import java.io.Serializable;

import com.wade.framework.data.impl.Pagination;

public abstract interface IDataInput extends Serializable {
    public abstract IDataMap getHead();
    
    public abstract IDataMap getData();
    
    public abstract Pagination getPagination();
    
    public abstract void setPagination(Pagination paramPagination);
    
    public abstract String toString();
}