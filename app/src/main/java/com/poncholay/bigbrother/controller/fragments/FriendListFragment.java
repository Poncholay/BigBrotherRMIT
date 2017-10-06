package com.poncholay.bigbrother.controller.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
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
import com.poncholay.bigbrother.controller.activities.EditFriendActivity;
import com.poncholay.bigbrother.controller.activities.FriendActivity;
import com.poncholay.bigbrother.controller.adapters.FriendRecyclerViewAdapter;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.ContactDataManager;
import com.poncholay.bigbrother.utils.CopyHelper;
import com.poncholay.bigbrother.view.AnchoredFloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends Fragment {

	private static final String LOG_TAG = ContactDataManager.class.getName();

	private FriendRecyclerViewAdapter mAdapter;
	private RecyclerView mRecyclerView;
	private int mSort = Constants.BY_NAME;

	public FriendListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friendlist, container, false);

		View recyclerView = view.findViewById(R.id.friendlist_recycler_view);
		setupRecyclerView(recyclerView, savedInstanceState);

		View fab = view.findViewById(R.id.friendlist_fab_add);
		setupFab(fab, view, recyclerView.getContext());

		setHasOptionsMenu(true);

		mSort = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sortFriends", Constants.BY_NAME);
		sort(false);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.bar_friend_list_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_sort) {
			sort(true);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case Constants.PICK_CONTACTS: {
				if (resultCode == RESULT_OK) {
					ContactDataManager contactsManager = new ContactDataManager(getActivity(), data);
					String firstname;
					String lastname;
					String email;
					try {
						firstname = contactsManager.getContactFirstName();
						lastname = contactsManager.getContactLastName();
						email = contactsManager.getContactEmail();

						Friend friend = new Friend();
						friend.setFirstname(firstname);
						friend.setLastname(lastname);
						friend.setEmail(email);
						Intent intent = new Intent(getActivity(), EditFriendActivity.class);
						intent.putExtra("friend", friend);
						intent.putExtra("mode", Constants.EDIT_FRIEND);
						startActivityForResult(intent, Constants.EDIT_FRIEND);
					} catch (ContactDataManager.ContactQueryException e) {
						Log.e(LOG_TAG, e.getMessage());
						Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.error_generic), Snackbar.LENGTH_SHORT).show();
					}
				}
				break;
			}
			case Constants.NEW_FRIEND: {
				if (resultCode == RESULT_OK) {
					Friend friend = data.getParcelableExtra("friend");
					if (friend != null) {
						friend.save();
						if (mAdapter != null) {
							mAdapter.add(friend);
						}
					}
				}
				break;
			}
			case Constants.EDIT_FRIEND: {
				if (resultCode == RESULT_OK) {
					Friend friend = data.getParcelableExtra("friend");
					if (friend != null) {
						friend.save();
						if (mAdapter != null) {
							int pos = mAdapter.getPosition(friend);
							mAdapter.remove(friend);
							mAdapter.insert(friend, pos == -1 ? mAdapter.getItemCount() : pos);
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
		outState.putParcelableArrayList("friends", new ArrayList<>(mAdapter.getlist()));
		outState.putInt("pos", ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
		outState.putInt("offset", (mRecyclerView.getChildAt(0) == null) ? 0 : (mRecyclerView.getChildAt(0).getTop() - mRecyclerView.getPaddingTop()));
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Constants.REQUEST_READ_CONTACT_PERMISSION: {
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startReadContact();
				}
			}
		}
	}

	private void sort(boolean change) {
		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		if (change) {
			mSort = mSort == Constants.BY_NAME ? Constants.BY_NAME_INV : Constants.BY_NAME;
		}
		editor.putInt("sortFriends", mSort);
		editor.apply();
		mAdapter.sort(mSort);
	}

	private FriendRecyclerViewAdapter createAdapter(List<Friend> friends) {
		return new FriendRecyclerViewAdapter(friends, new FriendRecyclerViewAdapter.OnFriendClickListener() {
			@Override
			public void onFriendClick(final Friend friend, View v) {
				Intent callFriendActivity = new Intent(getActivity(), FriendActivity.class);
				callFriendActivity.putExtra("friend", friend);
				startActivityForResult(callFriendActivity, Constants.EDIT_FRIEND);
			}

			@Override
			public boolean onFriendLongClick(final Friend friend, View v) {
				PopupMenu menu = new PopupMenu(getActivity(), v);
				menu.getMenu().add("Delete");
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getTitle().toString()) {
							case "Delete":
								mAdapter.remove(friend);
								File dir = CopyHelper.getDirectory(getActivity(), friend.getFirstname() + " " + friend.getLastname());
								if (dir != null) {
									dir.delete();
								}
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
		}, new FriendRecyclerViewAdapter.OnSortFriend() {
			@Override
			public void sort(int index, List<Friend> values) {
				switch (index) {
					case Constants.BY_NAME:
						Collections.sort(values, new Comparator<Friend>() {
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
						Collections.sort(values, new Comparator<Friend>() {
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
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private void setupRecyclerView(View recyclerView, Bundle savedInstanceState) {
		if (recyclerView instanceof RecyclerView) {
			final Context context = recyclerView.getContext();
			mRecyclerView = (RecyclerView) recyclerView;
			mAdapter = createAdapter(savedInstanceState == null ? retrieveFriends() : retrieveFriends(savedInstanceState));
			mRecyclerView.setAdapter(mAdapter);
			LinearLayoutManager llm = new LinearLayoutManager(context);
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			if (savedInstanceState != null) {
				int positionIndex = savedInstanceState.getInt("pos", 0);
				int offset = savedInstanceState.getInt("offset", 0);
				llm.scrollToPositionWithOffset(positionIndex, offset);
			}
			mRecyclerView.setLayoutManager(llm);
			mRecyclerView.addItemDecoration(new DividerItemDecoration(context, llm.getOrientation()));
		}
	}

	private void setupFab(View fab, View view, Context context) {
		if (fab instanceof FloatingActionButton) {
			AnchoredFloatingActionButton anchoredFab = new AnchoredFloatingActionButton(context, (FloatingActionButton) fab);

			final FloatingActionButton fromContact = (FloatingActionButton) view.findViewById(R.id.friendlist_fab_from_contact);
			fromContact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tryReadContact();
				}
			});
			anchoredFab.addChild(fromContact);

			final FloatingActionButton fromScratch = (FloatingActionButton) view.findViewById(R.id.friendlist_fab_from_scratch);
			fromScratch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startCreateContact();
				}
			});
			anchoredFab.addChild(fromScratch);

			anchoredFab.setup();
		}
	}

	private void tryReadContact() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constants.REQUEST_READ_CONTACT_PERMISSION);
		} else {
			startReadContact();
		}
	}

	private void startReadContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, Constants.PICK_CONTACTS);
		} else {
			Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.error_not_available), Snackbar.LENGTH_SHORT).show();
		}
	}

	private void startCreateContact() {
		Intent intent = new Intent(getActivity(), EditFriendActivity.class);
		intent.putExtra("mode", Constants.NEW_FRIEND);
		startActivityForResult(intent, Constants.NEW_FRIEND);
	}

	private List<Friend> retrieveFriends(Bundle bundle) {
		return bundle.getParcelableArrayList("friends");
	}

	private List<Friend> retrieveFriends() {
		return Friend.getAll(Friend.class);
	}

	public static String getTitle() {
		return "Friends";
	}
}
