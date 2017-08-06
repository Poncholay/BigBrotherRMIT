package com.poncholay.bigbrother.controllers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.IconUtils;

import java.util.List;

public class IconRecyclerViewAdapter extends RecyclerView.Adapter<IconRecyclerViewAdapter.ViewHolder> {

	private final List<Friend> mValues;
	private Context mContext;

	public IconRecyclerViewAdapter(List<Friend> items) {
		mValues = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_icon, parent, false);
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