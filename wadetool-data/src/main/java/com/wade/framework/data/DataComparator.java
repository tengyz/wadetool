package com.wade.framework.data;

import java.util.Comparator;

import net.sf.json.JSONObject;

public class DataComparator implements Comparator<Object> {
    private String key;
    
    private int keyType;
    
    private int order;
    
    public DataComparator(String key, int keyType, int order) {
        this.key = key;
        this.keyType = keyType;
        this.order = order;
    }
    
    public int compare(Object o1, Object o2) {
        if (((o1 instanceof JSONObject)) && ((o2 instanceof JSONObject))) {
            JSONObject data1 = (JSONObject)o1;
            JSONObject data2 = (JSONObject)o2;
            return jsonobjCompare(data1, data2);
        }
        if (((o1 instanceof IDataMap)) && ((o2 instanceof IDataMap))) {
            IDataMap data1 = (IDataMap)o1;
            IDataMap data2 = (IDataMap)o2;
            return idataCompare(data1, data2);
        }
        return 0;
    }
    
    private int idataCompare(IDataMap data1, IDataMap data2) {
        if (this.order == 0) {
            if (this.keyType == 2) {
                String value1 = data1.getString(this.key);
                String value2 = data2.getString(this.key);
                return value1.compareTo(value2);
            }
            if (this.keyType == 3) {
                int value1 = data1.getInt(this.key, 0);
                int value2 = data2.getInt(this.key, 0);
                return value1 == value2 ? 0 : value1 < value2 ? -1 : 1;
            }
            if (this.keyType == 4) {
                double value1 = data1.getDouble(this.key, 0.0D);
                double value2 = data2.getDouble(this.key, 0.0D);
                return value1 == value2 ? 0 : value1 < value2 ? -1 : 1;
            }
        }
        else {
            if (this.keyType == 2) {
                String value1 = data1.getString(this.key);
                String value2 = data2.getString(this.key);
                return value2.compareTo(value1);
            }
            if (this.keyType == 3) {
                int value1 = data1.getInt(this.key, 0);
                int value2 = data2.getInt(this.key, 0);
                return value1 == value2 ? 0 : value1 > value2 ? -1 : 1;
            }
            if (this.keyType == 4) {
                double value1 = data1.getDouble(this.key, 0.0D);
                double value2 = data2.getDouble(this.key, 0.0D);
                return value1 == value2 ? 0 : value1 > value2 ? -1 : 1;
            }
        }
        return 0;
    }
    
    private int jsonobjCompare(JSONObject data1, JSONObject data2) {
        if (this.order == 0) {
            if (this.keyType == 2) {
                String value1 = data1.getString(this.key);
                String value2 = data2.getString(this.key);
                return value1.compareTo(value2);
            }
            if (this.keyType == 3) {
                int value1 = Integer.parseInt(data1.getString(this.key));
                int value2 = Integer.parseInt(data2.getString(this.key));
                return value1 == value2 ? 0 : value1 < value2 ? -1 : 1;
            }
            if (this.keyType == 4) {
                double value1 = data1.getDouble(this.key);
                double value2 = data2.getDouble(this.key);
                return value1 == value2 ? 0 : value1 < value2 ? -1 : 1;
            }
        }
        else {
            if (this.keyType == 2) {
                String value1 = data1.getString(this.key);
                String value2 = data2.getString(this.key);
                return value2.compareTo(value1);
            }
            if (this.keyType == 3) {
                int value1 = Integer.parseInt(data1.getString(this.key));
                int value2 = Integer.parseInt(data2.getString(this.key));
                return value1 == value2 ? 0 : value1 > value2 ? -1 : 1;
            }
            if (this.keyType == 4) {
                double value1 = data1.getDouble(this.key);
                double value2 = data2.getDouble(this.key);
                return value1 == value2 ? 0 : value1 > value2 ? -1 : 1;
            }
        }
        return 0;
    }
}
