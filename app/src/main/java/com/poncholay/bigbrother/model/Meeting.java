package com.poncholay.bigbrother.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Meeting extends SugarRecord implements Parcelable {
	private String uniqueId;
	private String title;
	private Date start;
	private Date end;

	private String friendsIds;
	private List<Friend> friends;

	private double latitude;
	private double longitude;

	public Meeting() {}

	public Meeting(String title) {
		this.title = title;
	}

	public String getUniqueId() {
		return uniqueId == null ? "" : uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	//TODO : randomId
	
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
	private List<Friend> retrieveFriends() {
		StringBuilder where = new StringBuilder();
		if (!getFriendsIds().equals("")) {
			String[] ids = getFriendsIds().split(",");
			for (int i = 0; i < ids.length; i++) {
				where.append("id = ").append(ids[i]);
				if (i < ids.length - 1) {
					where.append(" OR ");
				}
			}
			friends = Friend.find(Friend.class, where.toString());
			return friends;
		}
		return new ArrayList<>();
	}

	public List<Friend> getFriends() {
		return friends == null ? retrieveFriends() : friends;
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
		this.setUniqueId(in.readString());
		this.setTitle(in.readString());
		this.setStart((Date) in.readSerializable());
		this.setEnd((Date) in.readSerializable());
		this.setFriendsIds(in.readString());
		Parcelable[] ps = in.readParcelableArray(Friend.class.getClassLoader());
		Friend[] friends = new Friend[ps.length];
		try {
			System.arraycopy(ps, 0, friends, 0, ps.length);
		} catch (Exception ignored) {}
		List<Friend> list = new ArrayList<>();
		Collections.addAll(list, friends);
		this.setFriends(list);
		this.setLatitude(in.readLong());
		this.setLongitude(in.readLong());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getId() == null ? -1 : this.getId());
		dest.writeString(this.getUniqueId());
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
}
