package com.poncholay.bigbrother.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.FriendActivity;
import com.poncholay.bigbrother.model.Friend;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static com.poncholay.bigbrother.utils.CopyHelper.getFile;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

	private final List<Friend> mValues;
	private Context mContext;
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

//	Intent callFriendActivity = new Intent(mContext, FriendActivity.class);
//					callFriendActivity.putExtra("friend", friend);
//					callFriendActivity.putExtra("mode", Constants.EDIT_FRIEND);
//					mFragment.startActivityForResult(callFriendActivity, Constants.EDIT_FRIEND);

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final Friend friend = mValues.get(position);

		if (friend != null) {
			holder.mItem = friend;

			holder.mIconView.setBorderWidth(2);
			holder.mIconView.setShadowRadius(2);

			holder.mIconView.setImageDrawable(TextDrawable.builder()
					.beginConfig()
					.height(100)
					.width(100)
					.fontSize(60)
					.textColor(Color.BLACK)
					.endConfig()
					.buildRect(friend.getFirstname().equals("") ? "?" : friend.getFirstname().substring(0, 1), Color.WHITE));

			if (friend.getHasIcon()) {
				File iconFile = getFile(mContext, friend.getFirstname() + " " + friend.getLastname(), Constants.ICON);
				if (iconFile != null) {
					Picasso.with(mContext)
							.load(iconFile)
							.into(holder.mIconView);
				}
			}

			holder.mFirstNameView.setText(friend.getFirstname());
			holder.mLastNameView.setText(friend.getLastname());

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

//	public void sortTasks(int index) {
//		switch (index) {
//			case S_TITLE:
//				Collections.sort(mList, new Comparator<Board>() {
//					@Override
//					public int compare(Board l, Board r) {
//						if (l == null || r == null) {
//							return l == null ? 1 : -1;
//						}
//						return l.getName().compareToIgnoreCase(r.getName());
//					}
//				});
//				break;
//			case -S_TITLE:
//				Collections.sort(mList, new Comparator<Board>() {
//					@Override
//					public int compare(Board l, Board r) {
//						if (l == null || r == null) {
//							return l == null ? -1 : 1;
//						}
//						return r.getName().compareToIgnoreCase(l.getName());
//					}
//				});
//				break;
//			case S_SOUND:
//				Collections.sort(mList, new Comparator<Board>() {
//					@Override
//					public int compare(Board l, Board r) {
//						if (l == null || r == null) {
//							return l == null ? -1 : 1;
//						}
//						return l.getCount() > r.getCount() ? 1 : -1;
//					}
//				});
//			case -S_SOUND:
//				Collections.sort(mList, new Comparator<Board>() {
//					@Override
//					public int compare(Board l, Board r) {
//						if (l == null || r == null) {
//							return l == null ? -1 : 1;
//						}
//						return r.getCount() > l.getCount() ? 1 : -1;
//					}
//				});
//			default:
//				break;
//		}
//		notifyDataSetChanged();
//	}

	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final TextView mFirstNameView;
		final TextView mLastNameView;
		final CircularImageView mIconView;

		Friend mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mFirstNameView = (TextView) view.findViewById(R.id.friend_firstname);
			mLastNameView = (TextView) view.findViewById(R.id.friend_lastname);
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