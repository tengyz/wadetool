package com.wade.framework.common.cache.timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 缓存清除操作
 * @author yz。teng
 *
 */
public class DefaultCacheClearInvoker extends ICacheClearInvoker {
    private static Logger log = Logger.getLogger(DefaultCacheClearInvoker.class);
    
    private List<Map<?, ?>> mapCaches = new ArrayList<Map<?, ?>>();
    
    private List<List<?>> listCaches = new ArrayList<List<?>>();
    
    public DefaultCacheClearInvoker(Object... objs) {
        for (Object obj : objs) {
            if (Map.class.isInstance(obj)) {
                mapCaches.add((Map<?, ?>)obj);
            }
            else if (List.class.isInstance(obj)) {
                listCaches.add((List<?>)obj);
            }
            else {
                Thrower.throwException(BizExceptionEnum.ERROR_MSG, obj.getClass());
            }
        }
    }
    
    @Override
    public synchronized void clearListeningCache() {
        clearMapCaches();
        clearListCaches();
    }
    
    private void clearMapCaches() {
        for (int i = 0, size = mapCaches.size(); i < size; i++) {
            Map<?, ?> cache = mapCaches.get(i);
            synchronized (cache) {
                cache.clear();
            }
        }
    }
    
    private void clearListCaches() {
        for (int i = 0, size = listCaches.size(); i < size; i++) {
            List<?> cache = listCaches.get(i);
            synchronized (cache) {
                cache.clear();
            }
        }
    }
}
