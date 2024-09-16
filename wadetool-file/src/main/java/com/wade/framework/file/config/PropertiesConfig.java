package com.wade.framework.file.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 解析properties文件工具类
 * @author yz.teng
 *
 */
public class PropertiesConfig {
    private Properties props = null;
    
    public PropertiesConfig(InputStream in) {
        this.props = new Properties();
        try {
            this.props.load(in);
        }
        catch (IOException e) {
            e.printStackTrace();
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "PropertiesConfig", e);
        }
        if (this.props == null)
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "PropertiesConfig");
    }
    
    public PropertiesConfig(String file) {
        this(PropertiesConfig.class.getClassLoader().getResourceAsStream(file));
    }
    
    public PropertiesConfig(File file) {
        this.props = new Properties();
        try {
            InputStream in = new FileInputStream(file);
            this.props.load(in);
        }
        catch (FileNotFoundException e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "PropertiesConfig", e);
        }
        catch (IOException e) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "PropertiesConfig", e);
        }
    }
    
    public String getProperty(String prop) {
        String value = this.props.getProperty(prop);
        try {
            return new String(value.getBytes("ISO-8859-1"));
        }
        catch (UnsupportedEncodingException e) {
        }
        return value;
    }
    
    public String getProperty(String prop, String defval) {
        String value = getProperty(prop);
        if (value == null) {
            return defval;
        }
        return value;
    }
    
    public Map<String, String> getProperties() {
        Map data = new HashMap();
        
        Enumeration e = this.props.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            data.put(key, getProperty(key));
        }
        return data;
    }
}