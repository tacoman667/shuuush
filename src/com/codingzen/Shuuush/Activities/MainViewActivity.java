package com.codingzen.Shuuush.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.Event;
import com.codingzen.Shuuush.EventsAdapter;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.R;
import com.codingzen.Shuuush.ShuuushApplication;
import com.codingzen.Shuuush.ToastManager;
import com.codingzen.Shuuush.WidgetNotificationManager;

public class MainViewActivity extends Activity {

	UpdateMissedEventsListViewReciever receiver;
	EventsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(null);
		setContentView(R.layout.main_view);

		setupSettingsButton();

		setupActivateDeactivateButton();

		setupReciever();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupIncidentsTable();

		Boolean isActive = Preferences.getBoolean(Preferences.IsShuuushEnabled);
		if (isActive) {
			WidgetNotificationManager.showNotification();
		}

	}

	private void setupReciever() {
		if (receiver != null) {
			ShuuushApplication.getAppContext().unregisterReceiver(receiver);
		}
		receiver = new UpdateMissedEventsListViewReciever();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ApplicationIntentActions.UPDATE_EVENTS_LISTVIEW);
		ShuuushApplication.getAppContext().registerReceiver(receiver, filter);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ShuuushApplication.getAppContext().unregisterReceiver(receiver);
		super.finish();
	}

	private void setupActivateDeactivateButton() {
		Button activateDeactivateButton = (Button) findViewById(R.id.activateDeactivateButton);

		activateDeactivateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Boolean isActive = Preferences.getBoolean(Preferences.IsShuuushEnabled);
				String action = null;

				if (isActive) {
					action = "com.codingzen.action.STOP_SHUUUSH";
					ToastManager.showToast(getApplicationContext(), getString(R.string.app_name) + " Has Been Deactivated");
				} else {
					action = "com.codingzen.action.START_SHUUUSH";
					ToastManager.showToast(getApplicationContext(), getString(R.string.app_name) + " Has Been Activated");
				}

				Intent intent = new Intent(action);
				intent.putExtra("isManualStart", true);
				startService(intent);
			}
		});
	}

	private void setupSettingsButton() {
		Button settingsButton = (Button) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainPreferenceActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
	}

	private void setupIncidentsTable() {
		final ListView listView = (ListView) findViewById(R.id.listView1);
		ArrayList<Event> events = Event.all(getApplicationContext());

		if (listView.getHeaderViewsCount() == 0) {
			TextView view = (TextView) getLayoutInflater().inflate(R.layout.events_list_header, null);
			listView.addHeaderView(view);
		}

		adapter = new EventsAdapter(this, events);
		listView.setAdapter(adapter);

	}

	private class UpdateMissedEventsListViewReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			Event event = Event.find(intent.getExtras().getLong("event_id"));
			LogManager.i("Event: " + ((event == null) ? "null" : event.toString()));
			if (event != null) {
				adapter.insert(event, 0);
				adapter.notifyDataSetChanged();
				LogManager.i("Event added to adapter");
			}

		}
	}

}
