package com.wade.framework.cache.localcache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 本地缓存xml解析
 * @Description TODO 
 * @ClassName   CacheXml 
 * @Date        2015年11月28日 上午11:40:21 
 * @Author      yz.teng
 */
final class CacheXml {
    private static final Logger log = Logger.getLogger(CacheFactory.class);
    
    private static final String CACHE_FILENAME = "localcache.xml";
    
    private static CacheXml instance = new CacheXml();
    
    private static Element root;
    
    private static Document document;
    
    private CacheXml() {
        SAXBuilder builder = new SAXBuilder();
        InputStream ins = null;
        try {
            ins = CacheXml.class.getClassLoader().getResourceAsStream(CACHE_FILENAME);
            if (null == ins) {
                throw new FileNotFoundException(CACHE_FILENAME);
            }
            document = builder.build(ins);
            root = document.getRootElement();
            if (null != ins)
                try {
                    ins.close();
                }
                catch (IOException e) {
                    log.error("关闭本地缓存配置文件句柄错误!", e);
                }
        }
        catch (Exception e) {
            log.error("本地缓存配置解析错误!", e);
            
            if (null != ins)
                try {
                    ins.close();
                }
                catch (IOException e2) {
                    log.error("关闭本地缓存配置文件句柄错误!", e2);
                }
        }
        finally {
            if (null != ins)
                try {
                    ins.close();
                }
                catch (IOException e) {
                    log.error("关闭本地缓存配置文件句柄错误!", e);
                }
        }
    }
    
    public static final CacheXml getInstance() {
        return instance;
    }
    
    private List getList(Element from, String propPath) {
        Element element = from;
        String[] nodes = propPath.split("/");
        for (int i = 0; i < nodes.length - 1; i++) {
            element = element.getChild(nodes[i]);
        }
        if (null != element) {
            return element.getChildren(nodes[(nodes.length - 1)]);
        }
        return new ArrayList();
    }
    
    public List<ReadOnlyCacheItem> getReadOnlyCacheItems() {
        List rtn = new ArrayList();
        
        Iterator iter = getList(root, "readonly/cache").iterator();
        while (iter.hasNext()) {
            Element elem = (Element)iter.next();
            String className = elem.getAttributeValue("className");
            String init = elem.getAttributeValue("init");
            String cronExpr = elem.getAttributeValue("cronExpr");
            
            if ((init == null) || ("".equals(init))) {
                init = "false";
            }
            if (cronExpr == null) {
                cronExpr = "";
            }
            ReadOnlyCacheItem item = new ReadOnlyCacheItem(className, init, cronExpr);
            rtn.add(item);
        }
        return rtn;
    }
    
    public List<ReadWriteCacheItem> getReadWriteCacheItems() {
        List rtn = new ArrayList();
        Iterator iter = getList(root, "readwrite/cache").iterator();
        while (iter.hasNext()) {
            Element elem = (Element)iter.next();
            String name = elem.getAttributeValue("name");
            String maxSize = elem.getAttributeValue("maxSize");
            String cronExpr = elem.getAttributeValue("cronExpr");
            
            if ((maxSize == null) || ("".equals(maxSize))) {
                maxSize = "2000";
            }
            if (cronExpr == null) {
                cronExpr = "";
            }
            ReadWriteCacheItem item = new ReadWriteCacheItem(name, Integer.parseInt(maxSize), cronExpr);
            rtn.add(item);
        }
        
        return rtn;
    }
    
    public class ReadOnlyCacheItem {
        public String className;
        
        public boolean isInitial;
        
        public String cronExpr;
        
        public ReadOnlyCacheItem(String className, String init, String cronExpr) {
            if (null == className) {
                throw new IllegalArgumentException("只读缓存配置错误：className不可为空!");
            }
            
            if ((!"true".equals(init)) && (!"false".equals(init))) {
                throw new IllegalArgumentException("只读缓存配置错误：init参数只能为true或false: " + className);
            }
            
            this.cronExpr = null;
            String[] items;
            if (!"".equals(cronExpr)) {
                items = cronExpr.split(" ");
                if (5 != items.length) {
                    log.error("读写缓存配置错误：cronExpr 只可配置：分    小时    日    月    周");
                    throw new IllegalArgumentException("读写缓存配置错误：cronExpr 只可配置：分    小时    日    月    周");
                }
                this.cronExpr = ("0 " + cronExpr);
            }
            
            this.className = className;
            this.isInitial = Boolean.parseBoolean(init);
        }
    }
    
    public class ReadWriteCacheItem {
        public String name;
        
        public int maxSize;
        
        public String cronExpr;
        
        public ReadWriteCacheItem(String name, int maxSize, String cronExpr) {
            if (null == name) {
                throw new IllegalArgumentException("读写缓存配置错误：name不可为空！");
            }
            if (maxSize < 0) {
                throw new IllegalArgumentException("读写缓存配置错误: maxSize < 0");
            }
            
            if (null == cronExpr) {
                throw new IllegalArgumentException("读写缓存配置错误：cronExpr不可为空！");
            }
            this.cronExpr = null;
            String[] items;
            if (!"".equals(cronExpr)) {
                items = cronExpr.split(" ");
                if (5 != items.length) {
                    log.error("读写缓存配置错误：cronExpr 只可配置：分    小时    日    月    周");
                    throw new IllegalArgumentException("读写缓存配置错误：cronExpr 只可配置：分    小时    日    月    周");
                }
                this.cronExpr = ("0 " + cronExpr);
            }
            this.name = name;
            this.maxSize = maxSize;
        }
    }
}