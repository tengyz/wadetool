package com.wade.framework.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(LogAop.class);
    
    /**
     * 定义Pointcut，Pointcut的名称，此方法不能有返回值，该方法只是一个标示
     */
    @Pointcut("@annotation(com.wade.framework.spring.BussinessLog)")
    public void controllerAspect() {
    }
    
    /**
     * 环绕通知（Around advice） ：包围一个连接点的通知，类似Web中Servlet规范中的Filter的doFilter方法。可以在方法的调用前后完成自定义的行为，也可以选择不执行。
     * @param joinPoint
     */
    @Around("controllerAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuffer logs = new StringBuffer();
        logs.append("\r\n###AOP log start").append("\r\n");
        // 开始计算时间
        Timer timer = new Timer();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容  
        logs.append("###方法描述:" + getControllerMethodDescription(joinPoint)).append("\r\n");
        logs.append("###URL: " + request.getRequestURL().toString()).append("\r\n");
        logs.append("###HTTP_METHOD:" + request.getMethod()).append("\r\n");
        logs.append("###IP:" + getIpAddr(request)).append("\r\n");
        logs.append("###CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()).append("\r\n");
        logs.append("###ARGS:" + Arrays.toString(joinPoint.getArgs())).append("\r\n");
        //获取所有参数方法一：  
        Enumeration<String> enu = request.getParameterNames();
        StringBuffer logsParam = new StringBuffer();
        logsParam.append("###[");
        while (enu.hasMoreElements()) {
            String paraName = (String)enu.nextElement();
            logsParam.append(paraName + ": " + request.getParameter(paraName)).append(",");
        }
        logsParam.append("]").append("\r\n");
        logs.append(logsParam);
        Object obj = joinPoint.proceed();
        logs.append("###USE TIME:" + Long.valueOf(timer.getUseTimeInMillis()) + " ms").append("\r\n");
        logs.append("###AOP log end");
        log.info(logs.toString());
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
                    sb.append("DESC:").append(desc).append(" ");
                    type = method.getAnnotation(BussinessLog.class).type();
                    sb.append(" TYPE:").append(type);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 获取IP地址
     * 
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
    
}
