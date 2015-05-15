package com.snapdonate.app.utils;

import android.content.Context;
import android.graphics.Typeface;

public class SFonts {
	public static final int FONT_VOLK_SWAGAN_SERIAL = 1;
	public static final int FONT_VOLK_SWAGAN_SERIAL_LIGHT = 2;
	public static final int FONT_VOLK_SWAGAN_SERIAL_BOLD = 3;
	public static final int FONT_VOLK_SWAGAN_SERIAL_XBOLD = 4;

	Typeface mVolkswaganSerial = null;
	Typeface mVolkswaganSerialLight = null;
	Typeface mVolkswaganSerialBold = null;
	Typeface mVolkswaganSerialXbold = null;

	private static SFonts mSFont = null;

	public static SFonts getInstance(Context context) {
		if (mSFont == null) {
			mSFont = new SFonts();
			mSFont.init(context);
		}
		return mSFont;
	}

	private void init(Context context) {
		mVolkswaganSerial = Typeface.createFromAsset(context.getAssets(),
				"fonts/SoftMaker - VolkswagenSerial.otf");
		mVolkswaganSerialBold = Typeface.createFromAsset(context.getAssets(),
				"fonts/SoftMaker - VolkswagenSerial-Bold.otf");
		mVolkswaganSerialLight = Typeface.createFromAsset(context.getAssets(),
				"fonts/SoftMaker - VolkswagenSerial-Light.otf");
		mVolkswaganSerialXbold = Typeface.createFromAsset(context.getAssets(),
				"fonts/SoftMaker - VolkswagenSerial-Xbold.otf");
	}

	public Typeface getFont(int fontID) {
		Typeface font = null;
		switch (fontID) {
		case FONT_VOLK_SWAGAN_SERIAL:
			font = mVolkswaganSerial;
			break;
		case FONT_VOLK_SWAGAN_SERIAL_LIGHT:
			font = mVolkswaganSerialLight;
			break;
		case FONT_VOLK_SWAGAN_SERIAL_BOLD:
			font = mVolkswaganSerialBold;
			break;
		case FONT_VOLK_SWAGAN_SERIAL_XBOLD:
			font = mVolkswaganSerialXbold;
			break;
		}
		return font;
	}

}
