package com.wade.framework.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerFactory;

/**
 * 日志工具类，提供debug、info、warn、error等级别的日志输出
 * 
 * @Description 日志工具类，提供debug、info、warn、error等级别的日志输出
 * @ClassName Logger
 * @Date 2016年9月18日 下午2:51:12
 * @Author tengyizu
 */
public class Logger {
    @SuppressWarnings("unchecked")
    private static final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap();
    
    private static final LoggerFactory factory = new SysLoggerFactory(Logger.class);
    
    private static int LOG_LIST_MAX_SIZE = 10;
    
    private org.apache.log4j.Logger log = null;
    
    public Logger(String name) {
        this.log = SysLogger.getLogger(name, factory);
    }
    
    /**
     * 根据标识名称获取一个Logger的实例
     * 
     * @param name 日志标识
     * @return
     */
    public static Logger getLogger(String name) {
        if (loggerMap.containsKey(name))
            return (Logger)loggerMap.get(name);
        Logger log = new Logger(name);
        loggerMap.put(name, log);
        return log;
    }
    
    /**
     * 根据Class获取一个Logger的实例
     * 
     * @param clazz Class类
     * @return
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
    
    /**
     * 是否开启了debug的日志
     * 
     * @return
     */
    public boolean isDebugEnabled() {
        return this.log.isEnabledFor(Level.DEBUG);
    }
    
    /**
     * 打印info信息
     * 
     * @param obj 需打印的内容集合
     */
    public void info(Object... objs) {
        logger(Level.INFO, objs);
    }
    
    /**
     * 打印debug信息
     * 
     * @param objs 需要打印的内容集合
     */
    public void debug(Object... objs) {
        logger(Level.DEBUG, objs);
    }
    
    /**
     * 打印warn信息
     * 
     * @param objs 需要打印的内容集合
     */
    public void warn(Object... objs) {
        warn(null, objs);
    }
    
    /**
     * 打印warn信息，包含异常信息
     * 
     * @param e 需要打印的异常
     * @param objs 需要打印的warn信息集合
     */
    public void warn(Throwable e, Object... objs) {
        logger(Level.WARN, e, objs);
    }
    
    /**
     * 打印error信息
     * 
     * @param objs 需要打印的error信息集合
     */
    public void error(Object... objs) {
        error(null, objs);
    }
    
    /**
     * 打印error信息，包含异常信息
     * 
     * @param e 需要打印的异常
     * @param objs 需要打印的error信息集合
     */
    public void error(Throwable e, Object... objs) {
        logger(Level.ERROR, e, objs);
    }
    
    /**
     * 打印fatal信息
     * 
     * @param obj
     */
    public void fatal(Object obj) {
        logger(Level.FATAL, obj);
    }
    
    private void logger(Level level, Object... objs) {
        logger(level, null, objs);
    }
    
    @SuppressWarnings("unchecked")
    private String getLogString(Object obj) {
        StringBuilder sb = new StringBuilder();
        if (obj == null)
            return "null";
        if ((obj instanceof List)) {
            @SuppressWarnings("rawtypes")
            List list = (List)obj;
            if (list.size() <= LOG_LIST_MAX_SIZE) {
                sb.append('[');
                for (int i = 0; i < list.size(); i++) {
                    sb.append(getLogString(list.get(i)));
                }
                sb.append(']');
                return sb.toString();
            }
            sb.append("list[").append(list.getClass()).append("].size:").append(list.size());
        }
        else if ((obj instanceof Map)) {
            @SuppressWarnings("rawtypes")
            Map<Object, Object> map = (Map)obj;
            sb.append("{");
            boolean first = true;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                
                Object key = entry.getKey();
                Object val = entry.getValue();
                
                sb.append(key).append('=').append(getLogString(val));
            }
            sb.append("}");
        }
        else if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            if (len > LOG_LIST_MAX_SIZE) {
                sb.append("Array[").append(obj.getClass()).append("].length:").append(len);
            }
            else {
                sb.append('[');
                for (int i = 0; i < len; i++) {
                    sb.append(getLogString(Array.get(obj, i)));
                    if (i < len - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(']');
            }
        }
        else {
            sb.append(obj);
        }
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    private void logger(Level level, Throwable e, Object[] objs) {
        if (!this.log.isEnabledFor(level)) {
            return;
        }
        @SuppressWarnings("rawtypes")
        List<Object> exList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        long threadId = Thread.currentThread().getId();
        sb.append(threadId).append(":");
        
        for (Object obj : objs) {
            if ((obj instanceof Throwable))
                exList.add((Throwable)obj);
            else {
                sb.append(getLogString(obj));
            }
            sb.append(' ');
        }
        this.log.log(level, sb.toString(), e);
        if (e != null) {
            exList.add(e);
        }
        
        if (!exList.isEmpty()) {
            for (Object ex : exList) {
                this.log.log(level, new StringBuilder().append(threadId).append(":").append(getStackTraceString((Throwable)ex)).toString());
            }
        }
    }
    
    public static String getStackTraceString(Throwable e) {
        return getStackTraceString(e, -1);
    }
    
    public static String getStackTraceString(Throwable e, int maxLen) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        if (maxLen <= 0) {
            return str;
        }
        return getByteSubString(str, maxLen);
    }
    
    public static String getByteSubString(String srcStr, int count) {
        if (srcStr == null)
            return "";
        if (count < 0) {
            return "";
        }
        if (count > srcStr.length() * 2) {
            return srcStr;
        }
        char[] cs = srcStr.toCharArray();
        
        int c = 0;
        int endPos = -1;
        for (int i = 0; i < cs.length; i++) {
            c++;
            if (cs[i] > 'ÿ') {
                c++;
            }
            if (c == count) {
                endPos = i + 1;
                break;
            }
            if (c > count) {
                endPos = i;
                break;
            }
        }
        if (endPos == -1) {
            return srcStr;
        }
        
        return new String(cs, 0, endPos);
    }
}