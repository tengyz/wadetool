package com.wade.framework.logger;

import org.apache.log4j.spi.LoggerFactory;

public class WadeLoggerFactory implements LoggerFactory {
    private String fqcn = null;
    
    public WadeLoggerFactory() {
        
    }
    
    public WadeLoggerFactory(Class<?> clazz) {
        this.fqcn = clazz.getName();
    }
    
    @Override
    public WadeLogger makeNewLoggerInstance(String name) {
        return new WadeLogger(name, fqcn);
    }
}
