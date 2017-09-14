package com.poncholay.bigbrother.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.poncholay.bigbrother.utils.database.DatabaseContract;
import com.poncholay.bigbrother.utils.database.DatabaseHelper;
import com.poncholay.bigbrother.utils.database.SQLiteObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_END_DATE;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_FRIENDS;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_LATITUDE;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_LONGITUDE;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_START_DATE;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.MeetingEntry.COL_MEETING_TITLE;

public class Meeting extends SQLiteObject implements Parcelable {
	private String title;
	private Date start;
	private Date end;

	private String friendsIds;
	private List<Friend> friends;

	private double latitude;
	private double longitude;

	public Meeting() {
		this("");
	}

	public Meeting(String title) {
		this.title = title;
		this.latitude = -1;
		this.longitude = -1;
		this.id = null;
	}

	public String getTitle() {
		return title == null ? "" : title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStart() {
		return start == null ? new Date() : start;
	}
	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end == null ? new Date() : end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}

	private String getFriendsIds() {
		return friendsIds == null ? "" : friendsIds;
	}
	private void setFriendsIds(String friendsIds) {
		this.friendsIds = friendsIds;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	private void updateListIds() {
		StringBuilder where = new StringBuilder();
		for (Friend friend : friends) {
			where.append(friend.getId()).append(",");
		}
		friendsIds = where.toString();
	}

	private List<Friend> retrieveMeetings() {
		StringBuilder where = new StringBuilder();
		if (!getFriendsIds().equals("")) {
			String[] ids = getFriendsIds().split(",");
			for (int i = 0; i < ids.length; i++) {
				where.append("id = ").append(ids[i]);
				if (i < ids.length - 1) {
					where.append(" OR ");
				}
			}
			friends = Friend.getAll(where.toString());
			return friends;
		}
		return new ArrayList<>();
	}

	public List<Friend> getFriends() {
		return friends == null ? retrieveMeetings() : friends;
	}
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
		updateListIds();
	}

	@Override
	public boolean equals(Object Meeting) {
		if (Meeting == null || !Meeting.class.isAssignableFrom(Meeting.getClass())) {
			return false;
		}
		final Meeting other = (Meeting) Meeting;
		return Objects.equals(other.getId(), this.getId());
	}

	@Override
	public int hashCode() {
		return 42 * (int)(getId() ^ (getId() >>> 32));
	}

	//__________
	//Parcelable

	public Meeting(Parcel in) {
		Long id = in.readLong();
		this.setId(id == -1 ? null : id);
		this.setTitle(in.readString());
		this.setStart((Date) in.readSerializable());
		this.setEnd((Date) in.readSerializable());
		this.setFriendsIds(in.readString());
		Parcelable[] ps = in.readParcelableArray(Meeting.class.getClassLoader());
		Friend[] friends = new Friend[ps.length];
		try {
			System.arraycopy(ps, 0, friends, 0, ps.length);
		} catch (Exception ignored) {}
		List<Friend> list = new ArrayList<>();
		Collections.addAll(list, friends);
		this.setFriends(list);
		this.setLatitude(in.readDouble());
		this.setLongitude(in.readDouble());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getId() == null ? -1 : this.getId());
		dest.writeString(this.getTitle());
		dest.writeSerializable(this.getStart());
		dest.writeSerializable(this.getEnd());
		dest.writeString(this.getFriendsIds());
		dest.writeParcelableArray(this.getFriends().toArray(new Friend[this.getFriends().size()]), 0);
		dest.writeDouble(this.getLatitude());
		dest.writeDouble(this.getLongitude());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Meeting>() {
		public Meeting createFromParcel(Parcel in) {
			return new Meeting(in);
		}

		public Meeting[] newArray(int size) {
			return new Meeting[size];
		}
	};

	//________
	//Database

	private ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(COL_MEETING_TITLE, getTitle());
		values.put(COL_MEETING_START_DATE, getStart().getTime());
		values.put(COL_MEETING_END_DATE, getEnd().getTime());
		values.put(COL_MEETING_FRIENDS, getFriendsIds());
		values.put(COL_MEETING_LATITUDE, getLatitude());
		values.put(COL_MEETING_LONGITUDE, getLongitude());
		return values;
	}

	private static Meeting fromCursor(Cursor cursor) {
		Meeting meeting = new Meeting();
		int idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry._ID);
		if (idx != - 1) {
			meeting.setId(cursor.getLong(idx));
		} else {
			return null;
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_TITLE);
		if (idx != - 1) {
			meeting.setTitle(cursor.getString(idx));
		} else {
			return null;
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_START_DATE);
		if (idx != - 1) {
			meeting.setStart(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_END_DATE);
		if (idx != - 1) {
			meeting.setEnd(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_FRIENDS);
		if (idx != - 1) {
			meeting.setFriendsIds(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_LATITUDE);
		if (idx != - 1) {
			meeting.setLatitude(cursor.getDouble(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_LONGITUDE);
		if (idx != - 1) {
			meeting.setLongitude(cursor.getDouble(idx));
		}
		return meeting;
	}

	private static Meeting getOne(Cursor cursor) {
		Meeting meeting = null;
		if (cursor.moveToNext()) {
			meeting = fromCursor(cursor);
			cursor.close();
		}
		return meeting;
	}

	public static Meeting getOne(int id) {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.MeetingEntry.MEETING_TABLE + " WHERE _ID = " + id, null);
		Meeting meeting = getOne(cursor);
		db.close();
		return meeting;
	}

	public static Meeting getOne(String where) {
		if (where.equals("")) {
			return null;
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.MeetingEntry.MEETING_TABLE + " WHERE " + where, null);
		Meeting meeting = getOne(cursor);
		db.close();
		return meeting;
	}

	public static List<Meeting> getAll() {
		ArrayList<Meeting> friendList = new ArrayList<>();
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.MeetingEntry.MEETING_TABLE, null);
		while (cursor.moveToNext()) {
			Meeting friend = fromCursor(cursor);
			if (friend != null) {
				friendList.add(friend);
			}
		}
		cursor.close();
		db.close();
		return friendList;
	}

	public void save() {
		if (id != null && id != -1) {
			update();
			return;
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		ContentValues values = getValues();
		long id = db.insertWithOnConflict(
				DatabaseContract.MeetingEntry.MEETING_TABLE,
				null,
				values,
				SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		if (id != -1) {
			this.setId(id);
		}
	}

	private void update() {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		ContentValues values = getValues();
		long id = db.updateWithOnConflict(
				DatabaseContract.MeetingEntry.MEETING_TABLE,
				values,
				DatabaseContract.MeetingEntry._ID + " = ?",
				new String[]{getId().toString()},
				SQLiteDatabase.CONFLICT_REPLACE
		);
		if (id != -1) {
			setId(id);
		}
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		db.delete(
				DatabaseContract.MeetingEntry.MEETING_TABLE,
				DatabaseContract.MeetingEntry._ID + " = ?",
				new String[]{getId().toString()}
		);
		db.close();
	}
}

//		Cursor cursor = db.query(DatabaseContract.MeetingEntry.MEETING_TABLE,
//				new String[]{
//						DatabaseContract.MeetingEntry._ID,
//						COL_MEETING_FIRSTNAME,
//						COL_MEETING_LASTNAME,
//						COL_MEETING_EMAIL,
//						COL_MEETING_BIRTHDAY,
//						COL_MEETING_HAS_ICON
//				}, null, null, null, null, null, null);
//TODO : Remove that