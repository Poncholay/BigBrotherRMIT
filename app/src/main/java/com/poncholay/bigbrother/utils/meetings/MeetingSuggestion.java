package com.poncholay.bigbrother.utils.meetings;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 10/8/17.
 * mathieucorti@gmail.com
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.activities.BigBrotherActivity;
import com.poncholay.bigbrother.controller.receivers.SuggestionsReceiver;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.services.LocationTrackingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.poncholay.bigbrother.utils.database.DatabaseHelper.DatabaseContext.context;

public class MeetingSuggestion {

    private static MeetingSuggestion instance = null;

    private static String TAG = "MeetingSuggestion";
    private Intent          _trackingIntent     = null;
    private AlarmManager    _suggestionsAlarm   = null;
    private PendingIntent _operation            = null;

    private MeetingSuggestion() {
    }

    public static MeetingSuggestion getInstance() {
        if (instance == null) {
            instance = new MeetingSuggestion();
        }
        return instance;
    }

    public void start() {

        // Start tracking service
        Log.i(TAG, "Starting tracking service.");
        _trackingIntent = new Intent(context, LocationTrackingService.class);
        context.startService(_trackingIntent);

        SharedPreferences sharedPref;
        sharedPref 	 = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        Log.i(TAG, "start: Meeting interval : " + sharedPref.getFloat("suggestionInterval", 5));
        int interval = (int) (sharedPref.getFloat("suggestionInterval", 5) * 60f * 1000f);
        Date when = new Date(System.currentTimeMillis() + interval);

        try {
            Log.i(TAG, "Scheduling meeting suggestions every " + interval + " milliseconds.");
            Intent intent = new Intent(context, SuggestionsReceiver.class);
            _operation = PendingIntent.getBroadcast(
                    context,
                    Constants.SUGGESTIONS_RECEIVER_PENDING_INTENT,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

           _suggestionsAlarm = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);

            _suggestionsAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
                    when.getTime(),
                    interval,
                    _operation);
        } catch(Exception e) {
            Log.e(TAG, "Failed to schedule meeting suggestions : " + e.toString());
        }
    }

    public void stop() {
        if (_trackingIntent != null) {
            context.stopService(_trackingIntent);
        }
        if (_suggestionsAlarm != null && _operation != null) {
            _suggestionsAlarm.cancel(_operation);
        }
    }

    public static void launchMeetingDiscovery(final Context context) {
        new FindPossibleFriends(context, LocationTrackingService.getUserLocation(), new FindPossibleFriends.FindPossibleFriendsCallback() {
            @Override
            public void onSuccess(List<FriendDistance> friendDistances) {
                if (friendDistances.size() > 0) {
                    Intent i = new Intent(context, BigBrotherActivity.class);
                    i.putParcelableArrayListExtra("distances", new ArrayList<Parcelable>(friendDistances));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icon)
                            .setContentTitle("Friends available for meeting")
                            .setContentText("Click to show or dismiss to ignore")
                            .setAutoCancel(true);

                    mBuilder.setContentIntent(pi);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(-42, mBuilder.build());
                } else {
                    Log.i(TAG, "LaunchMeetingDiscovery Failed : no close friends found.");
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "onError: Impossible to suggest a meeting : " + msg);
            }
        }).execute();
    }

}
