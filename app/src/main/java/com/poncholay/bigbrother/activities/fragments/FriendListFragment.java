package com.poncholay.bigbrother.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controllers.FriendRecyclerViewAdapter;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.TitledFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FriendListFragment extends TitledFragment {

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

		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			mRecyclerView = (RecyclerView) view;
			List<Friend> friends = savedInstanceState == null ? retrieveFriends() : retrieveFriends(savedInstanceState);
			mAdapter = new FriendRecyclerViewAdapter(friends, mListener);
			mRecyclerView.setAdapter(mAdapter);
			LinearLayoutManager llm = new LinearLayoutManager(context);
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			mRecyclerView.setLayoutManager(llm);
			mRecyclerView.addItemDecoration(new DividerItemDecoration(context, llm.getOrientation()));
		}

		return view;
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
		List<Friend> list = mAdapter.getlist();
		outState.putParcelableArrayList("friends", new ArrayList<>(list));
		super.onSaveInstanceState(outState);
	}

	private List<Friend> retrieveFriends() {
//		return Lists.newArrayList(Friend.findAll(Friend.class)); //TODO : uncomment
		return Arrays.asList(new Friend(), new Friend("", "Djo", "Lopez", "DjoPeloz@gmail.com", new Date(), ""));
	}

	private List<Friend> retrieveFriends(Bundle bundle) {
		return bundle.getParcelableArrayList("friends");
	}
	
	public String getTitle() {
		return "Friends";
	}
}
