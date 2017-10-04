package com.poncholay.bigbrother.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.poncholay.bigbrother.R;

/**
 * Created by Poncholay on 02/10/17.
 */
public class ReminderReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "OnReceive alarm test", Toast.LENGTH_SHORT).show();
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("Meeting")
				.setContentText("x starts in 5 minutes")
				.setAutoCancel(true);
		Intent i = new Intent(context, EditMeetingActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
}
