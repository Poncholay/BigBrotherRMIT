package com.poncholay.bigbrother.model;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

public class AnchoredFloatingActionButton {

	private final FloatingActionButton anchor;
	private final Context context;
	private final List<FloatingActionButton> fabs;
	private boolean fabStatus;

	public AnchoredFloatingActionButton(Context context, FloatingActionButton anchor) {
		this.anchor = anchor;
		this.context = context;
		this.fabStatus = false;
		this.fabs = new ArrayList<>();
	}

	public void addChild(FloatingActionButton child) {
		fabs.add(child);
	}

	public void setup() {
		anchor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggle();
			}
		});
	}

	public void toggle() {
		if (!fabStatus) {
			animateFab(0, 1, true, 1);
		} else {
			animateFab(1, 0, false, -1);
		}
	}

	private void animateFab(int fromAlpha, int toAlpha, boolean status, int multiplicator) {
		Animation alpha = new AlphaAnimation(fromAlpha, toAlpha);

		int i = 0;
		for (FloatingActionButton fab : fabs) {
			float height = multiplicator * (float)(anchor.getHeight() + fab.getHeight() * i + 0.15 * fab.getHeight() * (i + 1));
			Animation translate = new TranslateAnimation(0, 0, height, 0);
			translate.setInterpolator(context, android.R.anim.linear_interpolator);

			AnimationSet slide = new AnimationSet(true);
			slide.setInterpolator(context, android.R.anim.linear_interpolator);
			slide.setDuration(300);
			slide.addAnimation(alpha);
			slide.addAnimation(translate);

			CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
			layoutParams.bottomMargin += height;
			fab.setLayoutParams(layoutParams);
			fab.startAnimation(slide);
			fab.setClickable(status);
			fab.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
			i++;
		}
		fabStatus = status;
	}

	public boolean getStatus() {
		return this.fabStatus;
	}
}
