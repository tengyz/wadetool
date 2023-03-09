package com.wade.framework.db.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Db {
    private static final Logger log = LogManager.getLogger(Db.class);
    
    private Connection conn;
    
    private String userName = "";
    
    private String pwd = "";
    
    private String className = "com.mysql.cj.jdbc.Driver";
    
    private String url = "";
    
    public Db() {
        try {
            url = DbConfig.CACHE_DBURL;
            userName = DbConfig.CACHE_DBUSERNAME;
            pwd = DbConfig.CACHE_DBPASSOWRD;
            Class.forName(className);
            conn = (Connection)DriverManager.getConnection(url, userName, pwd);
        }
        catch (Exception e) {
            log.error("jdbc DB获取数据库异常", e);
        }
    }
    
    public Connection getConnection() {
        return conn;
    }
    
}
