package com.snapdonate.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
	public static final String KEY_GCM_ID = "GCM_ID";
	private static SharedPreferences applicationSharedPreference = null;

	public static SharedPreferences getSharedPreferences(Context context) {
		if (applicationSharedPreference == null) {
			applicationSharedPreference = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
		return applicationSharedPreference;
	}

	public static void saveString(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}

	public static String getString(Context context, String key, String defaultValue) {
		return getSharedPreferences(context).getString(key, defaultValue);
	}

}
