package com.snapdonate.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.services.UpdateDonationsDataOnServerService;
import com.snapdonate.app.utils.DeviceInfo;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ThanksGivingFragment extends Fragment implements OnClickListener {
	private Donation mDonation;
	private TextView mThankyouTextView;
	private TextView mReferenceNumberTextView;
	private TextView mShareDonationTextView;
	private TextView mAddFavouriteTextView;
	private TextView mCharityNameTextView;
	private TextView mAddToFavouriteTextView;
	private Button mSnapAgainButton;

	public ThanksGivingFragment(Donation donation) {
		mDonation = donation;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.thanks_giving, container, false);
		init(v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setInitialData();
		updateCharityTodoHistoryList();
		UpdateServer();
	}

	private void init(View view) {
		mThankyouTextView = (TextView) view
				.findViewById(R.id.thanksYouDonationTextView);
		mReferenceNumberTextView = (TextView) view
				.findViewById(R.id.refrenceDonationTextView);
		mShareDonationTextView = (TextView) view
				.findViewById(R.id.shareTextView);
		mAddFavouriteTextView = (TextView) view
				.findViewById(R.id.addCharityFutureUseTextView);
		mAddToFavouriteTextView = (TextView) view
				.findViewById(R.id.addFavTextView);
		mSnapAgainButton = (Button) view.findViewById(R.id.snapeAgainButton);
		mSnapAgainButton.setOnClickListener(this);
		mCharityNameTextView = (TextView) view
				.findViewById(R.id.charityNameTextView);
		mThankyouTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mReferenceNumberTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mShareDonationTextView.setTypeface(SFonts
				.getInstance(getActivity()).getFont(
						SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mAddFavouriteTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mAddToFavouriteTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mSnapAgainButton.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mCharityNameTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
	}

	private void UpdateServer() {
		if (NetworkManager.isNetworkAvailable(getActivity())) {
			mDonation.setStatus(SUtils.CHARITY_IS_HISTORY);
			mDonation.setTimeStamp(String.valueOf(java.lang.System.currentTimeMillis()));
			ArrayList<NameValuePair> donationData = new ArrayList<NameValuePair>();
			donationData = donationInfo(getActivity(), mDonation);
			UpdateDonationsDataOnServerService updateDonationsDataOnServerService = new UpdateDonationsDataOnServerService(
					donationData);
			updateDonationsDataOnServerService
					.setDataSendCompletion(new OnDataSendCompletion() {
						@Override
						public void onStartSendingDataToServer() {
						}

						@Override
						public void onCompleteSendingDataToServer(String result) {
						}
					});
			updateDonationsDataOnServerService.execute();
		}
	}

	private void updateCharityTodoHistoryList() {
		// TODO: first save on local to make flow
		mDonation.setIsSynced(SUtils.IS_NOT_SYNCED);
		// TODO: remove this charity from database and update Todo list data
		DataBaseManager.updateDonationAtTodoHistoryAndServer(getActivity(),
				mDonation);
	}

	private void setInitialData() {
		if (mDonation.getDonationReferenceId() != null) {
			mReferenceNumberTextView
					.setText(getString(R.string.your_reference_number) + " "
							+ mDonation.getDonationReferenceId());
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.snapeAgainButton:
			getActivity()
					.sendBroadcast(
							new Intent(
									SUtils.BROADCASTRECEIVER_ACTION_FINISH_MAIN_SCREEN));
			break;

		}
	}

	private ArrayList<NameValuePair> donationInfo(Context context,
			Donation donation) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_DEVICE_ID,
				DeviceInfo.getInstance(context).getDeviceUUID()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_ID,
				donation.getCharity().getId()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_NAME,
				donation.getCharity().getName()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_AMOUNT,
				donation.getAmount()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_DATE,
				donation.getTimeStamp()));
		params.add(new BasicNameValuePair(
				SUtils.DONATION_CHARITY_OPTION,
				(donation.getSatus().equals(SUtils.CHARITY_IS_TODO) || donation
						.getSatus().equals(SUtils.CHARITY_IS_PENDING)) ? "0"
						: "1"));
		params.add(new BasicNameValuePair(
				SUtils.DONATION_CHARITY_PLATFORM_KEY,
				SUtils.DONATION_CHARITY_PLATFORM_VALUE));
		return params;

	}
}
