package com.poncholay.bigbrother.controller.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.adapters.DynamicTitledFragmentPagerAdapter;
import com.poncholay.bigbrother.controller.fragments.FriendListFragment;
import com.poncholay.bigbrother.controller.fragments.MapFragment;
import com.poncholay.bigbrother.controller.fragments.MeetingListFragment;
import com.poncholay.bigbrother.services.MeetingSuggestionsService;

import me.relex.circleindicator.CircleIndicator;

public class BigBrotherActivity extends AppCompatActivity {

	private DynamicTitledFragmentPagerAdapter mAdapter;

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
			Intent intent = new Intent(this, MeetingSuggestionsService.class);
			stopService(intent);
			startService(intent);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Constants.REQUEST_ACCESS_COARSE_LOCATION: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Intent intent = new Intent(this, MeetingSuggestionsService.class);
					stopService(intent);
					startService(intent);
				}
			}
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
