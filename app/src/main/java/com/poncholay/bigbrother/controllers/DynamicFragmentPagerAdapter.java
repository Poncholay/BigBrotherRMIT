package com.poncholay.bigbrother.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.poncholay.bigbrother.model.TitledFragment;

public class DynamicFragmentPagerAdapter extends FragmentStatePagerAdapter {

	private SparseArray<TitledFragment> registeredFragments = new SparseArray<>();

	public DynamicFragmentPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TitledFragment fragment = registeredFragments.get(position);
		if (fragment != null) {
			fragment.setLoaded(true);
		}
		return super.instantiateItem(container, position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		TitledFragment fragment = registeredFragments.get(position);
		if (fragment != null) {
			fragment.setLoaded(false);
		}
		super.destroyItem(container, position, object);
	}

	@Override
	public int getCount() {
		return registeredFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return registeredFragments.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return registeredFragments.get(position).getTitle();
	}

	public void push(TitledFragment fragment) {
		registeredFragments.append(registeredFragments.size(), fragment);
		notifyDataSetChanged();
	}
}