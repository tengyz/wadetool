package com.wade.framework.data.impl;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * ArrayList 继承类
 * @Description ArrayList 实现类
 * @ClassName   DatasetList 
 * @Date        2016年1月20日 下午2:36:10 
 * @Author      yz.teng
 */
public class DataArrayList extends ArrayList<Object> implements IDataList {
    
    private static final long serialVersionUID = 1L;
    
    private static final String CLASS_STRING1 = "\"class\":";
    
    private static final String CLASS_REP_STRING1 = "\"__classToByFrameWork__\":";
    
    private static final String CLASS_STRING2 = "class";
    
    private static final String CLASS_REP_STRING2 = "__classToByFrameWork__";
    
    public DataArrayList() {
        super(20);
    }
    
    public DataArrayList(IDataMap data) {
        super(20);
        add(data);
    }
    
    public DataArrayList(IDataMap[] datas) {
        super(20);
        for (IDataMap data : datas) {
            add(data);
        }
    }
    
    public DataArrayList(IDataList list) {
        super(20);
        addAll(list);
    }
    
    public DataArrayList(String jsonArray) {
        super(20);
        if ((jsonArray != null) && (jsonArray.indexOf(CLASS_STRING1) != -1)) {
            jsonArray = StringUtils.replace(jsonArray, CLASS_STRING1, CLASS_REP_STRING1);
        }
        JSONArray array = JSONArray.fromObject(jsonArray);
        addAll(fromJSONArray(array));
    }
    
    public DataArrayList(JSONArray array) {
        super(20);
        addAll(fromJSONArray(array));
    }
    
    public static DataArrayList fromJSONArray(JSONArray array) {
        if (array != null) {
            DataArrayList list = new DataArrayList();
            int i = 0;
            for (int cnt = array.size(); i < cnt; i++) {
                Object value = array.get(i);
                if (value != null) {
                    if ((value instanceof JSONObject)) {
                        list.add(JSONUtils.isNull(value) ? null : DataHashMap.fromJSONObject((JSONObject)value));
                    }
                    else if ((value instanceof DataHashMap)) {
                        list.add((IDataMap)value);
                    }
                    else if ((value instanceof String)) {
                        if ((value != null) && (((String)value).indexOf(CLASS_REP_STRING2) != -1)) {
                            value = StringUtils.replace((String)value, CLASS_REP_STRING2, CLASS_STRING2);
                        }
                        if (((String)value).startsWith("{")) {
                            list.add(new DataHashMap((String)value));
                        }
                        else if (((String)value).startsWith("[")) {
                            list.add(new DataArrayList((String)value));
                        }
                        else {
                            list.add(value);
                        }
                    }
                    else {
                        list.add(value);
                    }
                }
                else {
                    list.add(null);
                }
            }
            return list;
        }
        return null;
    }
    
    @Override
    public String[] getNames() {
        return size() > 0 ? ((IDataMap)get(0)).getNames() : null;
    }
    
    @Override
    public Object get(int index) {
        return super.get(index);
    }
    
    @Override
    public Object get(int index, String name) {
        IDataMap data = (IDataMap)get(index);
        return data == null ? null : data.get(name);
    }
    
    @Override
    public Object get(int index, String name, Object def) {
        Object value = get(index, name);
        return value == null ? def : value;
    }
    
    @Override
    public IDataMap getData(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if ((value instanceof String)) {
            return new DataHashMap((String)value);
        }
        if ((value instanceof JSONObject)) {
            return DataHashMap.fromJSONObject((JSONObject)value);
        }
        return (IDataMap)value;
    }
    
    @Override
    public IDataList getDataset(int index) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if ((value instanceof String)) {
            return new DataArrayList((String)value);
        }
        if ((value instanceof JSONArray)) {
            return fromJSONArray((JSONArray)value);
        }
        return (IDataList)value;
    }
    
    @Override
    public IDataMap first() {
        return size() > 0 ? (IDataMap)get(0) : null;
    }
    
    @Override
    public IDataMap toData() {
        IDataMap data = new DataHashMap();
        Iterator it = iterator();
        while (it.hasNext()) {
            IDataMap element = (IDataMap)it.next();
            Iterator iterator = element.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                if (data.containsKey(key)) {
                    IDataList list = (IDataList)data.get(key);
                    list.add(element.get(key));
                }
                else {
                    IDataList list = new DataArrayList();
                    list.add(element.get(key));
                    data.put(key, list);
                }
            }
        }
        
        return data;
    }
    
    @Override
    public String toString() {
        return JSONArray.fromObject(this).toString();
    }
}