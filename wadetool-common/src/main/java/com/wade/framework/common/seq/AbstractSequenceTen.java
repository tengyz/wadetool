package com.wade.framework.common.seq;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.util.StringHelper;

/**
 * 序列工具类(先查询数据库或者分布式id生成器)
 * @Description 序列工具类 
 * @ClassName   AbstractSequence 
 * @Date        2017年5月25日 下午3:34:17 
 * @Author      yz.teng
 */
public abstract class AbstractSequenceTen implements ISequence {
    private static final Logger log = LogManager.getLogger(AbstractSequenceTen.class);
    
    private static final int MIN_FETCH_SIZE = 1;
    
    private static final int MAX_FETCH_SIZE = 50;
    
    private static SimpleDateFormat yy = new SimpleDateFormat("yy");
    
    private String seqName;
    
    private int fetchSize;
    
    private String sql;
    
    private Map<String, Queue<String>> cacheMap = new HashMap();
    
    public AbstractSequenceTen(String seqName) {
        this(seqName, 1);
    }
    
    public AbstractSequenceTen(String seqName, int fetchSize) {
        if (StringHelper.isBlank(seqName)) {
            throw new IllegalArgumentException("序列名不能为空！");
        }
        seqName = seqName;
    }
    
    protected final String nextval(String connName) {
        if (StringHelper.isBlank(connName)) {
            throw new IllegalArgumentException("connName连接名不能为空！connName=" + connName);
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
                for (int j = 0; j < MAX_FETCH_SIZE; j++) {
                    seqCache.add(connName + generateShortUuid());
                }
                rtn = (String)seqCache.poll();
                if (null != rtn) {
                    return rtn;
                }
            }
            catch (Exception e) {
                log.error("批量获取序列时发生错误！", e);
                e.printStackTrace();
            }
        }
        return rtn;
    }
    
    public static String[] chars = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        String str = null;
        str = yy.format(System.currentTimeMillis());
        return str + shortBuffer.toString();
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
