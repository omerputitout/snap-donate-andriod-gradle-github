package com.snapdonate.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.snapdonate.app.R;
import com.snapdonate.app.fragments.DonationAmountFragment;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

import java.sql.Date;
import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter implements OnClickListener {
	private ArrayList<Donation> mDonationsArrayList;
	private int mDonationsRowHeight;
	private LayoutParams mDonationsRowLayoutParams;
	private LayoutInflater mInflater = null;

	public HistoryAdapter(ArrayList<Donation> data) {
		this.mDonationsArrayList = data;
	}

	@Override
	public int getCount() {
		if (mDonationsArrayList != null) {
			return mDonationsArrayList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return mDonationsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(mDonationsArrayList.get(position).getAutoId());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mInflater == null) {
			mInflater = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			setRowParams(parent.getContext());
		}
		View row = convertView;
		Holder holder = null;
		if (row == null) {
			holder = new Holder();
			row = mInflater.inflate(R.layout.history_list_item_layout, parent,
					false);
			holder.charityLogoImageView = (ImageView) row
					.findViewById(R.id.charityLogoTextView);
			holder.charityAmountTextView = (TextView) row
					.findViewById(R.id.amountTextView);
			holder.charityDateTextView = (TextView) row
					.findViewById(R.id.dateTextView);
			holder.donateAgainButton = (Button) row
					.findViewById(R.id.finishNowButton);
			holder.charityNameTextView = (TextView) row
					.findViewById(R.id.charityNameTextView);
			setHolderViewsFont(holder, parent);
			row.setLayoutParams(mDonationsRowLayoutParams);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}
		setHolderViewsDetail(holder, position);
		setHolderViewsClicklistners(holder, position);
		return row;
	}

	private void setHolderViewsFont(Holder holder, ViewGroup parent) {
		holder.charityAmountTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		holder.charityNameTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		holder.charityDateTextView.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		holder.donateAgainButton.setTypeface(SFonts.getInstance(
				parent.getContext()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
	}

	private void setHolderViewsDetail(Holder holder, int position) {
		holder.charityAmountTextView.setText(SUtils.DONATION_CURRENCY_SIGN
				+ mDonationsArrayList.get(position).getAmount());
		Date donationDate = new Date(Long.parseLong(mDonationsArrayList
				.get(position).getTimeStamp()));
		holder.charityDateTextView.setText(SUtils.getFormatedStringFromDate(
				donationDate, SUtils.SIMPLE_DATE_FORMAT));
		Charity charity = mDonationsArrayList.get(position).getCharity();
		Bitmap charityLogoBitmap = charity.getLogo();
		if (charityLogoBitmap != null) {
			// present in assets
			holder.charityLogoImageView.setImageBitmap(charityLogoBitmap);
			holder.charityNameTextView.setVisibility(View.GONE);
			holder.charityLogoImageView.setVisibility(View.VISIBLE);
		} else {
			holder.charityLogoImageView.setVisibility(View.GONE);
			holder.charityNameTextView.setVisibility(View.VISIBLE);
			holder.charityNameTextView.setText(charity.getName());
		}
	}

	private void setHolderViewsClicklistners(Holder holder, int position) {
		holder.donateAgainButton.setTag(position);
		holder.donateAgainButton.setOnClickListener(this);
	}

	private void setRowParams(Context context) {
		mDonationsRowHeight = (int) (SUtils.getWindowHeight(context) * 0.11);
		mDonationsRowLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				mDonationsRowHeight);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.finishNowButton:
			((FragmentActivity) view.getContext())
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(
							R.id.todoMainContentLinearLayout,
							new DonationAmountFragment(mDonationsArrayList.get(
									(Integer) view.getTag()).getCharity(),
									R.id.todoMainContentLinearLayout),
							SUtils.DONATION_AMOUNT_FRAGMENT)
					.addToBackStack(SUtils.DONATION_AMOUNT_FRAGMENT).commit();
			break;
		}
	}

	public static class Holder {
		ImageView charityLogoImageView;
		TextView charityAmountTextView;
		TextView charityDateTextView;
		Button donateAgainButton;
		TextView charityNameTextView;
	}
}
