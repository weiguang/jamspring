package com.okayjam.web.common.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * 时间工具类
 *
 * @author Chen weiguang
 * @date 2021/03/05 20:55
 **/
public class DateUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(LocalDateTime date) {
        return date.format(DEFAULT_FORMATTER);
    }

    public static String formatDate(LocalDateTime date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DEFAULT_FORMATTER);
    }

    public static Date praseDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
    }


    public static Date toDate(LocalDateTime dateTime) {
        return toDate(dateTime, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime dateTime, ZoneId zoneId) {
        Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
        return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDate(Date date) {
        return toLocalDate(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDate(Date date, ZoneId zoneId) {
        return date == null ? null : date.toInstant().atZone(zoneId).toLocalDateTime();
    }


    /**
     * 获取时间格式工具
     *
     * @param date 时间字符串
     * @return 时间格式工具
     */
    public static SimpleDateFormat getFormatByDate(String date) {
        SimpleDateFormat sdf;
        int size = StringUtils.hasLength(date) ? date.length() : 0;
        if (size == 12) {
            sdf = new SimpleDateFormat("yyyyMMddHHmm");
        } else if (size == 10) {
            sdf = new SimpleDateFormat("yyyyMMddHH");
        } else if (size == 8) {
            sdf = new SimpleDateFormat("yyyyMMdd");
        } else if (size == 6) {
            sdf = new SimpleDateFormat("yyyyMM");
        } else if (size == 4) {
            sdf = new SimpleDateFormat("yyyy");
        } else {
            sdf = new SimpleDateFormat("yyyyMMdd");
        }
        return sdf;
    }

    /**
     * 获取时间差
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 时间差
     */
    public static Long getDurationMs(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        if (date1.after(date2)) {
            return date1.getTime() - date2.getTime();
        } else {
            return date2.getTime() - date1.getTime();
        }
    }

    /**
     * 获取时间差(毫秒)
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 时间差(毫秒)
     */
    public static Long getDurationMs(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        return Math.abs(ChronoUnit.MILLIS.between(date1, date2));
    }


    /**
     * 计算两个时间差（年，月，星期，日，时，分，秒）
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return string
     */
    public static String calculateTimeDifference(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return "";
        }
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime fromDateTime = LocalDateTime.ofInstant(startDate.toInstant(), zoneId);
        LocalDateTime toDateTime = LocalDateTime.ofInstant(endDate.toInstant(), zoneId);
        return calculateTimeDifference(fromDateTime, toDateTime);
    }

    /**
     * 计算两个时间差（年，月，星期，日，时，分，秒）
     *
     * @param fromDateTime 开始时间
     * @param toDateTime 结束时间
     * @return string
     */
    public static String calculateTimeDifference(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        if (null == fromDateTime || null == toDateTime) {
            return "";
        }
        LocalDateTime tempDateTime = fromDateTime;
//
//        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
//        tempDateTime = tempDateTime.plusYears(years);
//
//        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
//        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        return (0 == days ? "" : days + "天")
                + (0 == hours ? "" : hours + "时")
                + (minutes + "分")
                + (seconds + "秒");

    }

    /**
     * 计算两个时间差（年，月，星期，日，时，分，秒）
     *
     * @param fromDateTime 开始时间
     * @param toDateTime 结束时间
     * @return string
     */
    public static String calculateTimeDifference2(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        if (null == fromDateTime || null == toDateTime) {
            return "";
        }
        LocalDateTime tempDateTime = fromDateTime;

        long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

        return (0 == years ? "" : years + "y")
                + (0 == months ? "" : months + "M")
                + (0 == days ? "" : days + "d")
                + (0 == hours ? "" : hours + "h")
                + (minutes + "m")
                + (seconds + "s");

    }

    /**
     * 时间加减
     *
     * @param date 时间
     * @param delta 增量
     * @param periodUnit
     *         时间单位，Calendar.YEAR,Calendar.MONTH,Calendar.DATE,Calendar.HOUR,Calendar.MINUTE,Calendar.SECOND
     * @return 新时间
     */
    public static Date plusTime(Date date, int delta, int periodUnit) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(periodUnit, delta);//日期减1年
        return rightNow.getTime();
    }


    /**
     * 时间加减
     *
     * @param date 时间
     * @param delta 增量
     * @param unit 时间单位(ChronoUnit中的常量)
     * @return 新时间
     */
    public static LocalDateTime plusTime(LocalDateTime date, long delta, TemporalUnit unit) {
        return date.plus(delta, unit);
    }


}
