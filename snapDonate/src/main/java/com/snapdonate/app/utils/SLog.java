package com.snapdonate.app.utils;

import android.util.Log;

public class SLog {
	public static String TAG = "SnapDonate";

	public static void showLog(String message) {
		if (SUtils.IS_DEBUGABLE) {
			Log.d(TAG, message);
		}
	}
	
	
}
