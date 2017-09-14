package com.poncholay.bigbrother.utils.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Poncholay on 14/09/17.
 */
public abstract class SQLiteObject {
	protected Long id = null;
	private String table;

	public SQLiteObject(String table) {
		this.table = table;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}

	public abstract ContentValues getValues();
	public abstract boolean fromCursor(Cursor cursor);

	private static <T extends SQLiteObject> T getOne(Class<T> c, Cursor cursor) {
		T instance = null;
		boolean success = false;
		if (cursor.moveToNext()) {
			try {
				instance = c.newInstance();
				success = instance.fromCursor(cursor);
			} catch (Exception e) {
				return null;
			}
		}
		return success ? instance : null;
	}

	public static <T extends SQLiteObject> T getOne(Class<T> c, int id) {
		T instance;
		try {
			instance = c.newInstance();
		} catch (Exception e) {
			return null;
		}

		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + instance.getTable() + " WHERE _ID = " + id, null);
		T t = getOne(c, cursor);
		db.close();
		cursor.close();
		return t;
	}

	public static <T extends SQLiteObject> T getOne(Class<T> c, String where) {
		if (where.equals("")) {
			return null;
		}

		T instance;
		try {
			instance = c.newInstance();
		} catch (Exception e) {
			return null;
		}

		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + instance.getTable() + " WHERE " + where, null);
		T t = getOne(c, cursor);
		db.close();
		cursor.close();
		return t;
	}

	private static <T extends SQLiteObject> List<T> getAll(Class<T> c, Cursor cursor) {
		ArrayList<T> tList = new ArrayList<>();
		while (cursor.moveToNext()) {
			try {
				T instance = c.newInstance();
				if (instance.fromCursor(cursor)) {
					tList.add(instance);
				}
			} catch (Exception e) {
				return tList;
			}
		}
		return tList;
	}

	public static <T extends SQLiteObject> List<T> getAll(Class<T> c, String where) {
		if (where.equals("")) {
			return new ArrayList<>();
		}

		T instance;
		try {
			instance = c.newInstance();
		} catch (Exception e) {
			return new ArrayList<>();
		}

		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + instance.getTable() + " WHERE " + where, null);
		List<T> tList = getAll(c, cursor);
		db.close();
		cursor.close();
		return tList;
	}

	public static <T extends SQLiteObject> List<T> getAll(Class<T> c) {
		T instance;
		try {
			instance = c.newInstance();
		} catch (Exception e) {
			return new ArrayList<>();
		}

		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + instance.getTable(), null);
		List<T> tList = getAll(c, cursor);
		db.close();
		cursor.close();
		return tList;
	}

	public void save() {
		if (id != null && id != -1) {
			update();
			return;
		}
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		ContentValues values = getValues();
		long id = db.insertWithOnConflict(
				getTable(),
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
		db.updateWithOnConflict(
				getTable(),
				values,
				BaseColumns._ID + " = ?",
				new String[]{getId().toString()},
				SQLiteDatabase.CONFLICT_REPLACE
		);
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = DatabaseHelper.getInstance().getDatabase();
		db.delete(
				getTable(),
				BaseColumns._ID + " = ?",
				new String[]{getId().toString()}
		);
		db.close();
	}
}
