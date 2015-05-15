package com.snapdonate.app.services;

import android.app.IntentService;
import android.content.Intent;

import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class RemoveTodoService extends IntentService {

	public RemoveTodoService() {
		super(null);
	}

	public RemoveTodoService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String mSavedServerID = (String) intent
				.getStringExtra(SUtils.SAVED_SERVER_ID);
		String response = "";
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SUtils.ID, mSavedServerID));
		try {
			String url;
			if (SUtils.IS_LIVE) {
				url = URLManager.DELETE_DONATION_URL_LIVE;
			} else {
				url = URLManager.DELETE_DONATION_URL_LOTIV;
			}
			response = NetworkManager.getHTTPPostRequestResponse(
					url, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SLog.showLog("Result SendDonationsDataOnServer : " + response);
		if (response.equals(SUtils.SERVER_RESPONSE_TRUE)) {
			DataBaseManager.removeFromTODO(this, mSavedServerID);
		}
	}
}
