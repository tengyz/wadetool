package com.wade.framework.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MD5加密工具
 * 
 * @Description MD5加密工具
 * @ClassName MD5Util
 * @Date 2016年9月21日 上午10:00:27
 * @Author tengyizu
 */
public class MD5Util {
    private static final Logger log = LogManager.getLogger(MD5Util.class);
    
    public static String computeGBK(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("GBK"));
        }
        catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException caught!", e);
            System.exit(-1);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
    
    public static String computeUTF(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("utf-8"));
        }
        catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException caught!", e);
            System.exit(-1);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
    
    public static String compute(String inStr) {
        try {
            char[] charArray = inStr.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            
            for (int i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte)charArray[i];
            }
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int)md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        }
        catch (NoSuchAlgorithmException e) {
            log.error("加密数据出错：", e);
            return null;
        }
    }
    
    /* 下面是主函数调用 */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // 加密后的数据:d0521106f6ba7f9ac0a7370fb28d0ec6
        // ="accountVal419057292buyerIp192.168.1.101buyNum10chargeTypeCF点券gameArea上海电信一区hidChargeType-CFDQhidGameArea320notifyUrlhttp://121.101.212.86/card_ws/sup/sup_orderNotify.dopartnerId1016ptOrderNo201212210001ptPayTime20121221112333sum10.000supProductId1043supUserId1072unitPrice1.000wdH5UdkK7dc6QEf4YE3q8EL4tFEr68HmpvkRxvcm7CJTRCMOZ1Ej81hPu1kk";
        // String b = "11111111";
        // String postString = MD5Util.computeUTF(b);
        // System.out.println("加密后的数据:" + postString);
        // System.out.println("加密后的数据:" + MD5Util.hexdigest(b));
        
    }
}
