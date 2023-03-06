package com.wade.framework.common.seq;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.cache.CacheConfig;
import com.wade.framework.common.util.HttpHelper;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.db.util.DbUtil;

/**
 * 序列工具类(先查询数据库或者分布式id生成器)
 * @Description 序列工具类 
 * @ClassName   AbstractSequence 
 * @Date        2017年5月25日 下午3:34:17 
 * @Author      yz.teng
 */
public abstract class AbstractSequenceFilterKeyword implements ISequence {
    private static final Logger log = LogManager.getLogger(AbstractSequenceFilterKeyword.class);
    
    private static final int MIN_FETCH_SIZE = 1;
    
    private static final int MAX_FETCH_SIZE = 20;
    
    private String seqName;
    
    private String filterKeyword;
    
    private int fetchSize;
    
    private String sql;
    
    private Map<String, Queue<String>> cacheMap = new HashMap();
    
    public AbstractSequenceFilterKeyword(String seqName, String filterKeyword) {
        this(seqName, 1, filterKeyword);
    }
    
    public AbstractSequenceFilterKeyword(String seqName, int fetchSize, String filterKeyword) {
        if (StringHelper.isBlank(seqName)) {
            throw new IllegalArgumentException("序列名不能为空！");
        }
        if (StringHelper.isBlank(filterKeyword)) {
            throw new IllegalArgumentException("过滤关键字不能为空！");
        }
        
        if (fetchSize < MIN_FETCH_SIZE) {
            this.fetchSize = MIN_FETCH_SIZE;
            log.warn("批量获取序列，fetchSize不能为负数[fetchSize=" + fetchSize + "]，系统自动修改为" + MIN_FETCH_SIZE);
        }
        
        if (fetchSize > MAX_FETCH_SIZE) {
            this.fetchSize = MAX_FETCH_SIZE;
            log.warn("批量获取序列，fetchSize设置过大[fetchSize=" + fetchSize + "]，系统自动修改为" + MAX_FETCH_SIZE);
        }
        this.seqName = seqName;
        this.fetchSize = fetchSize;
        
        //oracle
        //this.sql = ("select " + this.seqName + ".nextval  from dual connect by level <= " + this.fetchSize);
        //拼转sql
        StringBuffer sqlAllTemp = new StringBuffer();
        sqlAllTemp.append("SELECT A.NEXTVALS FROM ( ");
        for (int i = 0; i < fetchSize; i++) {
            if (i == fetchSize - 1) {
                sqlAllTemp.append("SELECT NEXTVAL('");
                sqlAllTemp.append(seqName);
                sqlAllTemp.append("') AS NEXTVALS  FROM DUAL ");
            }
            else {
                if (i != fetchSize) {
                    sqlAllTemp.append("SELECT NEXTVAL('");
                    sqlAllTemp.append(seqName);
                    sqlAllTemp.append("') AS NEXTVALS  FROM DUAL UNION ");
                }
                else {
                    sqlAllTemp.append("SELECT NEXTVAL('");
                    sqlAllTemp.append(seqName);
                    sqlAllTemp.append("') AS NEXTVALS  FROM DUAL ");
                }
            }
        }
        sqlAllTemp.append(" )A  ORDER BY  A.NEXTVALS ASC ");
        this.sql = sqlAllTemp.toString();
    }
    
    protected final String nextval(String connName, String filterKeyword) throws Exception {
        if (StringHelper.isBlank(connName)) {
            throw new IllegalArgumentException("connName连接名不能为空！connName=" + connName);
        }
        if (StringHelper.isBlank(filterKeyword)) {
            throw new IllegalArgumentException("filterKeyword连接名不能为空！filterKeyword=" + filterKeyword);
        }
        Queue seqCache = (Queue)this.cacheMap.get(connName);
        while (null == seqCache) {
            synchronized (this) {
                seqCache = (Queue)this.cacheMap.get(connName);
                if (null != seqCache) {
                    break;
                }
                seqCache = new ConcurrentLinkedQueue();
                this.cacheMap.put(connName, seqCache);
            }
        }
        String rtn = (String)seqCache.poll();
        if (null != rtn) {
            return rtn;
        }
        synchronized (this) {
            try {
                rtn = (String)seqCache.poll();
                if (null != rtn) {
                    return rtn;
                }
                
                IDataList ds = null;
                try {
                    //调用微服务查询数据库获取序列
                    IDataMap param = new DataHashMap();
                    param.put("sql", sql);
                    String url = CacheConfig.GATEWAY_ADDR + "/common/queryList";
                    //String url = "http://10.124.131.213:8100/common/queryList";
                    String getList = HttpHelper.requestService(url, param.toString());
                    ds = new DataArrayList(getList);
                }
                catch (Exception e) {
                    log.error("AbstractSequence调用微服务查询数据库获取序列异常！", e);
                    try {
                        //当调用微服务异常时，直接查询数据库
                        DbUtil db = new DbUtil();
                        ds = db.queryList(sql);
                        log.info("AbstractSequence当调用微服务异常时，直接jdbc获取数据库时间=:" + ds);
                    }
                    catch (Exception e2) {
                        log.error("AbstractSequence直接jdbc获取数据库时间异常:", e);
                    }
                }
                
                if (null != ds && !"".equals(ds)) {
                    if (null != ds.getData(0) && !"".equals(ds.getData(0))) {
                        if (ds.getData(0).getString("NEXTVALS").indexOf("4") >= 0) {
                            log.info(("包含关键词:" + ds.getData(0).getString("NEXTVALS")));
                        }
                        else {
                            rtn = ds.getData(0).getString("NEXTVALS");
                        }
                    }
                    
                    for (int j = 0; j < ds.size(); j++) {
                        IDataMap getData = ds.getData(j);
                        if (0 != j) {
                            if (StringHelper.isNonBlank(getData.getString("NEXTVALS"))) {
                                //使用indexOf方法
                                if (getData.getString("NEXTVALS").indexOf("4") >= 0) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("包含关键词:" + getData.getString("NEXTVALS"));
                                    }
                                }
                                else {
                                    if (null == rtn) {
                                        rtn = getData.getString("NEXTVALS");
                                    }
                                    seqCache.add(getData.getString("NEXTVALS"));
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error("批量获取序列时发生错误！", e);
                e.printStackTrace();
            }
        }
        return rtn;
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        //        String sqlStartTemp1 = "select aaa.nextvals from ( ";
        //        String sqlTemp2 = "select nextval('seq_test1_num1') as nextvals  from dual";
        //        String sqlEndTemp1 = " )aaa  order by  aaa.nextvals asc ";
        //        String sqlAllTemp = "";
        //        for (int i = 0; i < 3; i++) {
        //            if (i == 3 - 1) {
        //                sqlAllTemp = sqlAllTemp + sqlTemp2;
        //            }
        //            else {
        //                sqlAllTemp = sqlAllTemp + sqlTemp2 + " UNION ";
        //            }
        //            
        //        }
        //        sqlAllTemp = sqlStartTemp1 + sqlAllTemp + sqlEndTemp1;
        //        
        //        System.out.println(" sqlAllTemp：" + sqlAllTemp);
        
    }
    
}
