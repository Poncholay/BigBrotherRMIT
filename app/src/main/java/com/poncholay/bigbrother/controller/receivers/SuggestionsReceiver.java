package com.poncholay.bigbrother.controller.receivers;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 10/8/17.
 * mathieucorti@gmail.com
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.poncholay.bigbrother.utils.meetings.MeetingSuggestion;

public class SuggestionsReceiver extends BroadcastReceiver {

  private static String TAG = "SUGGESTION_RECEIVER";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "onReceive: launching a meeting discovery.");
    MeetingSuggestion.launchMeetingDiscovery(context);
  }

}
