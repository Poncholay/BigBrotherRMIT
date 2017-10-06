package com.poncholay.bigbrother.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.poncholay.bigbrother.services.MeetingSuggestionsService;

/**
 * Created by Poncholay on 05/10/17.
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static boolean connected = false;

	@Override
	public void onReceive(final Context context, final Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		connected = networkInfo != null && networkInfo.isConnected();
		if (connected) {
			MeetingSuggestionsService.launchMeetingDiscovery(context);
		}
	}

	public static void update(boolean status) {
		connected = status;
	}

	public static boolean isConnected() {
		return connected;
	}
}
