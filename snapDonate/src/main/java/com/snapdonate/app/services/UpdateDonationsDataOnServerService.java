package com.snapdonate.app.services;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

public class UpdateDonationsDataOnServerService extends
		AsyncTask<String, Void, String> {
	private OnDataSendCompletion mListener = null;
	private ArrayList<NameValuePair> mParams;

	public UpdateDonationsDataOnServerService(ArrayList<NameValuePair> param) {
		this.mParams = param;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mListener.onStartSendingDataToServer();
	}

	@Override
	protected String doInBackground(String... param) {
		String response = "";
		try {
			String url;
			if (SUtils.IS_LIVE) {
				url = URLManager.UPDATE_DONATION_URL_LIVE;
			} else {
				url = URLManager.UPDATE_DONATION_URL_LOTIV;
			}
			response = NetworkManager.getHTTPPostRequestResponse(
					url, mParams);
		} catch (Exception e) {
			// TODO: handle exception
		}
		SLog.showLog("Result SendDonationsDataOnServer : " + response);
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mListener.onCompleteSendingDataToServer(result);
	}

	public void setDataSendCompletion(OnDataSendCompletion listener) {
		this.mListener = listener;
	}

}
