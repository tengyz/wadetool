package com.wade.framework.data.impl;

import com.wade.framework.data.IDataInput;
import com.wade.framework.data.IDataMap;

public class DataInput implements IDataInput {
    private static final long serialVersionUID = 1L;
    
    private IDataMap data;
    
    private IDataMap head;
    
    private Pagination pagin = null;
    
    public DataInput() {
        this.head = new DataHashMap();
        this.data = new DataHashMap();
    }
    
    public DataInput(IDataMap head, IDataMap data) {
        this.head = head;
        this.data = data;
    }
    
    public IDataMap getHead() {
        return this.head;
    }
    
    public void setHead(IDataMap head) {
        this.head = head;
    }
    
    public IDataMap getData() {
        return this.data;
    }
    
    public void setData(IDataMap data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return this.pagin;
    }
    
    public void setPagination(Pagination pagin) {
        this.pagin = pagin;
        if (pagin != null) {
            this.head.put("X_PAGINCOUNT", String.valueOf(pagin.getCount()));
            this.head.put("X_PAGINCURRENT", String.valueOf(pagin.getCurrent()));
            this.head.put("X_PAGINSELCOUNT", String.valueOf(pagin.isNeedCount()));
            this.head.put("X_PAGINSIZE", String.valueOf(pagin.getPageSize()));
        }
    }
    
    public String toString() {
        StringBuilder str = new StringBuilder(100);
        str.append("{");
        if (this.head != null) {
            str.append("\"head\":" + this.head.toString());
        }
        if (this.data != null) {
            if (this.head != null) {
                str.append(",");
            }
            str.append("\"data\":" + this.data.toString());
        }
        str.append("}");
        
        return str.toString();
    }
}
