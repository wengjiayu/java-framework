package com.fatiger.framework.common.utils;

import com.fatiger.framework.core.exception.SysException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.ExceptionErrorCode.SYS_ERROR_CODE;

/**
 * @author wengjiayu
 * @date 25/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class DateUtil {

    private static final String DATE_STRING_NOT_NULL = "The 'dateString' must not be null!";
    private static final String DATE_NOT_NULL = "The 'date' must not be null!";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String YYYYMM_DATE_PATTERN = "yyyyMM";
    public static final String YYYYMMDD_DATE_PATTERN = "yyyyMMdd";
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATEDETAIL_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String HUJIANG_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String HUJIANG_DATE_FORMAT_PASS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String HUJIANG_DATE_FORMAT_PASS_SSSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";
    public static final long MILLIS_A_DAY = 86400000L;

    private DateUtil() {
    }

    public static int currentYear() {
        return DateTime.now().getYear();
    }

    public static Date currentDate() {
        return DateTime.now().toDate();
    }

    public static String nowDate(String strFormat) {
        Assert.notNull(strFormat, "The 'dateString' must not be null!");
        return (new DateTime()).toString(strFormat, Locale.CHINESE);
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp((new DateTime()).getMillis());
    }

    public static Date toDate(String dateString, String pattern) {
        Assert.notNull(dateString, "The 'dateString' must not be null!");
        Assert.notNull(pattern, "The 'pattern' must not be null!");
        return DateTime.parse(dateString, DateTimeFormat.forPattern(pattern)).toDate();
    }

    public static Date toISODate(String dateString) {
        Assert.notNull(dateString, "The 'dateString' must not be null!");
        return DateTime.parse(dateString, ISODateTimeFormat.dateTime()).toDate();
    }

    public static Date toDate(String dateString) {
        Assert.notNull(dateString, "The 'dateString' must not be null!");
        return DateTime.parse(dateString).toDate();
    }

    public static Date toDateTime(String dateString) {
        Assert.notNull(dateString, "The 'dateString' must not be null!");
        return DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate();
    }

    public static String toDateString(Date date, String pattern) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(pattern, "The 'pattern' must not be null!");
        return (new DateTime(date)).toString(pattern, Locale.CHINESE);
    }

    public static String toDateString(Date date) {
        Assert.notNull(date, "The 'date' must not be null!");
        return (new DateTime(date)).toString("yyyy-MM-dd", Locale.CHINESE);
    }

    public static String toDateTimeString(Date date) {
        Assert.notNull(date, "The 'date' must not be null!");
        return (new DateTime(date)).toString("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    }

    public static Date diffDate(Date date, Integer days) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(days, "The 'days' must not be null!");
        return (new DateTime(date)).minusDays(days).toDate();
    }

    public static long getMillis(Date date) {
        Assert.notNull(date, "The 'date' must not be null!");
        return (new DateTime(date)).getMillis();
    }

    public static Date addDate(Date date, Integer days) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(days, "The 'days' must not be null!");
        return (new DateTime(date)).plusDays(days).toDate();
    }

    public static Date addYear(Date date, Integer years) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(years, "The 'dateString' must not be null!");
        return (new DateTime(date)).plusYears(years).toDate();
    }

    public static Date addMonth(Date date, Integer months) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(months, "The 'dateString' must not be null!");
        return (new DateTime(date)).plusMonths(months).toDate();
    }

    public static Date addHours(Date date, Integer hours) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(hours, "The 'hours' must not be null!");
        return (new DateTime(date)).plusHours(hours).toDate();
    }

    public static Date addMinutes(Date date, Integer minutes) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(minutes, "The 'minutes' must not be null!");
        return (new DateTime(date)).plusMinutes(minutes).toDate();
    }

    public static Date addSeconds(Date date, Integer seconds) {
        Assert.notNull(date, "The 'date' must not be null!");
        Assert.notNull(seconds, "The 'seconds' must not be null!");
        return (new DateTime(date)).plusSeconds(seconds).toDate();
    }

    public static String getMonth(String quarters) {
        Assert.notNull(quarters, "The 'quarters' must not be null!");
        int m = Integer.parseInt(quarters);
        m = m * 3 - 2;
        String month;
        if (m > 0 && m < 10) {
            month = "0" + String.valueOf(m);
        } else {
            month = String.valueOf(m);
        }

        return month;
    }

    public static String getQuarters(String month) {
        Assert.notNull(month, "The 'month' must not be null!");
        String quarters = null;
        int m = Integer.parseInt(month);
        if (m == 1 || m == 2 || m == 3) {
            quarters = "1";
        }

        if (m == 4 || m == 5 || m == 6) {
            quarters = "2";
        }

        if (m == 7 || m == 8 || m == 9) {
            quarters = "3";
        }

        if (m == 10 || m == 11 || m == 12) {
            quarters = "4";
        }

        return quarters;
    }

    public static String getFirstDateOfWeek(String datestr) {
        Assert.notNull(datestr, "The 'dateString' must not be null!");
        DateTime dt = DateTime.parse(datestr);
        return dt.plusDays(-dt.getDayOfWeek() + 1).toString("yyyy-MM-dd");
    }

    public static int getWeekOfYear(String datestr) {
        Assert.notNull(datestr, "The 'dateString' must not be null!");
        return DateTime.parse(datestr).weekOfWeekyear().get();
    }

    public static String getWeekday(String datestr) {
        Assert.notNull(datestr, "The 'dateString' must not be null!");

        try {
            switch (DateTime.parse(datestr).dayOfWeek().get()) {
                case 1:
                    return "星期一";
                case 2:
                    return "星期二";
                case 3:
                    return "星期三";
                case 4:
                    return "星期四";
                case 5:
                    return "星期五";
                case 6:
                    return "星期六";
                default:
                    return "星期天";
            }
        } catch (Exception var2) {
            throw new SysException(SYS_ERROR_CODE, var2.getMessage(), var2);
        }
    }

    public static Date getDate(Object object) {
        Assert.notNull(object, "The 'object' must not be null!");
        if (object instanceof String) {
            return DateTime.parse((String) object).toDate();
        } else if (!(object instanceof Date) && !(object instanceof Timestamp)) {
            if (object instanceof Long) {
                return (new DateTime((Long) object)).toDate();
            } else {
                throw new SysException(SYS_ERROR_CODE, "this object can't to date!");
            }
        } else {
            return (Date) object;
        }
    }

    public static Date fromTimeticks(Long ticks) {
        Assert.notNull(ticks, "The 'ticks' must not be null!");
        return (new DateTime(ticks)).toDate();
    }

    public static Long toTimeticks(Date time) {
        return time.getTime();
    }


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


