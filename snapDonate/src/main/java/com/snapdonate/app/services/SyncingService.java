package com.snapdonate.app.services;

import android.app.IntentService;
import android.content.Intent;

import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.utils.DeviceInfo;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class SyncingService extends IntentService {

	public SyncingService() {
		super(null);
	}

	public SyncingService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Donation donation = (Donation) intent
				.getSerializableExtra(SUtils.DONATION_MODEL);
		String response = "";
		ArrayList<NameValuePair> donationParams = new ArrayList<NameValuePair>();
		donationParams = donationData(donation);
		try {
			String url;
			if (SUtils.IS_LIVE) {
				url = URLManager.ADD_DONATION_URL_LIVE;
			} else {
				url = URLManager.ADD_DONATION_URL_LOTIV;
			}
			response = NetworkManager.getHTTPPostRequestResponse(
					url, donationParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!response.equals("")) {
			DataBaseManager.updateDataAsSycned(getApplicationContext(),
					donation.getAutoId(), response);
		}
	}

	private ArrayList<NameValuePair> donationData(Donation donation) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SUtils.DEVICE_ID, DeviceInfo.getInstance(
				this).getDeviceUUID()));
		params.add(new BasicNameValuePair("charity_id", donation
				.getCharity().getId()));
		params.add(new BasicNameValuePair("charity_name", donation
				.getCharity().getName()));
		params.add(new BasicNameValuePair("charity_amount", donation
				.getAmount()));
		params.add(new BasicNameValuePair("charity_date", donation
				.getTimeStamp()));
		params.add(new BasicNameValuePair("option", (donation.getSatus()
				.equals(SUtils.CHARITY_IS_TODO) || donation.getSatus()
				.equals(SUtils.CHARITY_IS_PENDING)) ? "0" : "1"));
		params.add(new BasicNameValuePair("plateform", "Android"));
		params.add(new BasicNameValuePair("charity_donation_reference_id",
				donation.getDonationReferenceId()));
		return params;
	}
}
