package com.poncholay.bigbrother.services;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/14/17.
 * mathieucorti@gmail.com
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.activities.BigBrotherActivity;
import com.poncholay.bigbrother.controller.activities.EditMeetingActivity;
import com.poncholay.bigbrother.controller.activities.SnoozeActivity;
import com.poncholay.bigbrother.controller.fragments.MeetingListFragment;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.MeetingSuggestion;
import com.poncholay.bigbrother.utils.ParcelableUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingSuggestionsService extends Service implements LocationListener {

    private static final String TAG = "Service_MS";

    private static Location userLocation = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        requestLocationUpdate();

        return Service.START_STICKY;
    }

    private void requestLocationUpdate() {
        try {
            LocationManager lm = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
            Boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, this);
                userLocation = lm.getLastKnownLocation(isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER);
            } else {
                Log.e(TAG, "ACCESS_COARSE_LOCATION : PERMISSION_DENIED.");
            }
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION" + e.toString());
        }
    }

    public static Location getUserLocation() {
        return userLocation;
    }

    public static void launchMeetingDiscovery(final Context context) {
        new MeetingSuggestion(context, userLocation, new MeetingSuggestion.MeetingSuggestionCallback() {
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
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "onError: Impossible to suggest a meeting : " + msg);
            }
        }).execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            userLocation = location;
            launchMeetingDiscovery(getApplicationContext());
        } else {
            Log.e(TAG, "onLocationChanged : new location is null");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");
    }
}
