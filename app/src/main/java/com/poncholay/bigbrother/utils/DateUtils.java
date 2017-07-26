package com.poncholay.bigbrother.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	public static String toString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		return format.format(date);
	}

	public static String toLiteString(Date date) {
		String dateStr = DateUtils.toString(date);
		try {
			String[] segments = dateStr.split("-");
			int y = Integer.parseInt(segments[0]);
			int m = Integer.parseInt(segments[1]);
			segments = segments[2].split("T");
			int d = Integer.parseInt(segments[0]);
			String monthStr =
					m == 1 ? "Jan" : m == 2 ? "Feb" :
							m == 3 ? "Mar" : m == 4 ? "Apr" :
									m == 5 ? "May" : m == 6 ? "Jun" :
											m == 7 ? "Jul" : m == 8 ? "Aug" :
													m == 9 ? "Sep" : m == 10 ? "Nov" :
															m == 11 ? "Dec" : "Nul";
			String suffix =
					d % 10 == 1 ? "st" : d % 10 == 2 ? "nd" :
							d % 10 == 3 ? "rd" : "th";
			return monthStr + " " + d + suffix + " " + y;
		} catch (Exception e) {
			return "Anytime";
		}
	}
}
