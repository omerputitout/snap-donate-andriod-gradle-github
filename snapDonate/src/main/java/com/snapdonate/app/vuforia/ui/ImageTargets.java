/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.snapdonate.app.vuforia.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;
import com.snapdonate.app.R;
import com.snapdonate.app.SplashActivity;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.interfaces.OnDialogButtonClickListener;
import com.snapdonate.app.menu.MenuFragmentActivity;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.tabs.MainTabScreen;
import com.snapdonate.app.utils.AppDialog;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.vuforia.LoadingDialogHandler;
import com.snapdonate.app.vuforia.SampleAppMenuInterface;
import com.snapdonate.app.vuforia.SampleApplicationControl;
import com.snapdonate.app.vuforia.SampleApplicationException;
import com.snapdonate.app.vuforia.SampleApplicationGLView;
import com.snapdonate.app.vuforia.SampleApplicationSession;

public class ImageTargets extends Activity implements SampleApplicationControl,
		SampleAppMenuInterface, OnDialogButtonClickListener {
	private static final String TAG = "ImageTargets";
	SampleApplicationSession vuforiaAppSession;
	private DataSet mCurrentDataset;
	private int mCurrentDatasetSelectionIndex = 0;
	private int mStartDatasetsIndex = 0;
	private int mDatasetsNumber = 0;
	private ArrayList<String> mDatasetStrings = new ArrayList<String>();
	// Our OpenGL view:
	private SampleApplicationGLView mGlView;
	// Our renderer:
	private ImageTargetRenderer mRenderer;
	private boolean mSwitchDatasetAsap = false;
	private boolean mFlash = false;
	private boolean mContAutofocus = false;
	private boolean mExtendedTracking = false;
	private View mFlashOptionView;
	LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
	boolean mIsDroidDevice = false;
	private LinearLayout mCameraViewLinearLayout;
	private TextView mTabSnapTextView;
	private TextView mTabTodoTextView;
	private TextView mTabFaqsTextView;
	private TextView mFitTheCharityTextView;
	private ImageView mTabTodoImageView;
	private ImageView mTabFaqsImageView;
	private ImageView mTabTodoCounterImageView;
	private TextView mTabTodoCounterTextView;
	private Button mNotSnappingTapHereButton;
	private LinearLayout mMoreButtonLinearLayout;
	private GestureDetector mGestureDetector;

	private final int REQUEST_CODE_CLOUDS_TARGET_DETECTION = 1;

	private BroadcastReceiver conectivityBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				// fetch data
				SLog.showLog("Netowk Available ");
				openCloudsTargetDetectionActivity();
			} else {
				// display error
				SLog.showLog("Netowk Not Available ");
			}
		}
	};

	private void openCloudsTargetDetectionActivity() {
		// Generates an Alert Dialog to show the netwrok is available
		AppDialog.showAlert(this,
				getString(R.string.connection_found_imagetargets),
				getString(R.string.ontinue), getString(R.string.cancel), this,
				REQUEST_CODE_CLOUDS_TARGET_DETECTION);
	}

	// Called when the activity first starts or the user navigates back to an
	// activity.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snap);
		initLocalViews();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(conectivityBroadcastReceiver, filter);
		mGestureDetector = new GestureDetector(this, new GestureListener());
		vuforiaAppSession = new SampleApplicationSession(this);
		startLoadingAnimation();
		mDatasetStrings.add("SnapDonateDD.xml");
		vuforiaAppSession
				.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
				"droid");
	}

	// Process Single Tap event to trigger autofocus
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		// Used to set autofocus one second after a manual focus is triggered
		private final Handler autofocusHandler = new Handler();

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Generates a Handler to trigger autofocus
			// after 1 second
			autofocusHandler.postDelayed(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
					if (!result)
						Log.e("SingleTapUp", "Unable to trigger focus");
				}
			}, 1000L);
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// // Process the Gestures
		return mGestureDetector.onTouchEvent(event);
	}

	private void setTodoCounter() {
		int count = DataBaseManager.getDonationsTodoCount(this);
		if (count > 0) {
			int width = SUtils.getWindowWidht(this);
			int height = SUtils.getWindowHeight(this);
			float top = height * 0.084f;
			float left = width * 0.433f;
			SLog.showLog("top : " + top + " left :" + left);
			float imageViewHeight = height * 0.031f;
			float imageViewWidht = width * 0.055f;
			SLog.showLog("iv_widht : " + imageViewWidht + " , iv_height :"
					+ imageViewHeight);
			LayoutParams param = new LayoutParams((int) imageViewWidht,
					(int) imageViewHeight);
			param.setMargins((int) left, (int) top, 0, 0);
			mTabTodoCounterImageView.setLayoutParams(param);
			mTabTodoCounterImageView.setVisibility(View.VISIBLE);
			mTabTodoCounterTextView.setLayoutParams(param);
			mTabTodoCounterTextView.setVisibility(View.VISIBLE);
			mTabTodoCounterTextView.setText(String.valueOf(count));
		} else {
			mTabTodoCounterImageView.setVisibility(View.INVISIBLE);
			mTabTodoCounterTextView.setVisibility(View.INVISIBLE);
		}
	}

	private void initLocalViews() {
		mCameraViewLinearLayout = (LinearLayout) findViewById(R.id.snapCameraLinearLayout);
		// Gets a reference to the loading dialog
		loadingDialogHandler.mLoadingDialogContainer = findViewById(R.id.snapLoadingIndicator);
		mTabSnapTextView = (TextView) findViewById(R.id.snapTabSnapTextView);
		mTabSnapTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoTextView = (TextView) findViewById(R.id.snapTabTodoTextView);
		mTabTodoTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoCounterTextView = (TextView) findViewById(R.id.snapTabTodoCounterTextView);
		mTabTodoCounterTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));
		mTabFaqsTextView = (TextView) findViewById(R.id.snapTabFaqsTextView);
		mTabFaqsTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoImageView = (ImageView) findViewById(R.id.snapTabTodoImageView);
		mTabTodoCounterImageView = (ImageView) findViewById(R.id.snapTabTodoCounterImageView);
		mTabFaqsImageView = (ImageView) findViewById(R.id.snapTabFaqsImageView);
		mFitTheCharityTextView = (TextView) findViewById(R.id.snapFitTheCharityLogoTextView);
		mFitTheCharityTextView.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mNotSnappingTapHereButton = (Button) findViewById(R.id.snapNotSnappingTapHereButton);
		mNotSnappingTapHereButton.setTypeface(SFonts.getInstance(this).getFont(
				SFonts.FONT_VOLK_SWAGAN_SERIAL));
		mTabTodoImageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent(ImageTargets.this,
						MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_TODO_INDEX);
				startActivity(intent);
				return false;
			}
		});

		mTabFaqsImageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent(ImageTargets.this,
						MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_FAQS_INDEX);
				startActivity(intent);
				return false;
			}
		});

		mNotSnappingTapHereButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ImageTargets.this,
						MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_SNAP_INDEX);
				startActivity(intent);
			}
		});

		mMoreButtonLinearLayout = (LinearLayout) findViewById(R.id.snapMoreMenuLinearLayout);
		mMoreButtonLinearLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent = new Intent(v.getContext(),
						MenuFragmentActivity.class);
				intent.putExtra(SUtils.FROM, SUtils.IMAGE_TARGETS);
				v.getContext().startActivity(intent);
				return false;
			}
		});

	}

	public void loadAnimation() {
		int width = SUtils.getWindowWidht(this);
		int height = SUtils.getWindowHeight(this);
		SLog.showLog("height : " + height);
		SLog.showLog("width : " + width);
		float top = height * 0.83f;
		SLog.showLog("top : " + top);
		float iv_height = height * 0.083f;
		float iv_widht = width * 0.841f;
		SLog.showLog("iv_widht : " + iv_widht + " , iv_height :" + iv_height);
		LayoutParams param = new LayoutParams((int) iv_widht, (int) iv_height);
		param.setMargins(0, (int) top, 0, 0);
		param.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mNotSnappingTapHereButton.setLayoutParams(param);
		mNotSnappingTapHereButton.setVisibility(View.VISIBLE);
		Animation slideUpIn = AnimationUtils.loadAnimation(ImageTargets.this,
				R.anim.slide_up_in);
		mNotSnappingTapHereButton.startAnimation(slideUpIn);
	}

	// Called when the activity will start interacting with the user.
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		// This is needed for some Droid devices to force
		// portrait
		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		try {
			vuforiaAppSession.resumeAR();
		} catch (SampleApplicationException e) {
			Log.e(TAG, e.getString());
		}
		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}
		setTodoCounter();
	}

	// Callback for configuration changes the activity handles itself
	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);
		vuforiaAppSession.onConfigurationChanged();
	}

	// Called when the system is about to start resuming a previous activity.
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}
		// Turn off the flash
		if (mFlashOptionView != null && mFlash) {
			// OnCheckedChangeListener is called upon changing the checked state
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				((Switch) mFlashOptionView).setChecked(false);
			} else {
				((CheckBox) mFlashOptionView).setChecked(false);
			}
		}
		try {
			vuforiaAppSession.pauseAR();
		} catch (SampleApplicationException e) {
			Log.e(TAG, e.getString());
		}
	}

	// The final call you receive before your activity is destroyed.
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		try {
			vuforiaAppSession.stopAR();
		} catch (SampleApplicationException e) {
			Log.e(TAG, e.getString());
		}
		unregisterReceiver(conectivityBroadcastReceiver);
		System.gc();
	}

	// Initializes AR application components.
	private void initApplicationAR() {
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();
		mGlView = new SampleApplicationGLView(this);
		mGlView.init(translucent, depthSize, stencilSize);
		mRenderer = new ImageTargetRenderer(this, vuforiaAppSession);
		// mRenderer.setTextures(mTextures);
		mGlView.setRenderer(mRenderer);
	}

	private void startLoadingAnimation() {
		// Shows the loading indicator at start
		loadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
	}

	// Methods to load and destroy tracking data.
	@Override
	public boolean doLoadTrackersData() {
		TrackerManager tManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) tManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null)
			return false;
		if (mCurrentDataset == null)
			mCurrentDataset = imageTracker.createDataSet();
		if (mCurrentDataset == null)
			return false;
		if (!mCurrentDataset.load(
				mDatasetStrings.get(mCurrentDatasetSelectionIndex),
				STORAGE_TYPE.STORAGE_APPRESOURCE))
			return false;
		if (!imageTracker.activateDataSet(mCurrentDataset))
			return false;
		int numTrackables = mCurrentDataset.getNumTrackables();
		for (int count = 0; count < numTrackables; count++) {
			Trackable trackable = mCurrentDataset.getTrackable(count);
			if (isExtendedTrackingActive()) {
				trackable.startExtendedTracking();
			}
			String name = "Current Dataset : " + trackable.getName();
			trackable.setUserData(name);
			Log.d(TAG, "UserData:Set the following user data "
					+ (String) trackable.getUserData());
		}
		return true;
	}

	@Override
	public boolean doUnloadTrackersData() {
		// Indicate if the trackers were unloaded correctly
		boolean result = true;
		TrackerManager tManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) tManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null)
			return false;
		if (mCurrentDataset != null && mCurrentDataset.isActive()) {
			if (imageTracker.getActiveDataSet().equals(mCurrentDataset)
					&& !imageTracker.deactivateDataSet(mCurrentDataset)) {
				result = false;
			} else if (!imageTracker.destroyDataSet(mCurrentDataset)) {
				result = false;
			}
			mCurrentDataset = null;
		}
		return result;
	}

	@Override
	public void onInitARDone(SampleApplicationException exception) {
		if (exception == null) {
			initApplicationAR();
			mRenderer.mIsActive = true;
			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			mCameraViewLinearLayout.addView(mGlView);
			try {
				vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
			} catch (SampleApplicationException e) {
				Log.e(TAG, e.getString());
			}
			boolean result = CameraDevice.getInstance().setFocusMode(
					CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
			if (result)
				mContAutofocus = true;
			else
				Log.e(TAG, "Unable to enable continuous autofocus");
		} else {
			Log.e(TAG, exception.getString());
			finish();
		}
	}

	@Override
	public void onQCARUpdate(State state) {
		if (mSwitchDatasetAsap) {
			mSwitchDatasetAsap = false;
			TrackerManager tm = TrackerManager.getInstance();
			ImageTracker it = (ImageTracker) tm.getTracker(ImageTracker
					.getClassType());
			if (it == null || mCurrentDataset == null
					|| it.getActiveDataSet() == null) {
				Log.d(TAG, "Failed to swap datasets");
				return;
			}
			doUnloadTrackersData();
			doLoadTrackersData();
		}
	}

	@Override
	public boolean doInitTrackers() {
		// Indicate if the trackers were initialized correctly
		boolean result = true;
		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;
		// Trying to initialize the image tracker
		tracker = tManager.initTracker(ImageTracker.getClassType());
		if (tracker == null) {
			Log.e(TAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(TAG, "Tracker successfully initialized");
		}
		return result;
	}

	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
		boolean result = true;
		Tracker imageTracker = TrackerManager.getInstance().getTracker(
				ImageTracker.getClassType());
		if (imageTracker != null)
			imageTracker.start();
		return result;
	}

	@Override
	public boolean doStopTrackers() {
		// Indicate if the trackers were stopped correctly
		boolean result = true;
		Tracker imageTracker = TrackerManager.getInstance().getTracker(
				ImageTracker.getClassType());
		if (imageTracker != null)
			imageTracker.stop();
		return result;
	}

	@Override
	public boolean doDeinitTrackers() {
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;
		TrackerManager tManager = TrackerManager.getInstance();
		tManager.deinitTracker(ImageTracker.getClassType());
		return result;
	}

	boolean isExtendedTrackingActive() {
		return mExtendedTracking;
	}

	final public static int CMD_BACK = -1;
	final public static int CMD_EXTENDED_TRACKING = 1;
	final public static int CMD_AUTOFOCUS = 2;
	final public static int CMD_FLASH = 3;
	final public static int CMD_CAMERA_FRONT = 4;
	final public static int CMD_CAMERA_REAR = 5;
	final public static int CMD_DATASET_START_INDEX = 6;

	@Override
	public boolean menuProcess(int command) {
		boolean result = true;
		switch (command) {
		case CMD_BACK:
			finish();
			break;
		case CMD_FLASH:
			result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);
			if (result) {
				mFlash = !mFlash;
			} else {
				showToast(getString(mFlash ? R.string.menu_flash_error_off
						: R.string.menu_flash_error_on));
				Log.e(TAG, getString(mFlash ? R.string.menu_flash_error_off
						: R.string.menu_flash_error_on));
			}
			break;
		case CMD_AUTOFOCUS:
			if (mContAutofocus) {
				result = CameraDevice.getInstance().setFocusMode(
						CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
				if (result) {
					mContAutofocus = false;
				} else {
					showToast(getString(R.string.autofocus_error_off));
					Log.e(TAG, getString(R.string.autofocus_error_off));
				}
			} else {
				result = CameraDevice.getInstance().setFocusMode(
						CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
				if (result) {
					mContAutofocus = true;
				} else {
					showToast(getString(R.string.autofocus_error_on));
					Log.e(TAG, getString(R.string.autofocus_error_on));
				}
			}
			break;
		case CMD_CAMERA_FRONT:
		case CMD_CAMERA_REAR:
			// Turn off the flash
			if (mFlashOptionView != null && mFlash) {
				// OnCheckedChangeListener is called upon changing the checked
				// state
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					((Switch) mFlashOptionView).setChecked(false);
				} else {
					((CheckBox) mFlashOptionView).setChecked(false);
				}
			}
			vuforiaAppSession.stopCamera();
			try {
				vuforiaAppSession
						.startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA.CAMERA_FRONT
								: CameraDevice.CAMERA.CAMERA_BACK);
			} catch (SampleApplicationException e) {
				showToast(e.getString());
				Log.e(TAG, e.getString());
				result = false;
			}
			doStartTrackers();
			break;
		case CMD_EXTENDED_TRACKING:
			for (int tIdx = 0; tIdx < mCurrentDataset.getNumTrackables(); tIdx++) {
				Trackable trackable = mCurrentDataset.getTrackable(tIdx);
				if (!mExtendedTracking) {
					if (!trackable.startExtendedTracking()) {
						Log.e(TAG, "Failed to start extended tracking target");
						result = false;
					} else {
						Log.d(TAG,
								"Successfully started extended tracking target");
					}
				} else {
					if (!trackable.stopExtendedTracking()) {
						Log.e(TAG, "Failed to stop extended tracking target");
						result = false;
					} else {
						Log.d(TAG,
								"Successfully started extended tracking target");
					}
				}
			}
			if (result)
				mExtendedTracking = !mExtendedTracking;
			break;

		default:
			if (command >= mStartDatasetsIndex
					&& command < mStartDatasetsIndex + mDatasetsNumber) {
				mSwitchDatasetAsap = true;
				mCurrentDatasetSelectionIndex = command - mStartDatasetsIndex;
			}
			break;
		}

		return result;
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDialogPositiveButtonClick(int requestCode) {

		switch (requestCode) {
		case REQUEST_CODE_CLOUDS_TARGET_DETECTION:
			ImageTargets.this.finish();
			Intent intent = new Intent(ImageTargets.this, SplashActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDialogNegativeButtonClick(int requestCode) {
		switch (requestCode) {
		case REQUEST_CODE_CLOUDS_TARGET_DETECTION:
			// do nothing in this case
			break;

		default:
			break;
		}
	}

}
