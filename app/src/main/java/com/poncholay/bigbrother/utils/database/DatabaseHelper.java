package com.poncholay.bigbrother.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static class DatabaseContext {
		public static Context context;
	}

	private DatabaseHelper(Context context) {
		super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
	}

	private static DatabaseHelper databaseHelper;

	public static DatabaseHelper getInstance() {
		return databaseHelper == null ? new DatabaseHelper(DatabaseHelper.DatabaseContext.context) : databaseHelper;
	}

	public SQLiteDatabase getDatabase() {
		return getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createMeeting = "CREATE TABLE " + DatabaseContract.MeetingEntry.MEETING_TABLE + " ( " +
				DatabaseContract.MeetingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				DatabaseContract.MeetingEntry.COL_MEETING_TITLE + " TEXT NOT NULL, " +
				DatabaseContract.MeetingEntry.COL_MEETING_START_DATE + " LONG NOT NULL, " +
				DatabaseContract.MeetingEntry.COL_MEETING_END_DATE + " LONG NOT NULL, " +
				DatabaseContract.MeetingEntry.COL_MEETING_FRIENDS + " TEXT NOT NULL," +
				DatabaseContract.MeetingEntry.COL_MEETING_LATITUDE + " TEXT, " +
				DatabaseContract.MeetingEntry.COL_MEETING_LONGITUDE + " TEXT" + ");";
		db.execSQL(createMeeting);
		String createFriend = "CREATE TABLE " + DatabaseContract.FriendEntry.FRIEND_TABLE + " ( " +
				DatabaseContract.FriendEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				DatabaseContract.FriendEntry.COL_FRIEND_FIRSTNAME + " TEXT NOT NULL, " +
				DatabaseContract.FriendEntry.COL_FRIEND_LASTNAME + " TEXT, " +
				DatabaseContract.FriendEntry.COL_FRIEND_BIRTHDAY + " LONG, " +
				DatabaseContract.FriendEntry.COL_FRIEND_EMAIL + " TEXT," +
				DatabaseContract.FriendEntry.COL_FRIEND_HAS_ICON + " INT" + ");";
		db.execSQL(createFriend);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MeetingEntry.MEETING_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FriendEntry.FRIEND_TABLE);
		onCreate(db);
	}
}