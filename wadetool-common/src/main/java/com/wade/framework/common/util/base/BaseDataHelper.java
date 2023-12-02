package com.wade.framework.common.util.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.wade.framework.data.DataComparator;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;

/**
 * 数据类型处理基类
 * @Description 数据类型处理基类 
 * @ClassName   BaseDataHelper 
 * @Date        2017年5月25日 下午3:59:12 
 * @Author      yz.teng
 */
public final class BaseDataHelper {
    public static final void sort(IDataList data, String key, int keyType) {
        sort(data, key, keyType, 0);
    }
    
    public static final void sort(IDataList data, String key, int keyType, int order) {
        IDataMap[] maps = new DataHashMap[data.size()];
        IDataMap[] datas = (IDataMap[])data.toArray(maps);
        DataComparator c = new DataComparator(key, keyType, order);
        Arrays.sort(datas, c);
        
        List list = Arrays.asList(datas);
        
        data.clear();
        data.addAll(list);
    }
    
    public static final void sort(IDataList data, String key1, int keyType1, String key2, int keyType2) {
        sort(data, key1, keyType1, 0);
        sort(data, key2, keyType2, 0, key1, keyType1);
    }
    
    public static final void sort(IDataList data, String key1, int keyType1, int order1, String key2, int keyType2, int order2) {
        sort(data, key1, keyType1, order1);
        sort(data, key2, keyType2, order2, key1, keyType1);
    }
    
    private static final void sort(IDataList data, String key, int type, int order, String fix, int fixType) {
        IDataMap[] maps = new DataHashMap[data.size()];
        IDataMap[] datas = (IDataMap[])data.toArray(maps);
        
        DataComparator c = new DataComparator(key, type, order);
        
        if (fix == null) {
            Arrays.sort(datas, c);
        }
        else {
            int[] marks = mark(data, fix, fixType);
            
            int pre = 0;
            int i = 1;
            for (int size = marks.length; i < size; i++) {
                Arrays.sort(datas, pre, marks[i], c);
                pre = marks[i];
            }
        }
        List list = Arrays.asList(datas);
        
        data.clear();
        data.addAll(list);
    }
    
    public static final IDataList filter(IDataList source, String filter) throws Exception {
        if (StringUtils.isBlank(filter))
            return source;
        
        IDataMap ftdt = new DataHashMap();
        String[] fts = StringUtils.split(filter, ',');
        int i = 0;
        for (int size = fts.length; i < size; i++) {
            String[] ft = StringUtils.split(fts[i], '=');
            ftdt.put(ft[0], ft[1]);
        }
        
        IDataList subset = new DataArrayList();
        int i2 = 0;
        for (int size = source.size(); i2 < size; i2++) {
            IDataMap subdata = source.getData(i2);
            boolean include = true;
            String[] ftdtNames = ftdt.getNames();
            int j = 0;
            for (int nameSize = ftdtNames.length; j < nameSize; j++) {
                String subvalue = (String)subdata.get(ftdtNames[j]);
                if ((subvalue == null) || (!subvalue.equals(ftdt.get(ftdtNames[j])))) {
                    include = false;
                    break;
                }
            }
            if (include)
                subset.add(subdata);
        }
        return subset;
    }
    
    public static final IDataList distinct(IDataList source, String fieldNames, String token) throws Exception {
        if ("".equals(fieldNames))
            return source;
        
        List fieldValues = new ArrayList();
        IDataList subset = new DataArrayList();
        String theToken = (token == null) || ("".equals(token)) ? "," : token;
        
        String[] keys = fieldNames.split(theToken);
        int keySize = keys.length;
        int i = 0;
        for (int size = source.size(); i < size; i++) {
            String fieldValue = "";
            for (int j = 0; j < keySize; j++) {
                fieldValue = fieldValue + (String)source.get(i, keys[j]) + theToken;
            }
            if ((!"".equals(fieldValue)) && (!fieldValues.contains(fieldValue))) {
                fieldValues.add(fieldValue);
                subset.add(source.get(i));
            }
        }
        return subset;
    }
    
    public static final IDataMap datasetToData(IDataList list, boolean nullable) {
        IDataMap data = new DataHashMap();
        
        IDataList value = new DataArrayList();
        for (int i = 0; i < list.size(); i++) {
            value.add(nullable ? null : "");
        }
        int index = 0;
        for (Iterator i$ = list.iterator(); i$.hasNext();) {
            Object obj = i$.next();
            IDataMap d1 = null;
            if ((obj instanceof String))
                //d1 = new DataMap((String)obj);
                d1 = null; //TODO 注释掉了
            else if ((obj instanceof IDataMap)) {
                d1 = (IDataMap)obj;
            }
            Iterator iter = d1.keySet().iterator();
            while (iter.hasNext()) {
                String k1 = (String)iter.next();
                IDataList v1 = (IDataList)data.get(k1);
                
                if (null == v1) {
                    v1 = new DataArrayList();
                    v1.addAll(value);
                    
                    data.put(k1, value);
                }
                
                v1.set(index, d1.get(k1));
                data.put(k1, v1);
            }
            index++;
        }
        return data;
    }
    
    public static final int[] mark(IDataList dataset, String fix, int fixType) {
        int[] marks = new int[dataset.size() + 1];
        int idx = 0;
        
        if (fixType == 2) {
            String preValue = null;
            String curValue = null;
            int i = 0;
            for (int size = dataset.size(); i < size; i++) {
                IDataMap data = dataset.getData(i);
                curValue = data.getString(fix);
                if (!curValue.equals(preValue)) {
                    marks[(idx++)] = i;
                }
                preValue = curValue;
            }
        }
        else if (fixType == 3) {
            int preValue = -2147483648;
            int curValue = -2147483648;
            int i = 0;
            for (int size = dataset.size(); i < size; i++) {
                IDataMap data = dataset.getData(i);
                curValue = data.getInt(fix, 0);
                if (curValue != preValue) {
                    marks[(idx++)] = i;
                }
                preValue = curValue;
            }
        }
        else if (fixType == 4) {
            double preValue = (0.0D / 0.0D);
            double curValue = (0.0D / 0.0D);
            int i = 0;
            for (int size = dataset.size(); i < size; i++) {
                IDataMap data = dataset.getData(i);
                curValue = data.getDouble(fix, 0.0D);
                if (curValue != preValue) {
                    marks[(idx++)] = i;
                }
                preValue = curValue;
            }
        }
        marks[idx] = dataset.size();
        return trimRight(marks);
    }
    
    private static final int[] trimRight(int[] marks) {
        int tail = -1;
        for (int i = marks.length - 1; i >= 0; i--) {
            if (marks[i] != 0) {
                tail = i;
                break;
            }
        }
        int[] ms = new int[tail + 1];
        int i = 0;
        for (int size = ms.length; i < size; i++) {
            ms[i] = marks[i];
        }
        return ms;
    }
    
    public static void main(String[] args) {
        IDataList ds = new DataArrayList();
        IDataMap data = new DataHashMap();
        data.put("k1", "v1");
        data.put("k2", "v2");
        data.put("k3", "v3");
        ds.add(data);
        
        data = new DataHashMap();
        data.put("k1", "v1");
        ds.add(data);
        
        IDataList ds1 = new DataArrayList();
        ds1.add(data);
        
        data = new DataHashMap();
        data.put("k4", "v4");
        data.put("k7", ds1);
        ds.add(data);
        
        data = new DataHashMap();
        data.put("k1", "v1");
        data.put("k2", "v2");
        data.put("k5", "v5");
        ds.add(data);
        System.out.println(datasetToData(ds, false));
    }
}
