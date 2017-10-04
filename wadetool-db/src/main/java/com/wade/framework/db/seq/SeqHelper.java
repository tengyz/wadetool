package com.wade.framework.db.seq;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.common.util.StringHelper;
import com.wade.framework.data.Timer;

/**
 * 根据序列名称获取序列值
 * @Description 根据序列名称获取序列值 
 * @ClassName   SeqHelper 
 * @Date        2017年6月20日 下午2:45:35 
 * @Author      yz.teng
 */
public class SeqHelper {
    private static final Logger log = Logger.getLogger(SeqHelper.class);
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    
    private static DecimalFormat df = new DecimalFormat("000");
    
    // 编号记录
    private static long code;
    
    // 时间戳记录
    private static String timestamp;
    
    private static Class<?> seqClazz = null;
    
    private static final Map<String, AbstractSequence> seqCacheMap = new ConcurrentHashMap<String, AbstractSequence>();
    static {
        seqClazz = FuncSequence.class;
    }
    
    /**
     * 根据账户ID到账务库取序列号
     * @param seqName 序列名称
     * @param acctId 账户ID
     * @return
     * @throws Exception
     */
    public static String getSeqIdBySeqName(String seqName) throws Exception {
        return getSeqId(seqName);
    }
    
    protected static String getSeqId(final String seqName) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        String seqId = CacheUtil.get(seqCacheMap, seqName, new ICacheSourceProvider<AbstractSequence>() {
            @Override
            public AbstractSequence getSource() throws Exception {
                Constructor<?> c = seqClazz.getConstructor(String.class);
                return (AbstractSequence)c.newInstance(seqName);
            }
        }).getNextval("connName");
        log.info("获取序列名称:" + seqName + " 成功:" + seqId + " ,耗时:" + timer.getUseTimeInMillis() + " ms");
        
        return seqId;
    }
    
    /**
     * 功能描述: <br>
     * 获取随机序列
     * @return  201612202221480037754
     * @Author      yz.teng
     */
    public static synchronized String getSeqId() {
        // 开始计算时间
        Timer timer = new Timer();
        String str = null;
        str = sdf.format(System.currentTimeMillis());
        if (null != timestamp && timestamp.trim().length() > 0) {
            if (timestamp.equals(str)) {
                code++;
            }
            else {
                timestamp = str;
                code = 1;
            }
        }
        else {
            timestamp = str;
            code = 1;
        }
        StringBuilder strbuf = new StringBuilder();
        strbuf.append(str);
        strbuf.append(df.format(code));
        strbuf.append(StringHelper.getRandomNum(4));
        if (log.isDebugEnabled()) {
            log.debug("获取序列:getSeqId 成功, 耗时:" + timer.getUseTimeInMillis() + " ms");
        }
        return strbuf.toString();
    }
    
    /**
     * 获取字符串UUID（除掉"-"）
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        //        String aa = StringHelper.fillupFigure("123456789", 8, "0");
        String aa = getUUID();
        System.out.println(" aa：" + aa);
        System.out.println(" ，耗时：" + timer.getUseTimeInMillis() + " ms");
        
    }
}
