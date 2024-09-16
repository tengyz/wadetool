package com.wade.framework.db.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.file.config.PropertiesConfig;

/**
 * 配置文件常量类，读取application.properties文件，只加载一次
 * 
 * @Description 配置文件常量类，读取application.properties文件，只加载一次
 * @ClassName DbConfig
 * @Date 2016年9月19日 上午12:10:29
 * @Author tengyizu
 */
public class DbConfig {
    private final static Logger log = LogManager.getLogger(DbConfig.class);
    
    /**
     * 定义缓存
     */
    private static Map<String, String> data = new HashMap();
    
    /**
     * 初始化方法
     */
    private static DbConfig cfg = getInstance();
    
    /**
     * 从配置文件，读取db地址
     */
    public static final String CACHE_DBURL = getProperty("cache.dbUrl");
    
    public static final String CACHE_DBUSERNAME = getProperty("cache.dbUserName");
    
    public static final String CACHE_DBPASSOWRD = getProperty("cache.dbPassowrd");
    
    /**
     * 读取config.properties文件
     * 
     * @return
     * @Date 2016年9月19日 上午12:08:55
     * @Author tengyizu
     */
    public static synchronized DbConfig getInstance() {
        if (cfg == null) {
            cfg = new DbConfig();
            try {
                PropertiesConfig cfg = new PropertiesConfig("application.properties");
                data = cfg.getProperties();
            }
            catch (Exception e) {
                log.error("DbConfig读取application.properties文件异常:", e);
            }
        }
        return cfg;
    }
    
    public static String getProperty(String name) {
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
        System.out.println("====:" + getProperty("cache.dbUrl"));
        System.out.println("====:" + DbConfig.CACHE_DBURL);
    }
    
}
