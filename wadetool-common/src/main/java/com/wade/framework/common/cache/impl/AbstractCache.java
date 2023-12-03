package com.wade.framework.common.cache.impl;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wade.framework.common.cache.ICache;

/**
 * 缓存抽象类
 * @Description 缓存抽象类 
 * @ClassName   AbstractCache 
 * @Date        2017年5月24日 下午4:18:14 
 * @Author      yz.teng
 */
public abstract class AbstractCache implements ICache {
    protected String cacheName = null;
    
    protected boolean valid = false;
    
    protected Lock lock = new ReentrantLock();
    
    @Override
    public String getCacheName() {
        return cacheName;
    }
    
    @Override
    public boolean isValid() {
        return this.valid;
    }
    
    public void lock() {
        this.lock.lock();
    }
    
    public void unlock() {
        this.lock.unlock();
    }
    
    public boolean put(String cacheKey, Boolean valueBoolean) {
        return put(cacheKey, (Object)valueBoolean);
    }
    
    public boolean put(String cacheKey, Character valueCharacter) {
        return put(cacheKey, (Object)valueCharacter);
    }
    
    public boolean put(String cacheKey, Byte valueByte) {
        return put(cacheKey, (Object)valueByte);
    }
    
    public boolean put(String cacheKey, Short valueShort) {
        return put(cacheKey, (Object)valueShort);
    }
    
    public boolean put(String cacheKey, Integer valueInteger) {
        return put(cacheKey, (Object)valueInteger);
    }
    
    public boolean put(String cacheKey, Long valueLong) {
        return put(cacheKey, (Object)valueLong);
    }
    
    public boolean put(String cacheKey, Float valueFloat) {
        return put(cacheKey, (Object)valueFloat);
    }
    
    public boolean put(String cacheKey, Double valueDouble) {
        return put(cacheKey, (Object)valueDouble);
    }
    
    public boolean put(String cacheKey, Date valueDate) {
        return put(cacheKey, (Object)valueDate);
    }
    
    public boolean put(String cacheKey, byte[] valueArrayOfByte) {
        return put(cacheKey, (Object)valueArrayOfByte);
    }
    
    public boolean put(String cacheKey, String value) {
        return put(cacheKey, (Object)value);
    }
    
    public boolean put(String cacheKey, StringBuffer valueBuffer) {
        return put(cacheKey, (Object)valueBuffer);
    }
    
    public boolean put(String cacheKey, StringBuilder valueBuilder) {
        return put(cacheKey, (Object)valueBuilder);
    }
    
    public boolean put(String cacheKey, Object value) {
        return put(cacheKey, (Object)value, 0);
    }
    
    public boolean put(String cacheKey, Boolean valueBoolean, int secTTL) {
        return put(cacheKey, (Object)valueBoolean, secTTL);
    }
    
    public boolean put(String cacheKey, Character valueCharacter, int secTTL) {
        return put(cacheKey, (Object)valueCharacter, secTTL);
    }
    
    public boolean put(String cacheKey, Byte valueByte, int secTTL) {
        return put(cacheKey, (Object)valueByte, secTTL);
    }
    
    public boolean put(String cacheKey, Short valueShort, int secTTL) {
        return put(cacheKey, (Object)valueShort, secTTL);
    }
    
    public boolean put(String cacheKey, Integer valueInteger, int secTTL) {
        return put(cacheKey, (Object)valueInteger, secTTL);
    }
    
    public boolean put(String cacheKey, Long valueLong, int secTTL) {
        return put(cacheKey, (Object)valueLong, secTTL);
    }
    
    public boolean put(String cacheKey, Float valueFloat, int secTTL) {
        return put(cacheKey, (Object)valueFloat, secTTL);
    }
    
    public boolean put(String cacheKey, Double valueDouble, int secTTL) {
        return put(cacheKey, (Object)valueDouble, secTTL);
    }
    
    public boolean put(String cacheKey, Date valueDate, int secTTL) {
        return put(cacheKey, (Object)valueDate, secTTL);
    }
    
    public boolean put(String cacheKey, byte[] valueArrayOfByte, int secTTL) {
        return put(cacheKey, (Object)valueArrayOfByte, secTTL);
    }
    
    public boolean put(String cacheKey, String value, int secTTL) {
        return put(cacheKey, (Object)value, secTTL);
    }
    
    public boolean put(String cacheKey, StringBuffer valueBuffer, int secTTL) {
        return put(cacheKey, (Object)valueBuffer, secTTL);
    }
    
    public boolean put(String cacheKey, StringBuilder valueBuilder, int secTTL) {
        return put(cacheKey, (Object)valueBuilder, secTTL);
    }
    
    @Override
    public String toString() {
        return cacheName;
    }
}
