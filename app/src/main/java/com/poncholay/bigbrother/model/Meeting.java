package com.poncholay.bigbrother.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Meeting extends SugarRecord implements Parcelable {
	private String uniqueId;
	private String title;
	private Date start;
	private Date end;
	private List<Friend> friends;
	private LatLng location;

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

	public List<Friend> getFriends() {
		return friends == null ? new ArrayList<Friend>() : friends;
	}
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public LatLng getLocation() {
		return location == null ? new LatLng(1, 2) : location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
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
		Parcelable[] ps = in.readParcelableArray(Friend.class.getClassLoader());
		Friend[] friends = new Friend[ps.length];
		System.arraycopy(ps, 0, friends, 0, ps.length);
		this.setFriends(Arrays.asList(friends));
		this.setLocation((LatLng) in.readParcelable(LatLng.class.getClassLoader()));
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getId() == null ? -1 : this.getId());
		dest.writeString(this.getUniqueId());
		dest.writeString(this.getTitle());
		dest.writeSerializable(this.getStart());
		dest.writeSerializable(this.getEnd());
		dest.writeParcelableArray(this.getFriends().toArray(new Friend[this.getFriends().size()]), 0);
		dest.writeParcelable(this.getLocation(), 0);
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
