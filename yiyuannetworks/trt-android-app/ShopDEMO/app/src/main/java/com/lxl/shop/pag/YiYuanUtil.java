package com.lxl.shop.pag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/1/7
 */
public class YiYuanUtil {

    public static final String TIMEZONE_CN = "Asia/Shanghai";
    public final static String SIMPLEFORMATTYPESTRING3 = "yyyy-M-d HH:mm:ss";

    /**
     * 获取当前日期
     *
     * @return Calendar
     */
    public static Calendar getCurrentCalendar() {
        Calendar currentCalendar = Calendar.getInstance();
        return currentCalendar;
    }


    /**
     * 获取当前日期 8位
     *
     * @return String
     */
    public static String getCurrentTime(String format) {
        Calendar currentCalendar = getCurrentCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        TimeZone timeZone = TimeZone.getTimeZone(TIMEZONE_CN);
        dateFormat.setTimeZone(timeZone);
        String str = dateFormat.format(currentCalendar.getTime());
        return str;
    }

    /**
     * 获取当前日期 8位
     *
     * @return String
     */
    public static String getCurrentTime() {
        return getCurrentTime(SIMPLEFORMATTYPESTRING3);
    }

}
