package com.ofcampus.gcm;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.ActivityClassifiedDetails;
import com.ofcampus.activity.ActivityJobDetails;
import com.ofcampus.activity.ActivityNewsDetails;
import com.ofcampus.activity.ActivitySplash;
import com.ofcampus.model.UserDetails;

public class GcmIntentService extends IntentService {
	public static String TAG = "GcmIntentService";
	public static final int NOTIFICATION_ID = 1;
	// private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				// sendNotification("Deleted messages on server: " +
				// extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// Bundle[{postId=3, from=981282250109, title=shakti has
				// commented on news test., message=dibakar daa jindabad!.,
				// android.support.content.wakelockid=1,
				// collapse_key=do_not_collapse, post_type=3}]
				try {
					UserDetails mUserDetails = UserDetails.getLoggedInUser(GcmIntentService.this);
					if (mUserDetails != null) {
						String title = extras.getString("title");
						String postId = extras.getString("postId");
						String post_type = extras.getString("post_type");

						if (TextUtils.isEmpty(title)) {
							return;
						}
						if (TextUtils.isEmpty(postId)) {
							return;
						}
						if (TextUtils.isEmpty(post_type)) {
							return;
						}
						sendNotification(title, postId, post_type);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg, String postId, String post_type) {

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, msg, when);

		String title_ = this.getString(R.string.app_name);
		Intent notificationIntent = new Intent(this, ActivitySplash.class);
		if (post_type.equals("1")) {
			notificationIntent = new Intent(this, ActivityClassifiedDetails.class);
			String str = postId + "," + Util.TOOLTITLE[2];
			notificationIntent.putExtra("postId", str);
		} else if (post_type.equals("0")) {
			notificationIntent = new Intent(this, ActivityJobDetails.class);
			String str = postId + "," + Util.TOOLTITLE[0];
			notificationIntent.putExtra("postId", str);
		} else if (post_type.equals("3")) {
			notificationIntent = new Intent(this, ActivityNewsDetails.class);
			String str = postId + "," + Util.TOOLTITLE[1];
			notificationIntent.putExtra("postId", str);
		}
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent intent = PendingIntent.getActivity(this, 0, isAppRunning(GcmIntentService.this) ? new Intent() : notificationIntent, 0);
		notification.setLatestEventInfo(this, title_, getType(post_type) + " : " + msg, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

	private String getType(String post_type) {
		if (post_type.equals("1")) {
			return Util.TOOLTITLE[2];
		} else if (post_type.equals("0")) {
			return Util.TOOLTITLE[0];
		} else if (post_type.equals("3")) {
			return Util.TOOLTITLE[1];
		}
		return "";
	}

	private boolean isAppOnBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		final String packageName = context.getPackageName();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND && appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAppRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		final String packageName = context.getPackageName();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}
}