package com.poncholay.bigbrother.utils.meetings;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/28/17.
 * mathieucorti@gmail.com
 */

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.poncholay.bigbrother.controller.services.DummyLocationService;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.FriendDistance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FindPossibleFriends {

    private static final String TAG = "FindPossibleFriends";

    private Context                     _context;
    private FindPossibleFriendsCallback _findPossibleFriendsCallback;
    private Location                    _currentLocation;
    private int                         _friendsDistancesCount = 0;

    public FindPossibleFriends(Context context, Location currentLocation,
                               FindPossibleFriendsCallback findPossibleFriendsCallback) {
        _context = context;
        _findPossibleFriendsCallback = findPossibleFriendsCallback;
        _currentLocation = currentLocation;
    }

    public void execute() {
        if (_currentLocation == null) {
            _findPossibleFriendsCallback.onError("Waiting for use location, try again later.");
            return;
        }
        findFriends();
    }

    private void findFriends() {
        // Get friends location
        DummyLocationService dls = DummyLocationService.getSingletonInstance();

        List<DummyLocationService.FriendLocation> matched = dls.getFriendLocationsForTime(_context, Calendar.getInstance().getTime(), 10, 0);
        List<Friend> friends = Friend.findCurrent(matched);
        final List<FriendDistance> friendDistances = new ArrayList<>();

        // Search matching friends
        for (Friend friend : friends) {
            for (DummyLocationService.FriendLocation friendLocation : matched) {
                if (friendLocation.id.equals(friend.getId().toString())) {

                    boolean ctn = false;
                    for (FriendDistance fd : friendDistances) {
                        if (fd.getFriend().getId().equals(friend.getId())) {
                            ctn = true;
                            break;
                        }
                    }
                    if (ctn) {
                        break;
                    }

                    // Store friend current position
                    LatLng friendLatLng = new LatLng(friendLocation.latitude, friendLocation.longitude);

                    // Add a new friend distance
                    friendDistances.add(new FriendDistance(friend, friendLatLng,
                            new LatLng(_currentLocation.getLatitude(), _currentLocation.getLongitude()),
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
        if (friendDistances.size() == 0) {
            _findPossibleFriendsCallback.onSuccess(friendDistances);
            return;
        }
        for (FriendDistance f : friendDistances) {
            f.execute();
        }
    }

    private void findClosestFriend(final List<FriendDistance> friendDistances) {

        // Filter list
        Iterator<FriendDistance> friendIterator = friendDistances.iterator();
        while (friendIterator.hasNext()) {
            FriendDistance friend = friendIterator.next();
            if (friend.getTotalDuration() == -1) {
                String error = friend.getFriendTextDuration();
                friendIterator.remove();
                if (friendDistances.size() == 0) {
                    _findPossibleFriendsCallback.onError(error);
                }
            }
        }

        // Sort the list by the closest combined walk time
        Collections.sort(friendDistances, new Comparator<FriendDistance>() {
            @Override
            public int compare(FriendDistance o1, FriendDistance o2) {
                return Double.compare(o1.getTotalDuration(), o2.getTotalDuration());
            }
        });

        _findPossibleFriendsCallback.onSuccess(friendDistances);
    }

    //
    // Callback
    //

    public static abstract class FindPossibleFriendsCallback {
        public abstract void onSuccess(List<FriendDistance> friendsDistances);
        public abstract void onError(String msg);
    }
}
