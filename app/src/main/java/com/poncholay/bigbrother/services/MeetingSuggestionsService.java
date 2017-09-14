package com.poncholay.bigbrother.services;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/14/17.
 * mathieucorti@gmail.com
 */

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.poncholay.bigbrother.R;

import java.util.Calendar;

public class MeetingSuggestionsService extends Service {

    private static final String TAG = "MeetingSuggestions";

    private boolean isRunning  = false;
    private Thread worker = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        if (worker == null || worker.isInterrupted()) {

            worker = new Thread(new Runnable() {
                @Override
                public void run() {

                    //Your logic that service will perform will be placed here
                    //In this example we are just looping and waits for 1000 milliseconds in each loop.
                    for (int i = 0; i < 500; i++) {
                        try {

                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getBaseContext())
                                            .setSmallIcon(R.drawable.ic_time)
                                            .setContentTitle("My notification")
                                            .setContentText("Hello World!");

                            int mNotificationId = 001;
                            // Gets an instance of the NotificationManager service
                            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            // Builds the notification and issues it.
                            mNotifyMgr.notify(mNotificationId, mBuilder.build());


                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        if(isRunning){
                            Log.e(TAG, "Service running");
                        }
                    }

                    //Stop service once it finishes its task
                    stopSelf();
                }
            });

            worker.start();
        }

        return Service.START_STICKY;
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
