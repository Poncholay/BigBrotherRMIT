package com.poncholay.bigbrother.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.poncholay.bigbrother.utils.DummyLocationService;
import com.poncholay.bigbrother.utils.database.DatabaseContract;
import com.poncholay.bigbrother.utils.database.SQLiteObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.poncholay.bigbrother.utils.database.DatabaseContract.FriendEntry.COL_FRIEND_BIRTHDAY;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.FriendEntry.COL_FRIEND_EMAIL;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.FriendEntry.COL_FRIEND_FIRSTNAME;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.FriendEntry.COL_FRIEND_HAS_ICON;
import static com.poncholay.bigbrother.utils.database.DatabaseContract.FriendEntry.COL_FRIEND_LASTNAME;

public class Friend extends SQLiteObject implements Parcelable {
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

	public Friend(Long id, String firstname, String lastname, String email, Date birthday, boolean hasIcon) {
		super(DatabaseContract.FriendEntry.FRIEND_TABLE);
		this.setId(id);
		this.setFirstname(firstname);
		this.setLastname(lastname);
		this.setEmail(email);
		this.setBirthday(birthday);
		this.setHasIcon(hasIcon);
	}

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
		super(DatabaseContract.FriendEntry.FRIEND_TABLE);
		Long id = in.readLong();
		this.setId(id == -1 ? null : id);
		this.setFirstname(in.readString());
		this.setLastname(in.readString());
		this.setEmail(in.readString());
		this.setBirthday((Date) in.readSerializable());
		this.setHasIcon(in.readByte() != 0);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getId() == null ? -1 : this.getId());
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

	//________
	//Database

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(COL_FRIEND_FIRSTNAME, getFirstname());
		values.put(COL_FRIEND_LASTNAME, getLastname());
		values.put(COL_FRIEND_EMAIL, getEmail());
		values.put(COL_FRIEND_BIRTHDAY, getBirthday().getTime());
		values.put(COL_FRIEND_HAS_ICON, getHasIcon() ? 1 : 0);
		return values;
	}

	@Override
	public boolean fromCursor(Cursor cursor) {
		int idx = cursor.getColumnIndex(DatabaseContract.FriendEntry._ID);
		if (idx != - 1) {
			setId(cursor.getLong(idx));
		} else {
			return false;
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_FIRSTNAME);
		if (idx != - 1) {
			setFirstname(cursor.getString(idx));
		} else {
			return false;
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_LASTNAME);
		if (idx != - 1) {
			setLastname(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_EMAIL);
		if (idx != - 1) {
			setEmail(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_BIRTHDAY);
		if (idx != - 1) {
			setBirthday(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_HAS_ICON);
		if (idx != - 1) {
			setHasIcon(cursor.getInt(idx) == 1);
		}
		return true;
	}

	public static List<Friend> findCurrent(List<DummyLocationService.FriendLocation> matched) {
		StringBuilder query = new StringBuilder();

		Iterator<DummyLocationService.FriendLocation> it = matched.iterator();
		while (it.hasNext()) {
			DummyLocationService.FriendLocation friendLocation = it.next();
			query.append(DatabaseContract.FriendEntry._ID + " = ").append(friendLocation.id);
			if (it.hasNext()) {
				query.append(" OR ");
			}
		}
		return Friend.getAll(Friend.class, query.toString());
	}
}
