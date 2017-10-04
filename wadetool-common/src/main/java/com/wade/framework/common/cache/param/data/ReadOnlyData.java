package com.wade.framework.common.cache.param.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

public class ReadOnlyData implements IDataMap {
    /**
     * 
     */
    private static final long serialVersionUID = -114877960942030528L;
    
    private IDataMap data = null;
    
    public ReadOnlyData(IDataMap data) {
        this.data = data;
    }
    
    @Override
    public boolean getBoolean(String key) {
        return data.getBoolean(key);
    }
    
    @Override
    public boolean getBoolean(String key, boolean defVal) {
        return data.getBoolean(key, defVal);
    }
    
    @Override
    public IDataMap getData(String key) {
        return data.getData(key);
    }
    
    @Override
    public IDataMap getData(String key, IDataMap defVal) {
        return data.getData(key, defVal);
    }
    
    @Override
    public IDataList getDataset(String key) {
        return data.getDataset(key);
    }
    
    @Override
    public IDataList getDataset(String key, IDataList defVal) {
        return data.getDataset(key, defVal);
    }
    
    @Override
    public double getDouble(String key) {
        return data.getDouble(key);
    }
    
    @Override
    public double getDouble(String key, double defVal) {
        return data.getDouble(key, defVal);
    }
    
    @Override
    public int getInt(String key) {
        return data.getInt(key);
    }
    
    @Override
    public int getInt(String key, int defVal) {
        return data.getInt(key, defVal);
    }
    
    @Override
    public long getLong(String key) {
        return data.getLong(key);
    }
    
    @Override
    public long getLong(String key, long defVal) {
        return data.getLong(key, defVal);
    }
    
    @Override
    public String[] getNames() {
        return data.getNames();
    }
    
    @Override
    public String getString(String key) {
        return data.getString(key);
    }
    
    @Override
    public String getString(String key, String defVal) {
        return data.getString(key, defVal);
    }
    
    @Override
    public boolean isNoN(String key) {
        return data.isNoN(key);
    }
    
    @Override
    public IDataMap subData(String group) throws Exception {
        return data.subData(group);
    }
    
    @Override
    public IDataMap subData(String group, boolean isTrim) throws Exception {
        return data.subData(group, isTrim);
    }
    
    @Override
    public void clear() {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "clear");
    }
    
    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object val) {
        return data.containsValue(val);
    }
    
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return data.entrySet();
    }
    
    @Override
    public Object get(Object key) {
        return data.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    @Override
    public Set<String> keySet() {
        return data.keySet();
    }
    
    @Override
    public Object put(String key, Object val) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "put");
        return null;
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends Object> map) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "putAll");
    }
    
    @Override
    public Object remove(Object val) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "remove");
        return null;
    }
    
    @Override
    public int size() {
        return data.size();
    }
    
    @Override
    public Collection<Object> values() {
        return data.values();
    }
    
    @Override
    public String toString() {
        return data.toString();
    }
    
}
