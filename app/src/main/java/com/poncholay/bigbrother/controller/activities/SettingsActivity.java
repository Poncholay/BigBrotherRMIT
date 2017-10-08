package com.poncholay.bigbrother.controller.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.poncholay.bigbrother.R;

import java.text.DecimalFormat;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private DecimalFormat format;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setupToolbar();

		sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		format = new DecimalFormat();
		format.setDecimalSeparatorAlwaysShown(false);

		EditText interval = (EditText) findViewById(R.id.intervalEditText);
		final TextView intervalDetailLabel = (TextView) findViewById(R.id.intervalDetailsLabel);

		EditText delay = (EditText) findViewById(R.id.delayEditText);
		final TextView delayDetailLabel = (TextView) findViewById(R.id.delayDetailsLabel);

		final EditText snooze = (EditText) findViewById(R.id.snoozeEditText);
		final TextView snoozeDetailLabel = (TextView) findViewById(R.id.snoozeDetailsLabel);

		interval.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				float delay = 0;
				try {
					delay = Float.parseFloat(s.toString());
				} catch (RuntimeException ignored) {
				}
				intervalDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(delay)));

				editor.putFloat("suggestionInterval", delay);
				editor.apply();
			}
		});
		float intervalValue = sharedPref.getFloat("suggestionInterval", 5.0f);
		intervalDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(intervalValue)));
		interval.setText(String.valueOf(intervalValue));


		delay.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				float delay = 0;
				try {
					delay = Float.parseFloat(s.toString());
				} catch (RuntimeException ignored) {
				}
				delayDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(delay)));

				editor.putFloat("reminderDelay", delay);
				editor.apply();

				refreshSnooze(snooze, snoozeDetailLabel);
			}
		});
		float reminderValue = sharedPref.getFloat("reminderDelay", 5.0f);
		delayDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(reminderValue)));
		delay.setText(String.valueOf(reminderValue));


		snooze.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				if (sharedPref.getFloat("reminderDelay", 5.0f) != 0) {
					float delay = 0;
					try {
						delay = Float.parseFloat(s.toString());
					} catch (RuntimeException ignored) {
					}
					snoozeDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(delay)));

					editor.putFloat("snoozeDelay", delay);
					editor.apply();
				}
			}
		});
		refreshSnooze(snooze, snoozeDetailLabel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.bar_settings_activity, menu);
		return true;
	}

	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
		toolbar.setTitle("Settings");
		setSupportActionBar(toolbar);
		toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_toolbar_back, null));
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void refreshSnooze(EditText snooze, TextView snoozeDetailLabel) {
		float reminderDelay = sharedPref.getFloat("reminderDelay", 5.0f);
		float snoozeDelay = reminderDelay == 0 ? 0 : sharedPref.getFloat("snoozeDelay", 5.0f);

		snoozeDetailLabel.setText(String.format(Locale.getDefault(), "%smin", format.format(snoozeDelay)));
		snooze.setEnabled(reminderDelay != 0);
		snooze.setText(String.valueOf(snoozeDelay));
	}
}