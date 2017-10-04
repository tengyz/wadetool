package com.wade.framework.common.util;

import java.io.IOException;

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
import org.apache.log4j.Logger;

/**
 * rest调用工具类
 * 
 * @Description rest调用工具类
 * @ClassName HttpRequest
 * @Date 2015年11月5日 下午3:20:01
 * @Author tengyz
 */
public final class HttpHelper {
    
    private static final Logger log = Logger.getLogger(HttpHelper.class);
    
    /**
     * rest调用接口
     * @param url 地址
     * @param paramJson 入参json格式的字符串
     * @return  返回json数据的字符串
     * @throws Exception
     */
    public static String requestService(String url, String paramJson) throws Exception {
        log.info("======rest调用接口=======url" + url);
        log.info("======rest调用接口=======paramJson" + paramJson);
        StringEntity reqEntity = new StringEntity(paramJson);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间 
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
                rtnJson = EntityUtils.toString(entity);
                if (rtnJson == null || rtnJson.isEmpty()) {
                    throw new Exception("rest服务接口异常:" + url);
                }
            }
            else {
                throw new Exception("rest服务接口异常:" + url);
            }
        }
        catch (ClientProtocolException e) {
            throw new Exception("rest服务接口异常:" + url);
        }
        catch (IOException e) {
            throw new Exception("rest服务接口异常:" + url);
        }
        catch (Exception e) {
            throw new Exception("rest服务接口异常:" + url);
        }
        finally {
            
            if (httpclient != null) {
                try {
                    httpclient.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    throw new Exception("rest服务接口异常关闭异常:" + url);
                }
            }
        }
        log.info("======rest调用接口=====返回结果==rtnJson" + rtnJson);
        return rtnJson;
    }
    
    public static void main(String[] args) throws Exception {
        requestService("http://192.168.251.213:8080/providerService1/getName2", "");
    }
    
}
