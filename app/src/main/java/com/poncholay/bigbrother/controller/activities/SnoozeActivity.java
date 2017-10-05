package com.poncholay.bigbrother.controller.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.ParcelableUtils;

/**
 * Created by Poncholay on 05/10/17.
 */
public class SnoozeActivity extends Activity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			byte[] bytes = getIntent().getExtras().getByteArray("meeting");
			Meeting meeting = ParcelableUtils.unmarshall(bytes, Meeting.CREATOR);

			if (meeting != null) {
				meeting.createReminder(this, getSharedPreferences("settings", Context.MODE_PRIVATE).getFloat("snoozeDelay", 5.0f));
			}
		} catch (Exception ignored) {}
		finish();
	}
}
