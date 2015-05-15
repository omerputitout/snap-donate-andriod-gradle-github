package com.snapdonate.app.gcm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.snapdonate.app.utils.DeviceInfo;
import com.snapdonate.app.utils.Prefs;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

public class GcmManager {

	static GcmManager mGcmManager;
	/**
	 * Tag used on log messages.
	 */
	GoogleCloudMessaging mGcm;
	AtomicInteger mMsgId = new AtomicInteger();
	Context mContext;
	String mGcmRegistrationID;
	/**
	 * GCM Members
	 */
	private final String TAG = "GCM";

	public static GcmManager getInstance() {
		if (mGcmManager == null) {
			mGcmManager = new GcmManager();
		}
		return mGcmManager;
	}

	public void setUpGCM(Context context) {
		mContext = context;
		// TODO: Check device for Play Services APK.
		if (SUtils.checkPlayServices(context)) {
			// TODO: If this check succeeds, proceed with normal processing.
			// TODO: Otherwise, prompt user to get valid Play Services APK.
			mGcm = GoogleCloudMessaging.getInstance(context);
			mGcmRegistrationID = getRegistrationId(context);
			SLog.showLog("mRegid : " + mGcmRegistrationID);
			if (mGcmRegistrationID.isEmpty()) {
				registerInBackground();
			} else {
				sendRegistrationIdToBackend(mGcmRegistrationID);
			}
		} else {
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... params) {
						String message = "";
						try {
							if (mGcm == null) {
								mGcm = GoogleCloudMessaging
										.getInstance(mContext);
							}
							mGcmRegistrationID = mGcm.register(SUtils.SENDER_ID);
							message = "Device registered, registration ID="
									+ mGcmRegistrationID;
							// TODO: You should send the registration ID to your
							// TODO: server over HTTP,
							// TODO: so it can use GCM/HTTP or CCS to send
							// messages to
							// TODO: your app.
							// TODO: The request to your server should be
							// TODO: authenticated if your app
							// TODO: is using accounts.
							sendRegistrationIdToBackend(mGcmRegistrationID);
							// TODO: For this demo: we don't need to send it
							// because
							// TODO: the device
							// TODO: will send upstream messages to a server
							// that echo
							// TODO: back the
							// TODO: message using the 'from' address in the
							// message.
							// TODO: Persist the regID - no need to register
							// again.
							storeRegistrationId(mContext, mGcmRegistrationID);
						} catch (IOException ex) {
							message = "Error :" + ex.getMessage();
							// TODO: If there is an error, don't just keep
							// trying to
							// TODO: register.
							// TODO: Require the user to click a button again,
							// or
							// TODO: perform
							// TODO: exponential back-off.
						}
						return message;
					}

					@Override
					protected void onPostExecute(String message) {
						Log.i(TAG, message + "\n");
					}
				}.execute(null, null, null);
			}
		}).start();

	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		String registrationId = Prefs
				.getString(context, Prefs.KEY_GCM_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		return registrationId;
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(final String gcmRegistrationID) {
		// Your implementation here.
		new Thread(new Runnable() {
			@Override
			public void run() {
				new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... param) {
						String result = "";
						HttpClient httpclient = new DefaultHttpClient();
						String url;
						if (SUtils.IS_LIVE) {
							url = URLManager.ADD_GCM_REGISTRATION_URL_LIVE;
						} else {
							url = URLManager.ADD_GCM_REGISTRATION_URL_LOTIV;

						}
						HttpPost httppost = new HttpPost(url);
						// TODO: Request parameters and other properties.
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(SUtils.DEVICE_ID,
								DeviceInfo.getInstance(mContext)
										.getDeviceUUID()));
						params.add(new BasicNameValuePair(SUtils.GCM_REGID,
								gcmRegistrationID));
						try {
							httppost.setEntity(new UrlEncodedFormEntity(params,
									"UTF-8"));
							// TODO: Execute and get the response.
							HttpResponse response = httpclient
									.execute(httppost);
							HttpEntity entity = response.getEntity();
							if (entity != null) {
								try {
									// TODO: do something useful
									// TODO: EntityUtils to get the response
									// content
									result = EntityUtils.toString(entity);

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						SLog.showLog("Result : " + result);
						return result;
					}

					@Override
					protected void onPostExecute(String message) {
						if (message.equals("true")) {
							// TODO: key saved at server so donot send again
							storeRegistrationId(mContext, mGcmRegistrationID);
						}

					}
				}.execute(null, null, null);
			}
		}).start();
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regID
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regID) {
		Prefs.saveString(mContext, Prefs.KEY_GCM_ID, mGcmRegistrationID);
	}

	/**
	 * GCM Functions Ended
	 */

}
