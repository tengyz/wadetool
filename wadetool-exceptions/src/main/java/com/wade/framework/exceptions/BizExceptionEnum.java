package com.wade.framework.exceptions;

import com.wade.framework.exceptions.base.BaseException;
import com.wade.framework.exceptions.base.BaseException.ExceptionLevel;

public enum BizExceptionEnum implements BaseException {
    FILE_NOT_FOUND("%s文件没有找到", ExceptionLevel.ERROR), INPUTPARAMS_NOT_MATCH("函数参数个数不正确！", ExceptionLevel.ERROR), INPUTPAMAMS_NOT_FOUND("%s必须传入！",
            ExceptionLevel.ERROR), DATE_FORMART_ERR("日期格式化出现错误%s", ExceptionLevel.ERROR), CACHE_LOAD_ERROR("调用生成器加载缓存失败"), ERROR_MSG("%s",
            ExceptionLevel.ERROR), LoginServiceImpl("工号密码错误，请确认后重新输入！", ExceptionLevel.ERROR), RESULT_EMPTY("结果集为空", ExceptionLevel.ERROR);
    
    private ExceptionLevel level = null;
    
    private String message = null;
    
    private long errorCode;
    
    BizExceptionEnum(String message) {
        this.message = message;
        this.level = ExceptionLevel.ERROR;
    }
    
    BizExceptionEnum(String message, ExceptionLevel level) {
        this.message = message;
        this.level = level;
    }
    
    BizExceptionEnum(String message, long errorCode) {
        this.message = message;
        this.level = ExceptionLevel.ERROR;
        this.errorCode = errorCode;
    }
    
    BizExceptionEnum(String message, ExceptionLevel level, long errorCode) {
        this.message = message;
        this.level = level;
        this.errorCode = errorCode;
    }
    
    @Override
    public String getCode() {
        return toString();
    }
    
    @Override
    public ExceptionLevel getLevel() {
        return level;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public long getErrorCode() {
        return this.errorCode;
    }
    
}
