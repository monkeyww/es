package com.bestpay.insurance.cbs.common.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 时间工具类
 *
 * @author jumping
 * @version 1.0.0
 * @time 2015/07/02
 */
@Slf4j
public class DateUtil {

    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();
    /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<>();
    /**
     * 日期时间格式 *
     */
    public static final String TIMEPATTERN = "HHmmss";
    public static final String TIMESPATTERN = "yyyy/MM/ddHH:mm:ss";
    public static final String DATEPATTERN = "yyyyMMdd";
    public static final String SHORTDATEPATTERN = "yyMMdd";
    public static final String FULLPATTERN = "yyyyMMddHHmmss";
    public static final String FULLPATTERNS = "yyyyMMddHHmmssSS";
    public static final String PARTPATTERN = "yyMMddHHmmss";
    public static final String TICKETPATTERN = "yyyy.MM.dd HH:mm:ss";
    public static final String SETTLEPATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String HOUR_OF_MINUTE = "HHmm";
    public static final String TIMECOLPATTERN = "HH:mm:ss";
    public static final String DATEFULLPATTERN = "yyyyMMdd HH:mm:ss";
    public static final String YEAR_OF_MINUTE = "yyyyMMddHHmm";
    public static final String YEARDATE = "yyyy-MM-dd HH:mm";
    public static final String SHOTPATTERN = "yyyy-MM-dd";
    private static final String EXCEPTION_PROMPT = "异常[{}]";
    private static final String TRAN_EXCEPTION_PROMPT = "时间转换错误[{}]";
    
    private DateUtil() {

    }

    /**
     * 时间格式转换
     *
     * @param date          时间字符串
     * @param originPattern 原时间格式
     * @param targetPattern 新的时间格式
     * @return
     * @throws java.text.ParseException
     */
    public static String convert(String date, String originPattern, String targetPattern) throws ParseException {
        Date originDate = parse(date, originPattern);
        return format(originDate, targetPattern);
    }

    /**
     * 指定时间增加年月日
     *
     * @param time  时间
     * @param year  年
     * @param month 月
     * @param day   日
     * @param hour  时
     * @param min   分
     * @param sec   秒
     * @return
     */
    public static String calendarMethod(String time, int year, int month, int day, int hour, int min, int sec) {
        //时分秒默认是  00:00:00
        SimpleDateFormat sdate = new SimpleDateFormat(SHOTPATTERN);
        Date date = null;
        try {
            date = sdate.parse(time);
        } catch (Exception e) {
            log.error(EXCEPTION_PROMPT, e.toString());
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        c.add(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.HOUR_OF_DAY, hour);
        c.add(Calendar.MINUTE, min);
        c.add(Calendar.SECOND, sec);
        SimpleDateFormat sdate1 = new SimpleDateFormat(SETTLEPATTERN);
        return sdate1.format(c.getTime());
    }

    /**
     * 精确到月
     * 返回时间差如  几年 几个月 几天
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return
     */
    public static Map<String, Integer> calendarDateRange(String startDate, String endDate) {
        Map<String, Integer> dlist = new HashMap<>();
        SimpleDateFormat sdate = new SimpleDateFormat(SHOTPATTERN);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        int day = 0;
        int month = 0;
        int year = 0;
        try {
            start.setTime(sdate.parse(startDate));
            end.setTime(sdate.parse(endDate));
            day = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
            month = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
            if (month < 0) {
                month = (month + 12) % 12;
                year--;
            }
        } catch (Exception e) {
            log.error(EXCEPTION_PROMPT, e.toString());
            return null;
        }
        dlist.put("year", year);
        dlist.put("month", month);
        dlist.put("day", day);
        return dlist;
    }

    /**
     * 精确到日
     * 返回时间差如  几年 几个月 几天
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return
     */
    public static Map<String, Integer> calendarDateRangeDay(String startDate, String endDate) {
        Map<String, Integer> dlist = new HashMap<>();
        SimpleDateFormat sdate = new SimpleDateFormat(SHOTPATTERN);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        int day = 0;
        int month = 0;
        int year = 0;
        try {
            start.setTime(sdate.parse(startDate));
            end.setTime(sdate.parse(endDate));
            day = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
            month = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
            if (day < 0) {
                month--;
                end.add(Calendar.MONTH, -1);
                day = day + end.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            if (month < 0) {
                month = (month + 12) % 12;
                year--;
            }
        } catch (Exception e) {
            log.error(EXCEPTION_PROMPT, e.toString());
            return null;
        }
        dlist.put("year", year);
        dlist.put("month", month);
        dlist.put("day", day);
        return dlist;
    }

    /**
     * 增加时间
     *
     * @param day
     * @param startDate(yyyy-MM-dd HH:mm:ss)
     * @return(yyyy-MM-dd HH:mm:ss)
     */
    public static String add_day(Integer day, String startDate) {
        Date date = null;
        try {
            date = (new SimpleDateFormat(SETTLEPATTERN)).parse(startDate);
        } catch (ParseException e) {
            log.error(TRAN_EXCEPTION_PROMPT, e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return (new SimpleDateFormat(SETTLEPATTERN)).format(cal.getTime());
    }

    /**
     * 当年时间加一年
     *
     * @param da
     * @return
     */
    public static String dateCalcTest(Date da) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(da);
        calendar.add(Calendar.YEAR, 1);
        return (new SimpleDateFormat(SETTLEPATTERN)).format(calendar.getTime());
    }

    /**时间转换----2017-11-07  15:25:25  ----->2017-11-07  00:00:00
     * 用于计算保障开始时间
     * 增加时间获得年月日00:00:00
     *
     * @param day
     * @param startDate(yyyy-MM-dd HH:mm:ss)
     * @return(yyyy-MM-dd HH:mm:ss)
     */
    public static String addDay(Integer day, String startDate) {
        Date date = null;
        try {
            date = (new SimpleDateFormat(SETTLEPATTERN)).parse(startDate);
        } catch (ParseException e) {
            log.error(TRAN_EXCEPTION_PROMPT, e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return (new SimpleDateFormat(SHOTPATTERN)).format(cal.getTime());
    }

    /**
     * 增加时间并减去一秒
     *用于计算保障结束时间
     * @param day
     * @param startDate（yyyy-MM-dd）
     * @return(yyyy-MM-dd HH:mm:ss)
     */
    public static String addDaySub(Integer day, String startDate) {
        Date date = null;
        try {
            date = (new SimpleDateFormat(SHOTPATTERN)).parse(startDate);
        } catch (ParseException e) {
            log.error(TRAN_EXCEPTION_PROMPT, e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.SECOND, -1);
        return (new SimpleDateFormat(SETTLEPATTERN)).format(cal.getTime());
    }
    
    /**
	 * 增加时间并减去一秒
     * 用于计算保障结束时间
     * @param day
     * @param startDate
     * @return
     */
    public static Date addDaySubSecond(Integer day, Date startDate, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE, day);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 源日期和（目标日期加上毫秒数）比较大小， 大则返回false ，小或等于返回true
     *
     * @param src    源日期
     * @param target 目的日期
     * @param second 秒数
     * @return 成功，失败
     */
    public static boolean compareDateForSecond(Date src, Date target, int second) {
        Calendar targetTime = Calendar.getInstance();
        targetTime.setTime(target);
        targetTime.add(Calendar.SECOND, second);
        Calendar srcTime = Calendar.getInstance();
        srcTime.setTime(src);
        return srcTime.compareTo(targetTime) <= 0;
    }

    public static String getCurrentAfter(int minute) {
        Calendar targetTime = Calendar.getInstance();
        targetTime.setTime(new Date());
        targetTime.add(Calendar.MINUTE, minute);
        return format(targetTime.getTime(), DateUtil.FULLPATTERN);
    }


    /**
     * 比较开始时间和结束时间，大：1，等于：0 小于 -1
     *
     * @param sdate 开始时间
     * @param edate 结束时间
     * @return
     */
    public static int compareDate(String sdate, String edate) {
        SimpleDateFormat format = new SimpleDateFormat(SHOTPATTERN);
        Date firstdate = null;
        Date secdate = null;
        Calendar sourTime = Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();
        try {
            firstdate = format.parse(sdate);
            sourTime.setTime(firstdate);
            secdate = format.parse(edate);
            targetTime.setTime(secdate);
        } catch (Exception e) {
            log.error(EXCEPTION_PROMPT, e.toString());
        }
        return sourTime.compareTo(targetTime);
    }

    /**
     * 返回当前月末日期
     *
     * @return
     */
    public static String getLastMonthDay() {
        SimpleDateFormat format = new SimpleDateFormat(SHOTPATTERN);
        String lastDay = "";
        Calendar cale = null;

        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastDay = format.format(cale.getTime());
        return lastDay;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrent() {
        return format(new Date(), DateUtil.SETTLEPATTERN);
    }
    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDMS() {
        return format(new Date(), DateUtil.FULLPATTERN);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentYyMMddHHmmss() {
        return format(new Date(), DateUtil.PARTPATTERN);
    }

    /**
     * 获取当前时间（yyyy-mm-dd）
     *
     * @return
     */
    public static String getCurrentByYMD() {
        return format(new Date(), DateUtil.SHOTPATTERN);
    }


    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    /**
     * 使用线程容器来获取SimpleDateFormat
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return getSdf(pattern).format(date);
    }



    /**
     * 计算日期天数差
     * @param startDate
     * @param endDate
     * @return
     */
    public static int calendarRangeDays(String startDate,String endDate){
        SimpleDateFormat sdate = new SimpleDateFormat(SHOTPATTERN);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        int day=0;
        try {
            start.setTime(sdate.parse(startDate));
            end.setTime(sdate.parse(endDate));
            day = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        }catch (Exception e){
            log.error(EXCEPTION_PROMPT,e);
            log.error("转换错误{}",e.getMessage());
        }
        return day;
    }

    /**
     * 获取前一天
     * @param pattern 格式
     * @return
     */
    public static String getYesterDay(String pattern){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);//前一天
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(calendar.getTime());//前一天时间字符串
    }

    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat(DATEPATTERN);
        SimpleDateFormat sf2 = new SimpleDateFormat(SETTLEPATTERN);
        String sfstr = "";
        try {
            sfstr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return sfstr;
    }
    public static Date getMinDateTime() {
    	return getStringChangeDate("1970-01-01 00:00:00");
    }
    
    /**
     * 把字符串格式转化为时间格式
     * @param str
     * @return date
     */
    public static Date getStringChangeDate (String str){

    	Date date = new Date();
    	SimpleDateFormat sd = new SimpleDateFormat(SETTLEPATTERN);
    	
    	try {
			date = sd.parse(str);
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
    	return date;
    }
    
    /**
	 * 得到系统当前日期时间
	 *
	 * @return 当前日期时间
	 */
	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获得当前日期 yyyy-MM-dd
	 *
	 * @return Dec 9, 20113:19:32 PM
	 * @throws ParseException 
	 */
	public static Date getNowDate() throws ParseException {
		return parse(getDate(), Constant.YEAR_MONTH_DAY_FORMAT);
	}

	/**
	 * 获得当前日期 yyyy-MM-dd
	 *
	 * @return Dec 9, 20113:19:32 PM
	 * @throws ParseException 
	 */
	public static Date getNowDateTime() throws ParseException {
		return parse(getDateTime(), Constant.DEFAULT_DATETIME_FULL_FORMAT);
	}

	/**
	 * 得到用缺省方式格式化的当前日期
	 *
	 * @return 当前日期
	 */
	public static String getDate() {
		return getDateTime(Constant.YEAR_MONTH_DAY_FORMAT);
	}

	/**
	 * 得到用缺省方式格式化的当前日期及时间
	 *
	 * @return 当前日期及时间
	 */
	public static String getDateTime() {
		return getDateTime(Constant.DEFAULT_DATETIME_FULL_FORMAT);
	}

	/**
	 * 得到系统当前日期及时间，并用指定的方式格式化
	 *
	 * @param pattern
	 *            显示格式
	 * @return 当前日期及时间
	 */
	public static String getDateTime(String pattern) {
		Date datetime = getNow();
		return getDateTime(datetime, pattern);
	}

	/**
	 * 将日期转时间
	 *
	 * @param date
	 *            需要转换的日期
	 * @return
	 */
	public static String getDateTime(Date date) {
		return getDateTime(date, null);
	}

	/**
	 * 得到用指定方式格式化的日期
	 *
	 * @param date
	 *            需要进行格式化的日期
	 * @param pattern
	 *            显示格式
	 * @return 日期时间字符串
	 */
	public static String getDateTime(Date date, String pattern) {
		if (null == pattern || "".equals(pattern)) {
			pattern = Constant.DEFAULT_DATETIME_FULL_FORMAT;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	/**
	 * 将一个字符串用给定的格式转换为日期类型。 <br>
	 * 注意：如果返回null，则表示解析失败
	 *
	 * @param datestr
	 *            需要解析的日期字符串
	 * @param pattern
	 *            日期字符串的格式，默认为“yyyy-MM-dd”的形式
	 * @return 解析后的日期
	 * @throws ParseException
	 */
	public static Date parse(String datestr, String pattern) throws ParseException {
		Date date = null;

		if (null == pattern || "".equals(pattern)) {
			pattern = Constant.YEAR_MONTH_DAY_FORMAT;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		date = dateFormat.parse(datestr);

		return date;
	}

	/**
	 * 将字符串转 日期
	 *
	 * @param datetimeStr
	 *            需要转换的时间字符串
	 * @return
	 * @throws ParseException 
	 */
	public static Date parseDatetime(String datetimeStr) throws ParseException {
		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat(Constant.DEFAULT_DATETIME_FULL_FORMAT);
		date = df.parse(datetimeStr);
		return date;
	}

	/**
	 * 得到当前年份
	 *
	 * @return 当前年份
	 */
	public static int getCurrentYear() {
		return calendar().get(Calendar.YEAR);
	}

	/**
	 * 得到当前月份
	 *
	 * @return 当前月份
	 */
	public static int getCurrentMonth() {
		// 用get得到的月份数比实际的小1，需要加上
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到当前日
	 *
	 * @return 当前日
	 */
	public static int getCurrentDay() {
		return calendar().get(Calendar.DATE);
	}

	public static Calendar calendar() {
		return Calendar.getInstance();
	}

	/**
	 * 获得当前时间秒数
	 *
	 * @return 秒数
	 */
	public static long getCurrentSecondTime() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取当前时间毫秒数
	 *
	 * @return 毫秒数
	 */
	public static long getCurrentMillisecond() {
		return System.currentTimeMillis();
	}


	/**
	 * 计算两个日期相差天数。 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数
	 *
	 * @param one
	 *            第一个日期数，作为基准
	 * @param two
	 *            第二个日期数，作为比较
	 * @return 两个日期相差天数
	 */
	public static long diffDays(Date one, Date two) {
		return (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000);
	}


	/**
	 * 获取当前日期是星期几
	 *
	 * @param 当前日
	 * @return 星期几
	 */
	public static String getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;

		return Constant.weekDays[w];
	}

	/**
	 * 格式化时间 为 yyyy-MM-dd
	 *
	 * @param date
	 *          需要格式化的时间
	 * @return
	 * 			格式化后的字符串
	 */
	public static String formatDate(Date date) {
		String formDate = "";
		SimpleDateFormat format = new SimpleDateFormat(SHOTPATTERN);
		formDate = format.format(date);

		return formDate;
	}

	/**
	 * 将日期数据按指定格式转成对应的字符串格式
	 *
	 * @param date
	 *            需要要进行转化的日期
	 * @param pattern
	 *            转换格式
	 * @return
	 * 			转换后的字符串
	 */
	public static String formatDate(Date date, String pattern) {
		String formatDate = "";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		formatDate = format.format(date);
		return formatDate;
	}

	/**
	 * 将字符串的日期数据按指定格式转成对应的日期形式
	 *
	 * @param date
	 *            需要进行转化的日期字符串
	 * @param pattern
	 *            转换格式
	 * @return
	 * 			转化后的日期
	 * @throws ParseException
	 */
	public static Date formatDate(String date, String pattern) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.parse(date);
	}


	/**
	 * 字符串格式转时间戳
	 *
	 * @param datestr
	 * @return
	 */
	public static long strtotimes(String datestr) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(Constant.DEFAULT_DATETIME_FULL_FORMAT);
			Date date = df.parse(datestr);
			return date.getTime();
		} catch (ParseException e) {
			  log.error(EXCEPTION_PROMPT, e.toString());
			return -1;
		}
	}

	/**
	 * 根据出生日期计算年龄
	 * @param birthday 出生日期
	 * @return 年龄
	 */
	 public static int calAgeByBirthday(Date birthday) {
		Date now = new Date();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        int month = 0;
        int year = 0;
        int day  = 0;
        try {
        	 if (now.before(birthday)) {
    			 return 0;
    		 }
            start.setTime(birthday);
            end.setTime(now);
            day = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);
            month = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
            if (day < 0) {
                month--;
                end.add(Calendar.MONTH, -1);
            }
            if (month < 0) {
                year--;
            }
        } catch (Exception e) {
            log.error(EXCEPTION_PROMPT, e.toString());
            return 0;
        }
        return year;
    }
	 
}