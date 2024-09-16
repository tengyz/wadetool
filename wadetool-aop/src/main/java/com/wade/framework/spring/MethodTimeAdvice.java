package com.wade.framework.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AOP监控方法执行时间
 * @Description AOP监控方法执行时间 
 * @ClassName   MethodTimeAdvice 
 * @Date        2015年11月26日 下午12:29:53 
 * @Author      yz.teng
 */
public class MethodTimeAdvice implements MethodInterceptor {
    
    private static final Logger log = LogManager.getLogger(MethodTimeAdvice.class);
    
    /**
     * AOP监控方法执行时间
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 用 commons-lang 提供的 StopWatch 计时  
        StopWatch clock = new StopWatch();
        clock.start(); // 计时开始  
        Object result = invocation.proceed();
        clock.stop(); // 计时结束  
        // 方法参数类型，转换成简单类型  
        Class[] params = invocation.getMethod().getParameterTypes();
        String[] simpleParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            simpleParams[i] = params[i].getSimpleName();
        }
        Object[] args = invocation.getArguments();
        StringBuffer logs = new StringBuffer();
        //AOP输出方法执行时间
        logs.append("AOP方法执行时间[")
                .append(invocation.getThis().getClass().getName())
                .append(".")
                .append(invocation.getMethod().getName())
                .append("(")
                .append(StringUtils.join(simpleParams, ","))
                .append(")(")
                .append(StringUtils.join(args, ","))
                .append(")] ")
                .append(" method running time:")
                .append(clock.getTime())
                .append("ms");
        log.info(logs);
        return result;
    }
    
}
