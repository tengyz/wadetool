package com.wade.framework.common.util.base;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.localcache.CacheFactory;
import com.wade.framework.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.framework.common.cache.DBSystemTimeCache;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 时间工具基类
 * 
 * @Description 时间工具基类
 * @ClassName BaseTimeUtil
 * @Date 2015年11月4日 上午10:19:31
 * @Author yz.teng
 */
public final class BaseTimeHelper {
    
    private static Logger log = LogManager.getLogger(BaseTimeHelper.class);
    
    private static IReadOnlyCache cache = null;
    
    /**
     * 
     * 功能描述: <br>
     * 服务和数据库时间差
     * 
     * @return
     * @throws Exception
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private static final long getOffset() throws Exception {
        long getOffsetValue = 0;
        try {
            cache = CacheFactory.getReadOnlyCache(DBSystemTimeCache.class);
            getOffsetValue = ((Long)cache.get("DBSystemTimeCache")).longValue();
            return getOffsetValue;
        }
        catch (Exception e) {
            getOffsetValue = System.currentTimeMillis();
            log.error("本地缓存加载失败，强制使用本地时间处理器！", e);
            return getOffsetValue;
        }
    }
    
    /**
     * 
     * 功能描述: <br>
     * 获得当前系统时间
     * 
     * @param isLocal 是否本应用时间，否是数据库时间
     * @return
     * @throws Exception
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static final long currentTimeMillis(boolean isLocal) throws Exception {
        if (isLocal) {
            return System.currentTimeMillis();
        }
        return System.currentTimeMillis() - getOffset();
    }
    
    public static final String getSysDate(String format, boolean isLocal) throws Exception {
        if (isLocal) {
            return DateFormatUtils.format(System.currentTimeMillis(), format);
        }
        long now = System.currentTimeMillis() - getOffset();
        return DateFormatUtils.format(now, format);
    }
    
    public static final String getSysDate(String format) throws Exception {
        return getSysDate(format, false);
    }
    
    public static final String getSysDate(boolean isLocal) throws Exception {
        return getSysDate("yyyy-MM-dd", isLocal);
    }
    
    public static final String getSysDate() throws Exception {
        return getSysDate("yyyy-MM-dd");
    }
    
    public static final String getSysTime(boolean isLocal) throws Exception {
        return getSysDate("yyyy-MM-dd HH:mm:ss", isLocal);
    }
    
    public static final String getSysTime() throws Exception {
        return getSysDate("yyyy-MM-dd HH:mm:ss");
    }
    
    public static final String getTimestampFormat(String value) throws Exception {
        String format = null;
        switch (value.length()) {
            case 4:
                format = "yyyy";
                break;
            case 6:
                format = "yyyyMM";
                break;
            case 7:
                format = "yyyy-MM";
                break;
            case 8:
                format = "yyyyMMdd";
                break;
            case 10:
                format = "yyyy-MM-dd";
                break;
            case 13:
                format = "yyyy-MM-dd HH";
                break;
            case 16:
                format = "yyyy-MM-dd HH:mm";
                break;
            case 19:
                format = "yyyy-MM-dd HH:mm:ss";
                break;
            case 21:
                format = "yyyy-MM-dd HH:mm:ss.S";
                break;
            case 29:
                format = "yyyyMMddHHmmssS";
            case 5:
            case 9:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
        }
        if (StringUtils.isBlank(format)) {
            throw new Exception("无法解析正确的日期格式[" + value + "]");
        }
        return format;
    }
    
    public static final Timestamp encodeTimestamp(String timeStr) throws Exception {
        String format = getTimestampFormat(timeStr);
        return encodeTimestamp(format, timeStr);
    }
    
    public static final Timestamp encodeTimestamp(String format, String timeStr) throws Exception {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        
        if (format.length() != timeStr.length()) {
            format = getTimestampFormat(timeStr);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return new Timestamp(sdf.parse(timeStr).getTime());
        }
        catch (ParseException e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "timeutil - 10001", e);
        }
        return null;
    }
    
    static {
        // 类调用时加载
        try {
            cache = CacheFactory.getReadOnlyCache(DBSystemTimeCache.class);
        }
        catch (Exception e) {
            log.error(new Object[] {new StringBuilder().append(e.getMessage()).append(" 未找到时间缓存，强制使用本地时间处理器！").toString()}, e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        System.out.println(System.currentTimeMillis());
    }
}