package com.codingzen.Shuuush.Services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.format.DateUtils;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.LogManager;

public class StartCalendarIntegrationService extends IntentService {

	public StartCalendarIntegrationService() {
		super("StartCalendarIntegrationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		LogManager.i("Started Calendar Integration");

		PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ApplicationIntentActions.CALENDAR_INTEGRATION), 0);

		AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS * 15, pi);

		this.stopSelf();

	}

}
