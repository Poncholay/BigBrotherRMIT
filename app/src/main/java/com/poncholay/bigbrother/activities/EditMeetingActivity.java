package com.poncholay.bigbrother.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Meeting;
import com.poncholay.bigbrother.utils.BundleUtils;
import com.poncholay.bigbrother.utils.DateUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class EditMeetingActivity extends AppCompatActivity
		implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private Meeting mMeeting;
	private int mMode;

	private EditText mTitleView;
	private TextView mDateStartView;
	private TextView mDateEndView;

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

		setupToolbar();
		setupFormListeners();
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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

		mTitleView.setText(mMeeting.getTitle());
		mDateStartView.setText(DateUtils.toLiteString(mMeeting.getStart()));
		mDateEndView.setText(DateUtils.toLiteString(mMeeting.getEnd()));

		mDateStart = mMeeting.getStart();
		mDateEnd = mMeeting.getEnd();

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
	}

	private void showSelectableFriendList() {
		new MaterialDialog.Builder(this)
				.title(R.string.select_friends)
//				.customView(R.layout.select_list, true)
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

					}
				})
//				.build() //TODO : remove
				.show();
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
		dpd.setVersion(DatePickerDialog.Version.VERSION_2);
		dpd.show(getFragmentManager(), "Datepickerdialog");
	}
}
