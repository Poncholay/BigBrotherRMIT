package com.poncholay.bigbrother;

import android.app.Application;

import com.orm.SugarContext;

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
	}

	@Override
	public void onTerminate() {
		SugarContext.terminate();
		super.onTerminate();
	}
}
