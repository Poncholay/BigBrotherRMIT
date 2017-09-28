package com.poncholay.bigbrother.services;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/14/17.
 * mathieucorti@gmail.com
 */

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.DummyLocationService;
import com.poncholay.bigbrother.utils.FriendDistance;
import com.poncholay.bigbrother.utils.MeetingSuggestion;
import com.poncholay.bigbrother.utils.database.DatabaseContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MeetingSuggestionsService extends Service implements LocationListener {

    private static final String TAG = "Service_MS";

    private Location _userLocation = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        LocationManager lm = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);

        requestLocationUpdate();


        return Service.START_STICKY;
    }

    private void requestLocationUpdate() {
        try {
            LocationManager lm = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
            Boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, this);
//                if (_userLocation != null) {
                _userLocation = lm.getLastKnownLocation(isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER);
            } else {
                Log.e(TAG, "ACCESS_COARSE_LOCATION : PERMISSION_DENIED.");
            }
        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION" + e.toString());
        }
    }


    public Location get_userLocation() { return _userLocation; }

    @Override
    public void onLocationChanged(Location location) {

        if(location != null) {
            _userLocation = location;
            MeetingSuggestion ms = new MeetingSuggestion(getApplicationContext(), _userLocation, new MeetingSuggestion.MeetingSuggestionCallback() {
                @Override
                public void onSuccess(List<FriendDistance> friendDistances) {

                    FriendDistance sugFriendDist = friendDistances.get(0);
                    Friend closestFriend = sugFriendDist.get_friend();
                    Log.e(TAG, "onSuccess - closest friend : " + closestFriend.getFirstname() +
                            " " + closestFriend.getLastname() + " is at " + sugFriendDist.get_userTextDuration()
                            + " walking time !");
                }

                @Override
                public void onError() {
                    Log.e(TAG, "onError: Impossible to suggest a meeting");
                }
            });
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
