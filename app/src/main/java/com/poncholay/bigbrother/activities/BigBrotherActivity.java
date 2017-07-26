package com.poncholay.bigbrother.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.fragments.FriendListFragment;
import com.poncholay.bigbrother.controllers.DynamicTitledFragmentPagerAdapter;

import me.relex.circleindicator.CircleIndicator;

public class BigBrotherActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private DynamicTitledFragmentPagerAdapter mAdapter;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_big_brother);

		mContext = this;

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_big_brother);
		setSupportActionBar(toolbar);

		setupMenu(toolbar);
		setupPager(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bar_big_brother_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("pagerState", mAdapter.saveState());
		super.onSaveInstanceState(outState);
	}

	private void setupMenu(Toolbar toolbar) {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	private void setupPager(Bundle bundle) {
		ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOffscreenPageLimit(1);

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

		mAdapter.push(FriendListFragment.class);
		mAdapter.push(MeetingListFragment.class);
		mAdapter.push(LocalisationFragment.class);
	}

	//TODO : Implement real fragments
	static public class MeetingListFragment extends FriendListFragment {
		@Override
		public String getTitle() {
			return "Meetings";
		}
	}
	static public class LocalisationFragment extends FriendListFragment {
		@Override
		public String getTitle() {
			return "Localisation";
		}
	}
}
