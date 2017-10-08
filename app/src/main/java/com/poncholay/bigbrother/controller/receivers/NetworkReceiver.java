package com.poncholay.bigbrother.controller.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.poncholay.bigbrother.services.LocationTrackingService;
import com.poncholay.bigbrother.utils.meetings.MeetingSuggestion;

/**
 * Created by Poncholay on 05/10/17.
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static NetworkReceiver receiver;

	private boolean connected = true;

	private NetworkReceiver() {}

	public static void init(Activity activity) {
		if (receiver == null) {
			receiver = getInstance();
//			receiver.onReceive(activity, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
			final IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			activity.registerReceiver(receiver, intentFilter);
		}
	}

	public static NetworkReceiver getInstance() {
		if (receiver == null) {
			receiver = new NetworkReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		try {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				boolean previouslyConnected = connected;
				connected = networkInfo != null && networkInfo.isConnected();
				if (connected && !previouslyConnected) {
					MeetingSuggestion.launchMeetingDiscovery(context);
				}
			}
		} catch (IllegalArgumentException ignored) {
		}
	}

	public boolean isConnected() {
		return connected;
	}
}
