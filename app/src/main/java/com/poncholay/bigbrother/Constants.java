package com.poncholay.bigbrother;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	public static final int PICK_CONTACTS = 3;
	public static final int REQUEST_READ_CONTACT_PERMISSION = 4;
	public static final int REQUEST_PLACE_PICKER = 5;
	public static final int EDIT_FRIEND = 6;
	public static final int EDIT_MEETING = 7;
	public static final int NEW_FRIEND = 8;
	public static final int NEW_MEETING = 9;
	public static final int BY_DATE = 10;
	public static final int BY_DATE_INV = 11;
	public static final int BY_NAME = 12;
	public static final int BY_NAME_INV = 13;
	public static final int REQUEST_ACCESS_COARSE_LOCATION = 14;
	public static final String ICON = "icon.jpg";
	public static final int SUGGESTIONS_RECEIVER_PENDING_INTENT = 15;

	static final public SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
	static final public SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	static final public SimpleDateFormat fullFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
}
