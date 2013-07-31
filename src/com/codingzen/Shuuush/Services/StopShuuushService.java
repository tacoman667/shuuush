package com.codingzen.Shuuush.Services;

import android.app.IntentService;
import android.content.Intent;

import com.codingzen.Shuuush.DNDWidgetProvider;
import com.codingzen.Shuuush.DeviceAudioManager;
import com.codingzen.Shuuush.Event;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.WidgetNotificationManager;

public class StopShuuushService extends IntentService {

	public StopShuuushService() {
		super("TurnOffDndService");
	}

	@Override
	public void onHandleIntent(Intent intent) {

		LogManager.i("TurnOffDndService started");
		stopDnd();
		this.stopSelf();

	}

	private void stopDnd() {
		// Change preferences to show DND is disabled
		Preferences.putBoolean(Preferences.IsShuuushEnabled, false);

		// Set ringer mode to NORMAL
		new DeviceAudioManager().setRingerModeNormal();

		DNDWidgetProvider.updateWidget(this, false);

		// Remove the notification in the notification window
		WidgetNotificationManager.removeNotification();

		notifyIfThereAreEventsToView();
	}

	private void notifyIfThereAreEventsToView() {
		int eventCount = Event.count(getApplicationContext());
		if (eventCount > 0) {
			WidgetNotificationManager.showNotification("You have missed events to respond to.", eventCount, false);
		}
	}
}
