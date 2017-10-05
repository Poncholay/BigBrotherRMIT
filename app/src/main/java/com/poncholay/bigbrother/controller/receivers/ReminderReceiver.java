package com.poncholay.bigbrother.controller.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.activities.SnoozeActivity;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.ParcelableUtils;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Poncholay on 02/10/17.
 */
public class ReminderReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		byte[] bytes = intent.getExtras().getByteArray("meeting");
		Meeting meeting = ParcelableUtils.unmarshall(bytes, Meeting.CREATOR);

		if (meeting == null) {
			return;
		}

		Date current = new Date();
		Date start = meeting.getStart();
		int m = (int) ((start.getTime() / 1000 - current.getTime() / 1000) / 60);

		Intent i = new Intent(context, SnoozeActivity.class);
		i.putExtra("meeting", bytes);
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);

		float snoozeDuration = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getFloat("snoozeDelay", 5.0f);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle(String.format(Locale.getDefault(), "%s starts in %d minutes", meeting.getTitle(), m))
				.setContentText(String.format(Locale.getDefault(), "Click to snooze for %.2f mins or dismiss to ignore", snoozeDuration))
				.setAutoCancel(true);

		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(meeting.getId().intValue(), mBuilder.build());
	}
}
