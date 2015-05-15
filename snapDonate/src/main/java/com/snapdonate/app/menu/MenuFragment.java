package com.snapdonate.app.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snapdonate.app.R;
import com.snapdonate.app.fragments.AboutFragment;
import com.snapdonate.app.fragments.PrivacyFragment;
import com.snapdonate.app.fragments.TermsAndConditionsFragment;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;

public class MenuFragment extends Fragment implements OnClickListener {
	private TextView mAboutSnapdonateTextView;
	private TextView mPrivacyTextView;
	private TextView mTermsConditionsTextView;
	private LinearLayout mAboutLinearLayout;
	private LinearLayout mPrivacyLinearLayout;
	private LinearLayout mTermsConditionLinearLayout;

	public MenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		mAboutSnapdonateTextView = (TextView) view
				.findViewById(R.id.aboutSnapeDonateTextView);
		mAboutSnapdonateTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mPrivacyTextView = (TextView) view.findViewById(R.id.privacyTextView);
		mPrivacyTextView.setTypeface(SFonts.getInstance(getActivity()).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTermsConditionsTextView = (TextView) view
				.findViewById(R.id.termsAndConditionsTextView);
		mTermsConditionsTextView.setTypeface(SFonts.getInstance(getActivity())
				.getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mAboutLinearLayout = (LinearLayout) view
				.findViewById(R.id.aboutSnapDonateLinearLayout);
		mPrivacyLinearLayout = (LinearLayout) view
				.findViewById(R.id.privacyLinearLayout);
		mTermsConditionLinearLayout = (LinearLayout) view
				.findViewById(R.id.termsAndConditionLinearLayout);
		mAboutLinearLayout.setOnClickListener(this);
		mPrivacyLinearLayout.setOnClickListener(this);
		mTermsConditionLinearLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.aboutSnapDonateLinearLayout:
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.tabMainContentLinearLayout,
							new AboutFragment(), SUtils.ABOUT_FRAGMENT)
					.addToBackStack(SUtils.ABOUT_FRAGMENT).commit();
			break;
		case R.id.privacyLinearLayout:
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.tabMainContentLinearLayout,
							new PrivacyFragment(), SUtils.PRIVACY_FRAGMENT)
					.addToBackStack(SUtils.PRIVACY_FRAGMENT).commit();
			break;
		case R.id.termsAndConditionLinearLayout:
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.tabMainContentLinearLayout,
							new TermsAndConditionsFragment(),
							SUtils.TERMS_AAND_CONDITIONS_FRAGMENT)
					.addToBackStack(SUtils.TERMS_AAND_CONDITIONS_FRAGMENT)
					.commit();
			break;
		}
	}
}
