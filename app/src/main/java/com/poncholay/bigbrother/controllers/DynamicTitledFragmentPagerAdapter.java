package com.poncholay.bigbrother.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;

import com.poncholay.bigbrother.activities.fragments.TitledFragment;

public class DynamicTitledFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private final String TAG = this.getClass().getName();

	final private SparseArray<Pair<Class<? extends TitledFragment>, String>> registeredFragments = new SparseArray<>();

	public DynamicTitledFragmentPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public int getCount() {
		return registeredFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		try {
			return registeredFragments.get(position).first.newInstance();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		try {
			return registeredFragments.get(position).second;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return "Error";
		}
	}

	public void push(Class<? extends TitledFragment> fragment) {
		try {
			registeredFragments.append(registeredFragments.size(), new Pair<Class<? extends TitledFragment>, String>(fragment, fragment.newInstance().getTitle()));
		} catch (Exception e) {
			registeredFragments.append(registeredFragments.size(), new Pair<Class<? extends TitledFragment>, String>(fragment, fragment.getName()));
		}
		notifyDataSetChanged();
	}
}