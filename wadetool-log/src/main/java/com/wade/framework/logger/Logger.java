package com.wade.framework.logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerFactory;

import com.wade.framework.exceptions.Thrower;

/**
 * 日志工具类，提供debug、info、warn、error等级别的日志输出
 * 
 * @Description 日志工具类，提供debug、info、warn、error等级别的日志输出
 * @ClassName Logger
 * @Date 2016年9月18日 下午2:51:12
 * @Author tengyizu
 */
public class Logger {
    private static final Map<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();
    
    private static final LoggerFactory factory = new WadeLoggerFactory(Logger.class);
    
    private static final int LOG_LIST_MAX_SIZE = 10;
    
    private org.apache.log4j.Logger log = null;
    
    public Logger(String name) {
        log = WadeLogger.getLogger(name, factory);
    }
    
    /**
     * 根据标识名称获取一个Logger的实例
     * @param name	日志标识
     * @return
     */
    public static Logger getLogger(final String name) {
        if (loggerMap.containsKey(name))
            return loggerMap.get(name);
        Logger log = new Logger(name);
        loggerMap.put(name, log);
        return log;
    }
    
    /**
     * 根据Class获取一个Logger的实例
     * @param clazz	Class类
     * @return
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
    
    /**
     * 是否开启了debug的日志
     * @return
     */
    public boolean isDebugEnabled() {
        return log.isEnabledFor(Level.DEBUG);
    }
    
    /**
     * 打印info信息
     * @param obj	需打印的内容集合
     */
    public void info(Object... objs) {
        logger(Level.INFO, objs);
    }
    
    /**
     * 打印debug信息
     * @param objs	需要打印的内容集合
     */
    public void debug(Object... objs) {
        logger(Level.DEBUG, objs);
    }
    
    /**
     * 打印warn信息
     * @param objs	需要打印的内容集合
     */
    public void warn(Object... objs) {
        warn(null, objs);
    }
    
    /**
     * 打印warn信息，包含异常信息
     * @param e	需要打印的异常
     * @param objs	需要打印的warn信息集合
     */
    public void warn(Throwable e, Object... objs) {
        logger(Level.WARN, e, objs);
    }
    
    /**
     * 打印error信息
     * @param objs	需要打印的error信息集合
     */
    public void error(Object... objs) {
        error(null, objs);
    }
    
    /**
     * 打印error信息，包含异常信息
     * @param e	需要打印的异常
     * @param objs	需要打印的error信息集合
     */
    public void error(Throwable e, Object... objs) {
        logger(Level.ERROR, e, objs);
    }
    
    /**
     * 打印fatal信息
     * @param obj
     */
    public void fatal(Object obj) {
        logger(Level.FATAL, obj);
    }
    
    private void logger(Level level, Object... objs) {
        logger(level, null, objs);
    }
    
    private void logger(Level level, Throwable e, Object... objs) {
        if (!log.isEnabledFor(level))
            return;
        
        List<Throwable> exList = new ArrayList<Throwable>();
        StringBuilder sb = new StringBuilder();
        sb.append(Thread.currentThread().getId()).append(":");
        
        for (Object obj : objs) {
            if (obj == null) {
                sb.append("null");
            }
            else if (obj instanceof List<?>) {
                List<?> list = (List<?>)obj;
                if (list.size() <= LOG_LIST_MAX_SIZE) {
                    sb.append(list);
                }
                else {
                    sb.append("list[").append(list.getClass()).append("].size:").append(list.size());
                }
            }
            else if (obj instanceof Throwable) {
                exList.add((Throwable)obj);
            }
            else if (obj.getClass().isArray()) {
                int len = Array.getLength(obj);
                if (len > LOG_LIST_MAX_SIZE) {
                    sb.append("Array[").append(obj.getClass()).append("].length:").append(len);
                }
                else {
                    sb.append('[');
                    for (int i = 0; i < len; i++) {
                        sb.append(Array.get(obj, i));
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
            sb.append(' ');
        }
        log.log(level, sb.toString(), e);
        if (e != null) {
            exList.add(e);
        }
        
        if (!exList.isEmpty()) {
            for (Throwable ex : exList) {
                log.log(level, Thrower.getStackTraceString(ex));
            }
        }
    }
}