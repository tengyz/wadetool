package com.wade.framework.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * rest调用工具类
 * 
 * @Description rest调用工具类
 * @ClassName HttpRequest
 * @Date 2015年11月5日 下午3:20:01
 * @Author tengyz
 */
public final class HttpHelper {
    private static final Logger log = LogManager.getLogger(HttpHelper.class);
    
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    private static final String _GET = "GET";
    
    private static final String CONTENT_TYPE_JSON = "application/Json;charset=UTF-8";
    
    /**
     * rest调用接口
     * @param url 地址
     * @param paramJson 入参json格式的字符串
     * @return  返回json数据的字符串
     * @throws Exception
     */
    public static String requestService(String url, String paramJson) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("======rest调用接口=======url:" + url);
            log.debug("======rest调用接口=======paramJson:" + paramJson);
        }
        
        StringEntity reqEntity = new StringEntity(paramJson, "utf-8");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间 
        reqEntity.setContentEncoding("UTF-8");
        reqEntity.setContentType("application/json");
        httpPost.setEntity(reqEntity);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String rtnJson = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                rtnJson = EntityUtils.toString(entity, "UTF-8");
                if (rtnJson == null || rtnJson.isEmpty()) {
                    throw new Exception("HttpHelper--requestService服务接口异常:" + url);
                }
            }
            else {
                throw new Exception("HttpHelper--requestService服务接口异常:" + url);
            }
        }
        catch (ClientProtocolException e) {
            log.error("HttpHelper--requestService服务接口异常:" + url, e);
            throw new Exception("HttpHelper--requestService服务接口异常:" + url, e);
        }
        catch (IOException e) {
            log.error("HttpHelper--requestService服务接口异常:" + url, e);
            throw new Exception("HttpHelper--requestService服务接口异常:" + url, e);
        }
        catch (Exception e) {
            log.error("HttpHelper--requestService服务接口异常:" + url, e);
            throw new Exception("HttpHelper--requestService服务接口异常:" + url, e);
        }
        finally {
            if (null != response) {
                try {
                    response.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    log.error("HttpHelper--requestService服务接口异常:" + url, e);
                    throw new Exception("HttpHelper--requestService  response.close()服务接口异常关闭异常:" + url, e);
                }
            }
            if (null != httpclient) {
                try {
                    httpclient.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    log.error("HttpHelper--requestService服务接口异常:" + url, e);
                    throw new Exception("HttpHelper--requestService httpclient.close()服务接口异常关闭异常:" + url, e);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("====HttpHelper--requestService==返回结果==rtnJson:" + rtnJson);
        }
        return rtnJson;
    }
    
    /** 
     * get请求 
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String get(String url, Map<String, String> params, Map<String, String> headers, String contentType)
            throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        StringBuffer bufferRes = null;
        java.net.HttpURLConnection httpUrlConn = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            if (isHttps(url)) {
                httpUrlConn = initHttps(initParams(url, params), _GET, headers, contentType);
            }
            else {
                httpUrlConn = initHttp(initParams(url, params), _GET, headers, contentType);
            }
            inputStream = httpUrlConn.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = bufferedReader.readLine()) != null) {
                bufferRes.append(valueString);
            }
        }
        catch (Exception e) {
            System.err.println("UAC用户中心SDK，initHttps get,url=:" + url + "异常：" + e.fillInStackTrace());
            e.printStackTrace();
            e.fillInStackTrace();
            return null;
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                bufferedReader = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream = null;
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
                httpUrlConn = null;
            }
        }
        return bufferRes.toString();
    }
    
    /** 
     * 初始化http请求参数 
     */
    private static HttpsURLConnection initHttps(String url, String method, Map<String, String> headers, String contentType)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        System.setProperty("https.protocols", "TLSv1");
        List protocalList = new ArrayList();
        protocalList.add("TLSv1");
        for (int i = 0; i < protocalList.size(); i++) {
            try {
                java.net.URL _url = new java.net.URL(null, url, new sun.net.www.protocol.https.Handler());
                javax.net.ssl.HttpsURLConnection http = (javax.net.ssl.HttpsURLConnection)_url.openConnection();
                TrustManager[] tm = {new MyX509TrustManager()};
                SSLContext sslContext = SSLContext.getInstance(protocalList.get(i).toString());
                sslContext.init(null, tm, new java.security.SecureRandom());
                //从上述SSLContext对象中得到SSLSocketFactory对象    
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                //设置域名校验  
                http.setHostnameVerifier(new TrustAnyHostnameVerifier());
                http.setSSLSocketFactory(ssf);
                //连接超时  
                http.setConnectTimeout(15000);
                //读取超时 --服务器响应比较慢，增大时间  
                http.setReadTimeout(15000);
                http.setRequestMethod(method);
                http.setRequestProperty("Content-Type", contentType);
                http.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
                if (null != headers && !headers.isEmpty()) {
                    for (Entry<String, String> entry : headers.entrySet()) {
                        http.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                http.setDoOutput(true);
                http.setDoInput(true);
                http.connect();
                return http;
            }
            catch (Exception e) {
                System.err.println("UAC用户中心SDK，initHttps initHttps,异常：" + e.fillInStackTrace());
                e.printStackTrace();
                e.fillInStackTrace();
            }
        }
        return null;
    }
    
    /** 
     * 初始化http请求参数 
     */
    private static HttpURLConnection initHttp(String url, String method, Map<String, String> headers, String contentType) throws IOException {
        URL _url = new URL(url);
        java.net.HttpURLConnection http = (java.net.HttpURLConnection)_url.openConnection();
        // 连接超时  
        http.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间  
        http.setReadTimeout(25000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", contentType);
        http.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }
    
    /** 
     * 初始化参数 
     */
    public static String initParams(String url, Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        }
        sb.append(map2Url(params));
        return sb.toString();
    }
    
    /** 
     * map转url参数 
     */
    public static String map2Url(Map<String, String> paramToMap) {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            }
            else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (null != value || !"".equals(value.trim())) {
                try {
                    url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return url.toString();
    }
    
    /** 
     * 检测是否https 
     */
    private static boolean isHttps(String url) {
        return url.startsWith("https");
    }
    
    public static void main(String[] args) throws Exception {
        //requestService("http://localhost:8888/microservice/common/getSysDateByDB", "");
        Map<String, String> headers = new HashMap();
        headers.put("Authusername", "adinfoAdmin");
        headers.put("authpassword", "adinfo@wd1");
        String aaa = get("http://10.124.130.159/ADInfoCenter/users/xa/xauser2", null, headers, CONTENT_TYPE_JSON);
        System.out.println("aaa=:" + aaa);
    }
    
    private static class MyX509TrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }
    
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
