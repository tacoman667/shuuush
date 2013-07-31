package com.codingzen.Shuuush.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.codingzen.Shuuush.DNDWidgetProvider;
import com.codingzen.Shuuush.DeviceAudioManager;
import com.codingzen.Shuuush.Event;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.WidgetNotificationManager;

public class StartShuuushService extends IntentService {

	public StartShuuushService() {
		super("TurnOnDndService");
	}

	@Override
	public void onHandleIntent(Intent intent) {

		LogManager.i("TurnOnDndService started");

		Boolean isManualStart = intent.getExtras().getBoolean("isManualStart");
		LogManager.i("isManualStart extra value is " + isManualStart);
		LogManager.i("EventId being started is " + intent.getExtras().getString("eventid"));

		startDnd(isManualStart);
		this.stopSelf();

	}

	private void startDnd(Boolean isManualStart) {
		// Change preferences to reflect DND is enabled
		Preferences.putBoolean(Preferences.IsShuuushEnabled, true);

		// Set ringer mode to SILENT
		new DeviceAudioManager().setRingerModeSilent();

		if (!Preferences.getBoolean(Preferences.ShouldEnableFromCalendarEvents)) {
			startNewSessionInDatabase(this);
		}

		DNDWidgetProvider.updateWidget(this, true);

		Preferences.putBoolean(Preferences.IsManualStart, isManualStart);

		// Add notification to notification window
		WidgetNotificationManager.showNotification();
	}

	private void startNewSessionInDatabase(Context context) {
		Event.removeAll(context);
	}

}
