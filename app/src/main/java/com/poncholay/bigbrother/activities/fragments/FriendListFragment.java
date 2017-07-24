package com.poncholay.bigbrother.activities.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.activities.FriendActivity;
import com.poncholay.bigbrother.controllers.FriendRecyclerViewAdapter;
import com.poncholay.bigbrother.model.AnchoredFloatingActionButton;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.ContactDataManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends TitledFragment {

	private static final String LOG_TAG = ContactDataManager.class.getName();
	private static final int PICK_CONTACTS = 1;
	private static final int CREATE_CONTACT = 2;

	private static final int REQUEST_READ_CONTACT_PERMISSION = 3;

	private OnListFragmentInteractionListener mListener;
	private FriendRecyclerViewAdapter mAdapter;
	private RecyclerView mRecyclerView;

	public FriendListFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friendlist, container, false);

		View recyclerView = view.findViewById(R.id.friendlist_recycler_view);
		if (recyclerView instanceof RecyclerView) {
			final Context context = recyclerView.getContext();
			mRecyclerView = (RecyclerView) recyclerView;
			List<Friend> friends = savedInstanceState == null ? retrieveFriends() : retrieveFriends(savedInstanceState);
			mAdapter = new FriendRecyclerViewAdapter(friends, mListener);
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

		View fab = view.findViewById(R.id.friendlist_fab_add);
		if (fab instanceof FloatingActionButton) {
			final Context context = recyclerView.getContext();
			AnchoredFloatingActionButton anchoredFab = new AnchoredFloatingActionButton(context, (FloatingActionButton) fab);

			final FloatingActionButton recordFab = (FloatingActionButton) view.findViewById(R.id.friendlist_fab_from_contact);
			recordFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tryReadContact();
				}
			});
			anchoredFab.addChild(recordFab);

			final FloatingActionButton fromFileFab =  (FloatingActionButton) view.findViewById(R.id.friendlist_fab_from_scratch);
			fromFileFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startCreateContact();
				}
			});
			anchoredFab.addChild(fromFileFab);

			anchoredFab.setup();
		}

		return view;
	}

	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == PICK_CONTACTS) {
			 if (resultCode == RESULT_OK) {
				 ContactDataManager contactsManager = new ContactDataManager(getActivity(), data);
				 String name;
				 String email;
				 try {
					 name = contactsManager.getContactName();
					 email = contactsManager.getContactEmail();

					 Friend friend = new Friend();
					 friend.setFirstname(name);
					 friend.setLastname(name);
					 friend.setEmail(email);
					 friend.save();
					 Intent intent = new Intent(getActivity(), FriendActivity.class);
					 intent.putExtra("mode", FriendActivity.EDIT_MODE);
					 startActivityForResult(intent, CREATE_CONTACT);
				 } catch (ContactDataManager.ContactQueryException e) {
					 Log.e(LOG_TAG, e.getMessage());
					 Toast.makeText(getActivity(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
				 }
			 }
		 }
	 }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
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
			case REQUEST_READ_CONTACT_PERMISSION: {
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startReadContact();
				}
			}
		}
	}

	private void tryReadContact() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
			requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT_PERMISSION);
		} else {
			startReadContact();
		}
	}

	private void startReadContact() {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivityForResult(intent, PICK_CONTACTS);
		} else {
			Toast.makeText(getActivity(), getString(R.string.error_not_available), Toast.LENGTH_SHORT).show();
		}
	}

	private void startCreateContact() {
//		Intent intent = new Intent(getActivity(), FriendActivity.class);
//		intent.putExtra("mode", FriendActivity.CREATE_MODE);
//		startActivityForResult(intent, CREATE_CONTACT);
		Toast.makeText(getActivity(), getString(R.string.error_not_implemented), Toast.LENGTH_SHORT).show();
	}

	private List<Friend> retrieveFriends(Bundle bundle) {
		return bundle.getParcelableArrayList("friends");
	}

	private List<Friend> retrieveFriends() {
//		return Lists.newArrayList(Friend.findAll(Friend.class)); //TODO : uncomment
		return Arrays.asList(new Friend(), new Friend(), new Friend(), new Friend(), new Friend(), new Friend(), new Friend(), new Friend("", "Djo", "Lopez", "DjoPeloz@gmail.com", new Date(), ""));
	}

	public String getTitle() {
		return "Friends";
	}
}
