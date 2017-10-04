package com.wade.framework.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;

public class SysLogger extends Logger {
    private String fqcn = null;

    protected SysLogger(String name, String fqcn) {
        super(name);
        this.fqcn = fqcn;
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return Logger.getLogger(name, factory);
    }

    public static Logger getAcctLogger(Class<?> clazz, LoggerFactory factory) {
        return getLogger(clazz.getName(), factory);
    }

    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        super.forcedLog(this.fqcn == null ? fqcn : this.fqcn, level, message, t);
    }
}