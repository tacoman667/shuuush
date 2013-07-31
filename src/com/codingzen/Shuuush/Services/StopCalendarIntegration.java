package com.codingzen.Shuuush.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;

public class StopCalendarIntegration extends IntentService {

	public StopCalendarIntegration() {
		super("StopCalendarIntegration");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		LogManager.i("Stopped Calendar Integration");

		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ApplicationIntentActions.CALENDAR_INTEGRATION), 0);

		AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
		manager.cancel(pi);

		stopAnyShuuushAlarms();

		this.stopSelf();

	}

	private void stopAnyShuuushAlarms() {

		AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

		PendingIntent startPi = PendingIntent.getService(getApplicationContext(), 0, new Intent(ApplicationIntentActions.START_SHUUUSH), 0);
		manager.cancel(startPi);

		PendingIntent endPi = PendingIntent.getService(getApplicationContext(), 0, new Intent(ApplicationIntentActions.STOP_SHUUUSH), 0);
		manager.cancel(endPi);

		Preferences.putInt(Preferences.LastEventIdScheduled, 0);
	}

}
