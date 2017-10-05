package com.poncholay.bigbrother.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.BundleUtils;
import com.poncholay.bigbrother.utils.DateUtils;
import com.poncholay.bigbrother.utils.IconUtils;

public class FriendActivity extends AppCompatActivity {

	private Friend mFriend;

	private TextView mNameView;
	private TextView mEmailView;
	private TextView mDateView;
	private CircularImageView mIconView;
	private boolean mModified = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);

		mFriend = (Friend) BundleUtils.retrieveParcelable(savedInstanceState, getIntent().getExtras(), "friend");
		if (mFriend == null) {
			finish();
			return;
		}

		Log.i("FRIEND", "Friend id : " + mFriend.getId());
		setupToolbar();
		setupFormListeners();
		IconUtils.setupIconBig(mIconView, mFriend, this);
	}

	@Override
	public void onBackPressed() {
		returnFriend();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.bar_friend_activity, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case Constants.EDIT_FRIEND: {
				if (resultCode == RESULT_OK) {
					Friend friend = data.getParcelableExtra("friend");
					if (friend != null) {
						mFriend = friend;
						mModified = true;
						setupFormListeners();
						IconUtils.setupIconBig(mIconView, mFriend, this);
					}
				}
				break;
			}
			default:
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_edit) {
			Intent intent = new Intent(this, EditFriendActivity.class);
			intent.putExtra("mode", Constants.EDIT_FRIEND);
			intent.putExtra("friend", mFriend);
			startActivityForResult(intent, Constants.EDIT_FRIEND);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void returnFriend() {
		Intent returnIntent = new Intent();
		if (!mModified) {
			setResult(Activity.RESULT_CANCELED, returnIntent);
		} else {
			returnIntent.putExtra("friend", mFriend);
			setResult(Activity.RESULT_OK, returnIntent);
		}
		finish();
	}

	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_friend);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back, null));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void setupFormListeners() {
		mNameView = (TextView) findViewById(R.id.name_field);
		mEmailView = (TextView) findViewById(R.id.email_field);
		mDateView = (TextView) findViewById(R.id.date_field);

		mNameView.setText(mFriend.getFirstname() + " " + mFriend.getLastname());
		mEmailView.setText(mFriend.getEmail().equals("") ? "No email" : mFriend.getEmail());
		mDateView.setText(DateUtils.toFullString(mFriend.getBirthday()));

		mIconView = (CircularImageView) findViewById(R.id.icon_field);
	}
}
