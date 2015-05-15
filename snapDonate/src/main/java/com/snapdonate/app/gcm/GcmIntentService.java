package com.snapdonate.app.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.snapdonate.app.R;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.tabs.MainTabScreen;
import com.snapdonate.app.utils.SUtils;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder mBuilder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// TODO: The getMessageType() intent parameter must be the intent you received
		// TODO: in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		if (!bundle.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + bundle.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ bundle.toString());
				// TODO: If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				// TODO: This loop represents the service doing some work.
				// TODO: Post notification of received message.
				sendNotification(bundle.getString("message"));
			}
		}
		// TODO: Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// TODO: Put the message into a notification and post it.
	// TODO: This is just one simple example of what you might choose to do with
	// TODO: a GCM message.
	private void sendNotification(String message) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, MainTabScreen.class);
		intent.putExtra(SUtils.CHARITY_MODEL, new Charity());
		intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_TODO_INDEX);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		String[] events = new String[2];
		// TODO: Sets a title for the Inbox style big view
		// TODO: Moves events into the big view
		for (int index = 0; index < events.length; index++) {
			inboxStyle.addLine(events[index]);
		}

		NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.appicon)
				.setContentTitle(getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
				.setContentText(message).setAutoCancel(true).setSound(alarmSound);
		notificationCompatBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, notificationCompatBuilder.build());
	}
}