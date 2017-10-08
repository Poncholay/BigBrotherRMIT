package com.poncholay.bigbrother.utils;

import android.os.Bundle;
import android.os.Parcelable;

import com.poncholay.bigbrother.model.FriendDistance;

import java.util.ArrayList;

public class BundleUtils {
	public static Parcelable retrieveParcelable(Bundle bundle, Bundle extras, String type) {
		if (extras == null) {
			extras = bundle;
		}
		if (extras != null) {
			return extras.getParcelable(type);
		}
		return null;
	}

	public static ArrayList<FriendDistance> retrieveFriendDistances(Bundle bundle, Bundle extras, String type) {
		if (extras == null) {
			extras = bundle;
		}
		if (extras != null) {
			return extras.getParcelableArrayList(type);
		}
		return null;
	}

	public static int retrieveMode(Bundle bundle, Bundle extras) {
		if (extras == null) {
			extras = bundle;
		}
		if (extras != null) {
			return extras.getInt("mode", -1);
		}
		return -1;
	}
}
