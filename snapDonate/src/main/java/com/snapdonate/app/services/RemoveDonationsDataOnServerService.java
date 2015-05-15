package com.snapdonate.app.services;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

public class RemoveDonationsDataOnServerService extends
		AsyncTask<String, Void, String> {
	private OnDataSendCompletion mDataSendCompletionListener;
	private ArrayList<NameValuePair> mParams;

	public RemoveDonationsDataOnServerService(ArrayList<NameValuePair> params) {
		this.mParams = params;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDataSendCompletionListener.onStartSendingDataToServer();
	}

	@Override
	protected String doInBackground(String... param) {
		String response = null;
		try {
			String url;
			if (SUtils.IS_LIVE) {
				url = URLManager.DELETE_DONATION_URL_LIVE;
			} else {
				url = URLManager.DELETE_DONATION_URL_LOTIV;
			}
			response = NetworkManager.getHTTPPostRequestResponse(
					url, mParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SLog.showLog("Result SendDonationsDataOnServer : " + response);
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mDataSendCompletionListener.onCompleteSendingDataToServer(result);
	}

	public void setDataSendCompletion(OnDataSendCompletion listener) {
		this.mDataSendCompletionListener = listener;
	}
}
