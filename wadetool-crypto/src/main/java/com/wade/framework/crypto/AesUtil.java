package com.wade.framework.crypto;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Aes加密
 * @Description Aes加密 
 * @ClassName   AesUtil 
 * @Date        2019年8月29日 下午4:12:11 
 * @Author      yz.teng
 */
public class AesUtil {
    private static final Logger log = LogManager.getLogger(AesUtil.class);
    
    private static final String ENCODING = "UTF-8";
    
    public static final String KEY_ALGORITHM = "AES";
    
    public static final String SIGN_ALGORITHMS = "SHA1PRNG";
    
    /**
     * 获取解密后的字符串
     * @param content
     * @param passcode
     * @return
     */
    public static String RevertAESCode(String content, String passcode) {
        byte[] decryptFrom = parseHexStr2Byte(content);
        byte[] decryptResult = decrypt(decryptFrom, passcode);
        String decryptString = new String(decryptResult);
        return decryptString;
    }
    
    /**
     * 获取加密后的字符串
     * @param content
     * @param passcode
     * @return
     */
    public static String GetAESCode(String content, String passcode) {
        byte[] encryptResult = encrypt(content, passcode);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        return encryptResultStr;
    }
    
    /**
     * 加密
     * @param content
     * @param password
     * @return
     */
    private static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance(SIGN_ALGORITHMS);
            random.setSeed(password.getBytes(ENCODING));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
            /**创建密码器**/
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            byte[] byteContent = content.getBytes(ENCODING);
            /**初始化密码器**/
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return result;
        }
        catch (Exception e) {
            log.error("AesUtil encrypt 异常=:", e);
        }
        return null;
    }
    
    /**
     * 解密
     * @param content
     * @param password
     * @return
     */
    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance(SIGN_ALGORITHMS);
            random.setSeed(password.getBytes(ENCODING));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
            /**创建密码器**/
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            /**初始化密码器**/
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        }
        catch (Exception e) {
            log.error("AesUtil decrypt 异常=:", e);
        }
        return null;
    }
    
    /**
     * 将二进制转换成十六进制
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    
    /**
     * 将十六进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        else {
            byte[] result = new byte[hexStr.length() / 2];
            for (int i = 0; i < hexStr.length() / 2; i++) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }
            return result;
        }
    }
    
    /**
     * 加密和解密
     * @param args
     */
    public static void main(String[] args) {
        /**数据初始化**/
        String content = "http://www.baidu.com/";
        String password = "1234567890";
        /**加密**/
        System.out.println("加密前：" + content);
        String encryptResultStr = GetAESCode(content, password);
        System.out.println("加密后：" + encryptResultStr);
        /**解密**/
        String decryptString = RevertAESCode(encryptResultStr, password);
        System.out.println("解密后：" + new String(decryptString));
        
    }
}
