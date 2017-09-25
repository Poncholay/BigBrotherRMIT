package com.poncholay.bigbrother;

import android.app.Application;
import android.util.Log;

import com.poncholay.bigbrother.utils.WebService;
import com.poncholay.bigbrother.utils.database.DatabaseHelper;

import pl.aprilapps.easyphotopicker.EasyImage;

import static com.poncholay.bigbrother.utils.WebService.TYPE_GET;

public class BigBrotherApplication extends Application {

	@Override
	public void onCreate() {

		new WebService(this, "https://google.com", TYPE_GET, null, new WebService.WebServiceCallBack(this) {
			@Override
			public void onSuccess(String response) {
				Log.e("Yolo", "success");
			}
		}).execute();

		DatabaseHelper.DatabaseContext.context = this;
		EasyImage.configuration(this)
				.setAllowMultiplePickInGallery(false)
				.setCopyPickedImagesToPublicGalleryAppFolder(false)
				.setCopyTakenPhotosToPublicGalleryAppFolder(false);
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
