package com.wade.framework.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.wade.framework.common.util.base.BaseTimeHelper;

/**
 * 时间工具类
 * 
 * @Description 时间工具类(日期格式化,日期解析,日期操作,账期加减)
 * @ClassName TimeUtil
 * @Date 2015年11月4日 上午10:19:31
 * @Author yz.teng
 */
public final class TimeHelper {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    protected static final String getSysDate(String format, boolean isLocal) throws Exception {
        return BaseTimeHelper.getSysDate(format, isLocal);
    }
    
    /**
     * 根据指定的时间格式，获取当前时间
     * 
     * @param format
     * @return
     * @throws Exception
     * @Date 2016年9月18日 下午6:24:37
     * @Author tengyizu
     */
    public static final String getSysDate(String format) throws Exception {
        return getSysDate(format, false);
    }
    
    public static final String getSysDateLocal(String format) throws Exception {
        return getSysDate(format, true);
    }
    
    /**
     * 获得当前时间
     * 
     * @param isLocal 是否本地 true是本应用的时间 false是数据库时间
     * @return
     * @throws Exception
     */
    protected static final String getSysDate(boolean isLocal) throws Exception {
        return getSysDate(DATE_FORMAT, isLocal);
    }
    
    /**
     * 获得当前数据库系统时间
     * 
     * @return 时间字符串（格式："yyyy-MM-dd"）
     * @throws Exception
     */
    public static final String getSysDate() throws Exception {
        return getSysDate(DATE_FORMAT);
    }
    
    /**
     * 获得当前数据库系统时间
     * 
     * @return 时间Date（格式："yyyy-MM-dd"）
     * @throws Exception
     */
    public static final Date getSysDateForDate() throws Exception {
        return StringToDate(getSysDate(DATE_FORMAT), DATE_FORMAT);
    }
    
    /**
     * 获取数据库时间
     * 
     * @return 时间字符串（格式："yyyy-MM-dd HH:mm:ss"）
     * @throws Exception
     */
    public static final String getSysTime() throws Exception {
        return getSysDate(TIME_FORMAT);
    }
    
    /**
     * 获取数据库时间
     * 
     * @return 时间Date（格式："yyyy-MM-dd HH:mm:ss"）
     * @throws Exception
     */
    public static final Date getSysTimeForDate() throws Exception {
        return StringToDate(getSysDate(TIME_FORMAT), TIME_FORMAT);
    }
    
    protected static final String getSysTime(boolean isLocal) throws Exception {
        return getSysDate(TIME_FORMAT, isLocal);
    }
    
    public static final String getTimestampFormat(String value) throws Exception {
        return BaseTimeHelper.getTimestampFormat(value);
    }
    
    public static final Timestamp parse(String timeStr) throws Exception {
        String format = getTimestampFormat(timeStr);
        return parse(timeStr, format);
    }
    
    public static final Timestamp parse(String timeStr, String format) throws Exception {
        return BaseTimeHelper.encodeTimestamp(format, timeStr);
    }
    
    /**
     * 把Date时间格式化成String（格式：yyyy-MM-dd HH:mm:ss）
     * 功能描述: <br>
     * 〈功能详细描述〉
     * @param time
     * @return
     * @Author      yz.teng
     */
    public static final String format(Date time) {
        return format(time, TIME_FORMAT);
    }
    
    /**
     * 时间字符串转换成Date类型
     * @param timeStr
     * @param format
     * @return
     * @throws Exception
     * @Date        2017年7月19日 下午3:04:46 
     * @Author      yz.teng
     */
    public static final Date StringToDate(String timeStr, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(timeStr);
        return date;
    }
    
    /**
     * 时间格式化
     * 功能描述: <br>
     * 〈功能详细描述〉
     * @param time
     * @param format
     * @return
     * @Author      yz.teng
     */
    public static final String format(Date time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }
    
    /**
     * 时间格式化
     * 功能描述: <br>
     * 〈功能详细描述〉
     * @param time
     * @param format
     * @return
     * @throws Exception
     * @Author      yz.teng
     */
    public static final String format(String time, String format) throws Exception {
        return format(parse(time), format);
    }
    
    public static final int daysBetween(String dateStr1, String dateStr2) throws Exception {
        Date d1 = parse(dateStr1);
        Date d2 = parse(dateStr2);
        return daysBetween(d1, d2);
    }
    
    public static final int daysBetween(Date date1, Date date2) throws Exception {
        return (int)compareDate(date1, date2, Calendar.DATE);
    }
    
    public static final int daysBetween(Calendar cal1, Calendar cal2) throws Exception {
        return (int)compareCalendar(cal1, cal2, Calendar.DATE);
    }
    
    public static long compareDate(Date date1, Date date2, int field) throws Exception {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        
        return compareCalendar(c1, c2, field);
    }
    
    public static long compareCalendar(Calendar c1, Calendar c2, int field) throws Exception {
        long t1 = c1.getTimeInMillis();
        long t2 = c2.getTimeInMillis();
        
        switch (field) {
            case Calendar.SECOND:
                return t1 / 1000 - t2 / 1000;
            case Calendar.MINUTE:
                return t1 / 600000 - t2 / 60000;
            case Calendar.HOUR:
                return t1 / 3600000 - t2 / 3600000;
            case Calendar.DATE:
                int rawOffset = c1.getTimeZone().getRawOffset();
                return (t1 + rawOffset) / 86400000 - (t2 + rawOffset) / 86400000;
            case Calendar.MONTH:
                return c1.get(Calendar.YEAR) * 12 - c2.get(Calendar.YEAR) * 12 + c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
            case Calendar.YEAR:
                return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
            default:
                return t1 - t2;
        }
    }
    
    /**
     * 获取指定日期years年后的日期 日期格式要求为: yyyy-MM-dd
     * 
     * @param date 日期字符串
     * @param years 增加的年数
     * @return String
     * @author
     */
    public static final String dateAddYear(String date, int years) throws Exception {
        return dateAddAmount(date, Calendar.YEAR, years);
    }
    
    /**
     * 获取指定日期months月后的日期 日期格式要求为: yyyy-MM-dd
     * 
     * @param date 日期字符串
     * @param months 增加的月数
     * @return String
     * @author
     */
    public static final String dateAddMonth(String date, int months) throws Exception {
        return dateAddAmount(date, Calendar.MONTH, months);
    }
    
    /**
     * 获取指定日期days天后的日期 日期格式要求为: yyyy-MM-dd
     * 
     * @param date 日期字符串
     * @param days 增加的天数
     * @return String
     * @author
     */
    public static final String dateAddDay(String date, int days) throws Exception {
        return dateAddAmount(date, Calendar.DATE, days);
    }
    
    /**
     * 获取指定时间hours小时后的日期 日期格式要求为: yyyy-MM-dd hh:mm:ss
     * 
     * @param date 日期字符串
     * @param hours 增加的小时
     * @return String
     * @author
     */
    public static final String dateAddHour(String date, int hours) throws Exception {
        return dateAddAmount(date, Calendar.HOUR_OF_DAY, hours);
    }
    
    /**
     * 获取指定时间minutes分钟后的日期 日期格式要求为: yyyy-MM-dd hh:mm:ss
     * 
     * @param date
     * @param minutes
     * @return
     * @author
     */
    public static final String dateAddMinute(String date, int minutes) throws Exception {
        return dateAddAmount(date, Calendar.MINUTE, minutes);
    }
    
    /**
     * 获取指定时间seconds秒后的日期 日期格式要求为: yyyy-MM-dd hh:mm:ss
     * 
     * @param date 日期字符串
     * @param seconds 增加的秒数
     * @return
     * @author
     */
    public static final String dateAddSecond(String date, int seconds) throws Exception {
        return dateAddAmount(date, Calendar.SECOND, seconds);
    }
    
    /**
     * 日期字符串
     * 
     * @param dateStr
     * @param field
     * @param amount
     * @return
     * @throws Exception
     */
    public static final String dateAddAmount(String dateStr, int field, int amount) throws Exception {
        return dateAddAmount(dateStr, field, amount, getTimestampFormat(dateStr));
    }
    
    public static final String dateAddAmount(String dateStr, int field, int amount, String format) throws Exception {
        String inFormat = getTimestampFormat(dateStr);// 根据传入的日期字符串，得到其日期格式
        Date date = parse(dateStr, inFormat);
        Date retDate = dateAddAmount(date, field, amount);
        return format(retDate, format);
    }
    
    /**
     * 获取指定日期months月后的日期
     * 
     * @param date 日期对象
     * @param months 增加的月数
     * @return Date
     * @author
     */
    public static final Date dateAddMonth(Date date, int months) {
        return dateAddAmount(date, Calendar.MONTH, months);
    }
    
    /**
     * 获取指定日期days天后的日期
     * 
     * @param date 日期对象
     * @param days 增加的天数
     * @return Date
     * @author
     */
    public static final Date dateAddDay(Date date, int days) {
        return dateAddAmount(date, Calendar.DATE, days);
    }
    
    /**
     * 获取指定日期hours小时后的日期
     * 
     * @param date 日期对象
     * @param hours 增加的小时数
     * @return Date
     * @author
     */
    public static final Date dateAddHour(Date date, int hours) {
        return dateAddAmount(date, Calendar.HOUR_OF_DAY, hours);
    }
    
    /**
     * 获取指定日期minutes分钟后的日期
     * 
     * @param date 日期对象
     * @param minutes 增加的分钟数
     * @return Date
     * @author
     */
    public static final Date dateAddMinute(Date date, int minutes) {
        return dateAddAmount(date, Calendar.MINUTE, minutes);
    }
    
    /**
     * 获取指定时间seconds秒后的日期
     * 
     * @param date 日期对象
     * @param seconds 增加的秒数
     * @return
     * @author
     */
    public static final Date dateAddSecond(Date date, int seconds) throws Exception {
        return dateAddAmount(date, Calendar.SECOND, seconds);
    }
    
    /**
     * 获取指定日期指定的类型之后的日期
     * 
     * @param date 日期对象
     * @param field 指定改变的位置 参见Calendar
     * @param amount
     * @return Date
     * @author
     */
    public static final Date dateAddAmount(Date date, int field, int amount) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(field, amount);
        return cd.getTime();
    }
    
    /**
     * 获取系统最大日期1900-01-01 00:00:00
     * 
     * @return
     */
    public static final String getTheFirstDateTime() {
        return "1900-01-01 00:00:00";
    }
    
    /**
     * 获取系统最大日期2050-12-31 23:59:59
     * 
     * @return
     */
    public static final String getTheLastDateTime() {
        return "2050-12-31 23:59:59";
    }
    
    /**
     * 获取系统最大月份205012
     * 
     * @return
     */
    public static final String getTheLastDate205012() {
        return "205012";
    }
    
    /**
     * 获取系统最大日期20501231
     * 
     * @return
     */
    public static final String getTheLastDate20501231() {
        return "20501231";
    }
    
    /**
     * 获取系统最大日期2050-12-31
     * 
     * @return
     */
    public static final String getTheLastDate() {
        return "2050-12-31";
    }
    
    /**
     * 获取每天最大时间 23:59:59
     * 
     * @return
     */
    public static final String getTheLastTime235959() {
        return " 23:59:59";
    }
    
    /**
     * 获取每天的初始时间 00:00:00
     * 
     * @return
     */
    public static final String getTheFirstTime000000() {
        return " 00:00:00";
    }
    
    /**
     * 获取某个日期当月的最后一天的日期
     * 
     * @param dateStr
     * @return
     */
    public static final String getLastDateOfMonth(String dateStr) {
        return getTheDateOfMonth(dateStr, 31);
    }
    
    /**
     * 获取某个日期当月的第一天的日期
     * 
     * @param dateStr
     * @return
     */
    public static final String getFirstDateOfMonth(String dateStr) {
        return getTheDateOfMonth(dateStr, 1);
    }
    
    /**
     * 获取某个日期当月的最后一天的日期
     * 
     * @param dateStr
     * @return
     */
    private static final String getTheDateOfMonth(String dateStr, int day) {
        int yyyy = Integer.parseInt(dateStr.substring(0, 4));
        
        boolean hasSep = dateStr.indexOf('-') > 0;
        int mmIndex = hasSep ? 5 : 4;
        int mm = Integer.parseInt(dateStr.substring(mmIndex, mmIndex + 2));
        
        int maxDay = getLastDay(yyyy * 100 + mm);
        if (day > maxDay)
            day = maxDay;
        return yyyy + (hasSep ? "-" : "") + (mm > 9 ? mm : "0" + mm) + (hasSep ? '-' : "") + (day > 9 ? day : "0" + day);
    }
    
    /**
     * 获取某月的天数
     * 
     * @param yyyyMM
     * @return
     */
    public static final int getLastDay(int yyyyMM) {
        int yyyy = yyyyMM / 100;
        int mm = yyyyMM % 100;
        int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (mm != 2)
            return days[mm - 1];
        if (yyyy % 400 == 0 || (yyyy % 100 != 0 && yyyy % 4 == 0))
            return 29;
        return 28;
    }
    
    /**
     * 得到两个日期之间相差的月数
     * 
     * @param yyyymm1
     * @param yyyymm2
     * @return int
     */
    public static int diffMonths(int yyyymm1, int yyyymm2) {
        return (yyyymm1 / 100 - yyyymm2 / 100) * 12 + (yyyymm1 % 100 - yyyymm2 % 100);
    }
    
    public static Timestamp getTruncDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getTimestampByYYYYMMDD(String dateString) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);
        return new Timestamp(date.getTime());
    }
    
    public static Timestamp getTimestampByYYYYMMDDHHMMSS(String dateString) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(dateString);
        return new Timestamp(date.getTime());
    }
    
    public static Timestamp getTimestampByHHMMSS(String dateString) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = formatter.parse(dateString);
        return new Timestamp(date.getTime());
    }
    
    public static Timestamp getDateOfNextMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2) + 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getDateOfPreMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2) - 1);
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getDateOfCurrentMonthEndDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.getActualMaximum(5));
        
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getLastDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getPreLastDate(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.get(5) - 1);
        
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getNextDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, rightNow.get(5) + 1);
        
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getDateOfMonthFirstDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(5, 1);
        rightNow.set(11, 0);
        rightNow.set(14, 0);
        rightNow.set(13, 0);
        rightNow.set(12, 0);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static Timestamp getDateOfCurrentEndDay(Date date) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.set(11, 23);
        rightNow.set(14, 59);
        rightNow.set(13, 59);
        rightNow.set(12, 59);
        rightNow.set(2, rightNow.get(2));
        return new Timestamp(rightNow.getTimeInMillis());
    }
    
    public static int getMonthSpace(Timestamp date1, Timestamp date2) throws Exception {
        int result = 0;
        
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        
        if (c1.get(1) == c2.get(1))
            result = c2.get(2) - c1.get(2);
        else {
            result = 12 * (c2.get(1) - c1.get(1)) + c2.get(2) - c1.get(2);
        }
        
        return result == 0 ? 0 : Math.abs(result);
    }
    
    public static Timestamp timeAddMonth(Timestamp time, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(2, month);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public static Timestamp timeAddDay(Date time, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(5, day);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public static final Timestamp encodeTimestamp(String timeStr) throws Exception {
        String format = getTimestampFormat(timeStr);
        return encodeTimestamp(format, timeStr);
    }
    
    public static final Timestamp encodeTimestamp(String format, String timeStr) throws Exception {
        if (StringHelper.isEmpty(timeStr)) {
            return null;
        }
        
        if (format.length() != timeStr.length()) {
            format = getTimestampFormat(timeStr);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return new Timestamp(sdf.parse(timeStr).getTime());
        }
        catch (Exception e) {
            throw new Exception("encodeTimestamp时间工具异常！！！", e);
        }
    }
    
    /**
     * 测试方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        System.out.println(getTimestampByYYYYMMDD("29"));
    }
}
