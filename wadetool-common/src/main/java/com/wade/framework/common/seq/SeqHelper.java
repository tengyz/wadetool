package com.wade.framework.common.seq;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.util.CacheUtil;
import com.wade.framework.cache.util.ICacheSourceProvider;
import com.wade.framework.data.Timer;

/**
 * 根据序列名称获取序列值
 * @Description 根据序列名称获取序列值 
 * @ClassName   SeqHelper 
 * @Date        2017年6月20日 下午2:45:35 
 * @Author      yz.teng
 */
public class SeqHelper {
    private static final Logger log = LogManager.getLogger(SeqHelper.class);
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    
    private static DecimalFormat df = new DecimalFormat("000");
    
    // 编号记录
    private static long code;
    
    // 时间戳记录
    private static String timestamp;
    
    private static Class<?> seqClazz = null;
    
    private static Class<?> seqClazzFilterKeyword = null;
    
    private static Class<?> seqClazzTen = null;
    
    /**
     * 主机和进程的机器码
     */
    private static final SnowflakeSequence worker = new SnowflakeSequence();
    
    private static final Map<String, AbstractSequence> seqCacheMap = new ConcurrentHashMap<String, AbstractSequence>();
    
    private static final Map<String, AbstractSequenceTen> seqCacheMapTen = new ConcurrentHashMap<String, AbstractSequenceTen>();
    
    private static final Map<String, AbstractSequenceFilterKeyword> seqCacheMapFilterKeyword = new ConcurrentHashMap<String, AbstractSequenceFilterKeyword>();
    static {
        seqClazzTen = FuncSequenceTen.class;
        seqClazz = FuncSequence.class;
        seqClazzFilterKeyword = FuncSequenceFilterKeyword.class;
    }
    
    /**
     * 根据关键字过滤序列号
     * @param seqName 序列名称
     * @param filterKeyword 关键字(比如传入4，取出来的不等于4的)
     * @return
     * @throws Exception
     * @Date        2018年6月23日 下午4:28:10 
     * @Author      yz.teng
     */
    public static String getSeqIdBySeqName(String seqName, String filterKeyword) throws Exception {
        return getSeqId(seqName, filterKeyword);
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
        if (log.isDebugEnabled()) {
            log.debug("获取序列名称:" + seqName + " 成功:" + seqId + " ,耗时:" + timer.getUseTimeInMillis() + " ms");
        }
        return seqId;
    }
    
    protected static String getSeqId(final String seqName, final String filterKeyword) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        String seqId = CacheUtil.get(seqCacheMapFilterKeyword, seqName, new ICacheSourceProvider<AbstractSequenceFilterKeyword>() {
            @Override
            public AbstractSequenceFilterKeyword getSource() throws Exception {
                Constructor<?> c = seqClazzFilterKeyword.getConstructor(String.class, String.class);
                return (AbstractSequenceFilterKeyword)c.newInstance(seqName, filterKeyword);
            }
        }).getNextval("connName", filterKeyword);
        if (log.isDebugEnabled()) {
            log.debug("获取序列名称:" + seqName + " 成功:" + seqId + " ,耗时:" + timer.getUseTimeInMillis() + " ms");
        }
        if (null == seqId || "".equals(seqId)) {
            seqId = getSeqId(seqName, filterKeyword);
        }
        return seqId;
    }
    
    /**
     * 功能描述: <br>
     * 获取随机序列(时间字符串+递增数据串)字符串
     * @return  201809101346011039042291159056480
     * @Author      yz.teng
     */
    public static String getSeqId() {
        // 开始计算时间
        Timer timer = new Timer();
        String str = null;
        str = sdf.format(System.currentTimeMillis());
        StringBuilder strbuf = new StringBuilder();
        strbuf.append(str);
        try {
            String nextId = worker.nextId() + "";
            strbuf.append(nextId);
        }
        catch (Exception e) {
            log.error("获取SnowflakeSequence时发生错误！" + e);
            e.printStackTrace();
        }
        if (log.isDebugEnabled()) {
            log.debug("获取序列:getSeqId 成功, 耗时:" + timer.getUseTimeInMillis() + " ms");
        }
        return strbuf.toString();
    }
    
    /**
     * 分布式高效有序ID
     * @return
     * @Date        2018年6月26日 下午2:31:28 
     * @Author      yz.teng
     */
    public static long getId() {
        return worker.nextId();
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
     * 返回12位随机编码
     * @param seqName 序列前缀 比如输入：HB ，返回：HB19sO6Eyojq
     * @return HB19sO6Eyojq
     * @throws Exception
     * @Date        2019年10月30日 下午1:13:00 
     * @Author      yz.teng
     */
    public static String getSeqIdBySeqName10(String seqName) throws Exception {
        return getSeqId10(seqName);
    }
    
    protected static String getSeqId10(final String seqName) throws Exception {
        // 开始计算时间
        Timer timer = new Timer();
        String seqId = CacheUtil.get(seqCacheMapTen, seqName, new ICacheSourceProvider<AbstractSequenceTen>() {
            @Override
            public AbstractSequenceTen getSource() throws Exception {
                Constructor<?> c2 = seqClazzTen.getConstructor(String.class);
                return (AbstractSequenceTen)c2.newInstance(seqName);
            }
        }).getNextval(seqName);
        if (log.isDebugEnabled()) {
            log.debug("获取序列名称:" + seqName + " 成功:" + seqId + " ,耗时:" + timer.getUseTimeInMillis() + " ms");
        }
        return seqId;
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        // 开始计算时间
        //        Timer timer = new Timer();
        //        //8位数用0补齐
        //        //String aa = StringHelper.fillupFigure("123456789", 8, "0");
        //        String aa = getUUID();
        //        System.out.println(" aa：" + aa);
        //        System.out.println(" bb：" + getSeqId());
        //        System.out.println(" ，耗时：" + timer.getUseTimeInMillis() + " ms");
        final List<String> list = new ArrayList<String>();
        try {
            for (int i = 0; i < 10000000; i++) {
                //String applyCodeNum = SeqHelper.getSeqIdBySeqName("HRO", "5");
                //System.out.println("===========id:" + SeqHelper.getSeqIdBySeqName10("HB"));
                list.add(SeqHelper.getSeqIdBySeqName10("HB"));
                System.out.println("===========id:" + SeqHelper.getSeqIdBySeqName10("HB"));
                //                Thread thread = new Thread(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        try {
                //                            //                                            String applyCodeNum = SeqHelper.getSeqIdBySeqName("HRO", "4");
                //                            //                                            System.out.println("===========id:" + applyCodeNum);
                //                            //list.add(SeqHelper.getSeqIdBySeqName10("HB"));
                //                            //System.out.println("===========id:" + SeqHelper.getSeqIdBySeqName10("HB"));
                //                        }
                //                        catch (Exception e) {
                //                            e.printStackTrace();
                //                        }
                //                    }
                //                });
                //                thread.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("去重前的大小" + list.size());
        List<String> newlist = new ArrayList<String>(new HashSet<String>(list));
        System.out.println("去后大小" + newlist.size());
        
    }
    
}
