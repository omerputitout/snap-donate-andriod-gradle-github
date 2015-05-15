package com.snapdonate.app.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.snapdonate.app.R;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.utils.URLManager;

public class DonateNowFragment extends Fragment {
	/**
	 * WEb Urls contants
	 */
	public final String AMOUNT = "?amount=";
	public final String REFERENCE = "&reference=SnapDonate";
	public final String EXIT_URL = "&exitUrl=";
	private final int mMainContentId;
	private boolean mIsOnceExecuted = false;
	private Donation mDonation;
	private String mDonationReference;
	private String mURL;
	private String mDonationAmount;
	private String mCharityId;
	private WebView mWebview;
	private ProgressBar mProgressBar;

	public DonateNowFragment(Donation donation, int MainContent) {
		mDonation = donation;
		mMainContentId = MainContent;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.donate_now, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		mWebview = (WebView) view.findViewById(R.id.donateNowWebview);
		mWebview.setWebViewClient(new MyBrowser());
		mProgressBar = (ProgressBar) view
				.findViewById(R.id.donateNowProgressbar);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadCharityUrlInWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadCharityUrlInWebView() {
		mCharityId = mDonation.getCharity().getId();
		String endPoint;
		if (mCharityId
				.equalsIgnoreCase(SUtils.BOWELL_CANCER_UK_CHARITY_ID)) {
			// TODO: virgin money flow handling
			if (SUtils.IS_LIVE) {
				endPoint = URLManager.VIRGIN_MONEY_FUNDRAISER_URL;
			} else {
				endPoint = URLManager.VIRGIN_MONEY_FUNDRAISER_URL_SAND_BOX;
			}
		} else {
			// TODO: just giving money flow handling
			mDonationAmount = mDonation.getAmount();
			if (SUtils.IS_LIVE) {
				endPoint = URLManager.JUST_GIVING_DONATION_URL;
			} else {
				endPoint = URLManager.JUST_GIVING_DONATION_URL_SAND_BOX;
			}
		}
		mURL = endPoint + mCharityId + AMOUNT + mDonationAmount
				+ REFERENCE + EXIT_URL + URLManager.EXIT_URL;
		mWebview.getSettings().setLoadsImagesAutomatically(true);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebview.loadUrl(mURL);
	}

	private void openThanksScreen() {
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(mMainContentId,
						new ThanksGivingFragment(mDonation),
						SUtils.THANKS_GIVING_FRAGMENT)
				.addToBackStack(SUtils.THANKS_GIVING_FRAGMENT).commit();
	}

	private class MyBrowser extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView webView, String url) {
			webView.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView webView, String url) {
			super.onPageFinished(webView, url);
			mProgressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
			super.onPageStarted(webView, url, bitmap);
			mProgressBar.setVisibility(View.VISIBLE);
			showThanksView(url);
		}

		private void showThanksView(String url) {
			if (!mIsOnceExecuted && url.contains(URLManager.JUST_GIVING_URL)) {
				String[] referenceId = url.split("donationId=");
				mDonationReference = referenceId[1];
				mDonation.setDonationReferenceId(mDonationReference);
				// TODO: donated so go on thanks screen
				openThanksScreen();
				mIsOnceExecuted = true;
			} else if (!mIsOnceExecuted
					&& url.contains(URLManager.SANDBOX_URL)) {
				// TODO: donated so go on thanks screen
				openThanksScreen();
				mIsOnceExecuted = true;
			}
		}

		@Override
		public void onLoadResource(WebView webView, String url) {
			super.onLoadResource(webView, url);
		}
	}
}