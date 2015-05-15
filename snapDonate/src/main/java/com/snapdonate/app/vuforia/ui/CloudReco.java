/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.snapdonate.app.vuforia.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.TargetFinder;
import com.qualcomm.vuforia.TargetSearchResult;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;
import com.snapdonate.app.R;
import com.snapdonate.app.SplashActivity;
import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.menu.MenuFragmentActivity;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.tabs.MainTabScreen;
import com.snapdonate.app.utils.SFonts;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.vuforia.LoadingDialogHandler;
import com.snapdonate.app.vuforia.SampleApplicationControl;
import com.snapdonate.app.vuforia.SampleApplicationException;
import com.snapdonate.app.vuforia.SampleApplicationGLView;
import com.snapdonate.app.vuforia.SampleApplicationSession;

// The main activity for the CloudReco sample. 
public class CloudReco extends Activity implements SampleApplicationControl {
	private static final String LOGTAG = "CloudReco";

	SampleApplicationSession vuforiaAppSession;

	// These codes match the ones defined in TargetFinder in Vuforia.jar
	static final int INIT_SUCCESS = 2;
	static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
	static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
	static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
	static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
	static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
	static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
	static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
	static final int UPDATE_ERROR_UPDATE_SDK = -6;
	static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
	static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;

	static final int HIDE_LOADING_DIALOG = 0;
	static final int SHOW_LOADING_DIALOG = 1;

	// Our OpenGL view:
	private SampleApplicationGLView mGlView;

	// Our renderer:
	private CloudRecoRenderer mRenderer;

	@SuppressWarnings("unused")
	private boolean mContAutofocus = false;
	// private boolean mExtendedTracking = false;
	boolean mFinderStarted = false;
	boolean mStopFinderIfStarted = false;

	// The textures we will use for rendering:
	// private Vector<Texture> mTextures;
	
	// muhammad.omer database of vuforia
//	private static final String kAccessKey = "491f250ff3975b3cd4b17ac876d118b021c7a083";
//	private static final String kSecretKey = "32cef03fbd4c135d5822fd75825ad3f40c575fe0";

	// live Client database of vuforia
//	private static final String kAccessKey = "9f4bbdb13b9cd957d483162dc7b96885a762a496";
//	private static final String kSecretKey = "97b79c97a84f356c21285617dd4b35e37880f6a0";

	// View overlays to be displayed in the Augmented View
	@SuppressWarnings("unused")
	private RelativeLayout mUILayout;

	// Error message handling:
	private int mlastErrorCode = 0;
	private int mInitErrorCode = 0;
	private boolean mFinishActivityOnError;

	// Alert Dialog used to display SDK errors
	private AlertDialog mErrorDialog;

	private GestureDetector mGestureDetector;

	private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
			this);

	private double mLastErrorTime;

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

	// Called when the activity first starts or needs to be recreated after
	// resuming the application or a configuration change.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snap);

		initLocalViews();

		vuforiaAppSession = new SampleApplicationSession(this);

		startLoadingAnimation();

		vuforiaAppSession
				.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Creates the GestureDetector listener for processing double tap
		mGestureDetector = new GestureDetector(this, new GestureListener());

		mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
				"droid");

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
			SLog.showLog("iv_widht : " + imageViewWidht
					+ " , iv_height :" + imageViewHeight);
			LayoutParams param = new LayoutParams((int) imageViewWidht, (int) imageViewHeight);
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
		// TODO Auto-generated method stub
		mCameraViewLinearLayout = (LinearLayout) findViewById(R.id.snapCameraLinearLayout);
		// Gets a reference to the loading dialog
		loadingDialogHandler.mLoadingDialogContainer = findViewById(R.id.snapLoadingIndicator);

		mTabSnapTextView = (TextView) findViewById(R.id.snapTabSnapTextView);
		mTabSnapTextView.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));

		mTabTodoTextView = (TextView) findViewById(R.id.snapTabTodoTextView);
		mTabTodoTextView.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));

		mTabTodoCounterTextView = (TextView) findViewById(R.id.snapTabTodoCounterTextView);
		mTabTodoCounterTextView
				.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL_BOLD));

		mTabFaqsTextView = (TextView) findViewById(R.id.snapTabFaqsTextView);
		mTabFaqsTextView.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));

		mTabTodoImageView = (ImageView) findViewById(R.id.snapTabTodoImageView);
		mTabTodoCounterImageView = (ImageView) findViewById(R.id.snapTabTodoCounterImageView);
		mTabFaqsImageView = (ImageView) findViewById(R.id.snapTabFaqsImageView);

		mFitTheCharityTextView = (TextView) findViewById(R.id.snapFitTheCharityLogoTextView);
		mFitTheCharityTextView
				.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));

		mNotSnappingTapHereButton = (Button) findViewById(R.id.snapNotSnappingTapHereButton);
		mNotSnappingTapHereButton
				.setTypeface(SFonts.getInstance(this).getFont(SFonts.FONT_VOLK_SWAGAN_SERIAL));

		mTabTodoImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CloudReco.this, MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_TODO_INDEX);
				startActivity(intent);
				return false;
			}
		});

		mTabFaqsImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CloudReco.this, MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_FAQS_INDEX);
				startActivity(intent);
				return false;
			}
		});

		mNotSnappingTapHereButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CloudReco.this, MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_SNAP_INDEX);
				startActivity(intent);
			}
		});

		mMoreButtonLinearLayout = (LinearLayout) findViewById(R.id.snapMoreMenuLinearLayout);

		mMoreButtonLinearLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(v.getContext(),
						MenuFragmentActivity.class);
				intent.putExtra(SUtils.FROM, SUtils.IMAGE_TARGETS);
				v.getContext().startActivity(intent);
				return false;
			}
		});

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

	// Called when the activity will start interacting with the user.
	@Override
	protected void onResume() {
		Log.d(LOGTAG, "onResume");
		super.onResume();

		// This is needed for some Droid devices to force portrait
		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			vuforiaAppSession.resumeAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
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
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);

		vuforiaAppSession.onConfigurationChanged();
	}

	// Called when the system is about to start resuming a previous activity.
	@Override
	protected void onPause() {
		Log.d(LOGTAG, "onPause");
		super.onPause();

		try {
			vuforiaAppSession.pauseAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Pauses the OpenGLView
		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}
	}

	// The final call you receive before your activity is destroyed.
	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "onDestroy");
		super.onDestroy();

		try {
			vuforiaAppSession.stopAR();
		} catch (SampleApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		System.gc();
	}

	public void deinitCloudReco() {
		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null) {
			Log.e(LOGTAG,
					"Failed to destroy the tracking data set because the ImageTracker has not"
							+ " been initialized.");
			return;
		}

		// Deinitialize target finder:
		TargetFinder finder = imageTracker.getTargetFinder();
		finder.deinit();
	}

	private void startLoadingAnimation() {

		// Shows the loading indicator at start
		loadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

	}

	// Initializes AR application components.
	private void initApplicationAR() {
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		// Initialize the GLView with proper flags
		mGlView = new SampleApplicationGLView(this);
		mGlView.init(translucent, depthSize, stencilSize);

		// Setups the Renderer of the GLView
		mRenderer = new CloudRecoRenderer(vuforiaAppSession, this);
		// mRenderer.setTextures(mTextures);
		mGlView.setRenderer(mRenderer);

	}

	// Returns the error message for each error code
	private String getStatusDescString(int code) {
		if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
			return getString(R.string.update_error_authorization_failed_desc);
		if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
			return getString(R.string.update_error_project_suspended_desc);
		if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
			return getString(R.string.update_error_no_network_connection_desc);
		if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
			return getString(R.string.update_error_service_not_available_desc);
		if (code == UPDATE_ERROR_UPDATE_SDK)
			return getString(R.string.update_error_update_sdk_desc);
		if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
			return getString(R.string.update_error_timestamp_out_of_range_desc);
		if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
			return getString(R.string.update_error_request_timeout_desc);
		if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
			return getString(R.string.update_error_bad_frame_quality_desc);
		else {
			return getString(R.string.update_error_unknown_desc);
		}
	}

	// Returns the error message for each error code
	private String getStatusTitleString(int code) {
		if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
			return getString(R.string.update_error_authorization_failed_title);
		if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
			return getString(R.string.update_error_project_suspended_title);
		if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
			return getString(R.string.update_error_no_network_connection_title);
		if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
			return getString(R.string.update_error_service_not_available_title);
		if (code == UPDATE_ERROR_UPDATE_SDK)
			return getString(R.string.update_error_update_sdk_title);
		if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
			return getString(R.string.update_error_timestamp_out_of_range_title);
		if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
			return getString(R.string.update_error_request_timeout_title);
		if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
			return getString(R.string.update_error_bad_frame_quality_title);
		else {
			return getString(R.string.update_error_unknown_title);
		}
	}

	// Shows error messages as System dialogs
	public void showErrorMessage(int errorCode, double errorTime,
			boolean finishActivityOnError) {
		if (errorTime < (mLastErrorTime + 5.0) || errorCode == mlastErrorCode)
			return;

		mlastErrorCode = errorCode;
		if (mlastErrorCode == UPDATE_ERROR_NO_NETWORK_CONNECTION) {
			// network connections lost so tell user o choose the charity list
			// for working
			runOnUiThread(new Runnable() {
				public void run() {
					if (mErrorDialog != null) {
						mErrorDialog.dismiss();
					}

					// Generates an Alert Dialog to show the error message
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CloudReco.this);
					builder.setMessage(
							getString(R.string.connection_lost_cloud))
							.setCancelable(false)
							.setIcon(0)
							.setPositiveButton(
									getString(R.string.ontinue),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											CloudReco.this.finish();

											Intent intent = new Intent(
													CloudReco.this,
													SplashActivity.class);
											startActivity(intent);

										}
									});

					mErrorDialog = builder.create();
					mErrorDialog.show();
				}
			});

		} else {

			// any other error so work as vuforia requires
			mFinishActivityOnError = finishActivityOnError;

			runOnUiThread(new Runnable() {
				public void run() {
					if (mErrorDialog != null) {
						mErrorDialog.dismiss();
					}

					// Generates an Alert Dialog to show the error message
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CloudReco.this);
					builder.setMessage(
							getStatusDescString(CloudReco.this.mlastErrorCode))
							.setTitle(
									getStatusTitleString(CloudReco.this.mlastErrorCode))
							.setCancelable(false)
							.setIcon(0)
							.setPositiveButton(getString(R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (mFinishActivityOnError) {
												finish();
											} else {
												dialog.dismiss();
											}
										}
									});

					mErrorDialog = builder.create();
					mErrorDialog.show();
				}
			});
		}

	}

	public void startFinderIfStopped() {
		if (!mFinderStarted) {
			mFinderStarted = true;

			// Get the image tracker:
			TrackerManager trackerManager = TrackerManager.getInstance();
			ImageTracker imageTracker = (ImageTracker) trackerManager
					.getTracker(ImageTracker.getClassType());

			// Initialize target finder:
			TargetFinder targetFinder = imageTracker.getTargetFinder();

			targetFinder.clearTrackables();
			targetFinder.startRecognition();
		}
	}

	public void stopFinderIfStarted() {
		if (mFinderStarted) {
			mFinderStarted = false;

			// Get the image tracker:
			TrackerManager trackerManager = TrackerManager.getInstance();
			ImageTracker imageTracker = (ImageTracker) trackerManager
					.getTracker(ImageTracker.getClassType());

			// Initialize target finder:
			TargetFinder targetFinder = imageTracker.getTargetFinder();

			targetFinder.stop();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Process the Gestures
		// if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
		// return true;

		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean doLoadTrackersData() {
		Log.d(LOGTAG, "initCloudReco");

		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		// Initialize target finder:
		TargetFinder targetFinder = imageTracker.getTargetFinder();

		String kAccessKey;
		String kSecretKey;
		if (SUtils.IS_LIVE) {
			// live Client database of vuforia
			kAccessKey = "9f4bbdb13b9cd957d483162dc7b96885a762a496";
			kSecretKey = "97b79c97a84f356c21285617dd4b35e37880f6a0";
		}else {
			// muhammad.omer database of vuforia
//			kAccessKey = "491f250ff3975b3cd4b17ac876d118b021c7a083";
//			kSecretKey = "32cef03fbd4c135d5822fd75825ad3f40c575fe0";
			// live Client database of vuforia
			kAccessKey = "9f4bbdb13b9cd957d483162dc7b96885a762a496";
			kSecretKey = "97b79c97a84f356c21285617dd4b35e37880f6a0";
		}
		// Start initialization:
		if (targetFinder.startInit(kAccessKey, kSecretKey)) {
			targetFinder.waitUntilInitFinished();
		}

		int resultCode = targetFinder.getInitState();
		if (resultCode != TargetFinder.INIT_SUCCESS) {
			if (resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION) {
				mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
			} else {
				mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
			}

			Log.e(LOGTAG, "Failed to initialize target finder.");
			return false;
		}

		// Use the following calls if you would like to customize the color of
		// the UI
		// targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
		// targetFinder->setUIPointColor(0.0, 0.0, 1.0);

		return true;
	}

	@Override
	public boolean doUnloadTrackersData() {
		return true;
	}

	@Override
	public void onInitARDone(SampleApplicationException exception) {

		if (exception == null) {
			initApplicationAR();

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			// addContentView(mGlView, new
			// LayoutParams(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT));
			mCameraViewLinearLayout.addView(mGlView);

			// Start the camera:
			try {
				vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
			} catch (SampleApplicationException e) {
				Log.e(LOGTAG, e.getString());
			}

			boolean result = CameraDevice.getInstance().setFocusMode(
					CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

			if (result)
				mContAutofocus = true;
			else
				Log.e(LOGTAG, "Unable to enable continuous autofocus");

			// mUILayout.bringToFront();

			// Hides the Loading Dialog
			loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);

			// mUILayout.setBackgroundColor(Color.TRANSPARENT);

			// mSampleAppMenu = new SampleAppMenu(this, this, "Cloud Reco",
			// mGlView, mUILayout, null);
			// setSampleAppMenuSettings();

		} else {
			Log.e(LOGTAG, exception.getString());
			if (mInitErrorCode != 0) {
				showErrorMessage(mInitErrorCode, 10, true);
			} else {
				finish();
			}
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onQCARUpdate(State state) {
		// Get the tracker manager:
		TrackerManager trackerManager = TrackerManager.getInstance();

		// Get the image tracker:
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		// Get the target finder:
		TargetFinder finder = imageTracker.getTargetFinder();

		// Check if there are new results available:
		final int statusCode = finder.updateSearchResults();

		// Show a message if we encountered an error:
		if (statusCode < 0) {

			boolean closeAppAfterError = (statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION || statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);

			showErrorMessage(statusCode, state.getFrame().getTimeStamp(),
					closeAppAfterError);

		} else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE) {
			// Process new search results
			if (finder.getResultCount() > 0) {
				TargetSearchResult result = finder.getResult(0);

				// Check if this target is suitable for tracking:
				if (result.getTrackingRating() > 0) {
					Trackable trackable = finder.enableTracking(result);

					// if (mExtendedTracking)
					// trackable.startExtendedTracking();
				}
			}
		}
	}

	@Override
	public boolean doInitTrackers() {
		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;

		// Indicate if the trackers were initialized correctly
		boolean result = true;

		tracker = tManager.initTracker(ImageTracker.getClassType());
		if (tracker == null) {
			Log.e(LOGTAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(LOGTAG, "Tracker successfully initialized");
		}

		return result;
	}

	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
		boolean result = true;

		// Start the tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		imageTracker.start();

		// Start cloud based recognition if we are in scanning mode:
		TargetFinder targetFinder = imageTracker.getTargetFinder();
		targetFinder.startRecognition();
		mFinderStarted = true;

		return result;
	}

	@Override
	public boolean doStopTrackers() {
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());

		if (imageTracker != null) {
			imageTracker.stop();

			// Stop cloud based recognition:
			TargetFinder targetFinder = imageTracker.getTargetFinder();
			targetFinder.stop();
			mFinderStarted = false;

			// Clears the trackables
			targetFinder.clearTrackables();
		} else {
			result = false;
		}

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


}
