package com.wade.framework.data.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;

/**
 * @author yizuteng
 */
public class DatasetResult extends DataArrayList implements IDataList {
    
    private List names = new ArrayList();
    
    private int count;
    
    /**
     * construct function
     * @throws Exception
     */
    public DatasetResult() throws Exception {
        super();
    }
    
    /**
     * construct function
     * @param rs
     * @throws Exception
     */
    public DatasetResult(ResultSet rs) throws Exception {
        while (rs.next()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            
            IDataMap data = new DataHashMap();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String name = rsmd.getColumnName(i).toUpperCase();
                data.put(name, getValueByResultSet(rs, rsmd.getColumnType(i), name));
                if (rs.isFirst()) {
                    names.add(name);
                }
            }
            add(data);
        }
        
        count = size();
    }
    
    /**
     * get names
     * @return String[]
     */
    @Override
    public String[] getNames() {
        return (String[])names.toArray(new String[0]);
    }
    
    /**
     * get count
     * @return dint
     */
    public int count() {
        return count;
    }
    
    /**
     * set count
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * clear
     */
    @Override
    public void clear() {
        super.clear();
        names = new ArrayList();
        this.count = 0;
    }
    
    /**
     * get result value
     * 
     * @param rs
     * @param type
     * @param name
     * @return Object
     * @throws Exception
     */
    public static Object getValueByResultSet(ResultSet rs, int type, String name) throws Exception {
        if (type == Types.BLOB) {
            return rs.getBlob(name);
        }
        else {
            return rs.getString(name);
        }
    }
    
}