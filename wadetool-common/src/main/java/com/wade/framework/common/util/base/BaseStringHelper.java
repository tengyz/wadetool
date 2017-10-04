package com.wade.framework.common.util.base;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 字符串处理工具基类
 * @Description 字符串处理工具基类 
 * @ClassName   BaseStringUtil 
 * @Date        2017年5月25日 下午4:00:05 
 * @Author      yz.teng
 */
public final class BaseStringHelper {
    private static final Logger log = Logger.getLogger(BaseStringHelper.class);
    
    public static final int MESSAGE_CONFIRM = 1;
    
    public static final int MESSAGE_WARNING = 2;
    
    public static final int MESSAGE_ERROR = 3;
    
    private static String[] chineseDigits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    
    private static BaseStringHelper utility;
    
    public static final URL getClassResource(String file) {
        URL url = utility.getClass().getClassLoader().getResource(file);
        if (url == null)
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "file " + file + " not exist!");
        return url;
    }
    
    public static final String getUniqeName() throws Exception {
        return String.valueOf(System.currentTimeMillis()) + Math.abs(new Random().nextInt());
    }
    
    public static final String getDomainPath(String name) {
        String path = utility.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (StringUtils.isBlank(name)) {
            return path;
        }
        String[] dirs = StringUtils.split(name, '/');
        
        String pathname = "";
        boolean find = true;
        for (int i = 0; i < dirs.length; i++) {
            int index = path.indexOf(dirs[i]);
            
            if ((index != -1) && (find)) {
                find = true;
                pathname = path.substring(0, index + dirs[i].length() + 1);
            }
            else {
                find = false;
                if (i == 0)
                    return "";
                File file = new File(pathname + dirs[i]);
                if (file.exists())
                    pathname = pathname + dirs[i] + "/";
                else
                    return "";
            }
        }
        return pathname;
    }
    
    public static final String getClassRoot(String name) {
        return utility.getClass().getClassLoader().getResource(name).getPath();
    }
    
    public static final String getTimestampFormat(String value) {
        switch (value.length()) {
            case 4:
                return "yyyy";
            case 6:
                return "yyyyMM";
            case 7:
                return "yyyy-MM";
            case 8:
                return "yyyyMMdd";
            case 10:
                return "yyyy-MM-dd";
            case 13:
                return "yyyy-MM-dd HH";
            case 16:
                return "yyyy-MM-dd HH:mm";
            case 19:
                return "yyyy-MM-dd HH:mm:ss";
            case 21:
                return "yyyy-MM-dd HH:mm:ss.S";
            case 5:
            case 9:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
            case 20:
        }
        return null;
    }
    
    public static final String getMatchStr(String str, String regex) {
        List result = getMatchArray(str, regex);
        return result.size() == 0 ? null : (String)result.get(0);
    }
    
    public static final List getMatchArray(String str, String regex) {
        List result = new ArrayList();
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        
        return result;
    }
    
    public static final boolean isMatches(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
    public static final String trimPrefix(String str, String prefix) {
        return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
    }
    
    public static final String trimSuffix(String str, String suffix) {
        return str.endsWith(suffix) ? str.substring(0, str.length() - 1) : str;
    }
    
    public static final String getStrByArray(Object[] array) {
        return getStrByArray(array, ",");
    }
    
    public static final String getStrByArray(Object[] array, String separator) {
        return StringUtils.join(array, separator);
    }
    
    public static final Timestamp encodeTimestamp(String timeStr) throws Exception {
        String format = getTimestampFormat(timeStr);
        return encodeTimestamp(format, timeStr);
    }
    
    public static final Timestamp encodeTimestamp(String format, String timeStr) throws Exception {
        if (StringUtils.isBlank(timeStr))
            return null;
        if (format.length() != timeStr.length())
            format = getTimestampFormat(timeStr);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return new Timestamp(sdf.parse(timeStr).getTime());
    }
    
    public static final String decodeTimestamp(String format, String timeStr) throws Exception {
        Timestamp time = encodeTimestamp(format, timeStr);
        return decodeTimestamp(format, time);
    }
    
    public static final String decodeTimestamp(String format, Timestamp time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }
    
    public static final Timestamp getCurrentTime() throws Exception {
        return new Timestamp(System.currentTimeMillis());
    }
    
    public static final String getSysTime() throws Exception {
        return decodeTimestamp("yyyy-MM-dd HH:mm:ss", new Timestamp(System.currentTimeMillis()));
    }
    
    public static final String getSysDate() throws Exception {
        return decodeTimestamp("yyyy-MM-dd", new Timestamp(System.currentTimeMillis()));
    }
    
    public static final String getLastDay() throws Exception {
        return getLastDay(getSysDate());
    }
    
    public static final String getLastDay(String timestr) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(encodeTimestamp(timestr));
        cal.set(5, cal.getActualMaximum(5));
        
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        return dateformat.format(cal.getTime());
    }
    
    public static final String getPrevDayByCurrDate() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        return dateformat.format(cal.getTime());
    }
    
    public static final String formatDecimal(String format, double decimal) throws Exception {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(decimal);
    }
    
    public static final int getCharLength(String value) {
        char[] chars = value.toCharArray();
        
        int charlen = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > '')
                charlen += 2;
            else {
                charlen++;
            }
        }
        
        return charlen;
    }
    
    public static final int getCharLength(String value, int length) {
        char[] chars = value.toCharArray();
        
        int charidx = 0;
        int charlen = 0;
        
        while ((charlen < length) && (charidx < chars.length)) {
            if (chars[charidx] > '')
                charlen += 2;
            else {
                charlen++;
            }
            charidx++;
        }
        
        return charidx;
    }
    
    public static final String toChineseMoney(String money) throws Exception {
        if (money == null)
            return null;
        int index = money.indexOf(".");
        if (index == -1) {
            money = money + ".0";
            return amountToChinese(Double.parseDouble(money));
        }
        String decimal = money.substring(index + 1);
        if (decimal.length() >= 2)
            money = money.substring(0, index + 3);
        return amountToChinese(Double.parseDouble(money));
    }
    
    public static final String amountToChinese(double amount) throws Exception {
        if ((amount > 9999999999999.998D) || (amount < -9999999999999.998D)) {
            Thrower.throwException(BizExceptionEnum.ERROR_MSG, "参数值超出允许范围 (-9999999999999.999 ～ 9999999999999.999)！");
        }
        boolean negative = false;
        if (amount < 0.0D) {
            negative = true;
            amount *= -1.0D;
        }
        
        long temp = Math.round(amount * 100.0D);
        int numFen = (int)(temp % 10L);
        temp /= 10L;
        int numJiao = (int)(temp % 10L);
        temp /= 10L;
        
        int[] parts = new int[20];
        int numParts = 0;
        for (int i = 0; temp != 0L; i++) {
            int part = (int)(temp % 10000L);
            parts[i] = part;
            numParts++;
            temp /= 10000L;
        }
        
        boolean beforeWanIsZero = true;
        
        String chineseStr = "";
        for (int i = 0; i < numParts; i++) {
            String partChinese = partTranslate(parts[i]);
            if (i % 2 == 0) {
                if ("".equals(partChinese))
                    beforeWanIsZero = true;
                else {
                    beforeWanIsZero = false;
                }
            }
            if (i != 0) {
                if (i % 2 == 0) {
                    chineseStr = "亿" + chineseStr;
                }
                else if (("".equals(partChinese)) && (!beforeWanIsZero)) {
                    chineseStr = "零" + chineseStr;
                }
                else {
                    if ((parts[(i - 1)] < 1000) && (parts[(i - 1)] > 0))
                        chineseStr = "零" + chineseStr;
                    chineseStr = "万" + chineseStr;
                }
            }
            
            chineseStr = partChinese + chineseStr;
        }
        
        if ("".equals(chineseStr))
            chineseStr = chineseDigits[0];
        else if (negative) {
            chineseStr = "负" + chineseStr;
        }
        chineseStr = chineseStr + "元";
        
        if ((numFen == 0) && (numJiao == 0)) {
            chineseStr = chineseStr + "整";
        }
        else if (numFen == 0) {
            chineseStr = chineseStr + chineseDigits[numJiao] + "角";
        }
        else if (numJiao == 0)
            chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
        else {
            chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
        }
        
        return chineseStr.replaceAll("亿万", "亿");
    }
    
    private static final String partTranslate(int amountPart) {
        if ((amountPart < 0) || (amountPart > 10000)) {
            throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
        }
        
        String[] units = {"", "拾", "佰", "仟"};
        
        int temp = amountPart;
        
        String amountStr = new Integer(amountPart).toString();
        int amountStrLength = amountStr.length();
        boolean lastIsZero = true;
        String chineseStr = "";
        
        for (int i = 0; (i < amountStrLength) && (temp != 0); i++) {
            int digit = temp % 10;
            if (digit == 0) {
                if (!lastIsZero)
                    chineseStr = "零" + chineseStr;
                lastIsZero = true;
            }
            else {
                chineseStr = chineseDigits[digit] + units[i] + chineseStr;
                lastIsZero = false;
            }
            temp /= 10;
        }
        return chineseStr;
    }
    
    public static final String[] getValues(Object value) throws Exception {
        if (value == null)
            return new String[0];
        if ((value instanceof String[])) {
            return (String[])value;
        }
        return new String[] {(String)value};
    }
    
    public static final String getHostAddress() throws Exception {
        return InetAddress.getLocalHost().getHostAddress();
    }
    
    public static final boolean equalsNVL(Object obj1, Object obj2) {
        if ((obj1 == null) && (obj2 == null)) {
            return true;
        }
        if ((obj1 != null) && (obj2 != null) && (obj1.equals(obj2))) {
            return true;
        }
        return false;
    }
    
    public static final int hashCodeNVL(Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }
    
    public static final String getStackTrace(Throwable e) {
        return getStackTrace(e, 0);
    }
    
    public static final String getStackTrace(Throwable e, int maxLength) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        if (maxLength == 0)
            return str;
        
        int charLength = getCharLength(str, maxLength);
        return str.substring(0, charLength);
    }
    
    public static final Throwable getBottomException(Throwable exception) {
        if (exception == null)
            return null;
        
        if (exception.getCause() != null) {
            exception = exception.getCause();
            return getBottomException(exception);
        }
        return exception;
    }
    
}