package com.wade.framework.common.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.common.util.base.BaseStringHelper;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataHashMap;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

/**
 * 通用字符串工具类(字符串操作提供的方法,金额转换为大写,获取正则匹配字符串)
 * 
 * @Description (字符串操作提供的方法,金额转换为大写,获取正则匹配字符串)
 * @ClassName StringUtil ValidatorUtils
 * @Date 2016年9月29日 下午3:37:25
 * @Author tengyizu
 */
public class StringHelper {
    private static final Logger log = LogManager.getLogger(StringHelper.class);
    
    private static final String[] EMPTY_ARRAY = new String[0];
    
    public static final String EMPTY = "";
    
    private static final int PAD_LIMIT = 8192;
    
    public static final int VERIFY_BOTH = 1;
    
    public static final int VERIFY_EITHER = 2;
    
    public static final String NOTFOUND_KEYS = "NOTFOUND_KEYS";
    
    public static final String NOTFOUND_ERRORS = "NOTFOUND_ERRORS";
    
    public static final String DEFAULT_SEPARATOR = ",";
    
    /**
     * URL字符集
     */
    private final static String ENCODE = "UTF-8";
    
    public static boolean isEmpty(String value) {
        return (value == null) || (value.length() == 0);
    }
    
    public static String join(Object[] arr, String separator) {
        return arr == null ? null : join(arr, separator, 0, arr.length);
    }
    
    public static String join(Object[] arr, String separator, int startIndex, int endIndex) {
        if (arr == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        if (startIndex >= endIndex) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex)
                sb.append(separator);
            sb.append(arr[i]);
        }
        return sb.toString();
    }
    
    public static String[] split(String str, String sep) {
        return split(str, sep, false);
    }
    
    public static String[] split(String str, String sep, boolean needBlank) {
        int len = str.length();
        if (len == 0) {
            return EMPTY_ARRAY;
        }
        if (sep.length() == 1) {
            return split(str, sep.charAt(0), needBlank);
        }
        List list = new ArrayList();
        int idx = -1;
        int lastIdx = 0;
        int sepLen = sep.length();
        while ((idx = str.indexOf(sep, lastIdx)) >= 0) {
            if ((needBlank) || (lastIdx != idx))
                list.add(str.substring(lastIdx, idx));
            lastIdx = idx + sepLen;
        }
        if (lastIdx != str.length())
            list.add(str.substring(lastIdx));
        return (String[])list.toArray(EMPTY_ARRAY);
    }
    
    public static String[] split(String str, char ch) {
        return split(str, ch, false);
    }
    
    public static String[] split(String str, char ch, boolean needBlank) {
        int len = str.length();
        if (len == 0) {
            return EMPTY_ARRAY;
        }
        List list = new ArrayList();
        int lastIdx = 0;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == ch) {
                if ((needBlank) || (lastIdx != i)) {
                    list.add(str.substring(lastIdx, i));
                }
                lastIdx = i + 1;
            }
        }
        if (lastIdx != len) {
            list.add(str.substring(lastIdx));
        }
        return (String[])list.toArray(EMPTY_ARRAY);
    }
    
    public static String repeat(char ch, int repeat) {
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }
    
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if ((repeat == 1) || (inputLength == 0)) {
            return str;
        }
        if ((inputLength == 1) && (repeat <= 8192)) {
            return repeat(str.charAt(0), repeat);
        }
        
        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                return repeat(str.charAt(0), repeat);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--) {
                    output2[i] = ch0;
                    output2[(i + 1)] = ch1;
                    i--;
                }
                return new String(output2);
        }
        StringBuilder buf = new StringBuilder(outputLength);
        for (int i = 0; i < repeat; i++) {
            buf.append(str);
        }
        return buf.toString();
    }
    
    public static String repeat(String str, String separator, int repeat) {
        if ((str == null) || (separator == null)) {
            return repeat(str, repeat);
        }
        String result = repeat(new StringBuilder().append(str).append(separator).toString(), repeat);
        return removeEnd(result, separator);
    }
    
    public static String lpad(String str, char ch, int len) {
        if (isBlank(str))
            return repeat(ch, len);
        if (len <= str.length()) {
            return str;
        }
        return new StringBuilder().append(repeat(ch, len - str.length())).append(str).toString();
    }
    
    public static String rpad(String str, char ch, int len) {
        if (isBlank(str))
            return repeat(ch, len);
        if (len <= str.length()) {
            return str;
        }
        return new StringBuilder().append(str).append(repeat(ch, len - str.length())).toString();
    }
    
    public static String removeEnd(String str, String remove) {
        if ((isEmpty(str)) || (isEmpty(remove))) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }
    
    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }
    
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }
    
    public static String replace(String text, String searchString, String replacement, int max) {
        if ((isBlank(text)) || (isBlank(searchString)) || (replacement == null) || (max == 0)) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= (max < 0 ? 16 : max > 64 ? 64 : max);
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            max--;
            if (max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }
    
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }
    
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }
    
    public static String getByteSubString(String srcStr, int count) {
        if (srcStr == null)
            return "";
        if (count < 0) {
            return "";
        }
        if (count > srcStr.length() * 2) {
            return srcStr;
        }
        char[] cs = srcStr.toCharArray();
        int c = 0;
        int endPos = -1;
        for (int i = 0; i < cs.length; i++) {
            c++;
            if (cs[i] > 'ÿ') {
                c++;
            }
            if (c == count) {
                endPos = i + 1;
                break;
            }
            if (c > count) {
                endPos = i;
                break;
            }
        }
        if (endPos == -1) {
            return srcStr;
        }
        return new String(cs, 0, endPos);
    }
    
    /**
     * 获取分区号，id的后四位
     * 
     * @param id
     * @return
     */
    public static String getPartitionId(String id) {
        return id.length() > 4 ? id.substring(id.length() - 4) : id;
    }
    
    /**
     * get partition id
     * @param id
     * @return String
     * @throws Exception
     */
    //    public String getPartitionId(String id) throws Exception {
    //        return getPartitionId(id, 4);
    //    }
    
    /**
     * get partition id
     * @param id
     * @param length
     * @return String
     * @throws Exception
     */
    public String getPartitionId(String id, int length) throws Exception {
        return String.valueOf(Long.parseLong(id) % (int)Math.pow(10, length));
    }
    
    /**
     * 将标识采用Camel标记法. 首字母小写,后面每个单词大写字母开头 CHARGE_ID ==> chargeId
     * 
     * @param source 字符串
     * @return String
     * @author
     */
    public static String camelize(String source) {
        String the = source.toLowerCase();
        StringBuffer result = new StringBuffer();
        String[] theArray = the.split("_");
        result.append(theArray[0]);
        for (int i = 1; i < theArray.length; i++) {
            result.append(Character.toUpperCase(theArray[i].charAt(0)) + theArray[i].substring(1));
        }
        
        return result.toString();
    }
    
    /**
     * 根据指定的长度，将原字符串src拷贝到目标字符串dest中
     * 
     * @param dest 目标字符串
     * @param src 原字符串
     * @param len
     * @return String
     */
    public static String strncpy(String dest, String src, int len) {
        String tmp = null;
        if (src.length() <= len) {
            dest = src;
        }
        else {
            tmp = src.substring(0, len);
            if (dest.length() <= len) {
                dest = tmp;
            }
            else {
                dest = tmp + dest.substring(len);
            }
        }
        return dest;
    }
    
    /**
     * 比较指定长度的2个字符串
     * 
     * @param str1
     * @param str2
     * @param len
     * @return int
     */
    public static int strncmp(String str1, String str2, int len) {
        int cmpLen = len;
        if (str1.length() < cmpLen)
            cmpLen = str1.length();
        if (str2.length() < cmpLen)
            cmpLen = str2.length();
        int res = str1.substring(0, cmpLen).compareTo(str2.substring(0, cmpLen));
        if (res != 0)
            return res;
        if (len == cmpLen)
            return 0;
        if (str1.length() == str2.length())
            return 0;
        if (str1.length() == cmpLen)
            return -1;
        if (str2.length() == cmpLen)
            return 1;
        return 0;
    }
    
    /**
     * 将数字的金额转成大写
     * 
     * @param money
     * @return
     * @throws Exception
     */
    public static String toChineseMoney(String money) throws Exception {
        return BaseStringHelper.toChineseMoney(money);
    }
    
    /**
     * 将数字的金额转成大写
     * 
     * @param amount
     * @return
     * @throws Exception
     */
    public static String amountToChinese(double amount) throws Exception {
        return BaseStringHelper.amountToChinese(amount);
    }
    
    /**
     * 根据正则到字符串中查找符合正则的字符串内容
     * 
     * @param str 查找字符串
     * @param regex 正则表达式
     * @return
     */
    public static String getMatchStr(CharSequence str, String regex) {
        List<String> result = getMatchList(str, regex);
        if (result.isEmpty())
            return null;
        return result.get(0);
    }
    
    /**
     * 根据正则到字符串中查找符合正则的字符串内容的集合
     * 
     * @param str 查找字符串
     * @param regex 正则表达式
     * @return
     */
    public static List<String> getMatchList(CharSequence str, String regex) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
    
    /**
     * 补足位数, 取初始序列,不足num位前面补str
     * 比如：取初始序列,不足8位前面补 0
     * @param seqid ,num,str
     * @return @
     * @throws Exception
     */
    public static String fillupFigure(String seqid, int num, String str) throws Exception {
        StringBuilder strbuf = new StringBuilder();
        int len = seqid.length();
        if (len < num) {
            for (int i = 0; i < (num - len); i++) {
                strbuf.append(str);
            }
        }
        else if (len > num) // 该逻辑按原函数逻辑处理
        {
            seqid = seqid.substring(len - num);
        }
        strbuf.append(seqid);
        return strbuf.toString();
    }
    
    /**
     * 判断IData中是否有List，用来判断输入串反序列化生成的对象是否为IDataset
     * 
     * @param data
     * @return
     */
    public static boolean hasList(IDataMap data) {
        String[] names = data.getNames();
        for (String name : names) {
            Object obj = data.get(name);
            if (obj instanceof List<?>)
                return true;
        }
        return false;
    }
    
    /**
     * arr中是否含有对应key的键值对
     * 
     * @param arr
     * @param key
     * @return
     */
    
    public static boolean contains(String[] arr, String key) {
        if (arr == null || arr.length == 0)
            return false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * arr中是否含有对应key的键值对
     * 
     * @param arr
     * @param key
     * @return
     */
    
    public static boolean contains(String searchStr, String subStr) {
        return contains(searchStr, subStr, DEFAULT_SEPARATOR);
    }
    
    /**
     * 检查字符串中是否包含指定的字符。如果字符串为<code>null</code>，将返回<code>false</code>。
     * <pre>
     * StringUtil.contains(null, *)    = false
     * StringUtil.contains("", *)      = false
     * StringUtil.contains("abc", 'a') = true
     * StringUtil.contains("abc", 'z') = false
     * </pre>
     *
     * @param str 要扫描的字符串
     * @param searchChar 要查找的字符
     *
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean contains(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }
        
        return str.indexOf(searchChar) >= 0;
    }
    
    /**
     * 检查字符串中是否含有对应key的键值对
     * @param searchStr
     * @param subStr
     * @param separator
     * @return
     */
    public static boolean contains(String searchStr, String subStr, String separator) {
        searchStr = separator + searchStr + separator;
        subStr = separator + subStr + separator;
        return searchStr.indexOf(subStr) >= 0;
    }
    
    /**
     * 判断value是否为空
     * 
     * @param value
     * @return boolean true 为空  false非空
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
    
    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank2(String str) {
        int length;
        
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 判断value是否为空
     * 
     * @param value
     * @return boolean
     */
    public static boolean isNonBlank(String value) {
        return !isBlank(value);
    }
    
    /**
     * 检查字符串是否不是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * <pre>
     * StringUtil.isBlank(null)      = false
     * StringUtil.isBlank("")        = false
     * StringUtil.isBlank(" ")       = false
     * StringUtil.isBlank("bob")     = true
     * StringUtil.isBlank("  bob  ") = true
     * </pre>
     *
     * @param str 要检查的字符串
     *
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isNotBlank(String str) {
        int length;
        
        if ((str == null) || ((length = str.length()) == 0)) {
            return false;
        }
        
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 过滤字符串为空的问题
     * 
     * @param value
     * @return
     */
    public static String checkIsNull(String value) {
        if (value == null || value.length() == 0 || value.equals("null")) {
            return "";
        }
        return value;
    }
    
    /**
     * 判断在IData中key对应的value是否为空
     * 
     * @param data
     * @param key
     * @return
     */
    public static boolean isEmpty(IDataMap data, String key) {
        if (data == null)
            return true;
        if (!data.containsKey(key))
            return true;
        if (data.getString(key) == null || data.getString(key).trim().length() == 0) {
            return true;
        }
        return false;
    }
    
    /**
     * 验证IData中是否存在某些Key，且其字符串表示不为空，校验失败抛出异常
     * 
     * @param data 数据源
     * @param verifyKeys 校验key集合
     * @return
     * 测试：
     * IDataMap newItem = new DataHashMap();
     *   newItem.put("NAME1", "123");
     *   newItem.put("NAME2", "123");
     *   System.out.println(verifyData(newItem, "NAME"));
     */
    public static boolean verifyData(IDataMap data, String verifyKeys) {
        return verifyData(data, verifyKeys, true);
    }
    
    /**
     * 验证IData中是否存在某些Key，且其字符串表示不为空
     * 
     * @param data 数据源
     * @param verifyKeys 校验key集合
     * @param throwError 校验失败时是否抛出异常
     * @return
     */
    public static boolean verifyData(IDataMap data, String verifyKeys, boolean throwError) {
        return verifyData(data, verifyKeys, VERIFY_BOTH, throwError, null);
    }
    
    /**
     * 验证IData中是否存在某些Key，且其字符串表示不为空，校验失败抛出异常
     * 
     * @param data 数据源
     * @param verifyKeys 校验key集合
     * @param throwErrMsg 校验失败时异常信息
     * @return
     */
    public static boolean verifyData(IDataMap data, String verifyKeys, String throwErrMsg) {
        return verifyData(data, verifyKeys, VERIFY_BOTH, true, throwErrMsg);
    }
    
    /**
     * 验证IData中是否存在某些Key，且其字符串表示不为空
     * 
     * @param data
     * @param verifyKeys key的集合，以“，”分隔 如"key1,key2,key3"
     * @param verifyOper 验证操作类型， VERIFY_BOTH： 必须包含所有的key VERIFY_EITHER： 至少包含一个key
     * @param throwError 校验失败时是否抛出异常
     * @param errMsg 检验失败时的错误信息
     * @return
     */
    public static boolean verifyData(IDataMap data, String verifyKeys, int verifyOper, boolean throwError, String errMsg) {
        if (log.isDebugEnabled())
            log.debug("verifyData:" + data);
        String[] keys = verifyKeys.split(",");
        StringBuilder notFoundKeys = new StringBuilder();
        String msgInfo = null;
        switch (verifyOper) {
            case VERIFY_BOTH:
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    if (isEmpty(data, key)) {
                        log.warn(key + " has not found!");
                        notFoundKeys.append(notFoundKeys == null ? key : notFoundKeys + "," + key);
                    }
                }
                msgInfo = "不能为空！";
                break;
            case VERIFY_EITHER:
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    if (!isEmpty(data, key)) {
                        return true;
                    }
                }
                notFoundKeys = new StringBuilder(verifyKeys);
                msgInfo = "不能全部为空！";
        }
        if (notFoundKeys.length() == 0)
            return true;
        else {
            errMsg = isBlank(errMsg) ? (notFoundKeys + msgInfo) : errMsg;
            if (throwError) {
                Thrower.throwException(BizExceptionEnum.ERROR_MSG, errMsg);
            }
            data.put(NOTFOUND_KEYS, notFoundKeys);
            data.put(NOTFOUND_ERRORS, errMsg);
            return false;
        }
    }
    
    /**
     * 检查对象是否为数字型字符串,包含负数开头的。
     * @param obj
     * @return
     */
    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1)
            return false;
        
        int i = 0;
        if (length > 1 && chars[0] == '-')
            i = 1;
        
        for (; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查指定的字符串列表是否不为空。
     * @param values
     * @return
     */
    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        }
        else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }
    
    /**
     * 把通用字符编码的字符串转化为汉字编码。
     * @param unicode
     * @return
     */
    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for (int i = 0; i < unicode.length(); i++) {
                out.append(unicode.charAt(i));
            }
        }
        return out.toString();
    }
    
    /**
     * 把字符串首字母大写的转换下划线
     * @param name
     * @return 例如  toUnderlineStyle 变成 to_underline_style
     */
    public static String toUnderlineStyle(String name) {
        StringBuilder newName = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    newName.append("_");
                }
                newName.append(Character.toLowerCase(c));
            }
            else {
                newName.append(c);
            }
        }
        return newName.toString();
    }
    
    /**
     * 根据指定开始下标和结束长度截取字符串
     * @param data
     * @param offset 开始偏移下标
     * @param length 结束长度
     * @return
     */
    public static String convertString(byte[] data, int offset, int length) {
        if (data == null) {
            return null;
        }
        else {
            try {
                return new String(data, offset, length, "UTF-8");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * 把字符串转换成Byte类型
     * @param data
     * @return
     */
    public static byte[] convertBytes(String data) {
        if (data == null) {
            return null;
        }
        else {
            try {
                return data.getBytes("UTF-8");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //随机数生成最后又重复两个0和1，因为需要凑足数组长度为64
    private static char ch[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1'};
    
    //随机数生成
    private static Random random = new Random();
    
    /**
     * 生成指定长度的随机字符串
     * @param length 长度
     * @return
     */
    public static synchronized String getRandomString(int length) {
        if (length > 0) {
            int index = 0;
            char[] temp = new char[length];
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];//取后面六位，记得对应的二进制是以补码形式存在的。
                num >>= 6;//63的二进制为:111111
                // 为什么要右移6位？因为数组里面一共有64个有效字符。为什么要除5取余？因为一个int型要用4个字节表示，也就是32位。
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        }
        else if (length == 0) {
            return "";
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * 获取随机码
     * @param digit 位数
     * @return
     */
    public static synchronized String getRandomNum(int digit) {
        //产生4位的随机数(不足4位后加零)
        String fourRandom = "";
        String num = "1";
        for (int a = 1; a <= digit; a++) {
            num = num + "0";
        }
        int randomNum = (int)(Math.random() * Integer.valueOf(num));
        fourRandom = randomNum + "";
        int randLength = fourRandom.length();
        if (randLength < digit) {
            for (int i = 1; i <= digit - randLength; i++)
                fourRandom = fourRandom + "0";
        }
        return fourRandom;
    }
    
    /**
     * 获得map里面值时判断空处理，防止空指针
     * @param inputMap map在get时的值
     * @return
     */
    public static String getMapValue(Object inputMap) {
        return inputMap == null ? "" : inputMap + "";
    }
    
    /**
     * 获取对应长度字符串
     * 
     * @param value
     * @param length
     * @return
     */
    public static String getCharLengthStr(String value, int length) {
        if (StringUtils.isEmpty(value))
            return "";
        char chars[] = value.toCharArray();
        int charidx = 0;
        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++) {
            if (chars[charidx] > '\200') {
                if ((charlen += 2) > length)
                    charidx--;
            }
            else {
                charlen++;
            }
        }
        return value.substring(0, charidx);
    }
    
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    
    /**
     * encode charset
     * @param charSet
     * @return String
     * @throws Exception
     */
    public static String encodeCharset(String charSet) throws Exception {
        return new String(charSet.getBytes("GBK"), "ISO8859_1");
    }
    
    /***
     * throw error
     * @param message
     * @throws RuntimeException
     */
    public static void error(String message) throws RuntimeException {
        RuntimeException exception = new RuntimeException(message);
        log.error(message);
        throw exception;
    }
    
    /**
     * templageContentReplace:根据传入的Map参数替换短信模板中的变量
     * @param templateContent
     * @param paraMap
     * @return
     * @Date        2017年10月13日 上午11:41:47 
     * @Author      yz.teng
     * 
     * 用法：
     *  Map<String,Object> paraMap=new HashMap<String, Object>();
     *  paraMap.put("YYYY", "1000");
     *  paraMap.put("QF", "100");
     *  String aString=getSmsMTContent("您查询的余额为$YYYY$,欠费$QF$", paraMap);
     *  System.out.println(aString);
     */
    public static String templageContentReplace(String templateContent, Map<String, Object> paraMap) {
        String replaceCommand = "";
        String replaceChar = "$";
        for (String key : paraMap.keySet()) {
            replaceCommand = replaceChar + key + replaceChar;
            if (templateContent.indexOf(replaceCommand) > -1) {
                templateContent = templateContent.replace(replaceCommand, String.valueOf(paraMap.get(key)));
            }
        }
        return templateContent;
    }
    
    /**
     * URL 解码
     *
     * @return String
     * @author yz.teng
     * @date 2015-3-17 下午04:09:51
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * URL 转码
     *
     * @return String
     * @author yz.teng
     * @date 2015-3-17 下午04:10:28
     *  测试：String str = "测试1";
     *  System.out.println(getURLEncoderString(str));
     *  System.out.println(getURLDecoderString(str));
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 比较两个字符串（大小写不敏感）。
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, "abc")  = false
     * StringUtil.equalsIgnoreCase("abc", null)  = false
     * StringUtil.equalsIgnoreCase("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     *
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }
    
    public static String createPrimaryKeySeq() {
        String currentTimeMillisStr = new Long(System.currentTimeMillis()).toString();
        return currentTimeMillisStr + genRandStr(25);
    }
    
    /**
     * 根据长度生成随机字符串
     * @param len
     * @return
     */
    public static String genRandStr(int len) {
        String rand = "";
        int readomWordIndex = 0;
        String[] readomWord = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for (int i = 0; i < len; i++) {
            readomWordIndex = (int)(Math.random() * 52);
            rand += readomWord[readomWordIndex];
        }
        return rand;
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        //        Map<String, Object> paraMap = new HashMap<String, Object>();
        //        paraMap.put("YYYY", "1000");
        //        paraMap.put("QF", "100");
        //        String aString = templageContentReplace("您查询的余额为$YYYY$,欠费$QF$", paraMap);
        //        System.out.println(aString);
        //        IDataMap newItem = new DataHashMap();
        //        newItem.put("NAME1", "123");
        //        newItem.put("NAME2", "123");
        //        System.out.println(verifyData(newItem, "NAME"));
        System.out.println(camelize("SPECIAL_SELF_NUMBER"));
        //测试
        IDataMap newItem = new DataHashMap();
        newItem.put("NAME1", "123");
        newItem.put("NAME2", "123");
        System.out.println(verifyData(newItem, "NAME"));
    }
}
