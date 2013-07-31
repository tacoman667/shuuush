package com.codingzen.Shuuush.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.R;
import com.codingzen.Shuuush.ShuuushApplication;

public class MainPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		PreferenceManager.setDefaultValues(this, R.xml.main_preferences, false);

		LogManager.i(getLocalClassName() + ".onCreate was called");

		LogManager.i("OS Version: " + android.os.Build.VERSION.RELEASE);

		Preference calendarEventsPreference = findPreference("ShouldEnableFromCalendarEvents");
		calendarEventsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				String action = null;

				CheckBoxPreference cbp = (CheckBoxPreference) preference;

				if (cbp.isChecked()) {
					action = ApplicationIntentActions.START_CALENDAR_INTEGRATION;
					LogManager.i("Preference: " + cbp.isChecked());
				} else {
					action = ApplicationIntentActions.STOP_CALENDAR_INTEGRATION;
					LogManager.i("Preference: " + cbp.isChecked());
				}

				Intent intent = new Intent(action);
				startService(intent);

				return true;
			}
		});

		Preference whitelistPreference = findPreference("whitelist");
		whitelistPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(ShuuushApplication.getAppContext(), WhitelistActivity.class);
				startActivity(intent);
				return true;
			}
		});

	}

}
