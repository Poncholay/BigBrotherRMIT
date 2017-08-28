package com.poncholay.bigbrother.activities.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Lists;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.BitmapUtils;
import com.poncholay.bigbrother.utils.ContactDataManager;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.List;
import java.util.Random;

public class MapFragment extends Fragment implements OnMapReadyCallback {
	private static final String LOG_TAG = ContactDataManager.class.getName();

	private MapView mMapView;
	private GoogleMap mMap;
	private LatLng mPos = null;

	public MapFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_maps, container, false);

		mMapView = (MapView) view.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);
		mMapView.onResume();

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}

		mMapView.getMapAsync(this);

		return view;
	}

//	@Override
//	public boolean onMarkerClick(final Marker marker) {
//
//		// Retrieve the data from the marker.
//		Integer clickCount = (Integer) marker.getTag();
//
//		// Check if a click count was set, then display the click count.
//		if (clickCount != null) {
//			clickCount = clickCount + 1;
//			marker.setTag(clickCount);
//			Toast.makeText(this,
//					marker.getTitle() +
//							" has been clicked " + clickCount + " times.",
//					Toast.LENGTH_SHORT).show();
//		}
//
//		// Return false to indicate that we have not consumed the event and that we wish
//		// for the default behavior to occur (which is for the camera to move such that the
//		// marker is centered and for the marker's info window to open, if it has one).
//		return false;
//	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mPos != null) {
				CameraPosition cameraPosition = new CameraPosition.Builder().target(mPos).zoom(10).build();
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		setupMeetings();
		setupFriends();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	private void setupMeetings() {
		if (mMap != null) {
			List<Meeting> meetings = Lists.newArrayList(Meeting.findAll(Meeting.class));
			for (Meeting meeting : meetings) {
				mPos = new LatLng(meeting.getLatitude(), meeting.getLongitude());
				mMap.addMarker(new MarkerOptions()
						.position(mPos)
						.title(meeting.getTitle())
				);
			}
		}
	}

	private void setupFriends() {
		if (mMap != null) {
			List<Friend> friends = Lists.newArrayList(Friend.findAll(Friend.class));
			for (Friend friend : friends) {
//				mPos = new LatLng(friend.getLatitude(), friend.getLongitude());
				mPos = new LatLng(-37.813 + new Random().nextFloat() % 0.2, 144.962 + new Random().nextFloat() % 0.2);
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(mPos).title(friend.getFirstname() + " " + friend.getLastname());
				Bitmap bitmap;
				if (friend.getHasIcon()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					bitmap = BitmapFactory.decodeFile(IconUtils.getIconPath(friend, getContext()), options);
				} else {
					bitmap = BitmapUtils.drawableToBitmap(IconUtils.getIconTextDrawable(friend));
				}
				bitmap = BitmapUtils.getCircularBitmap(bitmap);
				bitmap = BitmapUtils.getResizedBitmap(bitmap, 100, 100);
				bitmap = BitmapUtils.addBorder(bitmap, Color.BLACK);
				markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

				mMap.addMarker(markerOptions);
			}
		}
	}

	public static String getTitle() {
		return "Localisation";
	}
}
