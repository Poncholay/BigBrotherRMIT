package com.poncholay.bigbrother.services;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/14/17.
 * mathieucorti@gmail.com
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.utils.MeetingSuggestion;

import java.util.List;

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

    public static void launchMeetingDiscovery(Context context) {
        new MeetingSuggestion(context, userLocation, new MeetingSuggestion.MeetingSuggestionCallback() {
            @Override
            public void onSuccess(List<FriendDistance> friendDistances) {
                if (friendDistances.size() > 0) {
                    FriendDistance sugFriendDist = friendDistances.get(0);
                    Friend closestFriend = sugFriendDist.getFriend();
                    Log.e(TAG, "onSuccess - closest friend : " + closestFriend.getFirstname() +
                            " " + closestFriend.getLastname() + " is at " + sugFriendDist.getUserTextDuration()
                            + " walking time !");
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
