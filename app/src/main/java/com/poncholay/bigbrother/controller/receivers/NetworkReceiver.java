package com.poncholay.bigbrother.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.poncholay.bigbrother.services.LocationTrackingService;
import com.poncholay.bigbrother.utils.meetings.MeetingSuggestion;

/**
 * Created by Poncholay on 05/10/17.
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static boolean connected = true;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		connected = networkInfo != null && networkInfo.isConnected();
		if (connected) {
			MeetingSuggestion.launchMeetingDiscovery(context);
		}
	}

	public static boolean isConnected() {
		return connected;
	}
}
