package com.fengda.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 日期时间工具类
 * 
 * @class DateUtils
 * @author daman.lu
 * @date 2014-9-29
 */
public class DateUtil {

	public static final long ONE_HOUR = 1 * 60 * 60 * 1000;
	public static final long ONE_DAY = 24 * DateUtil.ONE_HOUR;

	// 季节相关
	public static final int SEASON_UNKNOW = -1;
	public static final int SEASON_SPRING = 0;
	public static final int SEASON_SUMMER = 1;
	public static final int SEASON_AUTUMN = 2;
	public static final int SEASON_WINTER = 3;

	/** 时间日期格式化到年月日时分秒. */
	public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

	/** 时间日期格式化到年月日时分. */
	public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

	/** 时间日期格式化到年月日时. */
	public static final String dateFormatYMDH = "yyyy-MM-dd HH";

	/** 时间日期格式化到年月日. */
	public static final String dateFormatYMD = "yyyy-MM-dd";

	/** 时间日期格式化到年月. */
	public static final String dateFormatYM = "yyyy-MM";

	/** 时间日期格式化到月日. */
	public static final String dateFormatMD = "MM/dd";

	/** 时分秒. */
	public static final String dateFormatHMS = "HH:mm:ss";

	/** 时分. */
	public static final String dateFormatHM = "HH:mm";

	private static SimpleDateFormat sdf = new SimpleDateFormat();

	public static String formatFullfmt(Date date) {
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String formatNoSecondfmt(Date date) {
		sdf.applyPattern("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}

	public static String formatYmd(Date date) {
		sdf.applyPattern(dateFormatYMD);
		return sdf.format(date);
	}

	public static String formatYmdsfm(Date date) {
		sdf.applyPattern(dateFormatYMDHMS);
		return sdf.format(date);
	}

	public static String formatYmds(Date date) {
		sdf.applyPattern(dateFormatYMDH);
		return sdf.format(date);
	}

	public static String formatFourfmt(Date date) {
		sdf.applyPattern("MMdd");
		return sdf.format(date);
	}

	public static String formatTranfmt(Date date) {
		sdf.applyPattern("yyyyMMddHHmmss");
		return sdf.format(date);
	}

	public static Date format(String yyyyMMdd_HHmm) {
		sdf.applyPattern("yyyy-MM-dd HH:mm");
		try {
			return sdf.parse(yyyyMMdd_HHmm);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Date formatYMDH(String yyyyMMdd_HH) {
		sdf.applyPattern("yyyy-MM-dd HH");
		try {
			return sdf.parse(yyyyMMdd_HH);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Date format0(String yyyyMMdd_HHmmss) {
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(yyyyMMdd_HHmmss);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Date format1(String yyyyMMdd) {
		sdf.applyPattern("yyyy-MM-dd");
		try {
			return sdf.parse(yyyyMMdd);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Date format2(String yyyyMMdd) {
		sdf.applyPattern("yyyy-MM");
		try {
			return sdf.parse(yyyyMMdd);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static String formatTimefmt(Date date) {
		sdf.applyPattern("HH:mm");
		return sdf.format(date);
	}

	public static String formatDatefmt(Date date) {
		sdf.applyPattern("yyyy-MM-dd");
		return sdf.format(date);
	}

	/** 返回格式:yyyy/MM/dd */
	public static String formatDatefmt2(Date date) {
		sdf.applyPattern("yyyy/MM/dd");
		return sdf.format(date);
	}

	public static int getNumberDayInYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_YEAR);
	}

	public static int getWeekDay() { // 取当天是周几
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	public static int getMonth() { // 取当天是几月
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int month = c.get(Calendar.MONTH) + 1;
		return month;
	}

	public static int getTimeDifferSecond(Date date) {// 返回相差的秒数
		return (int) ((date.getTime() - new Date().getTime()) / 1000);
	}

	public static int getTimeDifferSecond(String time) {// 返回相差的秒数,date格式为yyyy-MM-dd
		// HH:mm:ss
		Date date = format0(time);
		return (int) ((date.getTime() - new Date().getTime()) / 1000);
	}

	public static int getTimeDifferMini(Date date) {// 返回相差的分钟数
		return (int) ((date.getTime() - new Date().getTime()) / (1000 * 60));
	}

	public static boolean isTheSameDay(Date date) {
		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
		c1.setTime(date);
		c2.setTime(new Date());
		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.DAY_OF_MONTH) == c2
						.get(Calendar.DAY_OF_MONTH)
				&& c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isTheSameDay(String date) {
		sdf.applyPattern("yyyy-MM-dd");
		try {
			Date datetime = sdf.parse(date);
			return isTheSameDay(datetime);
		} catch (ParseException e) {
		}
		return false;
	}

	/**
	 * 获取标准时间
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getStandardTime(long timestamp) {
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timestamp);
		return sdf.format(date);
	}

	/**
	 * 获取年月日时间
	 */
	public static String getYearMonthDayTime(long timestamp) {
		sdf.applyPattern("yyyy-MM-dd");
		Date date = new Date(timestamp);
		return sdf.format(date);
	}

	/**
	 * 获取月日时间
	 */
	public static String getMonthDayTime(long timestamp) {
		sdf.applyPattern("MM-dd");
		Date date = new Date(timestamp);
		return sdf.format(date);
	}

	/**
	 * 获取Date对象
	 * 
	 * @param timestamp
	 * @return
	 */
	public static Date getStandardDate(long timestamp) {
		return new Date(timestamp);
	}

	/**
	 * 得到昨天的日期
	 */
	public static Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		sdf.applyPattern("yyyy-MM-dd 00:00:00");
		return format0(sdf.format(cal.getTime()));
	}

	/**
	 * 得到昨天的日期字符串
	 */
	public static String getYesterdayString() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		sdf.applyPattern("yyyy-MM-dd 00:00:00");
		return sdf.format(cal.getTime());
	}

	/**
	 * 返回相差的小时数
	 * 
	 * @param date
	 * @return
	 */
	public static int getTimeDifferOurs(Date date) {
		return (int) ((new Date().getTime() - date.getTime()) / (1000 * 60 * 60));
	}

	/**
	 * 返回相差的小时数
	 * 
	 * @param time
	 * @return
	 */
	public static int getTimeDifferOurs(String time) {
		Date date = formatYMDH(time);
		return (int) ((date.getTime() - new Date().getTime()) / 1000);
	}

	/**
	 * 返回相差的天数
	 * 
	 * @param date
	 * @return
	 */
	public static int getTimeDifferDay2(Date date) {
		Calendar d = Calendar.getInstance();
		return (int) ((getTimeDifferOurs(date) + d.get(Calendar.HOUR)) / 24);
	}

	/**
	 * 得到与当前时间的分钟差字符串(负是前,正是后)
	 */
	public static String getBeforeMinString(int min) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, min);
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}

	/**
	 * 字符串转日期格式:年月日时分
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date genDateFromStr(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 字符串转日期格式:年月日
	 * @param dateStr
	 * @return
	 */
	public static Date genYearMonthDayFromStr(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date genYearMonthFromStr(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取日期中的年月日时分
	 * 
	 * @param date
	 * @return
	 */
	public static String getDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String time = sdf.format(date);
		// System.out.println("格式化后年月日时分：" + time);
		return time;
	}

	/**
	 * 获取日期中的年月日
	 * 
	 * @param date
	 * @return
	 */
	public static String getYearMonthDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yearMonthDayStr = sdf.format(date);
		// System.out.println("格式化后年月日：" + yearMonthDayStr);
		return yearMonthDayStr;
	}

	/**
	 * 获取日期字符串中的年月日
	 * 
	 * @param
	 * @return
	 */
	public static String getYearMonthDayStr(String str) {
		try{
			if (!StringUtils.isBlank(str)) {
				Date date = genYearMonthDayFromStr(str);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				str = sdf.format(date);
				// System.out.println("格式化后年月日：" + yearMonthDayStr);
				return str;
			}

		}catch (Exception e){

		}
		return str;

	}

	/**
	 * 获取日期字符串中的月日
	 * 
	 * @param str
	 *            2012-10-1
	 * @return
	 */
	public static String getMonthDayStr(String str) {
		if (!StringUtils.isBlank(str)) {
			Date date = genYearMonthDayFromStr(str);
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			String MonthDayStr = sdf.format(date);
			return MonthDayStr;
		}
		return "";
	}

	public static String getMonth(String str) {
		if (!StringUtils.isBlank(str)) {
			Date date = format2(str);
			SimpleDateFormat sdf = new SimpleDateFormat("MM");
			String MonthDayStr = sdf.format(date);
			return MonthDayStr;
		}
		return "";
	}



	/**
	 * 获取日期中的时分
	 * 
	 * @param date
	 * @return
	 */
	public static String getHourMinute(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String hourMinuteStr = sdf.format(date);
		// System.out.println("格式化后时分：" + hourMinuteStr);
		return hourMinuteStr;
	}

	/**
	 * 年月日比较
	 * 
	 * @param dat1
	 * @param dat2
	 * @return 返回值 0：相等 ;1： date2大于date1; 其他:date2小于date1
	 */
	public static int compareYearMonthDay(Date dat1, Date dat2) {
		int dateComPareFlag = 0;
		dateComPareFlag = dat2.compareTo(dat1);
		return dateComPareFlag;
	}

	public static boolean dateCompare(Date dat1, Date dat2) {
		boolean dateCompareFlag = true;
		if (dat2.compareTo(dat1) != 1) {
			dateCompareFlag = false; //
		}
		return dateCompareFlag;
	}

	/**
	 * 是否同一月
	 * 
	 * @param dat2
	 * @return
	 */
	public static boolean isSameMonth(Date dat2) {
		boolean dateCompareFlag = false;

		Date dat1 = new Date();

		if (dat2.compareTo(dat1) == 0) {
			dateCompareFlag = true;
		}
		return dateCompareFlag;
	}

	/** 获取当前月第一天 */
	public static String getCurrMonthFirstDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		String firstDay = formatDatefmt(c.getTime());
		return firstDay;
	}

	/** 获取当前月的最后一天 */
	public static String getCurrMonthLastDay() {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDay = formatDatefmt(ca.getTime());
		return lastDay;
	}

	/** 获取前月的最后一天 */
	public static String getFrontMonthLastDay() {
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);// 设置为1号,当前日期既为本月第一天
		String lastDay = formatDatefmt(cale.getTime());
		return lastDay;
	}

	/** 获取当前年和月 格式:yyyy年MM月 */
	public static String getCurrYearMonth() {
		Date now = new Date();
		sdf.applyPattern("yyyy年MM月");
		return sdf.format(now);
	}

	public static String getDateText(long milliseconds) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		builder.append(cal.get(Calendar.YEAR)).append("-")
				.append(cal.get(Calendar.MONTH) + 1).append("-")
				.append(cal.get(Calendar.DAY_OF_MONTH));
		return builder.toString();
	}

	public static String getDateCNText(long milliseconds) {
		StringBuilder builder = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);
		builder.append(cal.get(Calendar.YEAR)).append("年")
				.append(cal.get(Calendar.MONTH) + 1).append("月");
		return builder.toString();
	}

	/**
	 * 取时分秒 hh:mm
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String getTimeStr(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milliseconds);

		sdf.applyPattern("HH:mm");
		return sdf.format(cal.getTime());
		// int hour = cal.get(Calendar.HOUR_OF_DAY);
		// int mins = cal.get(Calendar.MINUTE);
		// img_white_return hour + ":" + mins;
	}

	/**
	 * 取时分秒 hh:mm:ss
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String getTimeStr2(long milliseconds) {
		return getStandardTime(milliseconds).substring(11, 19);
	}

	/**
	 * 取时分 hh:mm
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String getTimeStr3(long milliseconds) {
		return getStandardTime(milliseconds).substring(11, 16);
	}

	// get today begin time, 00:00
	public static long getTodayMillis() {
		return getOnedayMillis(System.currentTimeMillis());
	}

	public static long getTodayMillisForUTC() {
		return getOnedayMillisForUTC(System.currentTimeMillis());
	}

	/**
	 * 通过毫秒数，获取这一天00:00的毫秒数
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static long getOnedayMillis(long milliseconds) {
		Calendar cal = Calendar.getInstance();
		if (milliseconds > 0) {
			cal.setTimeInMillis(milliseconds);
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTimeInMillis();
	}

	public static long getOnedayMillisForUTC(long milliseconds) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		if (milliseconds > 0) {
			cal.setTimeInMillis(milliseconds);
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTimeInMillis();
	}

	/**
	 * 通过毫秒数，获取这一天00:00的毫秒数
	 * 
	 * @param
	 * @return
	 */
	public static long getOnedayBeginMillis(String yyMMdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(format1(yyMMdd));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTimeInMillis();
	}

	/**
	 * 通过毫秒数，获取这一天23:59:59的毫秒数
	 * 
	 * @param yyMMdd
	 * @return
	 */
	public static long getOnedayEndMillis(String yyMMdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(format0(yyMMdd + " 23:59:59"));
		return cal.getTimeInMillis();
	}

	/**
	 * 得到如10.10格式的日期
	 * 
	 * @param yyMMdd
	 * @return
	 */
	public static String getDateString(String yyMMdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(format1(yyMMdd));
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return month + "." + day;
	}

	// 通过毫秒数，获取本月1号00:00的毫秒数
	public static long getCurrentMonthBegin(long millis) {
		Calendar cal = Calendar.getInstance();
		if (millis > 0) {
			cal.setTimeInMillis(millis);
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		cal.clear();
		cal.set(year, month, 1);
		return cal.getTimeInMillis();
	}

	public static long getPreMonthBegin(long millis) {
		Calendar cal = Calendar.getInstance();
		if (millis > 0) {
			cal.setTimeInMillis(millis);
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		cal.clear();
		cal.set(year, month - 1, 1);
		return cal.getTimeInMillis();
	}

	public static long getNextMonthBegin(long millis) {
		Calendar cal = Calendar.getInstance();
		if (millis > 0) {
			cal.setTimeInMillis(millis);
		}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		cal.clear();
		cal.set(year, month + 1, 1);
		return cal.getTimeInMillis();
	}

	/**
	 * 获取当前时间的月份
	 * 
	 * @param millis
	 * @return
	 */
	public static int getCurrentTimeMonth(long millis) {
		Calendar cal = Calendar.getInstance();
		if (millis > 0) {
			cal.setTimeInMillis(millis);
		}
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 获取当前时间的年份
	 * 
	 * @param millis
	 * @return
	 */
	public static int getCurrentTimeYear(long millis) {
		Calendar cal = Calendar.getInstance();
		if (millis > 0) {
			cal.setTimeInMillis(millis);
		}
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 获取当前月份
	 * 
	 * @param
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 取得当前季节
	 * 
	 * @return
	 */
	public static int getSeason() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		if (month == 3 || month == 4 || month == 5) {
			return SEASON_SPRING;

		} else if (month == 6 || month == 7 || month == 8) {
			return SEASON_SUMMER;

		} else if (month == 7 || month == 8 || month == 9) {
			return SEASON_AUTUMN;

		} else if (month == 12 || month == 1 || month == 2) {
			return SEASON_WINTER;
		}

		return SEASON_UNKNOW;
	}

	/**
	 * 根据日期取得星期几
	 * 
	 * @param date
	 * @return
	 */
	public static String getWeek(Date date) {
		sdf.applyPattern("EEEE");
		return sdf.format(date);
	}

	/**
	 * 根据日期取得星期几
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeekIndex(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	/**
	 * 得到当前月的所有周的每天
	 * 
	 * @param date
	 * @return
	 */
	public static List<List<String>> getWeeksOfMonth(Date date) {
		List<List<String>> weeks = new ArrayList<List<String>>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		List<String> weekDays = new ArrayList<String>();
		String yearMonth = year + "-" + month + "-";
		for (int i = 1; i <= days; i++) {
			Date d = format1(yearMonth + i);
			int weekIndex = getWeekIndex(d);
			if (weekIndex == 0) {
				weekDays.add(yearMonth + i);
				weeks.add(weekDays);
				weekDays = new ArrayList<String>();
			} else {
				weekDays.add(yearMonth + i);
				if (i == days) {
					weeks.add(weekDays);
				}
			}
		}
		return weeks;
	}

	/**
	 * 获取当天和现在时分秒的timemillis
	 * 
	 * @param timemillis
	 * @return
	 */
	public static long getOnedayAndCurrTimeMillis(long timemillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format0(getDateText(timemillis) + " "
				+ getTimeStr2(System.currentTimeMillis())));
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取当月有多少天
	 * 
	 * @param timemillis
	 * @return
	 */
	public static int getDaysOfMonth(long timemillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timemillis);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取字符串时间的时间戳
	 * 
	 * @param yyyyMMdd_HHmmss
	 * @return
	 */
	public static long getStringDateTimeMillis(String yyyyMMdd_HHmmss) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(format0(yyyyMMdd_HHmmss));
		return cal.getTimeInMillis();
	}

	/**
	 * 描述：获取表示当前日期时间的字符串.
	 * 
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String String类型的当前日期时间
	 */
	public static String getCurrentDate(String format) {
		String curDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			Calendar c = new GregorianCalendar();
			curDateTime = mSimpleDateFormat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curDateTime;
	}

	/**
	 * @param oldTime
	 *            较小的时间
	 * @param newTime
	 *            较大的时间 (如果为空 默认当前时间 ,表示和当前时间相比)
	 * @return -1 ：同一天. 0：昨天 . 1 ：至少是前天.
	 * @throws ParseException
	 *             转换异常
	 */
	public static int isYeaterday(Date oldTime, Date newTime)
			throws ParseException {
		if (newTime == null) {
			newTime = new Date();
		}
		// 将下面的 理解成 yyyy-MM-dd 00：00：00 更好理解点
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = format.format(newTime);
		Date today = format.parse(todayStr);
		// 昨天
		if ((today.getTime() - oldTime.getTime()) > 0
				&& (today.getTime() - oldTime.getTime()) <= 86400000) {// 86400000=24*60*60*1000=
																		// 一天
			return 0;
			// 今天
		} else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
			return -1;
		} else if ((today.getTime() - oldTime.getTime()) > 86400000// 至少是前天
				&& (today.getTime() - oldTime.getTime()) <= 172800000) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * 获取两个日期相差的天数
	 * 
	 * @param date
	 *            日期字符串
	 * @param otherDate
	 *            另一个日期字符串
	 * @return 相差天数
	 */
	public static int getIntervalDays(String date, String otherDate) {
		return getIntervalDays(StringToDate(date), StringToDate(otherDate));
	}

	/**
	 * @param date
	 *            日期
	 * @param otherDate
	 *            另一个日期
	 * @return 相差天数
	 */
	public static int getIntervalDays(Date date, Date otherDate) {
		date = DateUtil.StringToDate(DateUtil.getDate(date));
		long time = Math.abs(date.getTime() - otherDate.getTime());
		return (int) time / (24 * 60 * 60 * 1000);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @return 日期
	 */
	public static Date StringToDate(String date) {
		DateStyle dateStyle = null;
		return StringToDate(date, dateStyle);
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param dateStyle
	 *            日期风格
	 * @return 日期
	 */
	public static Date StringToDate(String date, DateStyle dateStyle) {
		Date myDate = null;
		if (dateStyle == null) {
			List<Long> timestamps = new ArrayList<Long>();
			for (DateStyle style : DateStyle.values()) {
				Date dateTmp = StringToDate(date, style.getValue());
				if (dateTmp != null) {
					timestamps.add(dateTmp.getTime());
				}
			}
			myDate = getAccurateDate(timestamps);
		} else {
			myDate = StringToDate(date, dateStyle.getValue());
		}
		return myDate;
	}

	/**
	 * 将日期字符串转化为日期。失败返回null。
	 * 
	 * @param date
	 *            日期字符串
	 * @param parttern
	 *            日期格式
	 * @return 日期
	 */
	public static Date StringToDate(String date, String parttern) {
		Date myDate = null;
		if (date != null) {
			try {
				myDate = getDateFormat(parttern).parse(date);
			} catch (Exception e) {
			}
		}
		return myDate;
	}

	/**
	 * 获取SimpleDateFormat
	 * 
	 * @param parttern
	 *            日期格式
	 * @return SimpleDateFormat对象
	 * @throws RuntimeException
	 *             异常：非法日期格式
	 */
	public static SimpleDateFormat getDateFormat(String parttern)
			throws RuntimeException {
		return new SimpleDateFormat(parttern);
	}

	/**
	 * 获取精确的日期
	 * 
	 * @param timestamps
	 *            时间long集合
	 * @return 日期
	 */
	private static Date getAccurateDate(List<Long> timestamps) {
		Date date = null;
		long timestamp = 0;
		Map<Long, long[]> map = new HashMap<Long, long[]>();
		List<Long> absoluteValues = new ArrayList<Long>();

		if (timestamps != null && timestamps.size() > 0) {
			if (timestamps.size() > 1) {
				for (int i = 0; i < timestamps.size(); i++) {
					for (int j = i + 1; j < timestamps.size(); j++) {
						long absoluteValue = Math.abs(timestamps.get(i)
								- timestamps.get(j));
						absoluteValues.add(absoluteValue);
						long[] timestampTmp = { timestamps.get(i),
								timestamps.get(j) };
						map.put(absoluteValue, timestampTmp);
					}
				}

				// 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的
				long minAbsoluteValue = -1;
				if (!absoluteValues.isEmpty()) {
					// 如果timestamps的size为2，这是差值只有一个，因此要给默认值
					minAbsoluteValue = absoluteValues.get(0);
				}
				for (int i = 0; i < absoluteValues.size(); i++) {
					for (int j = i + 1; j < absoluteValues.size(); j++) {
						if (absoluteValues.get(i) > absoluteValues.get(j)) {
							minAbsoluteValue = absoluteValues.get(j);
						} else {
							minAbsoluteValue = absoluteValues.get(i);
						}
					}
				}

				if (minAbsoluteValue != -1) {
					long[] timestampsLastTmp = map.get(minAbsoluteValue);
					if (absoluteValues.size() > 1) {
						timestamp = Math.max(timestampsLastTmp[0],
								timestampsLastTmp[1]);
					} else if (absoluteValues.size() == 1) {
						// 当timestamps的size为2，需要与当前时间作为参照
						long dateOne = timestampsLastTmp[0];
						long dateTwo = timestampsLastTmp[1];
						if ((Math.abs(dateOne - dateTwo)) < 100000000000L) {
							timestamp = Math.max(timestampsLastTmp[0],
									timestampsLastTmp[1]);
						} else {
							long now = new Date().getTime();
							if (Math.abs(dateOne - now) <= Math.abs(dateTwo
									- now)) {
								timestamp = dateOne;
							} else {
								timestamp = dateTwo;
							}
						}
					}
				}
			} else {
				timestamp = timestamps.get(0);
			}
		}

		if (timestamp != 0) {
			date = new Date(timestamp);
		}
		return date;
	}

	 /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */   
	public static int daysBetween(Date smdate, Date bdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long between_days = 0;
		try {
			smdate = sdf.parse(sdf.format(smdate));
			bdate = sdf.parse(sdf.format(bdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smdate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bdate);
			long time2 = cal.getTimeInMillis();
			between_days = (time2 - time1) / (1000 * 3600 * 24);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 字符串的日期格式的计算
	 * @param smdate:时间小于bdate
	 * @param bdate：
	 * @return
	 */
	public static int daysBetween(String smdate, String bdate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		long between_days = 0;
		try {
			cal.setTime(sdf.parse(smdate));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(bdate));
			long time2 = cal.getTimeInMillis();
			between_days = (time2 - time1) / (1000 * 3600 * 24);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.parseInt(String.valueOf(between_days));
	}

	public static int leftTime(long verifyExpire) {
		int days = 0;
		try {
			long current = System.currentTimeMillis();//截止时间时间戳
			long zero=verifyExpire/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();//截止时间时间戳对应的当天零点零分零秒的毫秒数
			int result = CompuUtils.compareTo(verifyExpire, current);
			if (result == 1) {// 免认证时间还未截止
				days = (int) ((zero - current) / (1000 * 3600 * 24));
//				String v = getStandardTime(zero);
//				String c = getStandardTime(current);
//				days = daysBetween(c,v);
			} else if (result < 0) {// 免认证时间已截止
				days = 0;
			}
		} catch (Exception e) {

		}
		return days;
	}

}