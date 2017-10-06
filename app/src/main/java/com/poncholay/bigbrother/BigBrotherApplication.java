package com.poncholay.bigbrother;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.poncholay.bigbrother.controller.receivers.NetworkReceiver;
import com.poncholay.bigbrother.services.MeetingSuggestionsService;
import com.poncholay.bigbrother.utils.database.DatabaseHelper;

import pl.aprilapps.easyphotopicker.EasyImage;

public class BigBrotherApplication extends Application {

	@Override
	public void onCreate() {
		DatabaseHelper.DatabaseContext.context = this;
		EasyImage.configuration(this)
				.setAllowMultiplePickInGallery(false)
				.setCopyPickedImagesToPublicGalleryAppFolder(false)
				.setCopyTakenPhotosToPublicGalleryAppFolder(false);
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		NetworkReceiver.update(networkInfo != null && networkInfo.isConnected());
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
