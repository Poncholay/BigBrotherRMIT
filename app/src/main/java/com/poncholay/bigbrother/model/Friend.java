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
import java.util.Date;
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

	private ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(COL_FRIEND_FIRSTNAME, getFirstname());
		values.put(COL_FRIEND_LASTNAME, getLastname());
		values.put(COL_FRIEND_EMAIL, getEmail());
		values.put(COL_FRIEND_BIRTHDAY, getBirthday().getTime());
		values.put(COL_FRIEND_HAS_ICON, getHasIcon() ? 1 : 0);
		return values;
	}

	private static Friend fromCursor(Cursor cursor) {
		Friend friend = new Friend();
		int idx = cursor.getColumnIndex(DatabaseContract.FriendEntry._ID);
		if (idx != - 1) {
			friend.setId(cursor.getLong(idx));
		} else {
			return null;
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_FIRSTNAME);
		if (idx != - 1) {
			friend.setFirstname(cursor.getString(idx));
		} else {
			return null;
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_LASTNAME);
		if (idx != - 1) {
			friend.setLastname(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_EMAIL);
		if (idx != - 1) {
			friend.setEmail(cursor.getString(idx));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_BIRTHDAY);
		if (idx != - 1) {
			friend.setBirthday(new Date(cursor.getLong(idx)));
		}
		idx = cursor.getColumnIndex(DatabaseContract.FriendEntry.COL_FRIEND_HAS_ICON);
		if (idx != - 1) {
			friend.setHasIcon(cursor.getInt(idx) == 1);
		}
		return friend;
	}

	private static Friend getOne(Cursor cursor) {
		Friend friend = null;
		if (cursor.moveToNext()) {
			friend = fromCursor(cursor);
			cursor.close();
		}
		return friend;
	}

	public static Friend getOne(int id) {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.FriendEntry.FRIEND_TABLE + " WHERE _ID = " + id, null);
		Friend friend = getOne(cursor);
		db.close();
		return friend;
	}

	public static Friend getOne(String where) {
		if (where.equals("")) {
			return null;
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.FriendEntry.FRIEND_TABLE + " WHERE " + where, null);
		Friend friend = getOne(cursor);
		db.close();
		return friend;
	}

	private static List<Friend> getAll(Cursor cursor) {
		ArrayList<Friend> friendList = new ArrayList<>();
		while (cursor.moveToNext()) {
			Friend friend = fromCursor(cursor);
			if (friend != null) {
				friendList.add(friend);
			}
		}
		cursor.close();
		return friendList;
	}

	public static List<Friend> getAll(String where) {
		if (where.equals("")) {
			return new ArrayList<>();
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.FriendEntry.FRIEND_TABLE + " WHERE " + where, null);
		List<Friend> friends = getAll(cursor);
		db.close();
		return friends;
	}

	public static List<Friend> getAll() {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.FriendEntry.FRIEND_TABLE, null);
		List<Friend> friends = getAll(cursor);
		db.close();
		return friends;
	}

	public void save() {
		if (id != null && id != -1) {
			update();
			return;
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		ContentValues values = getValues();
		long id = db.insertWithOnConflict(
				DatabaseContract.FriendEntry.FRIEND_TABLE,
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
				DatabaseContract.FriendEntry.FRIEND_TABLE,
				values,
				DatabaseContract.FriendEntry._ID + " = ?",
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
				DatabaseContract.FriendEntry.FRIEND_TABLE,
				DatabaseContract.FriendEntry._ID + " = ?",
				new String[]{getId().toString()}
		);
		db.close();
	}
}
