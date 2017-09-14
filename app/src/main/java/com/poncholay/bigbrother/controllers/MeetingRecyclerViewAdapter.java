package com.poncholay.bigbrother.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.EditMeetingActivity;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder> {

	static final private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
	static final private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

	private final List<Meeting> mValues;
	private Context mContext;
	private Fragment mFragment;

	public MeetingRecyclerViewAdapter(List<Meeting> items, Fragment fragment) {
		mValues = items;
		mFragment = fragment;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		mContext = parent.getContext();
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_meeting, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final Meeting meeting = mValues.get(position);

		if (meeting != null) {
			holder.mItem = meeting;

			holder.mDateBeginView.setText(dateFormat.format(meeting.getStart()));
			holder.mDateEndView.setText(dateFormat.format(meeting.getEnd()));
			holder.mTimeBeginView.setText(timeFormat.format(meeting.getStart()));
			holder.mTimeEndView.setText(timeFormat.format(meeting.getEnd()));
			holder.mTitleView.setText(meeting.getTitle());
			DecimalFormat decimalFormat = new DecimalFormat("#.0###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			holder.mLocationView.setText(meeting.getLocationName());
			holder.mNbFriends.setText(String.format(Locale.US, "%d", meeting.getFriends().size()));

			if (meeting.getFriends().size() == 0) {
				holder.mFriendsContainer.setVisibility(View.GONE);
				holder.mLine.setVisibility(View.GONE);
			} else {
				IconRecyclerViewAdapter adapter = new IconRecyclerViewAdapter(meeting.getFriends(), Constants.HORIZONTAL);
				holder.mPeopleView.setAdapter(adapter);

				LinearLayoutManager llm = new LinearLayoutManager(holder.mPeopleView.getContext());
				llm.setOrientation(LinearLayoutManager.HORIZONTAL);
				holder.mPeopleView.setLayoutManager(llm);
			}

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent callMeetingActivity = new Intent(mContext, EditMeetingActivity.class);
					callMeetingActivity.putExtra("meeting", meeting);
					callMeetingActivity.putExtra("mode", Constants.EDIT_MEETING);
					mFragment.startActivityForResult(callMeetingActivity, Constants.EDIT_MEETING);
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
									remove(meeting);
									meeting.delete();
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

	public int getPosition(Meeting object) {
		return mValues.indexOf(object);
	}

	public void add(@Nullable Meeting object) {
		mValues.add(object);
		notifyDataSetChanged();
	}

	public void insert(@Nullable Meeting object, int pos) {
		mValues.add(pos, object);
		notifyDataSetChanged();
	}

	public void remove(@Nullable Meeting object) {
		mValues.remove(object);
		notifyDataSetChanged();
	}

	public List<Meeting> getlist() {
		return mValues;
	}

	public void sort(int index) {
		switch (index) {
			case Constants.BY_DATE:
				Collections.sort(mValues, new Comparator<Meeting>() {
					@Override
					public int compare(Meeting l, Meeting r) {
						if (l == null || r == null) {
							return l == null ? -1 : 1;
						}
						return l.getStart().before(r.getStart()) ? 1 : -1;
					}
				});
				break;
			case Constants.BY_DATE_INV:
				Collections.sort(mValues, new Comparator<Meeting>() {
					@Override
					public int compare(Meeting l, Meeting r) {
						if (l == null || r == null) {
							return l == null ? -1 : 1;
						}
						return l.getStart().after(r.getStart()) ? 1 : -1;
					}
				});
				break;
			case Constants.BY_NAME:
				Collections.sort(mValues, new Comparator<Meeting>() {
					@Override
					public int compare(Meeting l, Meeting r) {
						if (l == null || r == null) {
							return l == null ? -1 : 1;
						}
						return l.getTitle().compareToIgnoreCase(r.getTitle());
					}
				});
				break;
			case Constants.BY_NAME_INV:
				Collections.sort(mValues, new Comparator<Meeting>() {
					@Override
					public int compare(Meeting l, Meeting r) {
						if (l == null || r == null) {
							return l == null ? -1 : 1;
						}
						return r.getTitle().compareToIgnoreCase(l.getTitle());
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
		final TextView mDateBeginView;
		final TextView mDateEndView;
		final TextView mTimeBeginView;
		final TextView mTimeEndView;
		final TextView mTitleView;
		final TextView mLocationView;
		final TextView mNbFriends;
		final RecyclerView mPeopleView;

		final View mFriendsContainer;
		final View mLine;

		Meeting mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mDateBeginView	= (TextView) view.findViewById(R.id.meeting_start_date);
			mDateEndView 	= (TextView) view.findViewById(R.id.meeting_end_date);
			mTimeBeginView 	= (TextView) view.findViewById(R.id.meeting_start_time);
			mTimeEndView 	= (TextView) view.findViewById(R.id.meeting_end_time);
			mTitleView 		= (TextView) view.findViewById(R.id.meeting_title);
			mLocationView 	= (TextView) view.findViewById(R.id.meeting_location);
			mNbFriends 		= (TextView) view.findViewById(R.id.meeting_people_number);
			mPeopleView 	= (RecyclerView) view.findViewById(R.id.meeting_peoplelist);

			mFriendsContainer = view.findViewById(R.id.meeting_friendlist_container);
			mLine = view.findViewById(R.id.line3);
		}
	}
}