package com.poncholay.bigbrother.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.poncholay.bigbrother.R;
import com.poncholay.bigbrother.model.StepSlider;

public class SettingsActivity extends AppCompatActivity {

	private SwitchCompat aSwitch;

	private StepSlider intervalStepSlider;
	private TextView intervalDetailLabel;

	private StepSlider delayStepSlider;
	private TextView delayDetailLabel;
	private int[] steps = {0, 5, 10, 60, 240, 1440};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setupToolbar();

		aSwitch = (SwitchCompat) findViewById(R.id.switchInterval);

		intervalStepSlider = (StepSlider) findViewById(R.id.intervalStepSlider);
		intervalDetailLabel = (TextView) findViewById(R.id.intervalDetailsLabel);
		setupStepSlider(intervalStepSlider, intervalDetailLabel, "suggestionInterval");

		delayStepSlider = (StepSlider) findViewById(R.id.delayStepSlider);
		delayDetailLabel = (TextView) findViewById(R.id.delayDetailsLabel);
		setupStepSlider(delayStepSlider, delayDetailLabel, "reminderDelay");
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

	private void setupStepSlider(final StepSlider stepSlider, final TextView label, final String key) {
		stepSlider.setSteps(steps);
		stepSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				stepSlider.snapToPosition();
				label.setText(stepSlider.getStepLabel(progress));

				SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(key, stepSlider.getSliderProgress());
				editor.apply();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		int value = getPreferences(Context.MODE_PRIVATE).getInt(key, 5);
		for (int i = 0; i < steps.length; i++) {
			if (steps[i] == value) {
				stepSlider.setSliderProgress(i);
			}
		}
	}
}