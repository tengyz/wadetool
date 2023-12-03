package com.wade.framework.data.impl;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-12-02 16:33
 * @Author yizuteng
 */
public class Pagination implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final int MAX_PAGE_SIZE = 500;
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    public static final int MAX_RECODE_SIZE = 2147483647;
    
    private static final int MAX_FETCH_SIZE = 2000;
    
    private boolean needCount = true;
    
    private boolean onlyCount = false;
    
    private long count;
    
    private int current = 1;
    
    private int pagesize;
    
    private int fetchSize;
    
    public Pagination() {
    }
    
    public Pagination(boolean isbatch, int pagesize) {
        if (isbatch)
            this.pagesize = pagesize;
    }
    
    public Pagination(int pagesize) {
        this.pagesize = pagesize;
    }
    
    public Pagination(int pagesize, int current) {
        this.pagesize = pagesize;
        this.current = current;
    }
    
    public boolean next() {
        if (this.current >= getPageCount()) {
            return false;
        }
        this.current += 1;
        return true;
    }
    
    public int getFetchSize() {
        if ((this.fetchSize == 0) && (this.pagesize > 0))
            this.fetchSize = this.pagesize;
        else
            this.fetchSize = 20;
        return this.fetchSize;
    }
    
    public void setFetchSize(int fetchSize) {
        if ((fetchSize <= MAX_FETCH_SIZE) && (fetchSize >= 0))
            this.fetchSize = fetchSize;
        else
            this.fetchSize = getDefaultPageSize();
    }
    
    public static int getMaxPageSize() {
        return Integer.parseInt("2000");
    }
    
    public static int getDefaultPageSize() {
        return Integer.parseInt("2000");
    }
    
    public boolean isNeedCount() {
        return this.needCount;
    }
    
    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public void setCount(long count) {
        this.count = count;
    }
    
    public int getPageSize() {
        return this.pagesize;
    }
    
    public void setPageSize(int pagesize) {
        this.pagesize = pagesize;
    }
    
    public long getPageCount() {
        long pageCount = getCount() / getPageSize();
        if ((pageCount == 0L) || (getCount() % getPageSize() != 0L)) {
            pageCount += 1L;
        }
        return pageCount;
    }
    
    public boolean isOnlyCount() {
        return this.onlyCount;
    }
    
    public void setOnlyCount(boolean onlyCount) {
        this.onlyCount = onlyCount;
    }
    
    public int getCurrent() {
        return this.current;
    }
    
    public void setCurrent(int current) {
        this.current = current;
    }
    
    public int getStart() {
        if (this.current <= 1)
            return 1;
        return (this.current - 1) * this.pagesize + 1;
    }
    
    public int getEnd() {
        return this.current * this.pagesize;
    }
    
    public String toString() {
        return "";
    }
}