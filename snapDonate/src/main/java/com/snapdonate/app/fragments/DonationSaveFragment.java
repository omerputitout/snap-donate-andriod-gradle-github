package com.snapdonate.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.snapdonate.app.R;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.interfaces.OnDialogButtonClickListener;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.services.SendDonationsOnServerService;
import com.snapdonate.app.utils.AppDialog;
import com.snapdonate.app.utils.DeviceInfo;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DonationSaveFragment extends Fragment implements
		OnDialogButtonClickListener, OnClickListener {
	private static final int REQUEST_CODE_SAVE_FOR_LATER_DONATION = 1;
	private static final int REQUEST_CODE_WHEN_INTERNET_IS_AVAILABLE = 2;
	private static final int REQUEST_CODE__WHEN_INTERNET_IS_NOT_AVAILABLE = 3;
	private final int mMainContentId;
	private Donation mDonation;
	private Bitmap mCharityLogo;
	private TextView mSnappedTextView;
	private ImageView mCharityLogoImageView;
	private TextView mCharityNameTextView;
	private TextView mChosenToDonateTextView;
	private TextView mDonationAmountTextView;
	private Button mDonateNowButton;
	private Button mSaveForLaterButton;

	public DonationSaveFragment(Donation donation, int mainContent) {
		mDonation = donation;
		this.mMainContentId = mainContent;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.donation_save, container, false);
		init(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setInitialData();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.donateNowButton:
			mDonation.setStatus(SUtils.CHARITY_IS_TODO);
			if (NetworkManager.isNetworkAvailable(getActivity())) {
				showDonateNowAlertWhenInternetIsAvailable();
			} else {
				showDonateNowAlertWhenInternetIsNotAvailable();
			}
			break;
		case R.id.saveForLaterButton:
			onSaveForLaterButtonClick();
			break;
		}
	}

	private void init(View view) {
		mSnappedTextView = (TextView) view
				.findViewById(R.id.youHaveSnappedTextView);
		mChosenToDonateTextView = (TextView) view
				.findViewById(R.id.youHaveChosenToDonateTextView);
		mDonationAmountTextView = (TextView) view
				.findViewById(R.id.donationAmountTextView);
		mDonateNowButton = (Button) view.findViewById(R.id.donateNowButton);
		mSaveForLaterButton = (Button) view
				.findViewById(R.id.saveForLaterButton);
		mDonateNowButton.setOnClickListener(this);
		mSaveForLaterButton.setOnClickListener(this);
		mCharityLogoImageView = (ImageView) view
				.findViewById(R.id.charityLogoImageView);
		mCharityNameTextView = (TextView) view
				.findViewById(R.id.charityNameTextView);
		// TODO: setting fonts
		mSnappedTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mChosenToDonateTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mDonationAmountTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mDonateNowButton.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mSaveForLaterButton.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mCharityNameTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
	}

	private void setInitialData() {
		// TODO: setting charity logo and name
		mCharityLogo = mDonation.getCharity().getLogo();
		if (mCharityLogo != null) {
			// TODO: present in assets
			mCharityLogoImageView.setImageBitmap(mCharityLogo);
			mCharityNameTextView.setVisibility(View.GONE);
		} else {
			mCharityLogoImageView.setVisibility(View.GONE);
			mCharityNameTextView.setVisibility(View.VISIBLE);
			mSnappedTextView.setText(getResources().getString(
					R.string.you_have_chosen));
			mCharityNameTextView.setText(mDonation.getCharity().getName());
		}
		// TODO: setting amount
		mDonationAmountTextView.setText(SUtils.DONATION_CURRENCY_SIGN
				+ mDonation.getAmount());
	}

	private void onSaveForLaterButtonClick() {
		// TODO: send flurry stat
		setTimeForFlurryEvent(SUtils.SAVE_FOR_LATER_EVENT_NAME_FLURRY);
		mDonation.setStatus(SUtils.CHARITY_IS_TODO);
		mDonation.setTimeStamp(String.valueOf(java.lang.System.currentTimeMillis()));
		if (NetworkManager.isNetworkAvailable(getActivity())) {
			// TODO: make json array and send to server
			ArrayList<NameValuePair> donationParams = new ArrayList<NameValuePair>();
			donationParams = donationModelInfo(getActivity(), mDonation);
			SendDonationsOnServerService service = new SendDonationsOnServerService(
					donationParams);
			service.setDataSendCompletion(new OnDataSendCompletion() {
				@Override
				public void onStartSendingDataToServer() {
				}

				@Override
				public void onCompleteSendingDataToServer(String result) {
					if (result.equals("")) {
						// TODO: not sycned with server due to any issue
						mDonation.setIsSynced(SUtils.IS_NOT_SYNCED);
					} else {
						mDonation.setIsSynced(SUtils.IS_SYNCED);
						mDonation.setSavedServerId(result);
					}
					DataBaseManager.saveDonationForLater(getActivity(),
							mDonation);

					showSaveForLaterDonationAlert();
				}
			});
			service.execute();
		} else {
			// TODO: not syced on server due to no internet
			mDonation.setIsSynced(SUtils.IS_NOT_SYNCED);
			DataBaseManager.saveDonationForLater(getActivity(), mDonation);
			showSaveForLaterDonationAlert();
		}
	}

	private void showSaveForLaterDonationAlert() {
		String message = getResources().getString(R.string.we_saved_donation)
				+ " " + SUtils.DONATION_CURRENCY_SIGN + mDonation.getAmount()
				+ " to " + mDonation.getCharity().getName();
		AppDialog.showAlert(getActivity(), message,
				getResources().getString(R.string.cancel), getResources()
						.getString(R.string.ontinue), this,
				REQUEST_CODE_SAVE_FOR_LATER_DONATION);
	}

	private void showDonateNowAlertWhenInternetIsAvailable() {
		String message;
		if (mDonation.getCharity().getId()
				.equals(SUtils.BOWELL_CANCER_UK_CHARITY_ID)) {
			message = getResources().getString(
					R.string.wonderful_people_at_virginmoney);
		} else {
			message = getResources().getString(
					R.string.wonderful_people_at_justgiving);
		}
		AppDialog.showAlert(getActivity(), message,
				getResources().getString(R.string.cancel), getResources()
						.getString(R.string.ontinue), this,
				REQUEST_CODE_WHEN_INTERNET_IS_AVAILABLE);
	}

	private void showDonateNowAlertWhenInternetIsNotAvailable() {
		AppDialog.showAlert(
				getActivity(),
				getResources().getString(
						R.string.you_re_offline_making_donation),
				getResources().getString(R.string.cancel), getResources()
						.getString(R.string.save), this,
				REQUEST_CODE__WHEN_INTERNET_IS_NOT_AVAILABLE);
	}

	private void onContinueDonate() {
		// TODO: send flurry stat
		setTimeForFlurryEvent(SUtils.DONATE_NOW_EVENT_NAME_FLURRY);
		mDonation.setStatus(SUtils.CHARITY_IS_PENDING);
		mDonation.setTimeStamp(String.valueOf(java.lang.System.currentTimeMillis()));
		ArrayList<NameValuePair> donationParams = new ArrayList<NameValuePair>();
		donationParams = donationModelInfo(getActivity(), mDonation);
		SendDonationsOnServerService sendDonationsDataOnServerService = new SendDonationsOnServerService(
				donationParams);
		sendDonationsDataOnServerService
				.setDataSendCompletion(sendDonationsDataOnServerServiceListner);
		sendDonationsDataOnServerService.execute();
	}

	private void onSaveDonation() {
		// TODO: send flurry stat
		setTimeForFlurryEvent(SUtils.SAVE_FOR_LATER_EVENT_NAME_FLURRY);
		mDonation.setStatus(SUtils.CHARITY_IS_TODO);
		mDonation.setTimeStamp(String.valueOf(java.lang.System.currentTimeMillis()));
		// TODO: not syced on server due to no internet
		mDonation.setIsSynced(SUtils.IS_NOT_SYNCED);
		DataBaseManager.saveDonationForLater(getActivity(), mDonation);
		showSaveForLaterDonationAlert();
	}

	@Override
	public void onDialogPositiveButtonClick(int requestCode) {
	}

	@Override
	public void onDialogNegativeButtonClick(int requestCode) {
		switch (requestCode) {
		case REQUEST_CODE_SAVE_FOR_LATER_DONATION:
			getActivity().sendBroadcast(
					new Intent(SUtils.BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB));
			break;
		case REQUEST_CODE_WHEN_INTERNET_IS_AVAILABLE:
			onContinueDonate();
			break;
		case REQUEST_CODE__WHEN_INTERNET_IS_NOT_AVAILABLE:
			onSaveDonation();
			break;
		}
	}

	OnDataSendCompletion sendDonationsDataOnServerServiceListner = new OnDataSendCompletion() {
		@Override
		public void onStartSendingDataToServer() {
		}

		@Override
		public void onCompleteSendingDataToServer(String result) {
			if (result.equals("")) {
				// TODO: not sycned with server due to any issue
				mDonation.setIsSynced(SUtils.IS_NOT_SYNCED);
			} else {
				mDonation.setIsSynced(SUtils.IS_SYNCED);
				mDonation.setSavedServerId(result);
			}
			DataBaseManager.saveDonation(getActivity(), mDonation);
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(mMainContentId,
							new DonateNowFragment(mDonation, mMainContentId),
							SUtils.DONATE_NOW_FRAGMENT)
					.addToBackStack(SUtils.DONATE_NOW_FRAGMENT).commit();
		}
	};

	private void setTimeForFlurryEvent(String flurryEvent) {
		Calendar calender = Calendar.getInstance();
		int hours = calender.get(Calendar.HOUR);
		int minutes = calender.get(Calendar.MINUTE);
		int date = calender.get(Calendar.DATE);
		int month = calender.get(Calendar.MONTH);
		int year = calender.get(Calendar.YEAR);
		HashMap<String, String> statsHashMap = new HashMap<String, String>();
		statsHashMap.put(SUtils.DONATION_AMOUNT_TAG, mDonation.getAmount());
		statsHashMap.put(SUtils.TIME_TAG, hours + ":" + minutes);
		statsHashMap.put(SUtils.DATE_TAG, date + "/" + (month + 1) + "/"
				+ (year));
		FlurryAgent.logEvent(flurryEvent, statsHashMap);
	}

	private ArrayList<NameValuePair> donationModelInfo(Context context,
			Donation donation) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_DEVICE_ID,
				DeviceInfo.getInstance(context).getDeviceUUID()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_ID, donation
				.getCharity().getId()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_NAME,
				donation.getCharity().getName()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_AMOUNT,
				donation.getAmount()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_DATE,
				donation.getTimeStamp()));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_OPTION,
				(donation.getSatus().equals(SUtils.CHARITY_IS_TODO) || donation
						.getSatus().equals(SUtils.CHARITY_IS_PENDING)) ? "0"
						: "1"));
		params.add(new BasicNameValuePair(SUtils.DONATION_CHARITY_PLATFORM_KEY,
				SUtils.DONATION_CHARITY_PLATFORM_VALUE));
		return params;
	}
}
