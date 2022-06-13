package com.longmaster.core.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
    /**
     * 英文简写（默认）如：2010-12-01
     */
    public static String FORMAT_SHORT = "yyyy-MM-dd";
    /**
     * 英文全称 如：2010-12-01 23:15:06
     */
    public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    /**
     * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
     */
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
    /**
     * 中文简写 如：2010年12月01日
     */
    public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
    /**
     * 中文全称 如：2010年12月01日 23时15分06秒
     */
    public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    /**
     * 精确到毫秒的完整中文时间
     */
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

    public static String FORMAT_LONG_TIGHT = "yyyyMMddHHmmss";

    /**
     * 获得默认的 date pattern
     */
    public static String getDatePattern() {
        return FORMAT_LONG;
    }

    /**
     * 根据预设格式返回当前日期
     *
     * @return
     */
    public static String getNow() {
        return format(new Date());
    }

    public static Timestamp getNowForTimestamp() {
        return Timestamp.valueOf(new SimpleDateFormat(FORMAT_LONG).format(new Date()));
    }

    public static Timestamp getTimestampByDate(Date date) {
        return Timestamp.valueOf(new SimpleDateFormat(FORMAT_LONG).format(date));
    }

    /**
     * 根据用户格式返回当前日期
     *
     * @param format
     * @return
     */
    public static String getNow(String format) {
        return format(new Date(), format);
    }

    /**
     * 使用预设格式格式化日期
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, getDatePattern());
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */
    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 使用预设格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @return
     */
    public static Date parse(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 在日期上增加数个整月
     *
     * @param date 日期
     * @param n    要增加的月数
     * @return
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 方法名称：addYear
     */
    public static Date addYear(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, n);
        return cal.getTime();
    }

    /**
     * 在日期上增加天数
     *
     * @param date 日期
     * @param n    要增加的天数
     * @return
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    public static Date addMINUTE(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);
        return cal.getTime();
    }

    public static Date addMINUTE(String date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date, FORMAT_LONG));
        cal.add(Calendar.MINUTE, n);
        return cal.getTime();
    }

    /**
     * 方法名称：subtractDay 修改备注：
     */
    public static Date subtractDay(Date date, int n) {
        Date d = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, -n); // 减1天
        return cal.getTime();
    }

    /**
     * 获取时间戳
     */
    public static String getTimeString() {
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    /**
     * 获取日期年份
     *
     * @param date 日期
     * @return
     */
    public static String getYear(Date date) {
        return format(date).substring(0, 4);
    }

    /**
     * 按默认格式的字符串距离今天的天数
     *
     * @param date 日期字符串
     * @return
     */
    public static int countDays(String date) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date));
        long t1 = c.getTime().getTime();
        return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
    }

    /**
     * 按用户格式字符串距离今天的天数
     *
     * @param date   日期字符串
     * @param format 日期格式
     * @return
     */
    public static int countDays(String date, String format) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date, format));
        long t1 = c.getTime().getTime();
        return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
    }

    /**
     * 方法名称：month 方法描述：计算月差
     */
    public static int month(Date d1, Date d2) {
        int monthday;

        SimpleDateFormat sfy = new SimpleDateFormat("yyyy");
        SimpleDateFormat sfm = new SimpleDateFormat("MM");
        SimpleDateFormat sfd = new SimpleDateFormat("dd");

        String sYear = sfy.format(d1);
        String sMonth = sfm.format(d1);
        String sMontd = sfd.format(d1);

        String eYear = sfy.format(d2);
        String eMonth = sfm.format(d2);
        String eMontd = sfd.format(d2);
        if (Integer.parseInt(sMontd) > Integer.parseInt(eMontd)) {
            monthday = (Integer.parseInt(eYear) - Integer.parseInt(sYear)) * 12
                    + (Integer.parseInt(eMonth) - Integer.parseInt(sMonth) - 1);
        } else {
            monthday = (Integer.parseInt(eYear) - Integer.parseInt(sYear)) * 12
                    + (Integer.parseInt(eMonth) - Integer.parseInt(sMonth));
        }

        return monthday;
    }

    /**
     * 方法名称：monthCha 方法描述：计算月差,不加入天数 修改时间： 修改备注：
     */
    public static int monthCha(Date d1, Date d2) {
        if (d1.getTime() >= d2.getTime()) {
            return 0;
        } else {
            int monthday;

            SimpleDateFormat sfy = new SimpleDateFormat("yyyy");
            SimpleDateFormat sfm = new SimpleDateFormat("MM");

            String sYear = sfy.format(d1);
            String sMonth = sfm.format(d1);

            String eYear = sfy.format(d2);
            String eMonth = sfm.format(d2);
            monthday = (Integer.parseInt(eYear) - Integer.parseInt(sYear)) * 12
                    + (Integer.parseInt(eMonth) - Integer.parseInt(sMonth));

            return monthday;
        }
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;

    }

    /**
     * 方法名称：getWeek 方法描述：获取星期数
     */
    public static String getWeek(Date date) {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        return dateFm.format(date);
    }

    /**
     * 方法名称：getDay 方法描述：得到日
     */
    public static int getDay(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int day = now.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 方法名称：getDaysByYearMonth 方法描述：月份 天数 修改时间： 修改备注：
     */
    @SuppressWarnings("deprecation")
    public static int getDaysByYearMonth(String dates) {
        Date date = parse(dates);
        // 获取年
        int year = Integer.parseInt(getYear(date));
        // 获取月
        int month = date.getMonth() + 1;
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * @param hour
     * @return
     */
    public static String getAfterHour(int hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long timeLose = System.currentTimeMillis() + hour * 60 * 60 * 1000;

        return sdf.format(new Date(timeLose));
    }

    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * timestamp 转date
     *
     * @param timestamp
     * @return
     */
    public static Date timeStampToDate(String timestamp) {
        if (timestamp != null) {
            Timestamp ts = new Timestamp(Long.parseLong(timestamp));
            Date date = new Date();
            date = ts;
            return date;
        } else {
            return null;
        }
    }

    public static boolean isDateStr(String str, String formatDate) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(formatDate);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 获取２个时间之前的所有天数
     *
     * @param s
     * @param e
     * @return
     * @throws ParseException
     */
    public static List<String> getBetweenDays(String s, String e) throws ParseException {
        List<String> list = new ArrayList<String>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = format.parse(s);
        Date endTime = format.parse(e);
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(endTime);

        while (startTime.getTime() <= endTime.getTime()) {
            list.add(format.format(endTime));
            tempStart.add(Calendar.DAY_OF_YEAR, -1);
            endTime = tempStart.getTime();
        }
        return list;
    }

    public static String timeZoneTransfer(String time, String pattern, String nowTimeZone, String targetTimeZone) {
        if (TextUtil.isEmpty(time)) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + nowTimeZone));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return "";
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + targetTimeZone));
        return simpleDateFormat.format(date);
    }

    public static String getGmtTime(String date) {
        TimeZone tz = TimeZone.getTimeZone("GMT+8");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(parse(date, FORMAT_LONG));
        return nowAsISO;
    }

    public static String getGmtTime(Date date) {
        TimeZone tz = TimeZone.getTimeZone("GMT+8");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static List<String> getDaysBetween(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static List<String> getMonthBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        try {

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();

            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static List<String> getYearBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");//格式化为年月

        try {

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();

            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static List<String> getWeekBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-ww");//格式化为年周
        try {
            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();

            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.WEEK_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
