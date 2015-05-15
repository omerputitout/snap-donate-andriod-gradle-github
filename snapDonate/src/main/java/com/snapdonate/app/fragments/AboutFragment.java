package com.snapdonate.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.utils.SFonts;

public class AboutFragment extends Fragment {
	private TextView mAboutTextView;
	private TextView mSnapDonateTextView;

	public AboutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		mAboutTextView = (TextView) view
				.findViewById(R.id.whatIsSnapDonateTextView);
		mAboutTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mSnapDonateTextView = (TextView) view
				.findViewById(R.id.whatIsSnapeDonateDescriptionTextView);
		mSnapDonateTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
	}
}
