package com.wade.framework.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志输出注解
 * @Description 日志输出注解 
 * @ClassName   BussinessLog 
 * @Date        2017年8月6日 下午9:07:19 
 * @Author      yz.teng
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BussinessLog {
    String desc() default "";
    
    public enum OpType {
        ADD, UPDATE, DEL, SEARCH
    };
    
    OpType type() default OpType.SEARCH;
    
    String param() default "";
}
