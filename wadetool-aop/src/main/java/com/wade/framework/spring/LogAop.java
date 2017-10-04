package com.wade.framework.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.wade.framework.data.Timer;
import com.wade.framework.spring.BussinessLog.OpType;

/**
 * aop controller 日志
 * @Description aop controller 日志 
 * @ClassName   LogAop 
 * @Date        2017年6月17日 上午12:51:21 
 * @Author      yz.teng
 */
@Aspect
@Component
public class LogAop {
    private static final Logger log = Logger.getLogger(LogAop.class);
    
    /**
     * 定义Pointcut，Pointcut的名称，此方法不能有返回值，该方法只是一个标示
     */
    @Pointcut("@annotation(com.talkweb.framework.common.util.spring.BussinessLog)")
    public void controllerAspect() {
    }
    
    /**
     * 环绕通知（Around advice） ：包围一个连接点的通知，类似Web中Servlet规范中的Filter的doFilter方法。可以在方法的调用前后完成自定义的行为，也可以选择不执行。
     * @param joinPoint
     */
    @Around("controllerAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("=====aop log start=====");
        // 开始计算时间
        Timer timer = new Timer();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 记录下请求内容  
        log.debug("方法描述:" + getControllerMethodDescription(joinPoint));
        log.debug("URL : " + request.getRequestURL().toString());
        log.debug("HTTP_METHOD : " + request.getMethod());
        log.debug("IP : " + request.getRemoteAddr());
        log.debug("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.debug("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        log.debug("Headers-Uuid : " + request.getHeader("Uuid"));
        //获取所有参数方法一：  
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String)enu.nextElement();
            log.debug(paraName + ": " + request.getParameter(paraName));
        }
        Object obj = joinPoint.proceed();
        log.debug(" use time:" + Long.valueOf(timer.getUseTimeInMillis()) + " ms");
        log.debug("=====aop log end=====");
        return obj;
    }
    
    /**  
     * 获取注解中对方法的描述信息 用于Controller层注解  
     *  
     * @param joinPoint 切点  
     * @return 方法描述  
     * @throws Exception  
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String desc = "";
        OpType type;
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    desc = method.getAnnotation(BussinessLog.class).desc();
                    sb.append("desc:").append(desc).append(" ");
                    type = method.getAnnotation(BussinessLog.class).type();
                    sb.append(" type:").append(type);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
}
