package com.poncholay.bigbrother.controllers;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;

import java.util.List;

public class SelectFriendRecyclerViewAdapter extends FriendRecyclerViewAdapter {

	private final List<Friend> mSelectedValues;

	public SelectFriendRecyclerViewAdapter(List<Friend> items, List<Friend> selected, OnFriendClickListener listener) {
		super(items, listener, null);
		mSelectedValues = selected;
	}

	public List<Friend> getSelected() {
		return mSelectedValues;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
		final Friend friend = mValues.get(position);

		if (friend != null) {
			if (mSelectedValues.contains(friend)) {
				holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.fillColor, null));
			}
		}
	}
}