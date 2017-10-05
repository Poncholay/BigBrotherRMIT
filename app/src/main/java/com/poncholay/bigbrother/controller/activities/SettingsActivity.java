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
import android.widget.SeekBar;
import android.widget.TextView;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.view.StepSlider;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

	private EditText snooze;
	private TextView snoozeDetailLabel;

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;

	private int[] steps = {0, 5, 10, 60, 240, 1440};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setupToolbar();

		sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		snooze = (EditText) findViewById(R.id.snoozeEditText);
		snoozeDetailLabel = (TextView) findViewById(R.id.snoozeDetailsLabel);

		StepSlider intervalStepSlider = (StepSlider) findViewById(R.id.intervalStepSlider);
		TextView intervalDetailLabel = (TextView) findViewById(R.id.intervalDetailsLabel);
		setupStepSlider(intervalStepSlider, intervalDetailLabel, "suggestionInterval");

		StepSlider delayStepSlider = (StepSlider) findViewById(R.id.delayStepSlider);
		TextView delayDetailLabel = (TextView) findViewById(R.id.delayDetailsLabel);
		setupStepSlider(delayStepSlider, delayDetailLabel, "reminderDelay");

		snooze.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				snoozeDetailLabel.setText(String.format(Locale.getDefault(), "%smin", s));

				if (sharedPref.getInt("reminderDelay", 5) != 0) {
					float delay = 0;
					try {
						delay = Float.parseFloat(s.toString());
					} catch (RuntimeException ignored) {
					}

					editor.putFloat("snoozeDelay", delay);
					editor.apply();
				}
			}
		});

		refreshSnooze();
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

	private void refreshSnooze() {
		int reminderDelay = sharedPref.getInt("reminderDelay", 5);
		float snoozeDelay = reminderDelay == 0 ? 0 : sharedPref.getFloat("snoozeDelay", 5.0f);

		snoozeDetailLabel.setText(String.format(Locale.getDefault(), "%fmin", snoozeDelay));
		snooze.setEnabled(reminderDelay != 0);
		snooze.setText(String.valueOf(snoozeDelay));
	}

	private void setupStepSlider(final StepSlider stepSlider, final TextView label, final String key) {
		stepSlider.setSteps(steps);
		stepSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				stepSlider.snapToPosition();
				label.setText(stepSlider.getStepLabel(progress));

				editor.putInt(key, stepSlider.getSliderProgress());
				editor.apply();

				refreshSnooze();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		int value = sharedPref.getInt(key, 5);
		for (int i = 0; i < steps.length; i++) {
			if (steps[i] == value) {
				stepSlider.setSliderProgress(i);
			}
		}
	}
}