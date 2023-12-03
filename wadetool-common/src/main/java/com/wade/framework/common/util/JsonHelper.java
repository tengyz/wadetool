package com.wade.framework.common.util;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * json转换工具类
 * @Description json转换工具类
 * @ClassName   JsonHelper 
 * @Date        2015年11月4日 上午10:19:31 
 * @Author      tengyz
 */
public class JsonHelper {
    private static final Logger log = LogManager.getLogger(JsonHelper.class);
    
    /**
     * 字符串是否非空
     * 
     * @param str
     *            str
     * @return boolean
     */
    public static final boolean isNotBlank(Object str) {
        return !isBlank(str);
    }
    
    /**
     * 字符串是否为空
     * 
     * @param str
     *            str
     * @return boolean
     */
    public static final boolean isBlank(Object str) {
        if (str != null) {
            String s = str.toString();
            return "".equals(s) || "{}".equals(s) || "[]".equals(s) || "null".equals(s);
        }
        else {
            return true;
        }
    }
    
    /**
     * 判断List是否为null或者空
     * @param list
     * @return
     */
    public static boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
    
    /**
     * 判断是否是json结构
     */
    public static boolean isJson(String value) {
        try {
            JSONObject.fromObject(value);
        }
        catch (JSONException e) {
            log.error("isJson转换异常:", e);
            return false;
        }
        return true;
    }
    
    /**
     * 对象是否是数组
     * 
     * @param obj
     * @return
     */
    private static boolean isArray(Object obj) {
        return obj instanceof Collection || obj.getClass().isArray();
    }
    
    /**
     * 将一个 Object 或者List对象转化为JSONObject或者JSONArray
     * @param ObjOrList  Object 或者List对象
     * @return
     */
    public static Object toJSON(Object ObjOrList) {
        Object obj = null;
        try {
            obj = JSONObject.fromObject(ObjOrList);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("toJSON转换异常:", e);
        }
        return obj;
    }
    
    /**
     * 将一个 Object 或者List对象转化为JSOObject或者JSONArray
     * @param ObjOrList  Object 或者List对象 或者hashmap 但是如果是set  就会有问题
     * @return
     */
    public static String toJSONStr(Object ObjOrList) {
        String jsonstr = "";
        try {
            jsonstr = JSONObject.fromObject(ObjOrList).toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("toJSONStr转换异常:", e);
        }
        return jsonstr;
    }
    
    /**
     * 字符串转obj
     * @param jsonstr
     * @param clazz
     * @return
     */
    public static Object toObject(String jsonstr, Class<?> clazz) {
        Object parseObj = null;
        try {
            parseObj = JSONObject.toBean(JSONObject.fromObject(jsonstr), clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("parseToObject转换异常:", e);
        }
        return parseObj;
    }
    
    /**
     * 从一个JSON数组得到一个java对象数组，形如： [{"id" : idValue, "name" : nameValue}, {"id" :
     * idValue, "name" : nameValue}, ...]
     * 
     * @param jsonString
     * @param clazz
     * @return
     */
    public static Object[] toObjectArray(String jsonString, Class<?> clazz) {
        JSONArray array = JSONArray.fromObject(jsonString);
        Object[] obj = new Object[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            obj[i] = JSONObject.toBean(jsonObject, clazz);
        }
        return obj;
    }
    
    /**
     * 从一个JSON数组得到一个java对象集合
     * 
     * @param jsonString
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Object> toObjectList(String jsonString, Class<?> clazz) {
        JSONArray array = JSONArray.fromObject(jsonString);
        List<Object> list = new ArrayList<Object>();
        for (Iterator<Object> iter = array.iterator(); iter.hasNext();) {
            JSONObject jsonObject = (JSONObject)iter.next();
            list.add(JSONObject.toBean(jsonObject, clazz));
        }
        return list;
    }
    
    /**
     * 字符串 获取json对象
     * @param jsonString value
     * @return JSONObject
     */
    public static JSONObject toJsonObject(String jsonString) {
        JSONObject jObj;
        try {
            jObj = JSONObject.fromObject(jsonString);
        }
        catch (JSONException e) {
            log.error("toJsonObject转换异常:", e);
            jObj = new JSONObject();
        }
        return jObj;
    }
    
    public static JSONObject getJSONObject(JSONObject jo, String title) {
        if (jo.has(title)) {
            JSONObject resJo = jo.getJSONObject(title);
            if (isNotBlank(resJo) && !resJo.isEmpty()) {
                return resJo;
            }
            return null;
        }
        else {
            return null;
        }
    }
    
    /**
     * 字符串转list
     * @param jsonstr
     * @return
     */
    public static JSONArray toJsonArray(String jsonstr) {
        JSONArray parseObj = null;
        try {
            parseObj = JSONArray.fromObject(jsonstr);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("parseToJSONArray转换异常:", e);
        }
        return parseObj;
    }
    
    /**
     * json对象转换成实体bean
     * @param jsonObj
     * @param clazz
     * @return
     */
    public static Object toObject(JSONObject jsonObj, Class<?> clazz) {
        Object parseObj = null;
        try {
            parseObj = JSONObject.toBean(jsonObj, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("parseToObject转换异常:", e);
        }
        return parseObj;
    }
    
    /**
     * 将JSONArray对象转换成Map-List集合
     * @see JSONHelper#reflect(JSONObject)
     * @param json
     * @return
     */
    public static Object reflect(JSONArray json) {
        List<Object> list = new ArrayList<Object>();
        for (Object o : json) {
            if (o instanceof JSONArray) {
                list.add(reflect((JSONArray)o));
            }
            else if (o instanceof JSONObject) {
                list.add(toMap((JSONObject)o));
            }
            else {
                list.add(o);
            }
        }
        return list;
    }
    
    /**
     * 将JSONObjec对象转换成Map-List集合
     * @see JSONHelper#reflect(JSONArray)
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(JSONObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<?> keys = json.keySet();
        for (Object key : keys) {
            Object o = json.get((String)key);
            if (o instanceof JSONArray) {
                map.put((String)key, reflect((JSONArray)o));
            }
            else if (o instanceof JSONObject) {
                map.put((String)key, toMap((JSONObject)o));
            }
            else {
                map.put((String)key, o);
            }
        }
        return map;
    }
    
    /**
     * 将json字符串转换成Map-List集合
     * @see JSONHelper#reflect(JSONArray)
     * @param StringJson
     * @return
     */
    public static Map<String, Object> toMap(String StringJson) {
        JSONObject json = JSONObject.fromObject(StringJson);
        Map<String, Object> map = new HashMap<String, Object>();
        Set<?> keys = json.keySet();
        for (Object key : keys) {
            Object o = json.get((String)key);
            if (o instanceof JSONArray) {
                map.put((String)key, reflect((JSONArray)o));
            }
            else if (o instanceof JSONObject) {
                map.put((String)key, toMap((JSONObject)o));
            }
            else {
                map.put((String)key, o);
            }
        }
        return map;
    }
    
    /**
     * Map从Map<String, Object>转成Map<String, String>
     * @Description: Map从Map<String, Object>转成Map<String, String>
     * @author tengyz
     * @param objMap
     * @return
     */
    public static Map<String, String> mapToString(Map<String, Object> objMap) {
        Map<String, String> stringMap = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : objMap.entrySet()) {
            stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return stringMap;
    }
    
    public static <T> String toJSONString(List<T> list) {
        JSONArray jsonArray = JSONArray.fromObject(list);
        return jsonArray.toString();
    }
    
    public static String toJSONString(Object objectArray) {
        JSONArray jsonArray = JSONArray.fromObject(objectArray);
        return jsonArray.toString();
    }
    
    public static String toJSONString(JSONArray jsonArray) {
        return jsonArray.toString();
    }
    
    public static String toJSONString(JSONObject jsonObject) {
        return jsonObject.toString();
    }
    
    public static List toArrayList(Object object) {
        List arrayList = new ArrayList();
        JSONArray jsonArray = JSONArray.fromObject(object);
        Iterator it = jsonArray.iterator();
        while (it.hasNext()) {
            JSONObject jsonObject = (JSONObject)it.next();
            Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                Object key = keys.next();
                Object value = jsonObject.get(key);
                arrayList.add(value);
            }
        }
        return arrayList;
    }
    
    public static Collection toCollection(Object object) {
        JSONArray jsonArray = JSONArray.fromObject(object);
        return JSONArray.toCollection(jsonArray);
    }
    
    public static JSONArray toJSONArray(Object object) {
        return JSONArray.fromObject(object);
    }
    
    public static JSONObject toJsonobject(Object object) {
        return JSONObject.fromObject(object);
    }
    
    public static HashMap<String, Object> toHashMap(Object object) {
        HashMap data = new HashMap();
        JSONObject jsonObject = toJsonobject(object);
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            data.put(key, value);
        }
        return data;
    }
    
    public static List<Map<String, Object>> toList(Object object) {
        List list = new ArrayList();
        if (null == object) {
            return list;
        }
        JSONArray jsonArray = JSONArray.fromObject(object);
        for (Iterator i$ = jsonArray.iterator(); i$.hasNext();) {
            Object obj = i$.next();
            JSONObject jsonObject = (JSONObject)obj;
            Map map = new HashMap();
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                Object value = jsonObject.get(key);
                map.put(key, value);
            }
            list.add(map);
        }
        return list;
    }
    
    /**
     * 从JSONObject获取String值
     * 
     * @param jsonObj
     * @param key
     */
    public static String getStringFromJSON(JSONObject jsonObj, String key) {
        if (jsonObj == null) {
            return null;
        }
        if (!jsonObj.containsKey(key)) {
            return null;
        }
        String s = jsonObj.getString(key);
        if ("null".equals(s) || "".equals(s)) {
            s = null;
        }
        return s;
    }
    
    /**
     * 从JSONObject获取Date值
     * 
     * @param jsonObj
     * @param key
     */
    public static Date getDateFromJSON(JSONObject jsonObj, String key) {
        if (jsonObj == null) {
            return null;
        }
        if (!jsonObj.containsKey(key)) {
            return null;
        }
        String s = jsonObj.getString(key);
        if ("null".equals(s) || "".equals(s)) {
            return null;
        }
        else {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            }
            catch (java.text.ParseException e) {
                log.error("getDateFromJSON转换异常:", e);
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 取json字符串
     * 
     * @param obj
     * @return
     */
    public static String getJsonString(Object obj) {
        if (obj != null) {
            if (isArray(obj)) {
                JSONArray jsonArray = JSONArray.fromObject(obj);
                return jsonArray.toString();
            }
            else {
                JSONObject jsonObject = JSONObject.fromObject(obj);
                return jsonObject.toString();
            }
        }
        return "{}";
    }
    
    public static String getStringNX(JSONObject jsonO, String key) {
        if (jsonO.containsKey(key)) {
            return jsonO.getString(key);
        }
        return "";
    }
    
    /**
     * 从JSONObject获取Long值
     * 
     * @param jsonObj
     * @param key
     */
    public static Long getLongFromJSON(JSONObject jsonObj, String key) {
        if (jsonObj == null) {
            return null;
        }
        if (!jsonObj.containsKey(key) || "".equals(jsonObj.getString(key)) || "null".equals(jsonObj.getString(key))) {
            return null;
        }
        return jsonObj.getLong(key);
    }
    
    /**
     * 从JSONObject获取Integer值
     * 
     * @param jsonObj
     * @param key
     */
    public static Integer getIntFromJSON(JSONObject jsonObj, String key) {
        if (jsonObj == null) {
            return null;
        }
        if (!jsonObj.containsKey(key) || "".equals(jsonObj.getString(key)) || "".equals(jsonObj.getString(key))
                || "null".equals(jsonObj.getString(key))) {
            return null;
        }
        return jsonObj.getInt(key);
    }
    
    /**
     * 返回前台信息
     * 
     * @param respCode,respDesc
     * @return jsonString
     */
    public static JSONObject toJson(String respCode, Object respDesc) {
        IDataMap stateInfo = new DataHashMap();
        stateInfo.put("respCode", respCode);
        stateInfo.put("respDesc", respDesc);
        return JSONObject.fromObject(stateInfo);
    }
    
    /**
     * 返回前台信息
     * 
     * @param respCode,respDesc
     * @return jsonString
     */
    public static String toJsonString(String respCode, Object respDesc) {
        IDataMap stateInfo = new DataHashMap();
        stateInfo.put("respCode", respCode);
        stateInfo.put("respDesc", respDesc);
        return stateInfo.toString();
    }
    
    /**
     * 返回状态
     * @param respCode
     * @param respDesc
     * @param result
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject toJson(String respCode, Object respDesc, Object result) {
        IDataMap stateInfo = new DataHashMap();
        stateInfo.put("respCode", respCode);
        stateInfo.put("respDesc", respDesc);
        stateInfo.put("result", result);
        return JSONObject.fromObject(stateInfo);
    }
    
    /**
     * 返回状态
     * @param respCode
     * @param respDesc
     * @param result
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String toJsonString(String respCode, Object respDesc, Object result) {
        IDataMap stateInfo = new DataHashMap();
        stateInfo.put("respCode", respCode);
        stateInfo.put("respDesc", respDesc);
        stateInfo.put("result", result);
        return stateInfo.toString();
    }
    
    /**
     * json数据排序
     * 
     * @param array
     * @param sortKey
     * @return
     */
    public static JSONArray jsonArrayOrderBySortKey(JSONArray array, final String sortKey) {
        JSONArray cejar = new JSONArray();
        Object[] list = array.toArray();
        List<JSONObject> ceList = new ArrayList<JSONObject>();
        for (Object obj : list) {
            ceList.add((JSONObject)obj);
        }
        Collections.sort(ceList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return ((Integer)o1.getInt(sortKey)).compareTo((Integer)o2.getInt(sortKey));
            }
        });
        cejar.addAll(ceList);
        return cejar;
    }
    
    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        String tmp = "{\"err_code\":0,\"err_msg\":\"\u64cd\u4f5c\u6210\u529f\uff01\",\"sms_count\":1,\"tick_ids\":\"27911030\",\"remain_count\":6,\"server_time\":\"2015-05-07 11:47:56\"}";
        toMap(tmp);
    }
    
}
