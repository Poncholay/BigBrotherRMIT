package com.poncholay.bigbrother.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	private static String toString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		return format.format(date);
	}

	public static String toFullString(Date date) {
		String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		return toStringBody(date, months);
	}

	public static String toLiteString(Date date) {
		String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		return toStringBody(date, months);
	}

	private static String toStringBody(Date date, String[] months) {
		String dateStr = DateUtils.toString(date);
		try {
			String[] suffix = {"st", "nd", "rt"};
			String[] segments = dateStr.split("-");
			int y = Integer.parseInt(segments[0]);
			int m = Integer.parseInt(segments[1]);
			int d = Integer.parseInt(segments[2].split("T")[0]);

			String monthStr = m >= 1 && m <= 12 ? months[m - 1] : "Nul";
			String suffixStr = d % 10 >= 1 && d % 10 <= 2 ? suffix[d % 10] : "th";

			return monthStr + " " + d + suffixStr + " " + y;
		} catch (Exception e) {
			Log.e("Date", e.getMessage());
			return "Anytime";
		}
	}
}
