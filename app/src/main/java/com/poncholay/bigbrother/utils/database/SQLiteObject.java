package com.poncholay.bigbrother.utils.database;

/**
 * Created by Poncholay on 14/09/17.
 */
public abstract class SQLiteObject {
	protected Long id = null;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public abstract void save();
	public abstract void delete();
}
