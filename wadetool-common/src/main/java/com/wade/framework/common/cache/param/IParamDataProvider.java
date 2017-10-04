package com.wade.framework.common.cache.param;

import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.data.IDataList;

public interface IParamDataProvider {
    
    public IDataList getAllData(ParamConfigItem conf) throws Exception;
    
    public IDataList getSelectData(ParamConfigItem conf, String[] condColumns, String[] condValues) throws Exception;
    
}
