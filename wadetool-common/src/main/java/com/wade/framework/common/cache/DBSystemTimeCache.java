package com.wade.framework.common.cache;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wade.framework.cache.localcache.AbstractReadOnlyCache;
import com.wade.framework.common.util.HttpHelper;

/**
 * 获得数据库时间和本地应用时间差
 * 
 * @Description 获得数据库时间和本地应用时间差
 * @ClassName DBSystemTimeCache
 * @Date 2016年1月20日 下午2:36:10
 * @Author yz.teng
 */
public class DBSystemTimeCache extends AbstractReadOnlyCache {
    private static final Logger log = Logger.getLogger(DBSystemTimeCache.class);
    
    public Map<String, Object> loadData() throws Exception {
        // 通过spring注解获得bean
        Map rtn = new HashMap();
        try {
            String sysdate = HttpHelper.requestService("http://localhost:8888/microservice/common/getSysDateByDB", "");
            System.out.println("[DBSystemTimeCache]==sysdate=:" + sysdate);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long offset = System.currentTimeMillis() - format.parse(sysdate).getTime();
            log.debug("[DBSystemTimeCache]==offset=:" + offset);
            rtn.put("DBSystemTimeCache", Long.valueOf(offset));
        }
        catch (Exception e) {
            log.error("获取数据库时间异常:" + e.getMessage(), e);
            rtn.put("DBSystemTimeCache", Integer.valueOf(0));
        }
        return rtn;
    }
}