package com.poncholay.bigbrother;

import android.app.Application;
import android.content.res.Resources;

import com.orm.SugarContext;

import pl.aprilapps.easyphotopicker.EasyImage;

public class BigBrotherApplication extends Application {

	private static BigBrotherApplication instance;

	public static Resources getPrivateResources() {
		return instance.getResources();
	}

	@Override
	public void onCreate() {
		SugarContext.init(this);
		EasyImage.configuration(this)
				.setAllowMultiplePickInGallery(false)
				.setCopyPickedImagesToPublicGalleryAppFolder(false)
				.setCopyTakenPhotosToPublicGalleryAppFolder(false);
		instance = this;
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		SugarContext.terminate();
		super.onTerminate();
	}
}
