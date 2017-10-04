package com.wade.framework.exceptions;

import java.io.Serializable;

import com.wade.framework.exceptions.base.BaseException;

/**
 * 异常工具类 
 * @Description 异常工具类 
 * @ClassName   AcctException 
 * @Date        2017年5月25日 下午3:40:29 
 * @Author      yz.teng
 */
public class BussinessException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -6822779928581211623L;
    
    BaseException expType = null;
    
    public BaseException getExceptionType() {
        return expType;
    }
    
    public BussinessException(String message, BaseException expType) {
        super(message);
        this.expType = expType;
    }
    
    public BussinessException(String message, Throwable e, BaseException expType) {
        super(message, e);
        this.expType = expType;
    }
    
}
