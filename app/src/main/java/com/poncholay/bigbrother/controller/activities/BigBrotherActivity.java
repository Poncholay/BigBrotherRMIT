package com.poncholay.bigbrother.controller.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.adapters.DynamicTitledFragmentPagerAdapter;
import com.poncholay.bigbrother.controller.fragments.FriendListFragment;
import com.poncholay.bigbrother.controller.fragments.MapFragment;
import com.poncholay.bigbrother.controller.fragments.MeetingListFragment;
import com.poncholay.bigbrother.services.LocationTrackingService;
import com.poncholay.bigbrother.utils.meetings.MeetingSuggestion;

import java.util.Date;

import me.relex.circleindicator.CircleIndicator;

import static com.poncholay.bigbrother.utils.database.DatabaseHelper.DatabaseContext.context;

public class BigBrotherActivity extends AppCompatActivity {

	private DynamicTitledFragmentPagerAdapter mAdapter;
	private static String TAG = "BigBrother";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_big_brother);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_big_brother);
		setSupportActionBar(toolbar);

		setupPager(savedInstanceState);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_ACCESS_COARSE_LOCATION);
		} else {
			startMeetingSuggestions();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Constants.REQUEST_ACCESS_COARSE_LOCATION: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startMeetingSuggestions();
				}
			}
		}
	}

	private void startMeetingSuggestions() {

		// Start tracking service
		Intent intent = new Intent(this, LocationTrackingService.class);
		startService(intent);

		Date when = new Date(System.currentTimeMillis());
		SharedPreferences sharedPref;

		sharedPref 	 = getSharedPreferences("settings", Context.MODE_PRIVATE);
		int interval = sharedPref.getInt("suggestionInterval", 5);

		try{
			Intent suggestMeeting = new Intent(context, MeetingSuggestion.class);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context,
					Constants.MEETING_SUGGEST_PENDING_INTENT,
					suggestMeeting,
					PendingIntent.FLAG_CANCEL_CURRENT);

			AlarmManager alarms = (AlarmManager) context.getSystemService(
					Context.ALARM_SERVICE);

			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					when.getTime(),
					1 * 60 * 1000,
					pendingIntent);

			Log.e(TAG, "startMeetingSuggestions: Alarm will repeat.");

		}catch(Exception e){
			Log.e(TAG, "Failed to schedule meeting suggestions : " + e.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.bar_big_brother_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("pagerState", mAdapter.saveState());
		super.onSaveInstanceState(outState);
	}

	private void setupPager(Bundle bundle) {
		ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOffscreenPageLimit(2);

		CircleIndicator indicator = (CircleIndicator) findViewById(R.id.pager_indicator);

		PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tabs);
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setTabIndicatorColor(getColor(R.color.colorSplash));

		mAdapter = new DynamicTitledFragmentPagerAdapter(getSupportFragmentManager());
		mAdapter.registerDataSetObserver(indicator.getDataSetObserver());

		if (bundle != null) {
			Parcelable state = bundle.getParcelable("pagerState");
			mAdapter.restoreState(state, DynamicTitledFragmentPagerAdapter.class.getClassLoader());
		}

		mViewPager.setAdapter(mAdapter);
		indicator.setViewPager(mViewPager);

		mAdapter.push(FriendListFragment.class, FriendListFragment.getTitle());
		mAdapter.push(MeetingListFragment.class, MeetingListFragment.getTitle());
		mAdapter.push(MapFragment.class, MapFragment.getTitle());
	}
}
