package com.poncholay.bigbrother.controller.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.IconUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class IconRecyclerViewAdapter extends RecyclerView.Adapter<IconRecyclerViewAdapter.ViewHolder> {

	private List<Friend> mValues;
	private Context mContext;
	private int mOrientation;

	public IconRecyclerViewAdapter(List<Friend> items, int orientation) {
		if (orientation != Constants.VERTICAL && orientation != Constants.HORIZONTAL) {
			throw new InvalidParameterException("Invalid orientation");
		}
		mValues = items;
		mOrientation = orientation;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		View view = null;
		if (mOrientation == Constants.VERTICAL) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_icon_vertical, parent, false);
		} else  if (mOrientation == Constants.HORIZONTAL) {
			view = LayoutInflater.from(mContext).inflate(R.layout.item_icon_horizontal, parent, false);
		}
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final Friend friend = mValues.get(position);

		if (friend != null) {
			holder.mItem = friend;
			IconUtils.setupIcon(holder.mIconView, friend, mContext);
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

	public void setList(List<Friend> friends) {
		mValues = friends;
		notifyDataSetChanged();
	}

	public List<Friend> getlist() {
		return mValues;
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		final View mView;
		final CircularImageView mIconView;

		Friend mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mIconView = (CircularImageView) view.findViewById(R.id.icon);
		}
	}
}