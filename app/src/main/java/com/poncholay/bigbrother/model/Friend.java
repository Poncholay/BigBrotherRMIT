package com.poncholay.bigbrother.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.Objects;

public class Friend extends SugarRecord implements Parcelable {
	private String uniqueId;
	private String firstname;
	private String lastname;
	private String email;
	private Date birthday;
	private boolean hasIcon;

	public Friend() {
		this(null, null, null, null, null, false);
	}

	public Friend(String firstname, String lastname) {
		this(null, firstname, lastname, null, null, false);
	}

	public Friend(String uniqueId, String firstname, String lastname, String email, Date birthday, boolean hasIcon) {
		this.setUniqueId(uniqueId);
		this.setFirstname(firstname);
		this.setLastname(lastname);
		this.setEmail(email);
		this.setBirthday(birthday);
		this.setHasIcon(hasIcon);
	}

	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId == null ? "" : uniqueId;
	}
	//TODO : randomId

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname == null ? "" : firstname;
	}

	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname == null ? "" : lastname;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email == null ? "" : email;
	}

	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday == null ? new Date() : birthday;
	}

	public boolean getHasIcon() {
		return hasIcon;
	}
	public void setHasIcon(boolean hasIcon) {
		this.hasIcon = hasIcon;
	}

	@Override
	public boolean equals(Object friend) {
		if (friend == null || !Friend.class.isAssignableFrom(friend.getClass())) {
			return false;
		}
		final Friend other = (Friend) friend;
		return Objects.equals(other.getId(), this.getId());
	}

	@Override
	public int hashCode() {
		return 42 * (int)(getId() ^ (getId() >>> 32));
	}

	//__________
	//Parcelable

	public Friend(Parcel in) {
		Long id = in.readLong();
		this.setId(id == -1 ? null : id);
		this.setUniqueId(in.readString());
		this.setFirstname(in.readString());
		this.setLastname(in.readString());
		this.setEmail(in.readString());
		this.setBirthday((Date) in.readSerializable());
		this.setHasIcon(in.readByte() != 0);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getId() == null ? -1 : this.getId());
		dest.writeString(this.getUniqueId());
		dest.writeString(this.getFirstname());
		dest.writeString(this.getLastname());
		dest.writeString(this.getEmail());
		dest.writeSerializable(this.getBirthday());
		dest.writeByte((byte) (this.getHasIcon() ? 1 : 0));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Friend>() {
		public Friend createFromParcel(Parcel in) {
			return new Friend(in);
		}

		public Friend[] newArray(int size) {
			return new Friend[size];
		}
	};
}
