package com.wade.framework.common.cache;

import com.wade.framework.common.util.DataHelper;
import com.wade.framework.crypto.MD5Util;

/**
 * 缓存Key生成器<br>
 * 用于多处需要使用的缓存的Key获取
 * @author tengs
 *
 */
public class CacheKeyCreater {
    /**
     * redis缓存前缀
     */
    public static final String ACCT_KEY_PREFIX = "WADE_";
    
    public static final int CACHE_KEY_MAX_LEN = 250;
    
    /**
     * 根据传入的参数生成缓存KEY
     * @param objs
     * @return
     */
    public static final String getCacheKey(Object... objs) {
        int count = objs.length;
        
        StringBuilder sb = new StringBuilder(count * 20);
        sb.append(ACCT_KEY_PREFIX);
        for (int i = 0; i < count; i++) {
            Object o = objs[i];
            if (o == null)
                sb.append("null");
            else
                sb.append(o.getClass().isArray() ? DataHelper.join((Object[])o) : o.toString());
        }
        
        if (sb.length() > CACHE_KEY_MAX_LEN) {
            return MD5Util.hexdigest(sb.toString());
        }
        return sb.toString();
    }
    
    //    /**
    //     * 根据批次ID获取批量处理进程的执行总记录数Key，缓存到ShareCache中
    //     * @param processId
    //     * @return
    //     */
    //    public static String getBatchProcessAllCountKey(String processId) {
    //        return ACCT_KEY_PREFIX + "BatchProcess_All_" + processId;
    //    }
    
}
