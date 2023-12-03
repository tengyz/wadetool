package com.wade.framework.data.impl;

import java.io.Serializable;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.IDataOutput;

public class DataOutput implements IDataOutput, Serializable {
    private static final long serialVersionUID = 1L;
    
    private IDataMap head;
    
    private IDataList data;
    
    public DataOutput() {
        this.head = new DataHashMap();
        this.data = new DataArrayList();
    }
    
    public DataOutput(IDataMap head, IDataList data) {
        this.head = head;
        this.data = data;
    }
    
    @Override
    public IDataList getData() {
        return this.data;
    }
    
    public void setData(IDataList data) {
        this.data = data;
    }
    
    @Override
    public IDataMap getHead() {
        return this.head;
    }
    
    public void setHead(IDataMap head) {
        this.head = head;
    }
    
    @Override
    public long getDataCount() {
        return this.head.getLong("X_RESULTCOUNT", 0L);
    }
    
    @Override
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