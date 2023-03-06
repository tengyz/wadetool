package com.wade.framework.common.util;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class XmlHelper {
    private static final Logger log = LogManager.getLogger(XmlHelper.class);
    
    public static org.w3c.dom.Document parse(org.dom4j.Document doc) throws Exception {
        if (doc == null) {
            return null;
        }
        StringReader reader = new StringReader(doc.asXML());
        InputSource source = new InputSource(reader);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        return documentBuilder.parse(source);
    }
    
    public static org.dom4j.Document parse(org.w3c.dom.Document doc) throws Exception {
        if (doc == null) {
            return null;
        }
        DOMReader xmlReader = new DOMReader();
        return xmlReader.read(doc);
    }
    
    public static Element getElementByTagName(Element element, String tagName) {
        Iterator it = element.elementIterator();
        while (it.hasNext()) {
            Element ele = (Element)it.next();
            if (equalsIgnoreCase(ele.getName(), tagName)) {
                return ele;
            }
            if (ele.elementIterator().hasNext()) {
                Element e = getElementByTagName(ele, tagName);
                if (e != null) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }
    
    public static org.dom4j.Document parseByString(String xml) {
        org.dom4j.Document doc = null;
        try {
            InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            SAXReader builder = new SAXReader();
            doc = builder.read(in);
            in.close();
        }
        catch (Exception e) {
            log.error("parseByString异常:", e);
            e.printStackTrace();
        }
        return doc;
    }
    
    public static String getValueByPath(String xml, String xmlpath) {
        String strvalue = "";
        try {
            SAXReader builder = new SAXReader();
            org.dom4j.Document doc = builder.read(new StringReader(xml));
            XPath xPath = doc.createXPath(xmlpath);
            Map map = new HashMap();
            map.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            map.put("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
            xPath.setNamespaceURIs(map);
            Element elm = (Element)xPath.selectSingleNode(doc);
            strvalue = elm.getText();
        }
        catch (Exception e) {
            log.error("getValueByPath异常:", e);
            e.printStackTrace();
        }
        return strvalue;
    }
    
    public static String getNodeValueByPath(org.dom4j.Document doc, String xmlpath) {
        String strvalue = "";
        try {
            XPath xPath = doc.createXPath(xmlpath);
            Element elm = (Element)xPath.selectSingleNode(doc);
            if (elm != null) {
                strvalue = elm.getText().trim();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return strvalue;
    }
    
    public static String GetToken(String assertion) {
        String token = getValueByPath(assertion, "/Envelope/soap:Body/saml:AssertionIDRequest/saml:AssertionIDRef");
        return token;
    }
    
    public static boolean CheckSign(org.dom4j.Document doc, String certPath) {
        boolean retvalue = false;
        //        try {
        //            if (SignUtil.validateXmlSign(doc, CertUtil.getCert(certPath)))
        //                retvalue = true;
        //        }
        //        catch (Exception localException) {
        //        }
        return retvalue;
    }
    
    public static String GetStatus(org.dom4j.Document doc) {
        String status = "";
        try {
            XPath xPath = doc.createXPath("/*[name()='Envelope']/*[name()='Body']/*[name()='Response']/*[name()='Status']/*[name()='StatusCode']");
            Element elm = (Element)xPath.selectSingleNode(doc);
            if (elm != null)
                status = elm.attributeValue("Value");
        }
        catch (Exception localException) {
        }
        return status;
    }
    
    //    public static UserEntry GetUserInfo(org.dom4j.Document doc) {
    //        return get_userinfo(doc);
    //    }
    //    
    //    public static UserEntry get_userinfo(org.dom4j.Document doc) {
    //        UserEntry userentry = null;
    //        try {
    //            String userinfo = getNodeValueByPath(doc,
    //                    "/*[name()='Envelope']/*[name()='Body']/*[name()='Response']/*[name()='Assertion']/*[name()='Subject']/*[name()='NameID']");
    //            if ((userinfo != null) && (userinfo.length() > 0)) {
    //                userentry = new UserEntry();
    //                String[] user = userinfo.split(",");
    //                userentry.setUserid(user[0]);
    //                userentry.setEmpName(user[1]);
    //                userentry.setUserStatus(user[2]);
    //                userentry.setMail(user[3]);
    //            }
    //        }
    //        catch (Exception localException) {
    //        }
    //        return userentry;
    //    }
    //    
    
    public static Document XmlForSendRequest(String urlstr, String requestMethod, String contentype, String content) {
        URL url = null;
        Document doc = null;
        try {
            url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(requestMethod);
            if (contentype.length() > 0) {
                connection.setRequestProperty("Content-Type", contentype);
            }
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.connect();
            if (content.length() > 0) {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(content);
                out.flush();
                out.close();
            }
            SAXReader builder = new SAXReader();
            builder.setEncoding("UTF-8");
            InputStreamReader it = new InputStreamReader(connection.getInputStream(), "UTF-8");
            doc = builder.read(it);
            connection.disconnect();
        }
        catch (Exception e) {
            log.error("XmlForSendRequest异常:", e);
            e.printStackTrace();
        }
        return doc;
    }
    
    public static Document XmlForSendRequest(String urlstr) {
        return XmlForSendRequest(urlstr, "GET", "text/html;charset=UTF-8", "");
    }
    
    public static void main(String[] args) {
        String soap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\t<Body>\t\t<Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"0297dec9564f60343bfb6f703f31b340\" IssueInstant=\"2012-03-07T16:11:28.790+08:00\" Version=\"2.0\">\t\t\t<Status>\t\t\t\t<StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>\t\t\t</Status>\t\t\t<Assertion ID=\"138955a05f5ac3829cdbba5ba1421d76\" IssueInstant=\"2012-03-07T16:11:28.790+08:00\" Version=\"2.0\">\t\t\t\t<Issuer>http://idp_host/IDP</Issuer>\t\t\t\t<Subject>\t\t\t\t\t<NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">\t\t\t\t\t\tadmin,测试人员,测试部门,测试公司\t\t\t\t\t</NameID>\t\t\t\t\t<SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"/>\t\t\t\t</Subject>\t\t\t\t<Conditions>\t\t\t\t\t<OneTimeUse/>\t\t\t\t</Conditions>\t\t\t\t<AuthnStatement AuthnInstant=\"2012-03-07T16:11:28.790+08:00\">\t\t\t\t\t<AuthnContext>\t\t\t\t\t\t<AuthnContextClassRef>\t\t\t\t\t\t\turn:oasis:names:tc:SAML:2.0:ac:classes:Password\t\t\t\t\t\t</AuthnContextClassRef>\t\t\t\t\t</AuthnContext>\t\t\t\t</AuthnStatement>\t\t\t</Assertion>\t\t</Response>\t</Body><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>h8mdwOkZBEDv9GYoavb7hPsqP2Y=</DigestValue></Reference></SignedInfo><SignatureValue>iiU7DWvhA5Fz7KsJrTfztcjUe1pMg2/hBbMX8Lbkroj1Yo1MAzAjFjCPvRYvBORvrgZtaForxUpJlb4tdiVGUJx1Hpe1aM0odcvnqfMX7nY7RU1g7ZDkCjISJym0BYS9WyRS00k3icWYmTGEtg91pferffSvOAhhQS4x/i14pr4=</SignatureValue></Signature></Envelope>";
        
        //        String soap = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"><Body><Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" IssueInstant=\"2018-04-26 20:30:56\" Version=\"2.0\" ID=\"186PaR9mEMtXdV3fm8NJJfH7bBPCsY2C\"> <ds:Signature> <ds:SignedInfo> <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/> <ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"/> <ds:Reference> <ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#ref-MD5\"/> <ds:DigestValue>K0bmawIY8Ti2DnLXshOp4iq1Mke61nP8</ds:DigestValue> </ds:Reference> </ds:SignedInfo> <ds:SignatureValue> AMnK4587dyKOisuPsjR9u64y75cr9y9GK9Ex5B6aVziRBbPI53LwBR87Hu7e369Ht30H6c1rpcGX5g3Sk6NvXoCt7BKY02RbMF0LnF895yBjYl6aWTHt2cKBQie3Rmv3 </ds:SignatureValue> </ds:Signature> <Status> <StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/> </Status> <Assertion ID=\"0QVFL26TizJhxVvyTIWWrwt6KKz425d3\" IssueInstant=\"2018-04-26 20:30:56\" Version=\"2.0\"> <Issuer>http://idp_host/IDP</Issuer> <Subject> <NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\"> <uid>null</uid> </NameID> </Subject> <Conditions> <OneTimeUse/> </Conditions> <AuthnStatement AuthnInstant=\"2018-04-26 20:30:56\"> <AuthnContext> <AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</AuthnContextClassRef></AuthnContext></AuthnStatement></Assertion></Response> </Body> </Envelope>&oq=<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\"> <Body> <Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" IssueInstant=\"2018-04-26 20:30:56\" Version=\"2.0\" ID=\"186PaR9mEMtXdV3fm8NJJfH7bBPCsY2C\"> <ds:Signature> <ds:SignedInfo> <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"/><ds:Reference><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#ref-MD5\"/><ds:DigestValue>K0bmawIY8Ti2DnLXshOp4iq1Mke61nP8</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue> AMnK4587dyKOisuPsjR9u64y75cr9y9GK9Ex5B6aVziRBbPI53LwBR87Hu7e369Ht30H6c1rpcGX5g3Sk6NvXoCt7BKY02RbMF0LnF895yBjYl6aWTHt2cKBQie3Rmv3</ds:SignatureValue></ds:Signature><Status><StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></Status><Assertion ID=\"0QVFL26TizJhxVvyTIWWrwt6KKz425d3\" IssueInstant=\"2018-04-26 20:30:56\" Version=\"2.0\"><Issuer>http://idp_host/IDP</Issuer><Subject><NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\"> <uid>\\t\\t\\t\\t\\t\\tadmin,测试人员,测试部门,测试公司\\t\\t\\t\\t\\t</uid></NameID></Subject><Conditions><OneTimeUse/></Conditions><AuthnStatement AuthnInstant=\"2018-04-26 20:30:56\"><AuthnContext><AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</AuthnContextClassRef></AuthnContext></AuthnStatement></Assertion></Response></Body></Envelope>";
        //        String soap2 = "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\t<Body>\t\t<Response xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"0297dec9564f60343bfb6f703f31b340\" IssueInstant=\"2012-03-07T16:11:28.790+08:00\" Version=\"2.0\">\t\t\t<Status>\t\t\t\t<StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>\t\t\t</Status>\t\t\t<Assertion ID=\"138955a05f5ac3829cdbba5ba1421d76\" IssueInstant=\"2012-03-07T16:11:28.790+08:00\" Version=\"2.0\">\t\t\t\t<Issuer>http://idp_host/IDP</Issuer>\t\t\t\t<Subject>\t\t\t\t\t<NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">\t\t\t\t\t\tadmin,测试人员,测试部门,测试公司\t\t\t\t\t</NameID>\t\t\t\t\t<SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"/>\t\t\t\t</Subject>\t\t\t\t<Conditions>\t\t\t\t\t<OneTimeUse/>\t\t\t\t</Conditions>\t\t\t\t<AuthnStatement AuthnInstant=\"2012-03-07T16:11:28.790+08:00\">\t\t\t\t\t<AuthnContext>\t\t\t\t\t\t<AuthnContextClassRef>\t\t\t\t\t\t\turn:oasis:names:tc:SAML:2.0:ac:classes:Password\t\t\t\t\t\t</AuthnContextClassRef>\t\t\t\t\t</AuthnContext>\t\t\t\t</AuthnStatement>\t\t\t</Assertion>\t\t</Response>\t</Body><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>h8mdwOkZBEDv9GYoavb7hPsqP2Y=</DigestValue></Reference></SignedInfo><SignatureValue>iiU7DWvhA5Fz7KsJrTfztcjUe1pMg2/hBbMX8Lbkroj1Yo1MAzAjFjCPvRYvBORvrgZtaForxUpJlb4tdiVGUJx1Hpe1aM0odcvnqfMX7nY7RU1g7ZDkCjISJym0BYS9WyRS00k3icWYmTGEtg91pferffSvOAhhQS4x/i14pr4=</SignatureValue></Signature></Envelope>";
        //        
        //        SAXReader builder = new SAXReader();
        try {
            //            org.dom4j.Document doc = builder.read(new StringReader(soap));
            //            //            System.out.println("=====" + GetToken(soap));
            //            
            //            String userinfo = getNodeValueByPath(doc,
            //                    "/*[name()='Envelope']/*[name()='Body']/*[name()='Response']/*[name()='Assertion']/*[name()='Subject']/*[name()='NameID']");
            //            
            //            System.out.println("=====" + userinfo);
            
            String aaa = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><saml:AssertionIDRequest xmlns=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"BgqgdWv76VlPRmyk60SWN23hWmyhyErN\" Version=\"2.0\" IssueInstant=\"2018-04-26 20:20:42\"><saml:AssertionIDRef>ROSoY9WCrImJwy0rw8RlKU47d5XCBSq6pzHNwrLI1e7XBzY3x0QL9IhCK08BR7dnouzFEm65j05qYeQdK3enkzzyDVQU6T50F9KR8ofrenVlRa94lshewaw7Q6lkplI0</saml:AssertionIDRef></saml:AssertionIDRequest></soap:Body></soap:Envelope>";
            String token = GetToken(aaa);
            System.out.println("token=:" + token);
            Document doc = XmlForSendRequest(
                    "http://localhost:8101/authentication/check_authentication?appid=a123&token=ROSoY9WCrImJwy0rw8RlKU47d5XCBSq6pzHNwrLI1e7XBzY3x0QL9IhCK08BR7dnouzFEm65j05qYeQdK3enkzzyDVQU6T50F9KR8ofrenVlRa94lshewaw7Q6lkplI0"); //获取XML返回信息
            System.out.println("doc=:" + doc);
            String userinfo = getNodeValueByPath(doc,
                    "/*[name()='Envelope']/*[name()='Body']/*[name()='Response']/*[name()='Assertion']/*[name()='Subject']/*[name()='NameID']");
            System.out.println("=====" + userinfo);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
