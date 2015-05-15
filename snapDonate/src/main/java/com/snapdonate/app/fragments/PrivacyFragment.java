package com.snapdonate.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.utils.SFonts;

public class PrivacyFragment extends Fragment {
	private TextView mPrivacyTextView;
	private TextView mPrivacyDescriptionTextView;

	public PrivacyFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.privacy, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		mPrivacyTextView = (TextView) view.findViewById(R.id.privacyTextView);
		mPrivacyTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mPrivacyDescriptionTextView = (TextView) view
				.findViewById(R.id.privacyPolicyDescriptionTextView);
		mPrivacyDescriptionTextView.setTypeface(SFonts
				.getInstance(getActivity()).getFont(
						SFonts.FONT_VOLK_SWAGAN_SERIAL));
	}
}
