package com.poncholay.bigbrother.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.poncholay.bigbrother.Constants;
import com.poncholay.bigbrother.model.Friend;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.poncholay.bigbrother.utils.CopyHelper.getFile;

public class IconUtils {
	public static void setupIcon(CircularImageView view, Friend friend, Context context) {
		setupIcon(view, friend, context, true, 2, Color.WHITE);
	}

	public static void setupIconBig(CircularImageView view, Friend friend, Context context) {
		setupIcon(view, friend, context, false, 1, Color.BLACK);
	}

	private static void setupIcon(CircularImageView view, Friend friend, Context context, boolean shadow, int border, int borderColor) {
		view.setBorderWidth(border);
		view.setBorderColor(borderColor);
		if (shadow) {
			view.setShadowRadius(2);
		}

		if (friend.getHasIcon()) {
			File iconFile = getFile(context, friend.getFirstname() + " " + friend.getLastname(), Constants.ICON);
			if (iconFile != null) {
				Picasso.with(context)
						.load(iconFile)
						.into(view);
				return;
			}
		}

		view.setImageDrawable(TextDrawable.builder()
				.beginConfig()
				.height(100)
				.width(100)
				.fontSize(60)
				.textColor(Color.BLACK)
				.endConfig()
				.buildRect(friend.getFirstname().equals("") ? "?" : friend.getFirstname().substring(0, 1), Color.WHITE));
	}

	public static String getIconPath(Friend friend, Context context) {
		try {
			return getFile(context, friend.getFirstname() + " " + friend.getLastname(), Constants.ICON).getPath();
		} catch (Exception e) {
			return "";
		}
	}

	public static Drawable getIconTextDrawable(Friend friend) {
		return TextDrawable.builder()
				.beginConfig()
				.height(100)
				.width(100)
				.fontSize(60)
				.textColor(Color.BLACK)
				.endConfig()
				.buildRect(friend.getFirstname().equals("") ? "?" : friend.getFirstname().substring(0, 1), Color.WHITE);
	}
}
