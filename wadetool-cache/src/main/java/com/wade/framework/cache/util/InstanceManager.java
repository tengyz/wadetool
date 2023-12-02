package com.wade.framework.cache.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final Map<Class<?>, Object> insMap = new ConcurrentHashMap<Class<?>, Object>();
    
    public static <T> T getInstance(final Class<T> clazz) throws Exception {
        Class<?> clz = (Class<?>)clazz;
        Object obj = CacheUtil.get(insMap, clz, new ICacheSourceProvider<Object>() {
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
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, clazz, superClazz);
        }
        return getInstance(clazz);
    }
    
    public static <T> T newInstance(String clzName, Class<T> superClazz, T defIns) {
        try {
            Class<?> clazz = InstanceManager.class.getClassLoader().loadClass(clzName);
            if (!superClazz.isAssignableFrom(clazz)) {
                if (defIns != null)
                    return defIns;
                Thrower.throwException(BizExceptionEnum.ERROR_MSG, clazz, superClazz);
            }
            @SuppressWarnings("unchecked")
            T obj = (T)clazz.newInstance();
            return obj;
        }
        catch (ClassNotFoundException e) {
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, clzName);
        }
        catch (InstantiationException e) {
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, clzName);
        }
        catch (IllegalAccessException e) {
            if (defIns != null)
                return defIns;
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, e, clzName);
        }
        return null;
    }
}
