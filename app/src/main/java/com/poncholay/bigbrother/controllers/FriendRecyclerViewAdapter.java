package com.poncholay.bigbrother.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.FriendActivity;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

	protected final List<Friend> mValues;
	protected Context mContext;
	private Fragment mFragment;

	public FriendRecyclerViewAdapter(List<Friend> items, Fragment fragment) {
		mValues = items;
		mFragment = fragment;
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

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent callFriendActivity = new Intent(mContext, FriendActivity.class);
					callFriendActivity.putExtra("friend", friend);
					mFragment.startActivityForResult(callFriendActivity, Constants.EDIT_FRIEND);
				}
			});
			holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					PopupMenu menu = new PopupMenu(mContext, v);
					menu.getMenu().add("Delete");
					menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getTitle().toString()) {
								case "Delete":
									remove(friend);
									//TODO : delete directory
									friend.delete();
									return true;
								default:
									return true;
							}
						}
					});
					menu.show();
					return true;
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
		switch (index) {
			case Constants.BY_NAME:
				Collections.sort(mValues, new Comparator<Friend>() {
					@Override
					public int compare(Friend l, Friend r) {
						if (l == null || r == null) {
							return l == null ? 1 : -1;
						}
						return l.getFirstname().compareToIgnoreCase(r.getFirstname());
					}
				});
				break;
			case Constants.BY_NAME_INV:
				Collections.sort(mValues, new Comparator<Friend>() {
					@Override
					public int compare(Friend l, Friend r) {
						if (l == null || r == null) {
							return l == null ? -1 : 1;
						}
						return -l.getFirstname().compareToIgnoreCase(r.getFirstname());
					}
				});
				break;
			default:
				break;
		}
		notifyDataSetChanged();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final TextView mNameView;
		final TextView mLastNameView;
		final CircularImageView mIconView;

		Friend mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mNameView = (TextView) view.findViewById(R.id.friend_name);
			mLastNameView = (TextView) view.findViewById(R.id.friend_hint);
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