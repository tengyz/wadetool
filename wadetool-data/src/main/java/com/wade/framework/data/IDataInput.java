package com.wade.framework.data;

import java.io.Serializable;

import com.wade.framework.data.impl.Pagination;

/**
 * 请求参数
 */
public abstract interface IDataInput extends Serializable {
    /**
     * 获取head
     * @return
     */
    public abstract IDataMap getHead();
    
    public abstract IDataMap getData();
    
    /**
     * 获取分页
     * @return
     */
    public abstract Pagination getPagination();
    
    /**
     * 设置分页
     * @param paramPagination
     */
    public abstract void setPagination(Pagination paramPagination);
    
    public abstract String toString();
}