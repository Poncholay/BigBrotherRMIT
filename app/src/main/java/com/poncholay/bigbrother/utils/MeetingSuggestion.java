package com.poncholay.bigbrother.utils;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/28/17.
 * mathieucorti@gmail.com
 */

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.poncholay.bigbrother.model.Friend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeetingSuggestion {

    private static final String TAG = "MeetingSuggestion";

    private Context                     _context;
    private MeetingSuggestionCallback   _meetingSuggestionCallback;
    private int                         _friendsDistancesCount = 0;

    public MeetingSuggestion(Context context, Location currentLocation,
                             MeetingSuggestionCallback meetingSuggestionCallback) {
        _context = context;
        _meetingSuggestionCallback = meetingSuggestionCallback;
        suggestMeeting(currentLocation);
    }

    private void suggestMeeting(Location currentLocation) {
        // Get friends location
        DummyLocationService dls = DummyLocationService.getSingletonInstance();

        List<DummyLocationService.FriendLocation> matched = dls.getFriendLocationsForTime(_context, Calendar.getInstance().getTime(), 10, 10);
        List<Friend> friends = Friend.findCurrent(matched);
        final List<FriendDistance> friendDistances = new ArrayList<>();

        // Search matching friends
        for (Friend friend : friends) {
            for (DummyLocationService.FriendLocation friendLocation : matched) {
                if (friendLocation.id.equals(friend.getId().toString())) {

                    // Store friend current position
                    LatLng friendLatLng = new LatLng(friendLocation.latitude, friendLocation.longitude);

                    // Add a new friend distance
                    friendDistances.add(new FriendDistance(friend, friendLatLng,
                            new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            _context, new FriendDistance.FriendDistanceCallBack() {

                        // When the duration has been set (result of the call to the rest api)
                        // Check if all friends distances have been set
                        @Override
                        public void onDurationSet() {
                            if (++_friendsDistancesCount == friendDistances.size()) {
                                findClosestFriend(friendDistances);
                            }
                        }
                    }));
                }
            }
        }
    }

    private void findClosestFriend(final List<FriendDistance> friendDistances) {

        // Filter list
        // Note: Need to be replaced by remove if in Java8
        for (FriendDistance friend : friendDistances) {
            if (friend.get_totalDuration() == -1) {
                friendDistances.remove(friend);
            }
        }

        if (!friendDistances.isEmpty()) {

            // Sort the list by the closest combined walk time
            Collections.sort(friendDistances, new Comparator<FriendDistance>() {
                @Override
                public int compare(FriendDistance o1, FriendDistance o2) {
                    return Double.compare(o1.get_totalDuration(), o2.get_totalDuration());
                }
            });

            _meetingSuggestionCallback.onSuccess(friendDistances);
        } else {
            _meetingSuggestionCallback.onError();
        }
    }

    //
    // Callback
    //

    public static abstract class MeetingSuggestionCallback {
        public abstract void onSuccess(List<FriendDistance> friendsDistances);

        public abstract void onError();
    }
}
