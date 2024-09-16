package com.wade.framework.common.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.file.config.PropertiesConfig;

/**
 * 配置文件常量类，读取application.properties文件，只加载一次
 * 
 * @Description 配置文件常量类，读取application.properties文件，只加载一次
 * @ClassName CacheConfig
 * @Date 2016年9月19日 上午12:10:29
 * @Author tengyizu
 */
public class CacheConfig {
    private final static Logger log = LogManager.getLogger(CacheConfig.class);
    
    /**
     * 定义缓存
     */
    private static Map<String, String> data = new HashMap();
    
    /**
     * 初始化方法
     */
    private static CacheConfig cfg = getInstance();
    
    /**
     * 从配置文件，读取网关地址
     */
 //   public static final String GATEWAY_ADDR = getProperty("gateWayAddr");

    public static final String STATICPARAM_DISABLED = getProperty("staticparam.disabled");
    
    /**
     * 读取config.properties文件
     * 
     * @return
     * @Date 2016年9月19日 上午12:08:55
     * @Author tengyizu
     */
    public static synchronized CacheConfig getInstance() {
        if (cfg == null) {
            cfg = new CacheConfig();
            try {
                PropertiesConfig cfg = new PropertiesConfig("application.properties");
                data = cfg.getProperties();
            }
            catch (Exception e) {
                log.error("CacheConfig读取application.properties文件异常:", e);
            }
        }
        return cfg;
    }
    
    private static String getProperty(String name) {
        if (data.containsKey(name)) {
            String value = (String)data.get(name);
            return value != null ? value : null;
        }
        return null;
    }
    
    /**
     * 根据key获取value，如果获取为空赋上默认值
     * 
     * @param name 获取key
     * @param defval 默认值
     * @return
     * @Date 2016年9月19日 上午12:09:28
     * @Author tengyizu
     */
    private static String getProperty(String name, String defval) {
        String value = getProperty(name);
        if (value == null) {
            return defval;
        }
        return value;
    }
    
    public static Boolean getBoolean(String name, Boolean defaultValue) {
        String value = getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        if (value != null) {
            value = value.toLowerCase().trim();
            if ("true".equals(value)) {
                return true;
            }
            else if ("false".equals(value)) {
                return false;
            }
            throw new RuntimeException("The value can not parse to Boolean : " + value);
        }
        return defaultValue;
    }
    
    public static void main(String args[]) throws Exception {
        //System.out.println("====:" + CacheConfig.GATEWAY_ADDR);
    }
    
}
