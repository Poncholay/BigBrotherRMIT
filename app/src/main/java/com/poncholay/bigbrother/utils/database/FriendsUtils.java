package com.poncholay.bigbrother.utils.database;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/28/17.
 * mathieucorti@gmail.com
 */

import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.DummyLocationService;

import java.util.Iterator;
import java.util.List;

public class FriendsUtils {

    public static List<Friend> findCurrentFriends(List<DummyLocationService.FriendLocation> matched) {
        StringBuilder query = new StringBuilder();

        Iterator<DummyLocationService.FriendLocation> it = matched.iterator();
        while (it.hasNext()) {
            DummyLocationService.FriendLocation friendLocation = it.next();
            query.append(DatabaseContract.FriendEntry._ID + " = ").append(friendLocation.id);
            if (it.hasNext()) {
                query.append(" OR ");
            }
        }
        return Friend.getAll(Friend.class, query.toString());
    }
}
