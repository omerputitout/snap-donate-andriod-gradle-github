package com.snapdonate.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SUtils {

	/*
	 * Global
	 */
	public static final boolean IS_TESTING = false;
	public static final boolean IS_LIVE = true;
	public static final boolean IS_DEBUGABLE = true;
	public static final String DATABASE_NAME = "SnapDonate.sqlite";
	public static final String DONATION_CURRENCY_SIGN = "£";
	public static final String DATABASEHELPER_TAG = "DataBaseHelper";
	public static final String SAVED_SERVER_ID = "saved_server_id";
	public static final String ID = "id";
	public static final String SERVER_RESPONSE_TRUE = "true";

	/*
	 * Charity Keys
	 */
	public static final String CHARITY_ID = "CHARITY_ID";
	public static final String CHARITY_NAME = "CHARITY_NAME";
	public static final String CHARITY_MODEL = "CHARITY_MODEL";
	public static final String CHARITY_MEDIUM_SNAPPED = "SNAPPED";
	public static final String CHARITY_MEDIUM_CHOSEN = "CHOSEN";
	public static final String CHARITY_IS_TODO = "TODO";
	public static final String CHARITY_IS_HISTORY = "HISTORY";
	public static final String CHARITY_IS_PENDING = "CHARITY_IS_PENDING";
	/* Sync keys */
	public static final String IS_SYNCED = "1";
	public static final String IS_NOT_SYNCED = "0";
	/**
	 * Donation Model Keys
	 * */
	public static final String DONATION_CHARITY_ID = "charity_id";
	public static final String DONATION_CHARITY_NAME = "charity_name";
	public static final String DONATION_CHARITY_DEVICE_ID = "device_id";
	public static final String DONATION_CHARITY_AMOUNT = "charity_amount";
	public static final String DONATION_CHARITY_DATE = "charity_date";
	public static final String DONATION_CHARITY_OPTION = "option";
	public static final String DONATION_CHARITY_PLATFORM_KEY = "plateform";
	public static final String DONATION_CHARITY_PLATFORM_VALUE = "Android";
	/*
	 * Fragment Names Constants
	 */
	public static final String DONATION_AMOUNT_FRAGMENT = "DonationAmount_Fragment";
	public static final String DONATION_SAVE_FRAGMENT = "DonationSave_Fragment";
	public static final String DONATE_NOW_FRAGMENT = "DonateNow_Fragment";
	public static final String THANKS_GIVING_FRAGMENT = "ThanksGiving_Fragment";
	public static final String TODO_FRAGMENT = "TODO_Fragment";
	public static final String SEARCH_FRAGMENT = "Search_Fragment";
	public static final String FAVOURITE_FRAGMENT = "Favourite_Fragment";
	public static final String HISTORY_FRAGMENT = "History_Fragment";
	public static final String MENU_FRAGMENT = "Menu_Fragment";
	public static final String ABOUT_FRAGMENT = "About_Fragment";
	public static final String SHARE_FRAGMENT = "Share_Fragment";
	public static final String PRIVACY_FRAGMENT = "Privacy_Fragment";
	public static final String TERMS_AAND_CONDITIONS_FRAGMENT = "TermsAndConditions_Fragment";
	/*
	 * BroadCast
	 */
	public static final String BROADCASTRECEIVER_ACTION_LOAD_SNAP_BUTTON_ANIMATION = "BR_ACTION_LOAD_SNAP_BUTTON_ANIMATION";
	public static final String BROADCASTRECEIVER_ACTION_LOAD_TODO_TAB = "BR_ACTION_LOAD_TODO_TAB";
	public static final String BROADCASTRECEIVER_ACTION_REFRESH_TODO_DATA = "BR_ACTION_REFRESH_TODO_DATA";
	public static final String BROADCASTRECEIVER_ACTION_OPEN_SPECIFIED_TAB = "BR_ACTION_OPEN_SPECIFIED_TAB";
	public static final String BROADCASTRECEIVER_ACTION_SYNC_DATA_WITH_SERVER = "BR_ACTION_SYNC_DATA_WITH_SERVER";
	public static final String BROADCASTRECEIVER_ACTION_FINISH_MAIN_SCREEN = "BR_ACTION_FINISH_MAIN_SCREEN";

	/**
	 * twitter
	 */
	public static final String CALLBACK_URL = "http://www.google.com.pk/";
	public static final String TWITTER_CONSUMER_KEY = "YVtrhMynSKd8FBaifNYa8Yc2x";
	public static final String TWITTER_CONSUMER_SECRET = "1StLjLSochaoZdV2Se2HiBGsxDwyUqRLDsoRaG4LNyMW3KHVNC";
	public static final String PREF_ACCESS_TOKEN = "accessToken";
	/** Name to store the users access token secret */
	public static final String PREF_ACCESS_TOKEN_SECRET = "accessTokenSecret";
	public static final String TWEET_URL = "";

	/*
	 * Other Constants
	 */
	public static final String TAB_INDEX_TO_OPEN = "TAB_INDEX_TO_OPEN";
	public static final Integer TAB_SNAP_INDEX = 0;
	public static final Integer TAB_TODO_INDEX = 1;
	public static final Integer TAB_FAQS_INDEX = 2;
	public static final String IS_SECTION = "121212121";
	public static final String EMPTY_CELL = "EMPTY_CELL";
	public static final String DONATION_MODEL = "DONATION_MODEL";
	public static final String FROM = "FROM";
	public static final String IMAGE_TARGETS = "ImageTargets";
	public static final String MAIN_TAB_SCREEN = "MainTabScreen";
	public static final String APP_URL_LINK = "no link";
	public static final String BOWELL_CANCER_UK_CHARITY_ID = "11344";
	/**
	 * Flurry
	 */
	public static final String FLURRY_API_KEY = "JFFCXPPXS2JJJ48ZRPHW";
	public static final String DONATE_NOW_EVENT_NAME_FLURRY = "Donate Now";
	public static final String SAVE_FOR_LATER_EVENT_NAME_FLURRY = "Save For Later";
	public static final String TODO_EVENT_NAME_FLURRY = "Todo";
	public static final String FAVOURITE_EVENT_NAME_FLURRY = "Favourite";
	public static final String DONATION_AMOUNT_TAG = "donation amount";
	public static final String TIME_TAG = "time";
	public static final String DATE_TAG = "date";
	public static final String CHARITY_ID_TAG = "Charity Id";
	public static final String CHARITY_NAME_TAG = "Charity Name";
	/**
	 * GCM constants
	 */
	public static final String SENDER_ID = "409322953150";
	public static final String DEVICE_ID = "device_id";
	public static final String GCM_REGID = "gcm_regid";
	/**
	 * Simple Date Formator
	 */
	public static final String SIMPLE_DATE_FORMAT = "dd/MM/yy";
	/**
	 * MainTab Keys
	 * */
	public static final int SNAP_TAB_KEY = 0;
	public static final int TO_DO_TAB_KEY = 1;
	public static final int FAQS_TAB_KEY = 2;

	public static String getFormatedStringFromDate(Date date, String format) {
		SimpleDateFormat formator = new SimpleDateFormat(format,
				Locale.getDefault());
		return formator.format(date);
	}

	public static int getWindowHeight(Context context) {
		DisplayMetrics display = ((Activity) context).getResources()
				.getDisplayMetrics();
		return display.heightPixels;
	}

	public static int getWindowWidht(Context context) {
		DisplayMetrics display = ((Activity) context).getResources()
				.getDisplayMetrics();
		return display.widthPixels;
	}

	public static final void hideKeyBoard(EditText editTextField,
			Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) (context)
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(
				editTextField.getWindowToken(), 0);
		inputMethodManager.hideSoftInputFromWindow(
				editTextField.getWindowToken(), 0);
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServices(Context context) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}
}
