package com.snapdonate.app.tabs;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.snapdonate.app.R;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.gcm.GcmManager;
import com.snapdonate.app.menu.MenuFragmentActivity;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.model.Donation;
import com.snapdonate.app.services.RemoveTodoService;
import com.snapdonate.app.services.SyncingService;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.vuforia.LoadingDialogHandler;

@SuppressWarnings("deprecation")
public class MainTabScreen extends TabActivity implements OnTabChangeListener,
		OnClickListener {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	LoadingDialogHandler mLoadingDialogHandler = new LoadingDialogHandler(this);
	private ArrayList<Donation> mUnSycnedDonationsArrayList;
	private Charity mCharity = null;
	private int mCurrentTabIndex;
	private int mScreenWidth;
	private int mScreenHeight;

	private LinearLayout mMoreLinearLayout;
	private TabHost mTabHost;
	private ImageView mTabTodoCounterImageView;
	private TextView mTabTodoCounterTextView;
	private LinearLayout mTabOneIndicatorLinearLayout;
	private LinearLayout mTabThreeIndicatorLinearLayout;
	private LinearLayout mTabTwoIndicatorLinearLayout;
	// TODO: tabs Ids
	private TextView mTabOneNameTextView;
	private ImageView mTabOneImageView;
	private TextView mTabTwoNameTextView;
	private ImageView mTabTwoImageView;
	private TextView mTabThreeNameTextView;
	private ImageView mTabThreeImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_screen);
		initLocalViews();
		registerBroadcastReceivers();
		setTabs();
		setTodoCounter();
		GcmManager.getInstance().setUpGCM(this);
		checkAndsendAllUnSycneddataToServer();
		setUpFlurry();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startLoadingAnimation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBraodCastReceivers);
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onTabChanged(String tabID) {
		int selectedTab = mTabHost.getCurrentTab();
		switch (selectedTab) {
		case SUtils.SNAP_TAB_KEY:
			onSnapTabSelected();
			break;
		case SUtils.TO_DO_TAB_KEY:
			onTodoTabSelected();
			break;
		case SUtils.FAQS_TAB_KEY:
			onFaqsTabSelected();
			break;
		default:
			onSnapTabSelected();
			break;
		}
	}

	private void onFaqsTabSelected() {
		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqImageView)).setImageBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.faqsactive));
		((TextView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqTextView)).setTextColor(getResources()
				.getColor(R.color.tab_selected));
		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqBarImageView))
				.setVisibility(View.VISIBLE);

		// TODO: unselect others
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.snapnormal));
		((TextView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_normal));
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapBarImageView))
				.setVisibility(View.INVISIBLE);

		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.todonormal));
		((TextView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_normal));
		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoBarImageView))
				.setVisibility(View.INVISIBLE);
	}

	private void onTodoTabSelected() {
		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.todoactive));
		((TextView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_selected));
		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoBarImageView))
				.setVisibility(View.VISIBLE);

		// TODO: unselect others
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.snapnormal));
		((TextView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_normal));
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapBarImageView))
				.setVisibility(View.INVISIBLE);

		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqImageView)).setImageBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.faqsnormal));
		((TextView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqTextView)).setTextColor(getResources()
				.getColor(R.color.tab_normal));
		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqBarImageView))
				.setVisibility(View.INVISIBLE);
	}

	private void onSnapTabSelected() {
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.snapactive));
		((TextView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_selected));
		((ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapBarImageView))
				.setVisibility(View.VISIBLE);

		// TODO: unselect others
		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoImageView))
				.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.todonormal));
		((TextView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoTextView))
				.setTextColor(getResources().getColor(
						R.color.tab_normal));
		((ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoBarImageView))
				.setVisibility(View.INVISIBLE);

		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqImageView)).setImageBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.faqsnormal));
		((TextView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqTextView)).setTextColor(getResources()
				.getColor(R.color.tab_normal));
		((ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqBarImageView))
				.setVisibility(View.INVISIBLE);

		startLoadingAnimation();
		finish();
	}

	private void initLocalViews() {
		mScreenWidth = SUtils.getWindowWidht(this);
		mScreenHeight = SUtils.getWindowHeight(this);
		mCharity = (Charity) getIntent().getSerializableExtra(
				SUtils.CHARITY_MODEL);
		mCurrentTabIndex = getIntent().getIntExtra(SUtils.TAB_INDEX_TO_OPEN, 0);
		// TODO: Gets a reference to the loading dialog
		mLoadingDialogHandler.mLoadingDialogContainer = findViewById(R.id.progressBar);
		// TODO: hide initial loading
		mLoadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mMoreLinearLayout = (LinearLayout) findViewById(R.id.moreMenuLinearLayout);
		mMoreLinearLayout.setOnClickListener(this);

		mTabTodoCounterTextView = (TextView) findViewById(R.id.todoCounterTextView);
		mTabTodoCounterTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mTabTodoCounterImageView = (ImageView) findViewById(R.id.todoCounterImageView);
	}

	private void registerBroadcastReceivers() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB);
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA);
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB);
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_SYNC_DATA_WITH_SERVER);
		filter.addAction(SUtils.BROADCASTRECEIVER_ACTION_FINISH_MAIN_SCREEN);
		registerReceiver(mBraodCastReceivers, filter);
	}

	private void setTabs() {
		/** Add the tabs to the TabHost to display. */
		mTabHost.addTab(getSnapTabSpecs());
		mTabHost.addTab(getTodoTabSpecs());
		mTabHost.addTab(getFaqsTabSpecs());
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTabIndex);
	}

	private TabSpec getFaqsTabSpecs() {
		TabSpec tabThree = mTabHost.newTabSpec(getString(R.string.faqs));

		mTabThreeIndicatorLinearLayout = (LinearLayout) View.inflate(this,
				R.layout.tab_indicator_faqd_layout, null);
		float tabThreeHeight = mScreenHeight * 0.112f;
		float tabThreeWidht = mScreenWidth * 0.377f;
		LayoutParams paramTabThree = new LayoutParams((int) tabThreeWidht,
				(int) tabThreeHeight);
		mTabThreeIndicatorLinearLayout.setLayoutParams(paramTabThree);
		mTabThreeNameTextView = (TextView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqTextView);
		mTabThreeImageView = (ImageView) mTabThreeIndicatorLinearLayout
				.findViewById(R.id.faqImageView);

		// TODO: Set the Tab name and Activity
		// TODO: that will be opened when particular Tab will be selected
		mTabThreeNameTextView.setText(getString(R.string.faqs));
		mTabThreeNameTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabThreeImageView.setImageBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.faqsnormal));
		tabThree.setIndicator(mTabThreeIndicatorLinearLayout);
		tabThree.setContent(new Intent(this, FaqsFragmentActivity.class));
		return tabThree;
	}

	private TabSpec getTodoTabSpecs() {
		TabSpec tabTwo = mTabHost.newTabSpec(getString(R.string.to_do));
		mTabTwoIndicatorLinearLayout = (LinearLayout) View.inflate(this,
				R.layout.tab_indicator_todo_layout, null);

		float tabTwoHeight = mScreenHeight * 0.112f;
		float tabTwoWidht = mScreenWidth * 0.253f;
		LayoutParams paramTabTwo = new LayoutParams((int) tabTwoWidht,
				(int) tabTwoHeight);
		mTabTwoIndicatorLinearLayout.setLayoutParams(paramTabTwo);
		mTabTwoNameTextView = (TextView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoTextView);
		mTabTwoImageView = (ImageView) mTabTwoIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorTodoImageView);

		// TODO: Set the Tab name and Activity
		// TODO: that will be opened when particular Tab will be selected
		mTabTwoNameTextView.setText(getString(R.string.to_do));
		mTabTwoNameTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTwoImageView.setImageBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.todonormal));
		tabTwo.setIndicator(mTabTwoIndicatorLinearLayout);
		tabTwo.setContent(new Intent(this, TodoFragmentActivity.class));
		return tabTwo;
	}

	private TabSpec getSnapTabSpecs() {
		TabSpec tabOne = mTabHost.newTabSpec(getString(R.string.snap));
		mTabOneIndicatorLinearLayout = (LinearLayout) View.inflate(this,
				R.layout.tab_indicator_snap_layout, null);
		float tabOneHeight = mScreenHeight * 0.112f;
		float tabOneWidht = mScreenWidth * 0.37f;
		LayoutParams paramTabOne = new LayoutParams((int) tabOneWidht,
				(int) tabOneHeight);
		mTabOneIndicatorLinearLayout.setLayoutParams(paramTabOne);
		mTabOneNameTextView = (TextView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapTextView);
		mTabOneImageView = (ImageView) mTabOneIndicatorLinearLayout
				.findViewById(R.id.tabIndicatorSnapImageView);
		mTabOneIndicatorLinearLayout.setOnClickListener(this);

		// TODO: Set the Tab name and Activity
		// TODO: that will be opened when particular Tab will be selected
		mTabOneNameTextView.setText(getString(R.string.snap));
		mTabOneNameTextView.setTextColor(getResources().getColor(
				R.color.tab_selected));
		mTabOneNameTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabOneImageView.setImageBitmap(BitmapFactory.decodeResource(
				getResources(), R.drawable.snapactive));
		tabOne.setIndicator(mTabOneIndicatorLinearLayout);
		tabOne.setContent(new Intent(this, SnapFragmentActivity.class)
				.putExtra(SUtils.CHARITY_MODEL, mCharity));
		return tabOne;
	}

	private void setTodoCounter() {
		float topMargin = mScreenHeight * 0.084f;
		float leftMargin = mScreenWidth * 0.433f;
		float tabTodoCounterImageViewHeight = mScreenHeight * 0.031f;
		float tabTodoCounterImageViewWidht = mScreenWidth * 0.055f;
		LayoutParams param = new LayoutParams(
				(int) tabTodoCounterImageViewWidht,
				(int) tabTodoCounterImageViewHeight);
		param.setMargins((int) leftMargin, (int) topMargin, 0, 0);
		mTabTodoCounterImageView.setLayoutParams(param);
		mTabTodoCounterImageView.setVisibility(View.VISIBLE);
		mTabTodoCounterTextView.setLayoutParams(param);
		mTabTodoCounterTextView.setVisibility(View.VISIBLE);

		int count = DataBaseManager.getDonationsTodoCount(this);
		if (count > 0) {
			mTabTodoCounterTextView.setText(String.valueOf(count));
		} else {
			mTabTodoCounterImageView.setVisibility(View.INVISIBLE);
			mTabTodoCounterTextView.setVisibility(View.INVISIBLE);
		}
	}

	private void checkAndsendAllUnSycneddataToServer() {
		if (NetworkManager.isNetworkAvailable(this)) {
			mUnSycnedDonationsArrayList = DataBaseManager
					.getAllUnsycnedDonations(this);
			for (int index = 0; index < mUnSycnedDonationsArrayList.size(); index++) {
				Intent intent = new Intent(this, SyncingService.class);
				intent.putExtra(SUtils.DONATION_MODEL,
						mUnSycnedDonationsArrayList.get(index));
				this.startService(intent);
			}
			// TODO: delete from central data if any available
			ArrayList<String> toBeDeletedList = DataBaseManager
					.getTodoToBeDeleted(this);
			for (int index = 0; index < toBeDeletedList.size(); index++) {
				Intent intent = new Intent(this, RemoveTodoService.class);
				intent.putExtra("saved_server_id", toBeDeletedList.get(index));
				this.startService(intent);
			}
		}
	}

	private void setUpFlurry() {
		FlurryAgent.setReportLocation(true);
		FlurryAgent.onStartSession(this, SUtils.FLURRY_API_KEY);
		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setLogLevel(Log.DEBUG);
	}

	private BroadcastReceiver mBraodCastReceivers = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB)) {
				mCurrentTabIndex = 1;
				mTabHost.setCurrentTab(mCurrentTabIndex);
			}
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA)) {
				setTodoCounter();
			}
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB)) {
				mCurrentTabIndex = intent.getIntExtra(SUtils.TAB_INDEX_TO_OPEN,
						0);
				mTabHost.setCurrentTab(mCurrentTabIndex);
			}
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_SYNC_DATA_WITH_SERVER)) {
				checkAndsendAllUnSycneddataToServer();
			}
			if (intent.getAction().equals(
					SUtils.BROADCASTRECEIVER_ACTION_FINISH_MAIN_SCREEN)) {
				MainTabScreen.this.finish();
			}
		}
	};

	private void startLoadingAnimation() {
		// TODO: Shows the loading indicator at start
		mLoadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tabIndicatorSnapImageView:
			startLoadingAnimation();
			finish();
			break;
		case R.id.moreMenuLinearLayout:
			Intent intent = new Intent(view.getContext(),
					MenuFragmentActivity.class);
			intent.putExtra(SUtils.FROM, SUtils.MAIN_TAB_SCREEN);
			view.getContext().startActivity(intent);
			break;
		}
	}
}
