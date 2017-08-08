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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		return format.format(date);
	}

	public static String toFullString(Date date) {
		String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		return toStringDate(date, months);
	}

	public static String toLiteString(Date date) {
		String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		return toStringDate(date, months);
	}

	public static String toLiteStringTime(Date date) {
		String ret = toLiteString(date);
		ret += ", ";
		ret += toStringTime(date);
		return ret;
	}

	public static String toNumberString(Date date) {
		String dateStr = DateUtils.toString(date);
		try {
			String[] segments = dateStr.split("-");
			int y = Integer.parseInt(segments[0]);
			int m = Integer.parseInt(segments[1]);
			int d = Integer.parseInt(segments[2].split("T")[0]);

			return String.format(Locale.US, "%02d", d) + "/" + String.format(Locale.US, "%02d", m) + "/" + String.format(Locale.US, "%02d", y);
		} catch (Exception e) {
			Log.e("Date", e.getMessage());
			return "Anytime";
		}
	}

	public static String toNumberStringTime(Date date) {
		return DateUtils.toNumberString(date) + "\n" + DateUtils.toStringTime(date);
	}

	private static String toStringTime(Date date) {
		String dateStr = DateUtils.toString(date);
		try {
			System.out.println(dateStr);
			String[] segments = dateStr.split("T");
			segments = segments[1].split(":");
			int h = Integer.parseInt(segments[0]);
			int m = Integer.parseInt(segments[1]);
			String suffix = h > 12 ? "PM" : "AM";
			return String.format(Locale.US, "%02d", h % 12) + ":" + String.format(Locale.US, "%02d", m) + suffix;
		} catch (Exception e) {
			return "";
		}
	}

	private static String toStringDate(Date date, String[] months) {
		String dateStr = DateUtils.toString(date);
		try {
			String[] suffix = {"", "st", "nd", "rt"};
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
