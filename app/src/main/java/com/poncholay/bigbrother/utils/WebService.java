package com.poncholay.bigbrother.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Poncholay on 25/09/17.
 */
public class WebService {

	private static final int ERROR_IO = 0;
	private static final int ERROR_TIMEOUT = 1;

	public static final int TYPE_GET = 1;
	public static final int TYPE_POST = 2;
	public static final int TYPE_PUT = 3;
	public static final int TYPE_PATCH = 4;
	public static final int TYPE_DELETE = 5;

	Context context;
	String url;
	String data;
	WebServiceCallBack call_back;
	int type = 1;


	public WebService(Context context, String url, int type, String data, WebServiceCallBack call_back) {
		this.context = context;
		this.url = url;
		this.type = type;
		this.data = data;
		this.call_back = call_back;
	}

	public void execute() {
		if (checkNetwork()) {
			new Request().execute(url, data);
		}
	}

	private boolean checkNetwork() {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			try {
				new AlertDialog.Builder(context)
						.setTitle("Connection error")
						.setMessage("Your Internet connection appears to be offline.")
						.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
			} catch (Exception e) {
				Toast.makeText(context, "Your Internet connection appears to be offline", Toast.LENGTH_LONG).show();
			}
			return false;
		}
	}

	private class Request extends AsyncTask<String, Integer, String[]> {

		private HttpURLConnection conn;
		private InputStream is;

		@Override
		protected String[] doInBackground(String... params) {
			URL url = null;
			try {
				url = new URL(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			String postData = null;
			if (params.length == 2) {
				postData = params[1];
			}

			if (url == null) {
				return null;
			}

			String[] resultStr = new String[2];
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(60000);
				conn.setReadTimeout(60000);
				switch (type) {
					case TYPE_GET:
						conn.setRequestMethod("GET");
						break;

					case TYPE_POST:
						conn.setRequestMethod("POST");
						break;

					case TYPE_PUT:
						conn.setRequestMethod("PUT");
						break;

					case TYPE_PATCH:
						conn.setRequestMethod("PATCH");
						break;

					case TYPE_DELETE:
						conn.setRequestMethod("DELETE");
						break;

					default:
						return null;
				}
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setDoInput(true);
				if (postData != null) {
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(postData);
					wr.flush();
					wr.close();
				}
				conn.connect();
				int responseCode = conn.getResponseCode();
				if (responseCode >= 300) {
					is = conn.getErrorStream();
				} else {
					is = conn.getInputStream();
				}
				resultStr[0] = String.valueOf(responseCode);
				resultStr[1] = getStringFromInputStream(is);
			} catch (SocketTimeoutException connect_exception) {
				connect_exception.printStackTrace();
				resultStr[0] = String.valueOf(ERROR_TIMEOUT);
				resultStr[1] = null;
			} catch (IOException io_exception) {
				io_exception.printStackTrace();
				resultStr[0] = String.valueOf(ERROR_IO);
				resultStr[1] = null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			for (String s : resultStr) {
				Log.e("RESULT", s);
			}

			return resultStr;
		}

		private String getStringFromInputStream(InputStream is) {
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return sb.toString();
		}
	}

	public static abstract class WebServiceCallBack {
		private Context context;

		public WebServiceCallBack() {
			this.context = null;
		}

		public WebServiceCallBack(Context context) {
			this.context = context;
		}

		public abstract void onSuccess(String response);

		public void onError(int code, String response) {
			if (context != null) {
				Toast.makeText(context, "An error happened, please try later.", Toast.LENGTH_SHORT).show();
			}
		}

		public void onCancel() {}
	}
}
