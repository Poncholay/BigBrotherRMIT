package com.poncholay.bigbrother.controllers;

import android.content.Context;
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

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.DateUtils;

import java.util.List;

public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder> {

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

			holder.mDateView.setText(DateUtils.toLiteString(meeting.getStart()));
			holder.mTitleView.setText(meeting.getTitle());

			if (meeting.getFriends().size() == 0) {
				ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.mPeopleView.getLayoutParams();
				params.setMargins(0, 0, 0, 0);
				params.height = 0;
				holder.mPeopleView.setLayoutParams(params);
			} else {
				IconRecyclerViewAdapter adapter = new IconRecyclerViewAdapter(meeting.getFriends());
				holder.mPeopleView.setAdapter(adapter);

				final Context context = holder.mPeopleView.getContext();
				LinearLayoutManager llm = new LinearLayoutManager(context);
				llm.setOrientation(LinearLayoutManager.HORIZONTAL);
				holder.mPeopleView.setLayoutManager(llm);
			}

			holder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent callMeetingActivity = new Intent(mContext, MeetingActivity.class);
//					callMeetingActivity.putExtra("Meeting", Meeting);
//					mFragment.startActivityForResult(callMeetingActivity, Constants.EDIT_Meeting);
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
		final TextView mDateView;
		final TextView mTitleView;
		final RecyclerView mPeopleView;

		Meeting mItem;

		ViewHolder(View view) {
			super(view);
			mView = view;
			mDateView = (TextView) view.findViewById(R.id.meeting_date);
			mTitleView = (TextView) view.findViewById(R.id.meeting_title);
			mPeopleView = (RecyclerView) view.findViewById(R.id.meeting_peoplelist);
		}
	}
}