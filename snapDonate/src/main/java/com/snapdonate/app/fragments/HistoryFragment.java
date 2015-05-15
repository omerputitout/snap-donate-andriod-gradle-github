package com.snapdonate.app.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.adapter.HistoryAdapter;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

public class HistoryFragment extends Fragment implements OnClickListener {
	private static final String LOGOS = "Logos";
	private static final String PNG = ".png";
	private TextView mTodoTextView;
	private TextView mHistoryTextView;
	private TextView mShortDescriptionTextView;
	private TextView mTotalDonationLabelTextView;
	private TextView mTotalDonationSumTextView;
	private ListView mListView;

	public HistoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.history, container, false);
		init(view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setViewDetails();
	}

	private void init(View view) {
		mTodoTextView = (TextView) view.findViewById(R.id.todoTextView);
		mHistoryTextView = (TextView) view.findViewById(R.id.historyTextView);
		mShortDescriptionTextView = (TextView) view
				.findViewById(R.id.descriptionTextView);
		mTotalDonationLabelTextView = (TextView) view
				.findViewById(R.id.totalDonationTextView);
		mTotalDonationSumTextView = (TextView) view
				.findViewById(R.id.donationSumTextView);
		mListView = (ListView) view.findViewById(R.id.historyListView);
		setFonts();
		mTodoTextView.setOnClickListener(this);
	}

	private void setFonts() {
		mTodoTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mHistoryTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mShortDescriptionTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTotalDonationLabelTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTotalDonationSumTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
	}

	public void setViewDetails() {
		ArrayList<Donation> historyDonationArrayList = getHistoryListData();
		HistoryAdapter listAdpater = new HistoryAdapter(
				historyDonationArrayList);
		mListView.setAdapter(listAdpater);
		int sumOfDonations = 0;
		for (Donation iterableElement : historyDonationArrayList) {
			sumOfDonations = sumOfDonations
					+ Integer.parseInt(iterableElement.getAmount());
		}
		mTotalDonationSumTextView.setText(SUtils.DONATION_CURRENCY_SIGN
				+ sumOfDonations);
	}

	public ArrayList<Donation> getHistoryListData() {
		ArrayList<Donation> historyDonationArrayList = DataBaseManager
				.getDonationsHistoryList(getActivity());
		Collections.reverse(historyDonationArrayList);
		for (String charityNames : getCharityNamesHashSet(historyDonationArrayList)) {
			String imageName = charityNames;
			imageName = imageName.replace("/", "_");
			Bitmap charityBitmap = null;
			try {
				String[] files = getActivity().getAssets().list(LOGOS);
				for (int index = 0; index < files.length; index++) {
					if (files[index].contains(imageName)) {
						charityBitmap = BitmapFactory
								.decodeStream(getActivity().getAssets().open(
										LOGOS+"/" + files[index]));
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
			if (charityBitmap != null) {
				// TODO: present in assets
				for (Donation donation : historyDonationArrayList) {
					if (donation.getCharity().getVuforiaRecognizedName()
							.equals(charityNames)) {
						donation.getCharity().setLogo(charityBitmap);
					}
				}
			}
		}
		return historyDonationArrayList;
	}

	private HashSet<String> getCharityNamesHashSet(
			ArrayList<Donation> historyDonationArrayList) {
		HashSet<String> charityIdHashSet = new HashSet<String>();
		for (Donation donate : historyDonationArrayList) {
			charityIdHashSet.add(donate.getCharity()
					.getVuforiaRecognizedName());
		}
		return charityIdHashSet;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.todoTextView:
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.todoMainContentLinearLayout,
							new TodoFragment(), SUtils.TODO_FRAGMENT)
					.commit();
			break;
		}
	}
}
