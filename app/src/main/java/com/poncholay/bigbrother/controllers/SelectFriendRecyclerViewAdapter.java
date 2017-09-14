package com.poncholay.bigbrother.controllers;

import android.view.View;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.List;

public class SelectFriendRecyclerViewAdapter extends FriendRecyclerViewAdapter {

	protected final List<Friend> mSelectedValues;

	public SelectFriendRecyclerViewAdapter(List<Friend> items, List<Friend> selected) {
		super(items, null);
		mSelectedValues = selected;
	}

	public List<Friend> getSelected() {
		return mSelectedValues;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final Friend friend = mValues.get(position);

		if (friend != null) {
			holder.mItem = friend;

			IconUtils.setupIcon(holder.mIconView, friend, mContext);
			holder.mNameView.setText(friend.getFirstname() + " " + friend.getLastname());
			holder.mEmailView.setText(friend.getEmail().equals("") ? "No email" : friend.getEmail());

			if (mSelectedValues.contains(friend)) {
				holder.mView.setBackgroundColor(mContext.getResources().getColor(R.color.fillColor, null));
			}

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mSelectedValues.contains(friend)) {
						mSelectedValues.add(friend);
						v.setBackgroundColor(mContext.getResources().getColor(R.color.fillColor, null));
					} else {
						mSelectedValues.remove(friend);
						v.setBackgroundColor(mContext.getResources().getColor(R.color.colorSplash, null));
					}
				}
			});
		}
	}
}