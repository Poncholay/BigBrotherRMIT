package com.poncholay.bigbrother.services;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/14/17.
 * mathieucorti@gmail.com
 */

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.DummyLocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class MeetingSuggestionsService extends Service implements LocationListener {

    private static final String TAG = "MeetingSuggestions";

    private boolean isRunning  = false;
    private LatLng userLatLng;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        final Handler handler = new Handler();
        final int delay = 300000; // 5min

        handler.postDelayed(new Runnable(){
            public void run(){

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getBaseContext())
                                .setSmallIcon(R.drawable.ic_time)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");

                int mNotificationId = 1;
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                handler.postDelayed(this, delay);
            }
        }, delay);

        return Service.START_STICKY;
    }

    // TODO: Dumb copy paste from MapFragment, has to be merged
    private List<Friend> findCurrentFriends(List<DummyLocationService.FriendLocation> matched) {
        StringBuilder query = new StringBuilder();

        Iterator<DummyLocationService.FriendLocation> it = matched.iterator();
        while (it.hasNext()) {
            DummyLocationService.FriendLocation friendLocation = it.next();
            query.append("id = ").append(friendLocation.id);
            if (it.hasNext()) {
                query.append(" OR ");
            }
        }
        return Friend.getAll(Friend.class, query.toString());
    }

    private void suggestMeeting() {

        // Get friends location
        DummyLocationService dls = DummyLocationService.getSingletonInstance();

        List<DummyLocationService.FriendLocation> matched = dls.getFriendLocationsForTime(getApplicationContext(), Calendar.getInstance().getTime(), 10, 10);
        List<Friend> friends = findCurrentFriends(matched);

        for (Friend friend : friends) {
            for (DummyLocationService.FriendLocation friendLocation : matched) {
                if (friendLocation.id.equals(friend.getId().toString())) {
                    LatLng friendLatLng = new LatLng(friendLocation.latitude, friendLocation.longitude);
                }
            }
        }
    }

    private double getDistanceInfo(LatLng from, LatLng to) {
        StringBuilder stringBuilder = new StringBuilder();
        Double dist = 0.0;

        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" +
                from.latitude + "," + from.longitude + "&destination=" +
                to.latitude + "," + to.longitude + "&mode=walking&sensor=false";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = new JSONObject(stringBuilder.toString());

            JSONArray array = jsonObject.getJSONArray("routes");

            JSONObject routes = array.getJSONObject(0);

            JSONArray legs = routes.getJSONArray("legs");

            JSONObject steps = legs.getJSONObject(0);

            JSONObject distance = steps.getJSONObject("distance");

            Log.i("Distance", distance.toString());
            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dist;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location != null) {
            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e(TAG, "onStatusChanged: " + provider);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }
}
