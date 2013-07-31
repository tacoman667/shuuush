package com.codingzen.Shuuush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.codingzen.Shuuush.Activities.MainViewActivity;

public class WidgetNotificationManager {

	public static final int ID = 12345;
	public static final int icon = R.drawable.ic_app_icon;

	private static NotificationManager getNotificationManager() {
		Context context = ShuuushApplication.getAppContext();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return manager;
	}

	public static void removeNotification() {
		getNotificationManager().cancel(ID);
	}

	private static Notification createNotification(String tickerText, int badgeCount) {
		Context context = ShuuushApplication.getAppContext();

		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());

		StringBuilder contentText = new StringBuilder(context.getString(R.string.DNDServiceNotificationContentText));
		Boolean isManualStart = Preferences.getBoolean(Preferences.IsManualStart);

		contentText.append(" - ");
		if (isManualStart) {
			contentText.append(context.getString(R.string.UserActivatedNotificationText));
		} else {
			contentText.append(context.getString(R.string.CalendarActivatedNotificationText));
		}

		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent intent = new Intent(context, MainViewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(context, contentText, tickerText, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		if (badgeCount == -1) {
			badgeCount = Event.count(context);
		}
		notification.number = badgeCount;

		logBadgeCount(badgeCount);

		return notification;
	}

	public static void showNotification() {
		Context context = ShuuushApplication.getAppContext();
		String tickerText = context.getString(R.string.DNDNotificationTickerTextActivated);

		Notification notification = createNotification(tickerText, -1);
		getNotificationManager().notify(ID, notification);

	}

	public static void showNotification(String tickerText, int badgeCount, Boolean isOngoing) {
		Notification notification = createNotification(tickerText, badgeCount);

		notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;

		if (!isOngoing) {
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags = Notification.FLAG_SHOW_LIGHTS & Notification.FLAG_AUTO_CANCEL;
		}

		getNotificationManager().notify(ID, notification);

	}

	public static void updateNotification(String tickerText) {
		Notification notification = createNotification(tickerText, -1);

		if (!ShuuushApplication.isActive() && notification.number == 0) {
			removeNotification();
		} else {
			getNotificationManager().notify(ID, notification);
		}
	}

	private static void logBadgeCount(int count) {
		LogManager.i("Widget Badge Count is supposed to be " + count);
	}

}
