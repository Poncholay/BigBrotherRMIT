package com.poncholay.bigbrother.utils.meetings;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 10/8/17.
 * mathieucorti@gmail.com
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.services.LocationTrackingService;

import java.util.List;

public class MeetingSuggestion extends BroadcastReceiver {

    private static String TAG = "MeetingSuggestion";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Current location : " + LocationTrackingService.getUserLocation().toString());
    }

    public static void launchMeetingDiscovery(Context context) {
        new FindPossibleFriends(context, LocationTrackingService.getUserLocation(), new FindPossibleFriends.FindPossibleFriendsCallback() {
            @Override
            public void onSuccess(List<FriendDistance> friendDistances) {
                if (friendDistances.size() > 0) {
                    FriendDistance sugFriendDist = friendDistances.get(0);
                    Friend closestFriend = sugFriendDist.getFriend();
                    Log.e(TAG, "onSuccess - closest friend : " + closestFriend.getFirstname() +
                            " " + closestFriend.getLastname() + ", midpoint is at " + sugFriendDist.getUserTextDuration()
                            + " walking time !");
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "onError: Impossible to suggest a meeting : " + msg);
            }
        }).execute();
    }
}
