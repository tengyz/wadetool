package com.wade.framework.logger;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;

/**
 * 日志工具类，提供debug、info、warn、error等级别的日志输出
 * @author Lin
 *
 */
public class WadeLogger extends org.apache.log4j.Logger {
    private String fqcn = null;
    
    /**
     * 受保护的构造函数
     * @param name
     */
    protected WadeLogger(String name, String fqcn) {
        super(name);
        this.fqcn = fqcn;
    }
    
    public static org.apache.log4j.Logger getLogger(String name, LoggerFactory factory) {
        return org.apache.log4j.Logger.getLogger(name, factory);
    }
    
    public static org.apache.log4j.Logger getAcctLogger(Class<?> clazz, LoggerFactory factory) {
        return getLogger(clazz.getName(), factory);
    }
    
    @Override
    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        super.forcedLog(this.fqcn == null ? fqcn : this.fqcn, level, message, t);
    }
    
}