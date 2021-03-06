package com.wade.framework.data.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang.StringUtils;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;

/**
 * HashMap 继承类
 * @Description HashMap 实现类
 * @ClassName   DataMap 
 * @Date        2016年1月20日 下午2:36:10 
 * @Author      yz.teng
 */
public class DataHashMap extends HashMap<String, Object> implements IDataMap {
    
    private static final long serialVersionUID = 1L;
    
    private static final String CLASS_STRING1 = "\"class\":";
    
    private static final String CLASS_REP_STRING1 = "\"__^TWCS^__\":";
    
    private static final String CLASS_STRING2 = "class";
    
    private static final String CLASS_REP_STRING2 = "__^TWCS^__";
    
    public DataHashMap() {
    }
    
    public DataHashMap(Map<String, Object> map) {
        super(map);
    }
    
    public DataHashMap(String jsonObject) {
        
        if ((null != jsonObject) && (jsonObject.indexOf(CLASS_STRING1) != -1)) {
            jsonObject = StringUtils.replace(jsonObject, CLASS_STRING1, CLASS_REP_STRING1);
        }
        
        if(JSONUtils.mayBeJSON(jsonObject)){
            JSONObject map = JSONObject.fromObject(jsonObject);
            if (null !=map) {
                putAll(fromJSONObject(map));
            }
        }
        
    }
    
    public static DataHashMap fromJSONObject(JSONObject object) {
        if (object != null) {
            DataHashMap data = new DataHashMap();
            Iterator keys = object.keys();
            while (keys.hasNext()) {
                Object key = keys.next();
                Object value = object.get(key);
                
                if (((String)key).indexOf(CLASS_REP_STRING2) != -1) {
                    key = StringUtils.replace((String)key, CLASS_REP_STRING2, CLASS_STRING2);
                }
                
                if (value != null) {
                    if ((value instanceof JSONObject)) {
                        data.put((String)key, JSONUtils.isNull(value) ? null : fromJSONObject((JSONObject)value));
                    }
                    else if ((value instanceof JSONArray)) {
                        data.put((String)key, JSONUtils.isNull(value) ? null : DataArrayList.fromJSONArray((JSONArray)value));
                    }
                    else if ((value instanceof String)) {
                        data.put((String)key, value);
                    }
                    else
                        data.put((String)key, value);
                }
                else {
                    data.put((String)key, value);
                }
            }
            return data;
        }
        return null;
    }
    
    public String[] getNames() {
        String[] names = new String[size()];
        Iterator keys = keySet().iterator();
        int index = 0;
        while (keys.hasNext()) {
            names[index] = ((String)keys.next());
            index++;
        }
        return names;
    }
    
    public boolean isNoN(String name) {
        return (name == null) || (!containsKey(name));
    }
    
    public String getString(String name) {
        Object value = super.get(name);
        if (value == null)
            return null;
        return value.toString();
    }
    
    public String getString(String name, String defaultValue) {
        String value = getString(name);
        if (value == null)
            return defaultValue;
        return value;
    }
    
    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }
    
    public boolean getBoolean(String name, boolean defaultValue) {
        String value = getString(name);
        return "".equals(value) ? defaultValue : Boolean.valueOf(value).booleanValue();
    }
    
    public double getDouble(String name) {
        return getDouble(name, 0.0D);
    }
    
    public double getDouble(String name, double defaultValue) {
        Object value = super.get(name);
        if (value == null)
            return defaultValue;
        
        return Double.parseDouble(value.toString());
    }
    
    public int getInt(String name) {
        return getInt(name, 0);
    }
    
    public int getInt(String name, int defaultValue) {
        Object value = super.get(name);
        if (value == null)
            return defaultValue;
        return Integer.parseInt(value.toString());
    }
    
    public long getLong(String name) {
        return getLong(name, 0L);
    }
    
    public long getLong(String name, long defaultValue) {
        Object value = super.get(name);
        if (value == null)
            return defaultValue;
        return Long.parseLong(value.toString());
    }
    
    public IDataMap getData(String name) {
        Object value = super.get(name);
        if (value == null) {
            return null;
        }
        if ((value instanceof IDataMap)) {
            return (IDataMap)value;
        }
        return null;
    }
    
    public IDataMap getData(String name, IDataMap def) {
        Object value = super.get(name);
        if (value == null) {
            return def;
        }
        if ((value instanceof IDataMap)) {
            return (IDataMap)value;
        }
        return def;
    }
    
    public IDataList getDataset(String name, IDataList def) {
        Object value = super.get(name);
        if (value == null) {
            return def;
        }
        if ((value instanceof IDataList)) {
            return (IDataList)value;
        }
        return def;
    }
    
    public IDataList getDataset(String name) {
        Object value = super.get(name);
        if (value == null) {
            return null;
        }
        if ((value instanceof IDataList)) {
            return (IDataList)value;
        }
        return null;
    }
    
    public IDataMap subData(String group) throws Exception {
        return subData(group, false);
    }
    
    public IDataMap subData(String group, boolean istrim) throws Exception {
        IDataMap element = new DataHashMap();
        
        String[] names = getNames();
        String prefix = group + "_";
        for (String name : names) {
            if (name.startsWith(prefix)) {
                element.put(istrim ? name.substring(prefix.length()) : name, get(name));
            }
        }
        
        return element;
    }
    
    public String put(String key, String value) {
        return (String)super.put(key, value);
    }
    
    public IDataMap put(String key, IDataMap value) {
        return (IDataMap)super.put(key, value);
    }
    
    public IDataList put(String key, IDataList value) {
        return (IDataList)super.put(key, value);
    }
    
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }
}