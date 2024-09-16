package com.wade.framework.common.cache.param;

import com.wade.framework.common.cache.ICacheSourceProvider;
import com.wade.framework.common.cache.param.data.ParamConfigItem;
import com.wade.framework.data.IDataList;

/**
 * @author yzteng
 */
public class ParamCacheSourceProvider implements ICacheSourceProvider<IDataList> {
    private ParamConfigItem conf = null;
    
    private String[] cols = null;
    
    private String[] values = null;
    
    public ParamCacheSourceProvider(ParamConfigItem conf, String[] cols, String[] values) {
        this.conf = conf;
        this.cols = cols;
        this.values = values;
    }
    
    @Override
    public IDataList getSource() throws Exception {
        return conf.getParamDataProvider().getSelectData(conf, cols, values);
    }
}