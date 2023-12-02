package com.wade.framework.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.exceptions.base.BaseException;
import com.wade.framework.exceptions.base.BaseException.ExceptionLevel;

/**
 * 抛异常工具类 
 * @Description 抛异常工具类  
 * @ClassName   Thrower 
 * @Date        2017年5月25日 下午3:38:23 
 * @Author      yz.teng
 */
public class Thrower {
    private static final Logger log = LogManager.getLogger(Thrower.class);
    
    public static void throwException(BaseException e) throws BussinessException {
        throwException(e, (Throwable)null);
    }
    
    public static void throwException(BaseException e, Throwable exp) throws BussinessException {
        throwException(e.getCode(), e.getLevel(), e.getMessage(), exp, e);
    }
    
    public static void throwException(BaseException e, Object... param) throws BussinessException {
        throwException(e, null, param);
    }
    
    public static void throwException(BaseException e, Throwable exp, Object... param) throws BussinessException {
        String msg = String.format(e.getMessage(), param);
        throwException(e.getCode(), e.getLevel(), msg, exp, e);
    }
    
    private static void throwException(String code, ExceptionLevel level, String msg, Throwable e, BaseException exType) throws BussinessException {
        String message = msg;
        if (level == ExceptionLevel.WARN) {
            log.warn(message);
        }
        else if (level == ExceptionLevel.ERROR) {
            log.error(message);
        }
        if (e != null)
            throw new BussinessException(message, e, exType);
        else
            throw new BussinessException(message, exType);
    }
    
    public static String getStackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        return str;
    }
    
    public static String getStackTraceString(Throwable e, int maxLen) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        return str.substring(0, maxLen);
    }
    
}
