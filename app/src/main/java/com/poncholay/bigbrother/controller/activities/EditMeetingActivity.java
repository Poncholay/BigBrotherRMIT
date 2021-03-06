package com.poncholay.bigbrother.controller.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.controller.adapters.FriendRecyclerViewAdapter;
import com.poncholay.bigbrother.controller.adapters.IconRecyclerViewAdapter;
import com.poncholay.bigbrother.controller.adapters.SelectFriendRecyclerViewAdapter;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.model.FriendDistance;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.BundleUtils;
import com.poncholay.bigbrother.utils.DateUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditMeetingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private Meeting mMeeting;
	private int mMode;

	private EditText mTitleView;
	private TextView mDateStartView;
	private TextView mDateEndView;
	private TextView mNbFriends;
	private RecyclerView mFriendsView;
	private TextView mLocalisationView;

	private IconRecyclerViewAdapter mIconAdapter;
	private SelectFriendRecyclerViewAdapter mAdapter;

	private boolean mEditStart = true;
	private Date mDateStart = null;
	private Date mDateEnd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_meeting);

		mMode = BundleUtils.retrieveMode(savedInstanceState, getIntent().getExtras());
		if (mMode != Constants.EDIT_MEETING && mMode != Constants.NEW_MEETING) {
			finish();
			return;
		}

		mMeeting = (Meeting) BundleUtils.retrieveParcelable(savedInstanceState, getIntent().getExtras(), "meeting");
		if (mMeeting == null) {
			mMeeting = new Meeting();
		}

		FriendDistance friendDistance = (FriendDistance) BundleUtils.retrieveParcelable(savedInstanceState, getIntent().getExtras(), "friendDistance");
		if (friendDistance != null) {
			Friend friend = friendDistance.getFriend();
			mMeeting.getFriends().add(friend);
			mMeeting.setTitle("Meeting with " + friend.getFirstname() + " " + friend.getLastname());
			mMeeting.setLatitude(friendDistance.getMidPoint().latitude);
			mMeeting.setLongitude(friendDistance.getMidPoint().longitude);
			mMeeting.setStart(new Date(new Date().getTime() + (long) friendDistance.getUserDuration() * 1000 ));
			mMeeting.setEnd(new Date(mMeeting.getStart().getTime() + 1000 * 60 * 60));
		}

		setupToolbar();
		setupFormListeners();
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		if (mEditStart) {
			mDateStartView.setText(DateUtils.toLiteString(calendar.getTime()));
			mDateStart = calendar.getTime();
		} else {
			mDateEndView.setText(DateUtils.toLiteString(calendar.getTime()));
			mDateEnd = calendar.getTime();
		}
		TimePickerDialog dpd = TimePickerDialog.newInstance(this, 0, 0, 0, true);
		dpd.setVersion(TimePickerDialog.Version.VERSION_2);
		dpd.show(getFragmentManager(), "Timepickerdialog");

	}

	@Override
	public void onTimeSet(TimePickerDialog view, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mEditStart ? mDateStart : mDateEnd);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		if (mEditStart) {
			mDateStartView.setText(DateUtils.toLiteStringTime(calendar.getTime()));
			mDateStart = calendar.getTime();
		} else {
			mDateEndView.setText(DateUtils.toLiteStringTime(calendar.getTime()));
			mDateEnd = calendar.getTime();
		}
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(Activity.RESULT_CANCELED, returnIntent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.bar_edit_friend_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_validate) {
			validate();
		}

		return super.onOptionsItemSelected(item);
	}

	private void validate() {
		String title = mTitleView.getText().toString();

		if (title.trim().equals("")) {
			Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_empty_title), Snackbar.LENGTH_SHORT).show();
			return;
		}
		if (mDateStart.after(mDateEnd)) {
			Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_invalid_date), Snackbar.LENGTH_SHORT).show();
			return;
		}
		// Allow 10 minutes of leeway
		if (mDateStart.before(new Date(new Date().getTime() - 60 * 1000))) {
			Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_invalid_date_now), Snackbar.LENGTH_SHORT).show();
			return;
		}

		mMeeting.setTitle(title);
		mMeeting.setStart(mDateStart);
		mMeeting.setEnd(mDateEnd);

		Intent returnIntent = new Intent();
		returnIntent.putExtra("meeting", mMeeting);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_friend);
		if (mMode == Constants.NEW_FRIEND) {
			toolbar.setTitle("New Meeting");
		} else {
			toolbar.setTitle(mMeeting.getTitle());
		}
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
		mTitleView = (EditText) findViewById(R.id.title_field);
		mDateStartView = (TextView) findViewById(R.id.start_date_field);
		mDateEndView =  (TextView) findViewById(R.id.end_date_field);
		mNbFriends = (TextView) findViewById(R.id.meeting_people_number);
		mFriendsView = (RecyclerView) findViewById(R.id.meeting_peoplelist);
		mLocalisationView = (TextView) findViewById(R.id.meeting_localisation);

		mTitleView.setText(mMeeting.getTitle());
		mDateStartView.setText(DateUtils.toLiteStringTime(mMeeting.getStart()));
		mDateEndView.setText(DateUtils.toLiteStringTime(mMeeting.getEnd()));
		DecimalFormat decimalFormat = new DecimalFormat("#.0###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		mLocalisationView.setText(decimalFormat.format(mMeeting.getLatitude()) + " " + decimalFormat.format(mMeeting.getLongitude()));

		mDateStart = mMeeting.getStart();
		mDateEnd = mMeeting.getEnd();

		mNbFriends.setText(String.format(Locale.US, "%d", mMeeting.getFriends().size()));

		mIconAdapter = new IconRecyclerViewAdapter(mMeeting.getFriends(), Constants.HORIZONTAL);
		mFriendsView.setAdapter(mIconAdapter);
		LinearLayoutManager llm = new LinearLayoutManager(mFriendsView.getContext());
		llm.setOrientation(LinearLayoutManager.HORIZONTAL);
		mFriendsView.setLayoutManager(llm);

		final DatePickerDialog.OnDateSetListener callback = this;

		Button startDatePicker = (Button) findViewById(R.id.start_date_button);
		startDatePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditStart = true;
				displayDatePicker(callback, mDateStart);
			}
		});
		Button endDatePicker = (Button) findViewById(R.id.end_date_button);
		endDatePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditStart = false;
				displayDatePicker(callback, mDateEnd);
			}
		});

		final Button addPeople = (Button) findViewById(R.id.add_people_button);
		addPeople.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSelectableFriendList();
			}
		});

		final Button pickLocation = (Button) findViewById(R.id.pick_location_button);
		pickLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayPlacePicker();
			}
		});
	}

	private void showSelectableFriendList() {
		View selectListView = getLayoutInflater().inflate(R.layout.fragment_select_list, null);
		final RecyclerView recyclerView = (RecyclerView) selectListView.findViewById(R.id.friendlist_recycler_view);

		final Context context = recyclerView.getContext();
		mAdapter = new SelectFriendRecyclerViewAdapter(Friend.getAll(Friend.class), mMeeting.getFriends(), new FriendRecyclerViewAdapter.OnFriendClickListener() {
			@Override
			public void onFriendClick(Friend friend, View v) {
				if (!mAdapter.getSelected().contains(friend)) {
					mAdapter.getSelected().add(friend);
					v.setBackgroundColor(getResources().getColor(R.color.fillColor, null));
				} else {
					mAdapter.getSelected().remove(friend);
					v.setBackgroundColor(getResources().getColor(R.color.colorSplash, null));
				}
			}

			@Override
			public boolean onFriendLongClick(Friend friend, View v) {
				return false;
			}
		});
		recyclerView.setAdapter(mAdapter);
		LinearLayoutManager llm = new LinearLayoutManager(context);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new DividerItemDecoration(context, llm.getOrientation()));

		new MaterialDialog.Builder(this)
				.title(R.string.select_friends)
				.customView(selectListView, true)
				.negativeText(R.string.action_cancel)
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

					}
				})
				.positiveText(R.string.action_validate)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						mMeeting.setFriends(mAdapter.getSelected());
						mIconAdapter.setList(mAdapter.getSelected());
						mNbFriends.setText(String.format(Locale.US, "%d", mAdapter.getSelected().size()));
					}
				})
				.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_PLACE_PICKER) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(this, data);
				place.getName();
				mMeeting.setLatitude(place.getLatLng().latitude);
				mMeeting.setLongitude(place.getLatLng().longitude);
				mMeeting.setLocationName(place.getName().toString());
				DecimalFormat decimalFormat = new DecimalFormat("#.0###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
				mLocalisationView.setText(decimalFormat.format(mMeeting.getLatitude()) + " " + decimalFormat.format(mMeeting.getLongitude()));
			}
		}
	}

	private void displayPlacePicker() {
		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

		try {
			startActivityForResult(builder.build(this), Constants.REQUEST_PLACE_PICKER);
		} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
			Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_not_available), Snackbar.LENGTH_SHORT).show();
		}
	}

	private void displayDatePicker(DatePickerDialog.OnDateSetListener callback, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DatePickerDialog dpd = DatePickerDialog.newInstance(
				callback,
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
		);
		dpd.setMinDate(Calendar.getInstance());
		dpd.setVersion(DatePickerDialog.Version.VERSION_2);
		dpd.show(getFragmentManager(), "Datepickerdialog");
	}
}
