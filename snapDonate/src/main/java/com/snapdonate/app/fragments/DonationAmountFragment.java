package com.snapdonate.app.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

import java.io.IOException;

public class DonationAmountFragment extends Fragment implements OnClickListener {
	private static final String LOGOS = "Logos";
	private static final String PNG = ".png";
	private final int mMainContentID;
	private Charity mCharity;
	private Donation mDonation;
	private Bitmap mCharityBitmap;
	private TextView mSnappedTextView;
	private TextView mPickHowMuchTextView;
	private TextView mFourPoundTextView;
	private TextView mTwoPoundTextView;
	private TextView mThreePoundTextView;
	private TextView mFivePoundTextView;
	private TextView mTenPoundTextView;
	private TextView mFifteenPoundTextView;
	private TextView mTwentyPoundTextView;
	private TextView mTweentyFivePoundTextView;
	private TextView mFiftyPoundTextView;
	private ImageView mCharityLogoImageView;
	private TextView mCharityNameTextView;

	public DonationAmountFragment(Charity charity,
			int mainContentID) {
		mCharity = charity;
		this.mMainContentID = mainContentID;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.donation_amount, container, false);
		init(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setCharityLogo();
	}

	@Override
	public void onClick(View view) {
		goToDonateSaveScreen(Integer.parseInt((String) view.getTag()));
	}

	public void init(View view) {
		mSnappedTextView = (TextView) view
				.findViewById(R.id.youHaveSnappedTextView);
		mPickHowMuchTextView = (TextView) view
				.findViewById(R.id.pickHowDonateTextView);
		mFourPoundTextView = (TextView) view
				.findViewById(R.id.fourPoundDonationTextView);
		mTwoPoundTextView = (TextView) view
				.findViewById(R.id.twoPoundDonationTextView);
		mThreePoundTextView = (TextView) view
				.findViewById(R.id.threePoundDonationTextView);
		mFivePoundTextView = (TextView) view
				.findViewById(R.id.fivePoundDonationTextView);
		mTenPoundTextView = (TextView) view
				.findViewById(R.id.tenPoundDonationTextView);
		mFifteenPoundTextView = (TextView) view
				.findViewById(R.id.fifteenPoundDonationTextView);
		mTwentyPoundTextView = (TextView) view
				.findViewById(R.id.tweentyPoundDonationTextView);
		mTweentyFivePoundTextView = (TextView) view
				.findViewById(R.id.twentyFivePoundDonationTextView);
		mFiftyPoundTextView = (TextView) view
				.findViewById(R.id.fiftyPoundDonationTextView);
		mCharityLogoImageView = (ImageView) view
				.findViewById(R.id.charityLogoImageView);
		mCharityNameTextView = (TextView) view
				.findViewById(R.id.charityNameTextView);
		settingFonts();
		mFourPoundTextView.setOnClickListener(this);
		mTwoPoundTextView.setOnClickListener(this);
		mThreePoundTextView.setOnClickListener(this);
		mFivePoundTextView.setOnClickListener(this);
		mTenPoundTextView.setOnClickListener(this);
		mFifteenPoundTextView.setOnClickListener(this);
		mTwentyPoundTextView.setOnClickListener(this);
		mTweentyFivePoundTextView.setOnClickListener(this);
		mFiftyPoundTextView.setOnClickListener(this);
	}

	private void settingFonts() {
		mSnappedTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mPickHowMuchTextView.setTypeface(SFonts
				.getInstance(getActivity()).getFont(
						SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mCharityNameTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mFourPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mTwoPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mThreePoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mFivePoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mTenPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mFifteenPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mTwentyPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mTweentyFivePoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
		mFiftyPoundTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_LIGHT));
	}

	private void setCharityLogo() {
		String imageName = mCharity.getVuforiaRecognizedName();
		imageName = imageName.replace("/", "_");
		mCharityBitmap = null;
		try {
			String[] files = getActivity().getAssets().list(LOGOS);
			for (int index = 0; index < files.length; index++) {
				if (files[index].contains(imageName)) {
					mCharityBitmap = BitmapFactory
							.decodeStream(getActivity().getAssets().open(
									LOGOS + "/" + files[index]));
					if (files[index].equalsIgnoreCase(imageName + PNG)) {
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (mCharityBitmap != null) {
			// present in assets
			mCharityLogoImageView.setImageBitmap(mCharityBitmap);
			mCharityNameTextView.setVisibility(View.GONE);
		} else {
			mCharityLogoImageView.setVisibility(View.GONE);
			mCharityNameTextView.setVisibility(View.VISIBLE);
			mSnappedTextView.setText(getResources().getString(
					R.string.you_have_chosen));
			mCharityNameTextView.setText(mCharity.getName());
		}
	}

	private void goToDonateSaveScreen(int amount) {
		mDonation = new Donation();
		if (mCharityBitmap != null) {
			// TODO: present in assets
			mCharity.setLogo(mCharityBitmap);
		}
		mDonation.setCharity(mCharity);
		mDonation.setAmount(String.valueOf(amount));
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(
						mMainContentID,
						new DonationSaveFragment(mDonation, mMainContentID),
						SUtils.DONATION_AMOUNT_FRAGMENT)
				.addToBackStack(SUtils.DONATION_SAVE_FRAGMENT).commit();
	}
}
