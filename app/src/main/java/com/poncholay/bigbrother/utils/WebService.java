package com.poncholay.bigbrother.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.poncholay.bigbrother.controller.receivers.NetworkReceiver;

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

	private Context context;
	private String url;
	private String data;
	private WebServiceCallBack callback;
	private int type;


	public WebService(Context context, String url, int type, String data, WebServiceCallBack callback) {
		this.context = context;
		this.url = url;
		this.type = type;
		this.data = data;
		this.callback = callback;
	}

	public void execute() {
		if (NetworkReceiver.getInstance().isConnected()) {
			new Request().execute(url, data);
			return;
		}
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new Request().execute(url, data);
		} else {
			Toast.makeText(context, "Your internet connection appears to be offline", Toast.LENGTH_LONG).show();
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
				resultStr[0] = String.valueOf(ERROR_TIMEOUT);
				resultStr[1] = null;
			} catch (IOException io_exception) {
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
			return resultStr;
		}


		@Override
		protected void onPostExecute(final String[] s) {
			super.onPostExecute(s);

			if (Integer.parseInt(s[0]) == ERROR_IO) {
				onErrorExecute(ERROR_IO, null);
				return;
			}
			if (Integer.parseInt(s[0]) == ERROR_TIMEOUT) {
				onErrorExecute(ERROR_TIMEOUT, null);
				return;
			}
			callback.onSuccess(s[1]);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
			callback.onCancel();
		}

		protected void onErrorExecute(final int type, final String s) {
			callback.onError(type, s);
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

		public WebServiceCallBack(Context context) {
			this.context = context;
		}

		public abstract void onSuccess(String response);

		public void onError(int code, String response) {
			if (context != null) {
				Toast.makeText(context, "An error happened, please try again later", Toast.LENGTH_SHORT).show();

			}
		}

		public void onCancel() {}
	}
}
