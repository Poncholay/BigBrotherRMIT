package com.poncholay.bigbrother.controllers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.List;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

	protected final List<Friend> mValues;
	protected Context mContext;
	protected OnFriendClickListener mListener;
	protected OnSortFriend mSorter;

	public interface OnFriendClickListener {
		void onFriendClick(Friend friend, View v);
		boolean onFriendLongClick(Friend friend, View v);
	}

	public interface OnSortFriend {
		void sort(int index, List<Friend> values);
	}

	public FriendRecyclerViewAdapter(List<Friend> items, OnFriendClickListener listener, OnSortFriend sorter) {
		mValues = items;
		mListener = listener;
		mSorter = sorter;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Friend friend = mValues.get(position);

		if (friend != null) {
			holder.mItem = friend;

			IconUtils.setupIcon(holder.mIconView, friend, mContext);

			holder.mNameView.setText(friend.getFirstname() + " " + friend.getLastname());
			holder.mEmailView.setText(friend.getEmail().equals("") ? "No email" : friend.getEmail());

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onFriendClick(friend, v);
					}
				}
			});
			holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return mListener != null && mListener.onFriendLongClick(friend, v);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mValues.size();
	}

	public int getPosition(Friend object) {
		return mValues.indexOf(object);
	}

	public void add(@Nullable Friend object) {
		mValues.add(object);
		notifyDataSetChanged();
	}

	public void insert(@Nullable Friend object, int pos) {
		mValues.add(pos, object);
		notifyDataSetChanged();
	}

	public void remove(@Nullable Friend object) {
		mValues.remove(object);
		notifyDataSetChanged();
	}

	public List<Friend> getlist() {
		return mValues;
	}

	public void sort(int index) {
		if (mSorter != null) {
			mSorter.sort(index, mValues);
		}
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final TextView mNameView;
		final TextView mEmailView;
		final CircularImageView mIconView;

		Friend mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mNameView = (TextView) view.findViewById(R.id.friend_name);
			mEmailView = (TextView) view.findViewById(R.id.friend_hint);
			mIconView = (CircularImageView) view.findViewById(R.id.friend_icon);
		}

		@Override
		public String toString() {
			return super.toString() + " '" +
					mItem.getId() + " " +
					mItem.getFirstname() + " " +
					mItem.getLastname() + "'";
		}
	}
}