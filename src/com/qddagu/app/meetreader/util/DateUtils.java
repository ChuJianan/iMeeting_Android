package com.qddagu.app.meetreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	
	public final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static Locale LOCALE = Locale.CHINA;
	
	public static Date parse(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(PATTERN, LOCALE);
		return format.parse(date);
	}
	
	public static String format(Date date) {
		return format(date, PATTERN);
	}
	
	public static String format(Date date, String pattern) {
		if (date == null) return "";
		SimpleDateFormat format = new SimpleDateFormat(pattern, LOCALE);
		return format.format(date);
	}
}
