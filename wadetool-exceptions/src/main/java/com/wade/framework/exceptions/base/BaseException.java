package com.wade.framework.exceptions.base;

/**
 * 异常定义
 * @Description 异常定义
 * @ClassName   BaseException 
 * @Date        2017年5月25日 下午3:39:51 
 * @Author      yz.teng
 */
public interface BaseException {
    public enum ExceptionLevel {
        INFO, WARN, ERROR, FAIL
    }
    
    String getCode();
    
    ExceptionLevel getLevel();
    
    String getMessage();
}
