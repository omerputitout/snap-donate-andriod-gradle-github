package com.snapdonate.app.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.snapdonate.app.R;
import com.snapdonate.app.fragments.DonationAmountFragment;
import com.snapdonate.app.fragments.SearchFragment;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.utils.SUtils;

public class SnapFragmentActivity extends FragmentActivity {
	private Charity mCharity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snap_fragment_activity);
		setScreenContent();
	}

	private void setScreenContent() {
		mCharity = (Charity) getIntent().getSerializableExtra(
				SUtils.CHARITY_MODEL);
		if (mCharity != null && mCharity.getId() != null
				&& mCharity.getMedium() != null && mCharity.getName() != null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.mainContentLinearLayout,
							new DonationAmountFragment(mCharity,
									R.id.mainContentLinearLayout),
							SUtils.DONATION_AMOUNT_FRAGMENT).commit();
		} else {
			// TODO: when direct coming from image targets to todo or faqs tab
			// TODO: then dont add this fragment.
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.mainContentLinearLayout, new SearchFragment(),
							SUtils.SEARCH_FRAGMENT).commit();
		}
	}
}
