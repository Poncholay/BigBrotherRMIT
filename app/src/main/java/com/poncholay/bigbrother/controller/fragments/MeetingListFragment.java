package com.poncholay.bigbrother.controller.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.activities.BigBrotherActivity;
import com.poncholay.bigbrother.controller.activities.EditMeetingActivity;
import com.poncholay.bigbrother.controller.adapters.MeetingRecyclerViewAdapter;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.services.MeetingSuggestionsService;
import com.poncholay.bigbrother.utils.ContactDataManager;
import com.poncholay.bigbrother.utils.MeetingSuggestion;
import com.poncholay.bigbrother.view.AnchoredFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MeetingListFragment extends Fragment {

	private static final String LOG_TAG = ContactDataManager.class.getName();

	private MeetingRecyclerViewAdapter mAdapter;
	private RecyclerView mRecyclerView;

	private View view;

	public MeetingListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_meetinglist, container, false);
		View recyclerView = view.findViewById(R.id.meetinglist_recycler_view);
		setupRecyclerView(recyclerView, savedInstanceState);

		View fab = view.findViewById(R.id.meetinglist_fab_add);
		setupFab(fab, getActivity());

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
						meeting.save();
						meeting.createReminder(getActivity());
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
						meeting.save();
						meeting.updateReminder(getActivity());
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

	private MeetingRecyclerViewAdapter createAdapter(List<Meeting> meetings) {
		return new MeetingRecyclerViewAdapter(meetings, new MeetingRecyclerViewAdapter.OnMeetingClickListener() {
			@Override
			public void onMeetingClick(Meeting meeting, View v) {
				Intent callMeetingActivity = new Intent(getActivity(), EditMeetingActivity.class);
				callMeetingActivity.putExtra("meeting", meeting);
				callMeetingActivity.putExtra("mode", Constants.EDIT_MEETING);
				startActivityForResult(callMeetingActivity, Constants.EDIT_MEETING);
			}

			@Override
			public boolean onMeetingLongClick(final Meeting meeting, View v) {
				PopupMenu menu = new PopupMenu(getActivity(), v);
				menu.getMenu().add("Delete");
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getTitle().toString()) {
							case "Delete":
								if (mAdapter != null) {
									mAdapter.remove(meeting);
								}
								meeting.cancelReminder(getActivity());
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
		}, new MeetingRecyclerViewAdapter.OnSortMeeting() {
			@Override
			public void sort(int index, List<Meeting> values) {
				switch (index) {
					case Constants.BY_DATE:
						Collections.sort(values, new Comparator<Meeting>() {
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
						Collections.sort(values, new Comparator<Meeting>() {
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
						Collections.sort(values, new Comparator<Meeting>() {
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
						Collections.sort(values, new Comparator<Meeting>() {
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
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private void setupRecyclerView(View recyclerView, Bundle savedInstanceState) {
		if (recyclerView instanceof RecyclerView) {
			final Context context = recyclerView.getContext();
			mRecyclerView = (RecyclerView) recyclerView;
			List<Meeting> meetings = savedInstanceState == null ? retrieveMeetings() : retrieveMeetings(savedInstanceState);
			mAdapter = createAdapter(meetings);
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

	private void instantSuggestion(Context context) {
		new MeetingSuggestion(context, MeetingSuggestionsService.getUserLocation(), new MeetingSuggestion.MeetingSuggestionCallback() {
			@Override
			public void onSuccess(List<FriendDistance> friendsDistances) {
				Snackbar.make(view, "Yolo", Snackbar.LENGTH_LONG).show();
				for (FriendDistance f : friendsDistances) {
					Log.e("YOLO", f.getFriend().getFirstname());
					Log.e("YOLO", f.getUserTextDuration());
				}
			}

			@Override
			public void onError(String msg) {
				Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
			}
		}).execute();
	}

	private void setupFab(View fab, final Activity context) {
		if (fab instanceof FloatingActionButton) {
			AnchoredFloatingActionButton anchoredFab = new AnchoredFloatingActionButton(context, (FloatingActionButton) fab);

			final FloatingActionButton fromContact = (FloatingActionButton) view.findViewById(R.id.meetinglist_fab_instant);
			fromContact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					instantSuggestion(context);
				}
			});
			anchoredFab.addChild(fromContact);

			final FloatingActionButton fromScratch = (FloatingActionButton) view.findViewById(R.id.meetinglist_fab_from_scratch);
			fromScratch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), EditMeetingActivity.class);
					intent.putExtra("mode", Constants.NEW_MEETING);
					startActivityForResult(intent, Constants.NEW_MEETING);
				}
			});
			anchoredFab.addChild(fromScratch);

			anchoredFab.setup();
		}
	}

	private List<Meeting> retrieveMeetings(Bundle bundle) {
		return bundle.getParcelableArrayList("meetings");
	}

	private List<Meeting> retrieveMeetings() {
		return Meeting.getAll(Meeting.class);
	}

	public static String getTitle() {
		return "Meetings";
	}
}
