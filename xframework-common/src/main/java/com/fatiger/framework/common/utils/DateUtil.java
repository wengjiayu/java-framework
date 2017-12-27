package com.fatiger.framework.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author wengjiayu
 * @date 25/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class DateUtil {

    public static final String DEFAULT_DATEDETAIL_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";


    /**
     * 获取UTC时间戳（以秒为单位）
     */
    public static int getTimestampInSeconds() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取UTC时间戳（以毫秒为单位）
     */
    public static long getTimestampInMillis() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return cal.getTimeInMillis();
    }


    public static String dateToString(long timestamp) {
        SimpleDateFormat s = new SimpleDateFormat(DEFAULT_DATEDETAIL_PATTERN);
        return s.format(new Date(timestamp));
    }
}


