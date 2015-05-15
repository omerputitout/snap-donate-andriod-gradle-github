package com.snapdonate.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.snapdonate.app.database.DataBaseManager;
import com.snapdonate.app.utils.NetworkManager;
import com.snapdonate.app.vuforia.ui.CloudReco;
import com.snapdonate.app.vuforia.ui.ImageTargets;

public class SplashActivity extends Activity {
	private static final long SPLASH_DELAY_MILLISECONDS = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		DataBaseManager.makeDatabase(this);
		moveToNextScreen();
	}

	private void moveToNextScreen() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
					Intent intent = new Intent(SplashActivity.this, CloudReco.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(SplashActivity.this,
							ImageTargets.class);
					startActivity(intent);
				}
				finish();
			}
		}, SPLASH_DELAY_MILLISECONDS);
	}
}
