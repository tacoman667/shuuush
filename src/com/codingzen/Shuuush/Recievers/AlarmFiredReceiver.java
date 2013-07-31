package com.codingzen.Shuuush.Recievers;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.CalendarEvent;
import com.codingzen.Shuuush.CalendarInstance;
import com.codingzen.Shuuush.CalendarManager;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;

public class AlarmFiredReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		LogManager.i("AlarmFiredReceiver onReceive Fired.");

		Boolean isCalendarIntegrationEnabled = Preferences.getBoolean(Preferences.ShouldEnableFromCalendarEvents);

		if (isCalendarIntegrationEnabled) {
			Boolean isActive = Preferences.getBoolean(Preferences.IsShuuushEnabled);

			if (!isActive) { // DND Is not active
				ArrayList<CalendarInstance> instances = getNextCalendarInstances(context);

				if (instances != null) {
					for (CalendarInstance instance : instances) {
						if (instance.IsAvailabilityBusy) {
							LogManager.i("Instance: " + instance.Title + " availability setting is BUSY");

							if (!instance.IsAllDayEvent) {
								setupStartAndEndScheduledIntents(context, instance.EventId, instance.Title, instance.Begin, instance.End);
							}
						}
					}
				}
			}
		}

	}

	private void setupStartAndEndScheduledIntents(Context context, int id, String title, long start, long end) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent startIntent = new Intent(ApplicationIntentActions.START_SHUUUSH);
		startIntent.putExtra("eventid", id);
		startIntent.putExtra("isManualStart", false);
		PendingIntent startPendingIntent = PendingIntent.getService(context, 0, startIntent, PendingIntent.FLAG_ONE_SHOT);
		manager.set(AlarmManager.RTC_WAKEUP, start, startPendingIntent);

		Intent endIntent = new Intent(ApplicationIntentActions.STOP_SHUUUSH);
		endIntent.putExtra("eventid", id);
		PendingIntent endPendingIntent = PendingIntent.getService(context, 0, endIntent, PendingIntent.FLAG_ONE_SHOT);
		manager.set(AlarmManager.RTC_WAKEUP, end, endPendingIntent);

		LogManager.i("Added Start and End intents for Event: " + title);

		Preferences.putInt(Preferences.LastEventIdScheduled, id);
	}

	private ArrayList<CalendarInstance> getNextCalendarInstances(Context context) {
		CalendarManager manager = new CalendarManager(context);
		ArrayList<CalendarInstance> instances = manager.getInstances();
		return instances;
	}

	@SuppressWarnings("unused")
	private ArrayList<CalendarEvent> getNextCalendarEvents(Context context) {
		CalendarManager manager = new CalendarManager(context);
		ArrayList<CalendarEvent> events = manager.getEvents();
		return events;
	}

}
