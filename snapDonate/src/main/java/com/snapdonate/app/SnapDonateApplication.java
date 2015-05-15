package com.snapdonate.app;

import android.app.Application;

import com.snapdonate.app.utils.SFonts;

public class SnapDonateApplication extends Application {
	/*
	 * the first function which is called when user click on app icon . so
	 * anything that is global initialize it here at once
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		SFonts.getInstance(getApplicationContext());
	}
}
