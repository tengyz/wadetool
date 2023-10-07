package com.wade.framework.common.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class RedisConfig {
    private static final Logger log = LogManager.getLogger(ParamMgr.class);

    private static final String FILE_NAME = "application.properties";
    private static Properties pro;

    public static String get(String key, String defaultValue) {
        return pro.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return pro.getProperty(key);
    }

    static {
        try
        {
            pro = new Properties();
            pro.load(RedisConfig.class.getClassLoader().getResourceAsStream(FILE_NAME));
        } catch (Exception e) {
            log.error(FILE_NAME+"文件读取配置错误",e.getMessage());
        }
    }

}
