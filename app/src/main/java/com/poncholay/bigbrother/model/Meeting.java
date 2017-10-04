package com.poncholay.bigbrother.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.poncholay.bigbrother.activities.ReminderReceiver;
import com.poncholay.bigbrother.utils.database.DatabaseContract;
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
	private String locationName;

	public Meeting() {
		this("");
	}

	public Meeting(String title) {
		super(DatabaseContract.MeetingEntry.MEETING_TABLE);
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

	public String getLocationName() { return locationName; }
	public void setLocationName(String locationName) { this.locationName = locationName; }

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
				where.append(DatabaseContract.FriendEntry._ID + " = ").append(ids[i]);
				if (i < ids.length - 1) {
					where.append(" OR ");
				}
			}
			friends = Friend.getAll(Friend.class, where.toString());
			return friends;
		}
		return new ArrayList<>();
	}

	public void createReminder(Context context) {
		AlarmManager alarmMgr;
		PendingIntent alarmIntent;
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, ReminderReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, id.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		alarmMgr.set(AlarmManager.RTC_WAKEUP, getStart().getTime(), alarmIntent);
		alarmMgr.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 2000, alarmIntent);
	}

	public void cancelReminder(Context context) {
		AlarmManager alarmMgr;
		PendingIntent alarmIntent;
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, ReminderReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, id.intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmMgr.cancel(alarmIntent);
	}

	public void updateReminder(Context context) {
		cancelReminder(context);
		createReminder(context);
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
		super(DatabaseContract.MeetingEntry.MEETING_TABLE);
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
		this.setLocationName(in.readString());
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
		dest.writeString(this.getLocationName());
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

	@Override
	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(COL_MEETING_TITLE, getTitle());
		values.put(COL_MEETING_START_DATE, getStart().getTime());
		values.put(COL_MEETING_END_DATE, getEnd().getTime());
		values.put(COL_MEETING_FRIENDS, getFriendsIds());
		values.put(COL_MEETING_LATITUDE, getLatitude());
		values.put(COL_MEETING_LONGITUDE, getLongitude());
		return values;
	}

	@Override
	public boolean fromCursor(Cursor cursor) {
		int idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry._ID);
		if (idx != - 1) {
			setId(cursor.getLong(idx));
		} else {
			return false;
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_TITLE);
		if (idx != - 1) {
			setTitle(cursor.getString(idx));
		} else {
			return false;
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_START_DATE);
		if (idx != - 1) {
			setStart(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_END_DATE);
		if (idx != - 1) {
			setEnd(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_FRIENDS);
		if (idx != - 1) {
			setFriendsIds(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_LATITUDE);
		if (idx != - 1) {
			setLatitude(cursor.getDouble(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.MeetingEntry.COL_MEETING_LONGITUDE);
		if (idx != - 1) {
			setLongitude(cursor.getDouble(idx));
		}
		return true;
	}
}