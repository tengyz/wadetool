package com.wade.framework.common.cache;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.AbstractReadOnlyCache;
import com.wade.framework.common.util.HttpHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.db.util.DbUtil;

/**
 * 获得数据库时间和本地应用时间差
 * 
 * @Description 获得数据库时间和本地应用时间差
 * @ClassName DBSystemTimeCache
 * @Date 2016年1月20日 下午2:36:10
 * @Author yz.teng
 */
public class DBSystemTimeCache extends AbstractReadOnlyCache {
    private static final Logger log = LogManager.getLogger(DBSystemTimeCache.class);
    
    public Map<String, Object> loadData() throws Exception {
        Map rtn = new HashMap();
        try {
            //调用微服务获取数据库时间
            String sysdate = HttpHelper.requestService(CacheConfig.GATEWAY_ADDR + "/common/getSysDateByDB", "");
            if (log.isDebugEnabled()) {
                log.debug("调用微服务获取数据库时间=:" + sysdate);
            }
            rtn.put("DBSystemTimeCache", getLong(sysdate));
        }
        catch (Exception e) {
            log.error("DBSystemTimeCache调用微服务获取数据库时间异常:" + e.getMessage(), e);
            try {
                //当调用微服务异常时，直接查询数据库
                DbUtil db = new DbUtil();
                IDataList getList = db.queryList("select date_format(now(),'%Y-%c-%d %H:%i:%s') as nowtimes from dual");
                IDataMap getData = getList.first();
                String sysdate = getData.getString("NOWTIMES");
                log.info("当调用微服务异常时，直接jdbc获取数据库时间=:" + sysdate);
                rtn.put("DBSystemTimeCache", getLong(sysdate));
            }
            catch (Exception e2) {
                log.error("DBSystemTimeCache直接jdbc获取数据库时间异常:" + e.getMessage(), e);
                rtn.put("DBSystemTimeCache", Long.valueOf(0));
            }
        }
        log.info("DBSystemTimeCache调用微服务获取数据库时间rtn=:" + rtn);
        return rtn;
    }
    
    public Long getLong(String sysdate) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long offset = System.currentTimeMillis() - format.parse(sysdate).getTime();
        return Long.valueOf(offset);
    }
}