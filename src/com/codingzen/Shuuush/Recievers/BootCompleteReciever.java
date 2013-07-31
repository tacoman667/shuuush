package com.codingzen.Shuuush.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;

public class BootCompleteReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// WidgetNotificationManager.showBasicNotification(context.getApplicationContext());
		LogManager.i("Boot Complete");

		Boolean isCalendarIntegrationEnabled = Preferences.getBoolean(Preferences.ShouldEnableFromCalendarEvents);

		if (isCalendarIntegrationEnabled) {
			startCalendarIntegrationService(context);
		}

		// registerProximitSensorIntent(context);

	}

	private void startCalendarIntegrationService(Context context) {
		Intent intent = new Intent(ApplicationIntentActions.START_CALENDAR_INTEGRATION);
		context.startService(intent);
	}

	@SuppressWarnings("unused")
	private void registerProximitSensorIntent(Context context) {
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor proximitySensor = manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		SensorEventListener listener = new SensorListener(context);
		manager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI);
	}

	public class SensorListener implements SensorEventListener {

		Context _context;

		public SensorListener(Context context) {
			_context = context;
		}

		public final void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Do something here if sensor accuracy changes.
		}

		public final void onSensorChanged(SensorEvent event) {
			float distance = event.values[0];
			if (distance < 1.5) {
				Intent intent = new Intent(ApplicationIntentActions.START_SHUUUSH);
				_context.startService(intent);
			} else {
				Intent intent = new Intent(ApplicationIntentActions.STOP_SHUUUSH);
				_context.startService(intent);
			}
		}

	}
}
