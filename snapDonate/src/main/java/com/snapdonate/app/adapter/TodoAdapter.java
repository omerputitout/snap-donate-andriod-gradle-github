package com.snapdonate.app.adapter;

import java.sql.Date;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.fragments.DonateNowFragment;
import com.snapdonate.app.interfaces.OnDataSendCompletion;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.services.SendDonationsOnServerService;
import com.snapdonate.app.utils.AppDialog;
import com.snapdonate.app.utils.DeviceInfo;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

public class TodoAdapter extends ArrayAdapter<Donation> implements
		OnClickListener {
	private ArrayList<Donation> mTodoArrayList;
	private FragmentActivity mContext;
	private int mTodoRowHeight;
	private LayoutParams mTodoRowLayoutParams;
	private LayoutInflater mInflater;

	public TodoAdapter(FragmentActivity context,
			ArrayList<Donation> dataArrayList) {
		super(context, R.layout.todo_list_item_layout, dataArrayList);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mTodoArrayList = dataArrayList;
		this.mContext = context;
		int screenHeight = SUtils.getWindowHeight(context);
		mTodoRowHeight = (int) (screenHeight * 0.109);
		mTodoRowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				mTodoRowHeight);
		mTodoRowHeight = (int) (screenHeight * 0.109);
	}

	@Override
	public int getCount() {
		if (mTodoArrayList != null) {
			return mTodoArrayList.size();
		}
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder = null;
		if (row == null) {
			holder = new Holder();
			row = mInflater.inflate(R.layout.todo_list_item_layout, parent,
					false);
			holder.charityLogoImageView = (ImageView) row
					.findViewById(R.id.todoCharityLogoImageView);
			holder.charityAmountTextView = (TextView) row
					.findViewById(R.id.todoAmountTextView);
			holder.charityDateTextView = (TextView) row
					.findViewById(R.id.todoDateTextView);
			holder.finishNowButton = (Button) row
					.findViewById(R.id.todoFinishNowButton);
			holder.charityNameTextView = (TextView) row
					.findViewById(R.id.todoCharityNameTextView);
			setHolderViewsFont(holder, parent);
			row.setLayoutParams(mTodoRowLayoutParams);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}
		setHolderViewsDetail(holder, position);
		setHolderViewsClicklistners(holder, position);
		return row;
	}

	private void setHolderViewsFont(Holder holder, ViewGroup parent) {
		holder.charityAmountTextView.setTypeface(SFonts.getInstance(mContext)
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		holder.charityNameTextView.setTypeface(SFonts.getInstance(mContext)
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		holder.charityDateTextView.setTypeface(SFonts.getInstance(mContext)
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		holder.finishNowButton.setTypeface(SFonts.getInstance(mContext)
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
	}

	private void setHolderViewsDetail(Holder holder, int position) {
		holder.charityAmountTextView.setText(SUtils.DONATION_CURRENCY_SIGN
				+ mTodoArrayList.get(position).getAmount());
		Date donationDate = new Date(Long.parseLong(mTodoArrayList
				.get(position).getTimeStamp()));
		holder.charityDateTextView.setText(SUtils.getFormatedStringFromDate(
				donationDate, SUtils.SIMPLE_DATE_FORMAT));
		Charity charity = mTodoArrayList.get(position).getCharity();
		Bitmap charityBitmap = charity.getLogo();
		if (charityBitmap != null) {
			// present in assets
			holder.charityLogoImageView.setVisibility(View.VISIBLE);
			holder.charityNameTextView.setVisibility(View.GONE);
			holder.charityLogoImageView.setImageBitmap(charityBitmap);
		} else {
			holder.charityLogoImageView.setVisibility(View.GONE);
			holder.charityNameTextView.setVisibility(View.VISIBLE);
			holder.charityNameTextView.setText(charity.getName());
		}
	}

	private void setHolderViewsClicklistners(Holder holder, int position) {
		holder.finishNowButton.setTag(position);
		holder.finishNowButton.setOnClickListener(this);
	}

	private void onFinishButtonClick(int position) {
		if (NetworkManager.isNetworkAvailable(mContext)) {
			final Donation donation = mTodoArrayList.get(position);
			if (donation.getSavedServerId() == null) {
				// TODO: it means that this record is not synced at server so
				// sync it
				sendTodoDataOnServer(donation);
			} else {
				replaceFragmentTodo(donation);
			}
		} else {
			showDialog();
		}
	}

	private void sendTodoDataOnServer(final Donation donation) {
		ArrayList<NameValuePair> donationParams = new ArrayList<NameValuePair>();
		donationParams = donationModelInfo(getContext(), donation);
		SendDonationsOnServerService sendDonationDataOnServerService = new SendDonationsOnServerService(
				donationParams);
		sendDonationDataOnServerService
				.setDataSendCompletion(new OnDataSendCompletion() {
					@Override
					public void onStartSendingDataToServer() {
					}

					@Override
					public void onCompleteSendingDataToServer(String result) {
						if (result.equals("")) {
							// TODO: not sycned with server due to any
							// issue
							donation.setIsSynced(SUtils.IS_NOT_SYNCED);
						} else {
							donation.setIsSynced(SUtils.IS_SYNCED);
							donation.setSavedServerId(result);
						}
						updateDataBaseAndReplaceFragment(donation);
					}
				});
		sendDonationDataOnServerService.execute();
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

	private void showDialog() {
		AppDialog.showAlert(mContext,
				mContext.getString(R.string.no_internet_donation),
				mContext.getString(R.string.cancel));
	}

	private void replaceFragmentTodo(Donation donation) {
		// TODO: data synced so no need to add again on server
		mContext.getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.todoMainContentLinearLayout,
						new DonateNowFragment(donation,
								R.id.todoMainContentLinearLayout),
						SUtils.DONATE_NOW_FRAGMENT)
				.addToBackStack(SUtils.DONATE_NOW_FRAGMENT).commit();
	}

	private void updateDataBaseAndReplaceFragment(Donation donation) {
		DataBaseManager.updateDataAsSycned(mContext, donation.getAutoId(),
				donation.getSavedServerId());
		replaceFragmentTodo(donation);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.todoFinishNowButton:
			onFinishButtonClick((Integer) view.getTag());
			break;
		}
	}

	public static class Holder {
		ImageView charityLogoImageView;
		TextView charityAmountTextView;
		TextView charityDateTextView;
		Button finishNowButton;
		TextView charityNameTextView;
	}
}
