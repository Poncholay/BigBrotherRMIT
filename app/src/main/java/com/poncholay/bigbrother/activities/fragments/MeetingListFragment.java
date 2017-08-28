package com.poncholay.bigbrother.activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.EditMeetingActivity;
import com.poncholay.bigbrother.controllers.MeetingRecyclerViewAdapter;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.ContactDataManager;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MeetingListFragment extends Fragment {

	private static final String LOG_TAG = ContactDataManager.class.getName();

	private MeetingRecyclerViewAdapter mAdapter;
	private RecyclerView mRecyclerView;
	private int mSort;

	public MeetingListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meetinglist, container, false);
		View recyclerView = view.findViewById(R.id.meetinglist_recycler_view);
		setupRecyclerView(recyclerView, savedInstanceState);

		View fab = view.findViewById(R.id.meetinglist_fab_add);
		setupFab(fab);

		setHasOptionsMenu(true);

		int index = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sortMeetings", Constants.BY_NAME);
		mAdapter.sort(index);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.bar_meeting_list_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_sort) {
			sort();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case Constants.NEW_MEETING: {
				if (resultCode == RESULT_OK) {
					Meeting meeting = data.getParcelableExtra("meeting");
					if (meeting != null) {
						meeting.setId(meeting.save());
						if (mAdapter != null) {
							mAdapter.add(meeting);
						}
					}
				}
				break;
			}
			case Constants.EDIT_MEETING: {
				if (resultCode == RESULT_OK) {
					Meeting meeting = data.getParcelableExtra("meeting");
					if (meeting != null) {
						meeting.setId(meeting.save());
						if (mAdapter != null) {
							int pos = mAdapter.getPosition(meeting);
							mAdapter.remove(meeting);
							mAdapter.insert(meeting, pos == -1 ? mAdapter.getItemCount() : pos);
						}
					}
				}
				break;
			}
			default:
				break;
		}
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("meetings", new ArrayList<>(mAdapter.getlist()));
		outState.putInt("pos", ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
		outState.putInt("offset", (mRecyclerView.getChildAt(0) == null) ? 0 : (mRecyclerView.getChildAt(0).getTop() - mRecyclerView.getPaddingTop()));
		super.onSaveInstanceState(outState);
	}

	private void sort() {
		PopupMenu menu = new PopupMenu(getContext(), getActivity().findViewById(R.id.action_sort));
		menu.getMenu().add("Date");
		menu.getMenu().add("Date reverse");
		menu.getMenu().add("Title");
		menu.getMenu().add("Title reverse");
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				int index = -1;
				switch (item.getTitle().toString()) {
					case "Date":
						index = Constants.BY_DATE;
						break;
					case "Date reverse":
						index = Constants.BY_DATE_INV;
						break;
					case "Title":
						index = Constants.BY_NAME;
						break;
					case "Title reverse":
						index = Constants.BY_NAME_INV;
						break;
					default:
						break;
				}
				SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt("sortMeetings", index);
				editor.apply();
				mAdapter.sort(index);
				return true;
			}
		});
		menu.show();
	}

	private void setupRecyclerView(View recyclerView, Bundle savedInstanceState) {
		if (recyclerView instanceof RecyclerView) {
			final Context context = recyclerView.getContext();
			mRecyclerView = (RecyclerView) recyclerView;
			List<Meeting> meetings = savedInstanceState == null ? retrieveMeetings() : retrieveMeetings(savedInstanceState);
			mAdapter = new MeetingRecyclerViewAdapter(meetings, this);
			mRecyclerView.setAdapter(mAdapter);
			LinearLayoutManager llm = new LinearLayoutManager(context);
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			if (savedInstanceState != null) {
				int positionIndex = savedInstanceState.getInt("pos", 0);
				int offset = savedInstanceState.getInt("offset", 0);
				llm.scrollToPositionWithOffset(positionIndex, offset);
			}
			mRecyclerView.setLayoutManager(llm);
		}
	}

	private void setupFab(View fab) {
		if (fab instanceof FloatingActionButton) {
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), EditMeetingActivity.class);
					intent.putExtra("mode", Constants.NEW_MEETING);
					startActivityForResult(intent, Constants.NEW_MEETING);
				}
			});
		}
	}

	private List<Meeting> retrieveMeetings(Bundle bundle) {
		return bundle.getParcelableArrayList("meetings");
	}

	private List<Meeting> retrieveMeetings() {
		return Lists.newArrayList(Meeting.findAll(Meeting.class));
	}

	public static String getTitle() {
		return "Meetings";
	}
}
