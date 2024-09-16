package com.wade.framework.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * list工具类
 * @Description list工具类 
 * @ClassName   ListHelper 
 * @Date        2018年12月10日 上午11:34:42 
 * @Author      yz.teng
 */
public class ListHelper {
    public static final int ORDER_ASCEND = 0;
    
    public static final int ORDER_DESCEND = 1;
    
    public static final int TYPE_STRING = 2;
    
    public static final int TYPE_INTEGER = 3;
    
    public static final int TYPE_DOUBLE = 4;
    
    public static <K, V> Map<K, V> firstMap(List<?> list) {
        return getMap(list, 0);
    }
    
    public static <K, V> Map<K, V> getMap(List<?> list, int index) {
        if ((list != null) && (list.size() > index)) {
            Object obj = list.get(index);
            if ((obj instanceof Map)) {
                return (Map)obj;
            }
        }
        return null;
    }
    
    public static <T> List<T> getList(List<?> list, int index) {
        if ((list != null) && (list.size() > index)) {
            Object obj = list.get(index);
            if ((obj instanceof List)) {
                return (List)obj;
            }
        }
        return null;
    }
    
    public static <T> List<T> caseIgnore(List<?> list) {
        return (List<T>)list;
    }
    
    public static String getString(List<?> list, int index, Object key) {
        return getString(list, index, key, null);
    }
    
    public static String getString(List<?> list, int index, Object key, String defValue) {
        Map map = getMap(list, index);
        return map != null ? MapHelper.getString(map, key, defValue) : defValue;
    }
    
    public static boolean getBoolean(List<?> list, int index, Object key) {
        return getBoolean(list, index, key, false);
    }
    
    public static boolean getBoolean(List<?> list, int index, Object key, boolean defValue) {
        Map map = getMap(list, index);
        return map != null ? MapHelper.getBoolean(map, key, defValue) : defValue;
    }
    
    public static int getInt(List<?> list, int index, Object key) {
        return MapHelper.getInt(getMap(list, index), key);
    }
    
    public static int getInt(List<?> list, int index, Object key, int defValue) {
        Map map = getMap(list, index);
        return map != null ? MapHelper.getInt(map, key, defValue) : defValue;
    }
    
    public static long getLong(List<?> list, int index, Object key) {
        return MapHelper.getLong(getMap(list, index), key);
    }
    
    public static long getLong(List<?> list, int index, Object key, long defValue) {
        Map map = getMap(list, index);
        return map != null ? MapHelper.getLong(map, key, defValue) : defValue;
    }
    
    public static Map<String, List<Object>> toMap(List<Map<String, Object>> list) {
        return toMap(list, null);
    }
    
    public static Map<String, List<Object>> toMap(List<Map<String, Object>> list, String[] keys) {
        if ((list == null) || (list.isEmpty())) {
            return null;
        }
        Map retMap = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (Map)list.get(i);
            if (keys != null) {
                for (String key : keys) {
                    MapHelper.addToItem(retMap, key, map.get(key));
                }
            }
            else {
                for (Map.Entry entry : map.entrySet()) {
                    MapHelper.addToItem(retMap, (String)entry.getKey(), entry.getValue());
                }
            }
        }
        return retMap;
    }
}
