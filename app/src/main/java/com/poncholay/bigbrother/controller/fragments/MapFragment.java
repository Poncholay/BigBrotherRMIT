package com.poncholay.bigbrother.controller.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.controller.services.DummyLocationService;
import com.poncholay.bigbrother.controller.services.LocationTrackingService;
import com.poncholay.bigbrother.utils.BitmapUtils;
import com.poncholay.bigbrother.utils.ContactDataManager;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
	private static final String LOG_TAG = ContactDataManager.class.getName();

	private MapView mMapView;
	private GoogleMap mMap;
	private List<Marker> mMarkers;
	private LatLng mPos = null;

	public MapFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_maps, container, false);

		mMarkers = new ArrayList<>();

		mMapView = (MapView) view.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);
		mMapView.onResume();

		View fab = view.findViewById(R.id.localisation_fab_center);
		setupFab(fab);

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}

		mMapView.getMapAsync(this);

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.bar_map_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_refresh) {
			refresh();
			center();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			refresh();
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		setup();
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

	private void setupFab(View fab) {
		if (fab instanceof FloatingActionButton) {
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					refresh();
					centerToMe();
				}
			});
		}
	}

	private void centerToMe() {
		if (mPos != null) {
			CameraPosition cameraPosition = new CameraPosition.Builder().target(mPos).zoom(15).build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	private void center() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : mMarkers) {
			builder.include(marker.getPosition());
		}
		mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 75));
	}

	private void refresh() {
		mMap.clear();
		mMarkers.clear();
		setupMeetings();
		setupFriends();
		setupMyLocation();
	}

	private void setup() {
		refresh();
		center();
	}

	private void setupMeetings() {
		if (mMap != null) {
			List<Meeting> meetings = Meeting.getAll(Meeting.class);
			for (Meeting meeting : meetings) {
				if (meeting.getLatitude() != -1 && meeting.getLongitude() != -1) {
					mPos = new LatLng(meeting.getLatitude(), meeting.getLongitude());
					mMarkers.add(mMap.addMarker(new MarkerOptions()
							.position(mPos)
							.title(meeting.getTitle()))
					);
				}
			}
		}
	}

	private void setupFriends() {
		if (mMap != null) {
			DummyLocationService dls = DummyLocationService.getSingletonInstance();
			List<DummyLocationService.FriendLocation> matched = dls.getFriendLocationsForTime(this.getContext(), new Date(), 10, 0);
			List<Friend> friends = Friend.findCurrent(matched);
			for (Friend friend : friends) {
				for (DummyLocationService.FriendLocation friendLocation : matched) {
					if (friendLocation.id.equals(friend.getId().toString())) {
						mPos = new LatLng(friendLocation.latitude, friendLocation.longitude);

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

						mMarkers.add(mMap.addMarker(markerOptions));
					}
				}
			}
		}
	}

	private void setupMyLocation() {
		Location location = LocationTrackingService.getUserLocation();
		if (location != null) {
			mPos = new LatLng(location.getLatitude(), location.getLongitude());
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(mPos).title("Me");
			Bitmap bitmap = BitmapUtils.drawableToBitmap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_fab_instant));
			bitmap = BitmapUtils.getCircularBitmap(bitmap);
			bitmap = BitmapUtils.getResizedBitmap(bitmap, 80, 80);
			bitmap = BitmapUtils.addBorder(bitmap, Color.BLACK);
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
			mMarkers.add(mMap.addMarker(markerOptions));
		}
	}

	public static String getTitle() {
		return "Localisation";
	}
}
