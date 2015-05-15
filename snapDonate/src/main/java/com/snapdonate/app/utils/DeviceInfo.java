package com.snapdonate.app.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class DeviceInfo {
	static DeviceInfo mDeviceInfo = null;
	String mDeviceID;

	public static DeviceInfo getInstance(Context context) {
		if (mDeviceInfo == null) {
			mDeviceInfo = new DeviceInfo();
			mDeviceInfo.genereateDeviceUniqueID(context);
		}
		return mDeviceInfo;
	}

	private void genereateDeviceUniqueID(Context context) {
		final TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String telephonyManagerDevice, telephonyManagerSerial, androidID;
		telephonyManagerDevice = telephonyManager.getDeviceId();
		telephonyManagerSerial = telephonyManager.getSimSerialNumber();
		androidID = android.provider.Settings.Secure.getString(
				context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUUID = new UUID(androidID.hashCode(),
				((long) telephonyManagerDevice.hashCode() << 32)
						| telephonyManagerSerial.hashCode());
		mDeviceID = deviceUUID.toString();
	}

	public String getDeviceUUID() {
		return mDeviceID;
	}
}