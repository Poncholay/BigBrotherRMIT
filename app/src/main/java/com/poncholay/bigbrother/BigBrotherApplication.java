package com.poncholay.bigbrother;

import android.app.Application;
import android.content.Intent;

import com.orm.SugarContext;
import com.poncholay.bigbrother.services.MeetingSuggestionsService;

import pl.aprilapps.easyphotopicker.EasyImage;

public class BigBrotherApplication extends Application {

	@Override
	public void onCreate() {
		SugarContext.init(this);
		EasyImage.configuration(this)
				.setAllowMultiplePickInGallery(false)
				.setCopyPickedImagesToPublicGalleryAppFolder(false)
				.setCopyTakenPhotosToPublicGalleryAppFolder(false);
		super.onCreate();

		Intent intent = new Intent(this, MeetingSuggestionsService.class);
		stopService(intent);
		startService(intent);
	}

	@Override
	public void onTerminate() {
		SugarContext.terminate();
		super.onTerminate();
	}
}
