package com.poncholay.bigbrother.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

public class StepSlider extends AppCompatSeekBar {
	private int[] steps = {};

	public StepSlider(Context context) {
		super(context);
		init();
	}

	public StepSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public StepSlider(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setSteps(steps);
	}

	public void setSteps(int[] steps) {
		this.steps = steps;
		this.setMax((100 * steps.length) - 1);
	}

	public String getStepLabel(int position) {
		if (steps[position / 100] == 0) {
			return String.format("%5s", "Off");
		}
		if (steps[position / 100] > 60) {
			return String.format("%5s", String.format("%dh", steps[position / 100] / 60));
		}
		return String.format("%5s", String.format("%dmin", steps[position / 100]));
	}

	public int getSliderProgress() {
		int pos = getProgress() / 100;
		return (steps[pos]);
	}

	public void setSliderProgress(int pos) {
		if (pos == (steps.length - 1)) {
			setProgress(this.getMax());
		} else if (pos == 0) {
			setProgress(0);
		} else {
			int point = ((pos * 100) + ((pos + 1) * 100)) / 2;
			setProgress(point);
		}
	}

	public void snapToPosition() {
		int pos = getProgress() / 100;
		setSliderProgress(pos);
	}
}
