package com.wade.framework.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.cache.CacheUtil;
import com.wade.framework.common.cache.ICacheSourceProvider;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 类初始化管理工具
 * @Description 类初始化管理工具 
 * @ClassName   InstanceManager 
 * @Date        2017年5月25日 下午4:07:16 
 * @Author      yz.teng
 */
public class InstanceManager {
    private static final Logger log = LogManager.getLogger(StringHelper.class);
    
    private static final Map<Class<?>, Object> insMap = new ConcurrentHashMap<Class<?>, Object>();
    
    public static <T> T getInstance(final Class<T> clazz) throws Exception {
        Class<?> clz = (Class<?>)clazz;
        
        Object obj = CacheUtil.get(insMap, clz, new ICacheSourceProvider<Object>() {
            
            @Override
            public Object getSource() throws Exception {
                return clazz.newInstance();
            }
            
        });
        @SuppressWarnings("unchecked")
        T objT = (T)obj;
        return objT;
    }
    
    public static <T> T getInstance(final Class<T> clazz, Class<?> superClazz) throws Exception {
        if (!superClazz.isAssignableFrom(clazz)) {
            Thrower.throwException(BizExceptionEnum.WADE_COMP_CAST_ERROR, clazz, superClazz);
        }
        
        return getInstance(clazz);
    }
    
    public static <T> T newInstance(String clzName, Class<T> superClazz, T defIns) {
        try {
            Class<?> clazz = Class.forName(clzName);
            if (!superClazz.isAssignableFrom(clazz)) {
                if (defIns != null)
                    return defIns;
                Thrower.throwException(BizExceptionEnum.WADE_COMP_CAST_ERROR, clazz, superClazz);
            }
            @SuppressWarnings("unchecked")
            T obj = (T)clazz.newInstance();
            return obj;
        }
        catch (ClassNotFoundException e) {
            log.error(e);
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.WADE_COMP_NEW_INSTANCE, e, clzName);
        }
        catch (InstantiationException e) {
            log.error(e);
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.WADE_COMP_NEW_INSTANCE, e, clzName);
        }
        catch (IllegalAccessException e) {
            log.error(e);
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.WADE_COMP_NEW_INSTANCE, e, clzName);
        }
        return null;
    }
}
