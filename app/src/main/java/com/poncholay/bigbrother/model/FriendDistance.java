package com.poncholay.bigbrother.model;

/*
 * BigBrotherRMIT
 * Created by Mathieu Corti on 9/25/17.
 * mathieucorti@gmail.com
 */

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.poncholay.bigbrother.utils.WebService;
import com.poncholay.bigbrother.utils.database.DatabaseContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.poncholay.bigbrother.utils.WebService.TYPE_GET;

public class FriendDistance implements Parcelable {

    static public final String MODE_WALKING = "walking";

    private FriendDistanceCallBack _friendDistanceCallback;

    private Friend  _friend             = null;
    private Context _context            = null;

    private LatLng  _friendLocation     = null;
    private LatLng  _userLocation       = null;

    private LatLng  _midPoint           = null;

    private double  _userDuration       = -1;
    private String  _userTextDuration   = null;

    private double  _friendDuration     = -1;
    private String  _friendTextDuration = null;

    private String  _matrixStatus       = null;

    public FriendDistance(Friend friend, LatLng friendLocation, LatLng userLocation,
                          Context context, FriendDistanceCallBack onDistanceSet) {
        _friendDistanceCallback = onDistanceSet;
        _friend         = friend;
        _context        = context;
        _friendLocation = friendLocation;
        _userLocation   = userLocation;
        _midPoint       = midPoint(_userLocation, _friendLocation);
    }

    public void execute() {
        fetchDistances(_friendLocation, _midPoint, MODE_WALKING, new WebService.WebServiceCallBack(_context) {
            @Override
            public void onSuccess(String response) {
                setFullFriendDuration(isolateDuration(response));
            }
        });

        fetchDistances(_userLocation, _midPoint, MODE_WALKING, new WebService.WebServiceCallBack(_context) {
            @Override
            public void onSuccess(String response) {
                setFullUserDuration(isolateDuration(response));
            }
        });
    }

    private void fetchDistances(LatLng from, LatLng to, final String mode, WebService.WebServiceCallBack onSuccess) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" +
                _userLocation.latitude + "," + _userLocation.longitude + "&destination=" +
                _friendLocation.latitude + "," + _friendLocation.longitude + "&mode=" +
                mode + "&sensor=false";

        new WebService(_context, url, TYPE_GET, null, onSuccess).execute();
    }

    private Pair<Double, String> isolateDuration(final String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);

            _matrixStatus = jsonObject.getString("status");

            if (!_matrixStatus.equals("OK")) {
                throw new JSONException(_matrixStatus);
            }

            JSONObject routes   = jsonObject.getJSONArray("routes").getJSONObject(0);
            JSONObject legs     = routes.getJSONArray("legs").getJSONObject(0);
            JSONObject dur      = legs.getJSONObject("duration");

            return new Pair<>(Double.parseDouble(dur.getString("value")), dur.getString("text"));

        } catch (JSONException e) {
            return new Pair<>((double) -1, e.getMessage());
        }
    }

    public Friend getFriend() {
        return _friend;
    }

    public LatLng getMidPoint() { return _midPoint; }

    public double getTotalDuration() {

        if (getFriendDuration() == -1 || getUserDuration() == -1) {
            return -1;
        } else {
            return getFriendDuration() + getUserDuration();
        }
    }

    public double getFriendDuration() {
        return _friendDuration;
    }

    public String getFriendTextDuration() {
        return _friendTextDuration;
    }

    public double getUserDuration() {
        return _userDuration;
    }

    public String getUserTextDuration() {
        return _userTextDuration;
    }

    public String getMatrixStatus() {
        return _matrixStatus;
    }

    public void setUserLocation(LatLng userLocation) { _userLocation = userLocation; }

    private void setFullUserDuration(Pair<Double, String> duration) {
        _userDuration       = duration.first;
        _userTextDuration   = duration.second;
        if (_friendTextDuration != null) {
            _friendDistanceCallback.onDurationSet();
        }
    }

    private void setFullFriendDuration(Pair<Double, String> duration) {
        _friendDuration     = duration.first;
        _friendTextDuration = duration.second;
        if (_userTextDuration != null) {
            _friendDistanceCallback.onDurationSet();
        }
    }

    private static LatLng midPoint(LatLng latLng1, LatLng latLng2) {

        double dLon = Math.toRadians(latLng2.longitude - latLng1.longitude);

        //convert to radians
        final double radLat1 = Math.toRadians(latLng1.latitude);
        final double radLat2 = Math.toRadians(latLng2.latitude);
        final double radLon1 = Math.toRadians(latLng1.longitude);

        final double bx = Math.cos(radLat2) * Math.cos(dLon);
        final double by = Math.cos(radLat2) * Math.sin(dLon);
        final double radLat3 = Math.atan2(Math.sin(radLat1) + Math.sin(radLat2),
                Math.sqrt((Math.cos(radLat1) + bx) * (Math.cos(radLat1) + bx) + by * by));
        final double radLon3 = radLon1 + Math.atan2(by, Math.cos(radLat1) + bx);

        //print out in degrees
        System.out.println("Result midpoint calc : " + Math.toDegrees(radLat3) + " " + Math.toDegrees(radLon3));
        return new LatLng(Math.toDegrees(radLat3), Math.toDegrees(radLon3));
    }

    public static abstract class FriendDistanceCallBack {
        public abstract void onDurationSet();
    }

    public void setFriend(Friend _friend) {
        this._friend = _friend;
    }

    public void setUserTextDuration(String _userTextDuration) {
        this._userTextDuration = _userTextDuration;
    }

    //__________
    //Parcelable

    //Only what is necessary
    public FriendDistance(Parcel in) {
        this.setFriend((Friend) in.readParcelable(Friend.class.getClassLoader()));
        this.setUserTextDuration(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(_friend, 0);
        dest.writeString(_userTextDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<FriendDistance> CREATOR = new Parcelable.Creator<FriendDistance>() {
        public FriendDistance createFromParcel(Parcel in) {
            return new FriendDistance(in);
        }

        public FriendDistance[] newArray(int size) {
            return new FriendDistance[size];
        }
    };
}
