package com.wade.framework.common.cache;

import java.util.Date;

/**
 * 缓存接口类
 */
public interface ICache {
    public abstract String getCacheName();
    
    public abstract boolean remove(String cacheKey);
    
    public abstract void refresh();
    
    public abstract boolean isValid();
    
    public abstract Object get(String cacheKey);
    
    public abstract boolean put(String cacheKey, Boolean valueBoolean);
    
    public abstract boolean put(String cacheKey, Character valueCharacter);
    
    public abstract boolean put(String cacheKey, Byte valueByte);
    
    public abstract boolean put(String cacheKey, Short valueShort);
    
    public abstract boolean put(String cacheKey, Integer valueInteger);
    
    public abstract boolean put(String cacheKey, Long valueLong);
    
    public abstract boolean put(String cacheKey, Float valueFloat);
    
    public abstract boolean put(String cacheKey, Double valueDouble);
    
    public abstract boolean put(String cacheKey, Date valueDate);
    
    public abstract boolean put(String cacheKey, byte[] valueArrayOfByte);
    
    public abstract boolean put(String cacheKey, String value);
    
    public abstract boolean put(String cacheKey, StringBuffer valueBuffer);
    
    public abstract boolean put(String cacheKey, StringBuilder valueBuilder);
    
    public abstract boolean put(String cacheKey, Object value);
    
    public abstract boolean put(String cacheKey, Boolean valueBoolean, int secTTL);
    
    public abstract boolean put(String cacheKey, Character valueCharacter, int secTTL);
    
    public abstract boolean put(String cacheKey, Byte valueByte, int secTTL);
    
    public abstract boolean put(String cacheKey, Short valueShort, int secTTL);
    
    public abstract boolean put(String cacheKey, Integer valueInteger, int secTTL);
    
    public abstract boolean put(String cacheKey, Long valueLong, int secTTL);
    
    public abstract boolean put(String cacheKey, Float valueFloat, int secTTL);
    
    public abstract boolean put(String cacheKey, Double valueDouble, int secTTL);
    
    public abstract boolean put(String cacheKey, Date valueDate, int secTTL);
    
    public abstract boolean put(String cacheKey, byte[] valueArrayOfByte, int secTTL);
    
    public abstract boolean put(String cacheKey, String value, int secTTL);
    
    public abstract boolean put(String cacheKey, StringBuffer valueBuffer, int secTTL);
    
    public abstract boolean put(String cacheKey, StringBuilder valueBuilder, int secTTL);
    
    public abstract boolean put(String cacheKey, Object value, int secTTL);
    
    public abstract void touch(String cacheKey, int secTTL);
}
