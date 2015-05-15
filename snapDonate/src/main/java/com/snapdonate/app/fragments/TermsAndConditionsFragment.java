package com.snapdonate.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.utils.SFonts;

public class TermsAndConditionsFragment extends Fragment {
	private TextView mTermsConditionsTextView;
	private TextView mTermsConditionsDescriptionTextView;

	public TermsAndConditionsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.terms_and_conditions, container,
				false);
		init(view);
		return view;
	}

	private void init(View view) {
		mTermsConditionsTextView = (TextView) view
				.findViewById(R.id.termsAndConditionsTextView);
		mTermsConditionsTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mTermsConditionsDescriptionTextView = (TextView) view
				.findViewById(R.id.termsAndConditionsDescriptionTextView);
		mTermsConditionsDescriptionTextView.setTypeface(SFonts.getInstance(
				getActivity()).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
	}
}
