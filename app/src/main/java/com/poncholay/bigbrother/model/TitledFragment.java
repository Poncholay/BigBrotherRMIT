package com.poncholay.bigbrother.model;

import android.support.v4.app.Fragment;

public abstract class TitledFragment extends Fragment {
	public abstract String getTitle();

	boolean loaded = false;

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
