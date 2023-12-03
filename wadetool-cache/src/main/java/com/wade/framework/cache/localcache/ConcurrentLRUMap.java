package com.wade.framework.cache.localcache;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 冷热算法LRU
 * @Description 冷热算法LRU 
 * @ClassName   ConcurrentLRUMap 
 * @Date        2017年5月26日 下午3:37:08 
 * @Author      yz.teng
 */
public class ConcurrentLRUMap<K, V> implements Serializable {
    private static final long serialVersionUID = -7309303699204841905L;
    
    private static final int DEFAULT_INITIAL_CAPACITY = 1024;
    
    private static final int DEFAULT_MAX_SEGMENTS = 16;
    
    private static final int MAXIMUM_CAPACITY = 1073741824;
    
    private final int segmentMask;
    
    private final int segmentShift;
    
    private SegmentHashMap<K, V>[] segments;
    
    public ConcurrentLRUMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }
    
    public ConcurrentLRUMap(int maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize must > 0");
        }
        
        if (0 != maxSize % DEFAULT_MAX_SEGMENTS) {
            maxSize = (maxSize / DEFAULT_MAX_SEGMENTS + 1) * DEFAULT_MAX_SEGMENTS;
        }
        
        int sshift = 0;
        int ssize = 1;
        while (ssize < DEFAULT_MAX_SEGMENTS) {
            sshift++;
            ssize <<= 1;
        }
        
        if (ssize != DEFAULT_MAX_SEGMENTS) {
            throw new IllegalArgumentException("size must be power-of-two!");
        }
        
        this.segmentShift = (32 - sshift);
        this.segmentMask = (ssize - 1);
        this.segments = new SegmentHashMap[ssize];
        
        if (maxSize > MAXIMUM_CAPACITY)
            maxSize = MAXIMUM_CAPACITY;
        int c = maxSize / ssize;
        if (c * ssize != maxSize) {
            throw new IllegalArgumentException("make sure: maxSize / 16 == 0");
        }
        
        if (c * ssize < maxSize) {
            c++;
        }
        int cap = 1;
        while (cap < c) {
            cap <<= 1;
        }
        
        for (int i = 0; i < this.segments.length; i++)
            this.segments[i] = new SegmentHashMap(cap);
    }
    
    public V get(K key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).getEntry(key);
    }
    
    public Object put(K key, V value) {
        if (null == value) {
            throw new NullPointerException("value could not be null!");
        }
        
        int hash = hash(key.hashCode());
        return segmentFor(hash).addEntry(key, value);
    }
    
    public Object remove(K key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).remove(key);
    }
    
    public boolean containsKey(K key) {
        int hash = hash(key.hashCode());
        return segmentFor(hash).containsKey(key);
    }
    
    public boolean isEmpty() {
        return size() == 0;
    }
    
    public Set<K> keySet() {
        Set rtn = new HashSet();
        for (int i = 0; i < this.segments.length; i++) {
            Set set = this.segments[i].keySet();
            rtn.addAll(set);
        }
        return rtn;
    }
    
    public synchronized void clear() {
        for (int i = 0; i < this.segments.length; i++)
            this.segments[i].clear();
    }
    
    public int size() {
        int sum = 0;
        for (int i = 0; i < this.segments.length; i++) {
            sum += this.segments[i].size();
        }
        return sum;
    }
    
    private static final int hash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += (h << 3);
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> DEFAULT_MAX_SEGMENTS;
    }
    
    private final SegmentHashMap<K, V> segmentFor(int hash) {
        return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
    }
    
    private static final class SegmentHashMap<KK, VV> extends LinkedHashMap<KK, VV> {
        private static final long serialVersionUID = 6488943653970934521L;
        
        private final Lock lock = new ReentrantLock();
        
        private int maxSize;
        
        public SegmentHashMap(int maxSize) {
            super();
            this.maxSize = maxSize;
        }
        
        public VV addEntry(KK key, VV value) {
            this.lock.lock();
            try {
                Object oldvalue = super.put(key, value);
                return (VV)oldvalue;
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public VV getEntry(KK key) {
            this.lock.lock();
            try {
                Object value = get(key);
                Object localObject1;
                if (null == value) {
                    super.remove(key);
                    return null;
                }
                
                return (VV)value;
            }
            finally {
                this.lock.unlock();
            }
        }
        
        @Override
        public VV remove(Object key) {
            this.lock.lock();
            try {
                return super.remove(key);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        @Override
        public void clear() {
            this.lock.lock();
            try {
                super.clear();
            }
            finally {
                this.lock.unlock();
            }
        }
        
        @Override
        public boolean removeEldestEntry(Map.Entry<KK, VV> eldest) {
            return size() > this.maxSize;
        }
    }
}
