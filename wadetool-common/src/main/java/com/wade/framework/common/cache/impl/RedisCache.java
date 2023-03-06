package com.wade.framework.common.cache.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.cache.redis.RedisFactoryBusiness;

/**
 * redis缓存操作
 * @Description redis缓存操作 
 * @ClassName   RedisCache 
 * @Date        2018年11月26日 下午3:03:32 
 * @Author      yz.teng
 */
public class RedisCache extends AbstractCache {
    private static final Logger log = LogManager.getLogger(RedisCache.class);
    
    private RedisFactoryBusiness cache = null;
    
    public RedisCache(String cacheName) {
        this.cacheName = cacheName;
        try {
            if ("REDIS_STATICPARAM_CACHE".equals(cacheName)) {
                this.cache = new RedisFactoryBusiness();
            }
        }
        catch (IllegalArgumentException e) {
            this.cache = null;
            log.error("RedisCache异常", e);
        }
        this.valid = cache != null;
        
    }
    
    @Override
    public void refresh() {
        
    }
    
    @Override
    public Object get(String cacheKey) {
        if (log.isDebugEnabled()) {
            log.debug("======RedisCache====cacheKey======:" + cacheKey);
        }
        return RedisFactoryBusiness.getObject(prepareCacheKey(cacheKey));
    }
    
    @Override
    public boolean put(String cacheKey, Boolean valueBoolean, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBoolean);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueBoolean);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Boolean valueBoolean) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBoolean);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Byte valueByte, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueByte);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueByte);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Byte valueByte) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueByte);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, byte[] valueArrayOfByte, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueArrayOfByte);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueArrayOfByte);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, byte[] valueArrayOfByte) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueArrayOfByte);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Character valueCharacter, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueCharacter);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueCharacter);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Character valueCharacter) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueCharacter);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Date valueDate, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueDate);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueDate);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Date valueDate) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueDate);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Double valueDouble, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueDouble);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueDouble);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Double valueDouble) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueDouble);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Float valueFloat, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueFloat);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueFloat);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Float valueFloat) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueFloat);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Integer valueInteger, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueInteger);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueInteger);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Integer valueInteger) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueInteger);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Long valueLong, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueLong);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueLong);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Long valueLong) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueLong);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Object value) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), value);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Short valueShort, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueShort);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueShort);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Short valueShort) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueShort);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, String value, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), value);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, value);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, String value) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), value);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, StringBuffer valueBuffer, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBuffer);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueBuffer);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, StringBuffer valueBuffer) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBuffer);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, StringBuilder valueBuilder, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBuilder);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, valueBuilder);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, StringBuilder valueBuilder) {
        RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), valueBuilder);
        return true;
    }
    
    @Override
    public boolean put(String cacheKey, Object value, int secTTL) {
        if (0 == secTTL) {
            RedisFactoryBusiness.setObject(prepareCacheKey(cacheKey), value);
            return true;
        }
        RedisFactoryBusiness.setexObject(prepareCacheKey(cacheKey), secTTL, value);
        return true;
    }
    
    @Override
    public boolean remove(String cacheKey) {
        RedisFactoryBusiness.del(prepareCacheKey(cacheKey));
        return true;
    }
    
    private static String prepareCacheKey(String cacheKey) {
        if (log.isDebugEnabled()) {
            log.debug("======RedisCache====prepareCacheKey======:" + cacheKey);
        }
        int spaceIndex = cacheKey.indexOf(' ');
        int enterIndex = cacheKey.indexOf("\r\n");
        if (spaceIndex >= 0 || enterIndex >= 0) {
            if (spaceIndex >= 0) {
                cacheKey = cacheKey.replaceAll(" ", "&nbsp;");
            }
            if (enterIndex >= 0) {
                cacheKey = cacheKey.replaceAll("\r\n", "<br>");
            }
        }
        return cacheKey;
    }
    
}
