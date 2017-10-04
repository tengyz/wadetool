package com.wade.framework.file.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 解析xml工具类
 * 
 * @author yz.teng
 * 
 */
public class XMLConfig {
    public static Element getRoot(String file) throws Exception {
        InputStream in = null;
        try {
            in = XMLConfig.class.getClassLoader().getResourceAsStream(file);
            if (in == null) {
                throw new FileNotFoundException();
            }
            SAXReader reader = new SAXReader();
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            return root;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "文件不存在", e);
        }
        catch (DocumentException e) {
            e.printStackTrace();
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "文件不存在", e);
        }
        finally {
            if (in != null)
                try {
                    in.close();
                }
                catch (IOException e) {
                    Thrower.throwException(BizExceptionEnum.ERROR_MSG, "未关闭异常", e);
                }
        }
        return null;
    }
    
    public static IDataMap getProperties(Element node) throws Exception {
        IDataMap properties = new DataHashMap();
        Iterator iter = node.attributeIterator();
        while (iter.hasNext()) {
            Attribute attr = (Attribute)iter.next();
            properties.put(attr.getName(), attr.getValue());
        }
        
        return properties;
    }
    
    public static String getProperty(Element root, String xpath) throws Exception {
        Node node = root.selectSingleNode(xpath);
        return node.getText();
    }
    
    public static IDataList getElements(Element root, String xpath) throws Exception {
        IDataList data = new DataArrayList();
        List nodes = root.selectNodes(xpath);
        
        for (Iterator i$ = nodes.iterator(); i$.hasNext();) {
            Object obj = i$.next();
            IDataMap attrs = getProperties((Element)obj);
            data.add(attrs);
        }
        
        return data;
    }
    
    public static Node getNode(Element root, String xpath) throws Exception {
        return root.selectSingleNode(xpath);
    }
    
    public static List<Node> getNodes(Element root, String xpath) throws Exception {
        return root.selectNodes(xpath);
    }
}