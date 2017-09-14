package com.poncholay.bigbrother.utils.database;

import android.provider.BaseColumns;

public class DatabaseContract {
	public static final String DB_NAME = "com.poncholay.bigbrother.utils.db";
	public static final int DB_VERSION = 1;

	public class FriendEntry implements BaseColumns {
		public static final String FRIEND_TABLE = "friend";

		public static final String COL_FRIEND_FIRSTNAME = "firstname";
		public static final String COL_FRIEND_LASTNAME = "lastname";
		public static final String COL_FRIEND_EMAIL = "email";
		public static final String COL_FRIEND_BIRTHDAY = "birthday";
		public static final String COL_FRIEND_HAS_ICON = "has_icon";
	}

	public class MeetingEntry implements BaseColumns {
		public static final String MEETING_TABLE = "meeting";

		public static final String COL_MEETING_TITLE = "title";
		public static final String COL_MEETING_START_DATE = "start";
		public static final String COL_MEETING_END_DATE = "end";

		public static final String COL_MEETING_FRIENDS = "friends";

		public static final String COL_MEETING_LATITUDE = "latitude";
		public static final String COL_MEETING_LONGITUDE = "longitude";
	}
}