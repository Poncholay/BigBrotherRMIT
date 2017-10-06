package com.poncholay.bigbrother.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.Friend;
import com.poncholay.bigbrother.utils.BundleUtils;
import com.poncholay.bigbrother.utils.CopyHelper;
import com.poncholay.bigbrother.utils.DateUtils;
import com.poncholay.bigbrother.utils.IconUtils;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.poncholay.bigbrother.utils.CopyHelper.MOVE;
import static com.poncholay.bigbrother.utils.CopyHelper.getFile;

public class EditFriendActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

	private Friend mFriend;
	private int mMode;

	private EditText mFirstnameView;
	private EditText mLastnameView;
	private EditText mEmailView;
	private TextView mDateView;
	private CircularImageView mIconView;

	private Date mDate = null;
	private boolean mIconChanged = false;
	private String mIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_friend);

		mMode = BundleUtils.retrieveMode(savedInstanceState, getIntent().getExtras());
		if (mMode != Constants.EDIT_FRIEND && mMode != Constants.NEW_FRIEND) {
			finish();
			return;
		}

		mFriend = (Friend) BundleUtils.retrieveParcelable(savedInstanceState, getIntent().getExtras(), "friend");
		if (mFriend == null) {
			mFriend = new Friend();
		}

		setupToolbar();
		setupFormListeners();
		IconUtils.setupIcon(mIconView, mFriend, this);
	}

	@Override
	public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		mDateView.setText(DateUtils.toLiteString(calendar.getTime()));
		mDate = calendar.getTime();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		final Activity activity = this;

		EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
			@Override
			public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
				Snackbar.make(activity.findViewById(android.R.id.content), "An error occured", Snackbar.LENGTH_SHORT).show();
			}

			@Override
			public void onImagesPicked(@NonNull List<File> imagesFiles, EasyImage.ImageSource source, int type) {
				File file = imagesFiles.get(0);
				if (file != null) {
					Picasso.with(activity)
							.load(file)
							.into(mIconView);
					mIcon = file.getPath();
					mIconChanged = true;
				}
			}

			@Override
			public void onCanceled(EasyImage.ImageSource source, int type) {
				if (source == EasyImage.ImageSource.CAMERA) {
					File photoFile = EasyImage.lastlyTakenButCanceledPhoto(activity);
					if (photoFile != null) {
						photoFile.delete();
					}
				}
			}
		});
	}

	private boolean checkEmpty(String firstname) {
		if (firstname.trim().equals("")) {
			Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_empty_name), Snackbar.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	private boolean checkDuplicates(String firstname, String lastname) {
		if (mMode != Constants.EDIT_FRIEND || !firstname.equals(mFriend.getFirstname()) || !lastname.equals(mFriend.getLastname())) {
			List<Friend> boards = Friend.getAll(Friend.class, "firstname = '" + firstname + "' AND lastname = '" + lastname + "'");
			if (boards.size() != 0) {
				Snackbar.make(findViewById(android.R.id.content), String.format(Locale.US, getString(R.string.error_name_already_used), firstname + " " + lastname), Snackbar.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}

	private void moveOldIcon(String oldDirName, String newDirName) {
		if (mMode == Constants.EDIT_FRIEND && !oldDirName.equals(newDirName)) {
			File oldIcon = getFile(this, oldDirName, Constants.ICON);
			if (oldIcon != null) {
				if (!mIconChanged) {
					copyIcon(oldIcon);
				}
				File oldDir = oldIcon.getParentFile();
				oldIcon.delete();
				oldDir.delete();
			}
		}
	}

	private void moveNewIcon() {
		if (mIconChanged) {
			File newIcon = copyIcon(mIcon);
			mFriend.setHasIcon(newIcon != null);
			if (newIcon != null) {
				Picasso.with(this).invalidate(newIcon);
			}
		}
	}

	private void validate() {
		String firstname = mFirstnameView.getText().toString();
		String lastname = mLastnameView.getText().toString();
		String email = mEmailView.getText().toString();

		if (checkEmpty(firstname) || checkDuplicates(firstname, lastname)) {
			return;
		}

		String oldDirName = mFriend.getFirstname() + " " + mFriend.getLastname();

		mFriend.setFirstname(firstname);
		mFriend.setLastname(lastname);
		mFriend.setEmail(email);
		mFriend.setBirthday(mDate);

		String newDirName = firstname + " " + lastname;

		moveOldIcon(oldDirName, newDirName);
		moveNewIcon();

		Intent returnIntent = new Intent();
		returnIntent.putExtra("friend", mFriend);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	private File copyIcon(File icon) {
		CopyHelper copyHelper = new CopyHelper(this, mFriend.getFirstname() + " " + mFriend.getLastname(), "icon.jpg");
		return copyHelper.storeFile(icon, MOVE);
	}

	private File copyIcon(String icon) {
		CopyHelper copyHelper = new CopyHelper(this, mFriend.getFirstname() + " " + mFriend.getLastname(), "icon.jpg");
		return copyHelper.storeFile(icon, MOVE);
	}

	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_friend);
		if (mMode == Constants.NEW_FRIEND) {
			toolbar.setTitle("New Friend");
		} else {
			toolbar.setTitle(mFriend.getFirstname() + " " + mFriend.getLastname());
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
		final Activity activity = this;

		mFirstnameView = (EditText) findViewById(R.id.first_name_field);
		mLastnameView = (EditText) findViewById(R.id.last_name_field);
		mEmailView = (EditText) findViewById(R.id.email_field);
		mDateView = (TextView) findViewById(R.id.date_field);

		mFirstnameView.setText(mFriend.getFirstname());
		mLastnameView.setText(mFriend.getLastname());
		mEmailView.setText(mFriend.getEmail());
		mDateView.setText(DateUtils.toLiteString(mFriend.getBirthday()));

		mDate = mFriend.getBirthday();

		Button datePicker = (Button) findViewById(R.id.date_button);
		final DatePickerDialog.OnDateSetListener callback = this;
		datePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(mDate);
				DatePickerDialog dpd = DatePickerDialog.newInstance(
						callback,
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH)
				);
				dpd.setVersion(DatePickerDialog.Version.VERSION_2);
				dpd.show(getFragmentManager(), "Datepickerdialog");
			}
		});

		mIconView = (CircularImageView) findViewById(R.id.icon_field);
		mIconView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EasyImage.openChooserWithGallery(activity, "Pick an icon", 0);
			}
		});
	}
}
