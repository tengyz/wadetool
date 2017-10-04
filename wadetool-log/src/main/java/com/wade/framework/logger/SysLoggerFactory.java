package com.wade.framework.logger;

import org.apache.log4j.spi.LoggerFactory;

public class SysLoggerFactory implements LoggerFactory {
    private String fqcn = null;

    public SysLoggerFactory() {
    }

    public SysLoggerFactory(Class<?> clazz) {
        this.fqcn = clazz.getName();
    }

    public SysLogger makeNewLoggerInstance(String name) {
        return new SysLogger(name, this.fqcn);
    }
}