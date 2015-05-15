package com.snapdonate.app.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Explicitly specify that GcmIntentService will handle the intent.
        ComponentName componentName = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // TODO: Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(componentName)));
        setResultCode(Activity.RESULT_OK);
    }
}
