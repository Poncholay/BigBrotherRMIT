package com.poncholay.bigbrother.activities.fragments;

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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.utils.ContactDataManager;

public class MapFragment extends Fragment implements OnMapReadyCallback {
	private static final String LOG_TAG = ContactDataManager.class.getName();

	MapView mMapView;
	private GoogleMap mMap;

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

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mMap != null) {
				LatLng home = new LatLng(-37.865578, 144.981069);

				mMap.addMarker(new MarkerOptions().position(home).title("Home"));
				CameraPosition cameraPosition = new CameraPosition.Builder().target(home).zoom(15).build();

				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
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

	public static String getTitle() {
		return "Localisation";
	}
}
