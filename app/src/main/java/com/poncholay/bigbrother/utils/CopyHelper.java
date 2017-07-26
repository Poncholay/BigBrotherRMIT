package com.poncholay.bigbrother.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CopyHelper {

	private final Context mContext;
	private final String mBoardTitle;
	private final String mFileTitle;
	public static final int MOVE = 0;
	public static final int COPY = 1;

	public CopyHelper(Context context, String title, String fileTitle) {
		this.mContext = context;
		this.mBoardTitle = title;
		this.mFileTitle = fileTitle;
	}

	public File handleRecordAudio(int resultCode, Intent data) {
		return storeFile(resultCode, data, MOVE);
	}

	public File handleSelectFile(int resultCode, Intent data) {
		return storeFile(resultCode, data, COPY);
	}

	public File storeFile(int resultCode, final Intent data, final int type) {
		if (resultCode == 0 || data == null || data.getData() == null) {
			return null;
		}
		return storeFile(data, type);
	}

	public File storeFile(Intent data, int type) {
		String path = "";
		path = PathUtils.getPath(mContext, data.getData());
		if (path == null) {
			Toast.makeText(mContext, "Could not create file", Toast.LENGTH_SHORT).show();
			return null;
		}
		return storeFile(path, type);
	}

	public File storeFile(String path, int type) {
		File from = new File(path);
		return storeFile(from, type);
	}

	public File storeFile(File from, int type) {
		File to = getFile(mContext, mBoardTitle, mFileTitle);

		if (!from.exists() || to == null) {
			Toast.makeText(mContext, "Could not create file", Toast.LENGTH_SHORT).show();
			return null;
		}

		try {
			if (to.exists()) {
				to.delete();
			}
			if (type == MOVE) {
				FileUtils.moveFile(from, to);
			} else if (type == COPY) {
				FileUtils.copyFile(from, to);
			}
		} catch (IOException e) {
			Toast.makeText(mContext, "Could not create file", Toast.LENGTH_SHORT).show();
			if (type == MOVE) {
				from.delete();
			}
			return null;
		}

		Toast.makeText(mContext, "New file : " + to.getPath(), Toast.LENGTH_SHORT).show(); //TODO : remove

		return to;
	}

	static public File getFile(Context context, String boardTitle, String fileTitle) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File root = new File(context.getExternalFilesDir(null), boardTitle);
			if (!root.exists()) {
				if (!root.mkdirs()) {
					return null;
				}
			}
			return new File(root, fileTitle);
		}
		return null;
	}
}
