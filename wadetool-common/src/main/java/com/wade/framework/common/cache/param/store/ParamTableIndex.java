package com.wade.framework.common.cache.param.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.wade.framework.common.util.DataHelper;
import com.wade.framework.common.util.StringHelper;

/**
 * @author yizuteng
 */
public class ParamTableIndex implements Serializable {
    
    private static final long serialVersionUID = -6673183183232359047L;
    
    private int id = -1;
    
    private String[] idxColArr = null;
    
    private int colCount = 0;
    
    private int checkNum = 0;
    
    private Map<String, Integer> idxPosMap = null;
    
    private boolean containsEparchyKey = false;
    
    private String eparchyKey = null;
    
    protected ParamTableIndex(String indexCols) {
        this(indexCols.split(","));
    }
    
    protected ParamTableIndex(String indexCols, String eparchyKey) {
        this(indexCols.split(","), eparchyKey);
    }
    
    protected ParamTableIndex(String[] indexColArr) {
        this(indexColArr, null);
    }
    
    protected ParamTableIndex(String[] indexColArr, String eparchyKey) {
        this.idxColArr = indexColArr;
        
        this.colCount = idxColArr.length;
        this.checkNum = (this.colCount - 1) * this.colCount / 2;
        this.eparchyKey = eparchyKey;
        
        boolean checkEparchyKey = !StringHelper.isBlank(eparchyKey);
        
        this.idxPosMap = new HashMap<String, Integer>(this.colCount);
        for (int i = 0; i < this.colCount; i++) {
            this.idxPosMap.put(this.idxColArr[i], i);
            if (checkEparchyKey && eparchyKey.equals(this.idxColArr[i]))
                containsEparchyKey = true;
        }
    }
    
    public String[] getColumns() {
        return this.idxColArr;
    }
    
    public String[] getValues(String[] cols, String[] values) throws Exception {
        if (cols.length != colCount)
            return null;
        
        String[] vals = new String[colCount];
        int num = 0;
        for (int i = 0; i < cols.length; i++) {
            if (!idxPosMap.containsKey(cols[i])) {
                return null;
            }
            int pos = idxPosMap.get(cols[i]);
            vals[pos] = values[i];
            num += pos;
        }
        
        if (num != checkNum) {
            return null;
        }
        
        String[] arr = new String[containsEparchyKey ? 2 : 1];
        arr[0] = DataHelper.join(vals);
        if (containsEparchyKey) {
            int pos = idxPosMap.get(eparchyKey);
            vals[pos] = "ZZZZ";
            arr[1] = DataHelper.join(vals);
        }
        return arr;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
}
