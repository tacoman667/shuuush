package com.codingzen.Shuuush;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {

	public static final String IsShuuushEnabled = "IsShuuushEnabled";
	public static final String SmsAutoresponder = "SmsAutoresponder";
	public static final String ShouldUseDefaultMessage = "ShouldUseDefaultMessage";
	public static final String SmsAutoresponderText = "SmsAutoresponderText";
	public static final String OnlyAutorespondToContacts = "OnlyAutorespondToContacts";
	public static final String ShouldEnableFromCalendarEvents = "ShouldEnableFromCalendarEvents";
	public static final String LastEventIdScheduled = "LastEventIdScheduled";
	public static final String IsManualStart = "IsManualStart";
	public static final String IsWhitelistEvent = "IsWhitelistEvent";

	private static SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ShuuushApplication.getAppContext());
	}

	public static void putBoolean(String key, Boolean value) {
		Editor editor = getSharedPreferences().edit();
		editor.putBoolean(key, value);
		editor.commit();
		LogManager.i("Setting [" + key + "] set to [" + value + "]");
	}

	public static Boolean getBoolean(String key) {
		return getSharedPreferences().getBoolean(key, false);
	}

	public static String getString(String key) {
		return getSharedPreferences().getString(key, "");
	}

	public static void putInt(String key, int value) {
		Editor editor = getSharedPreferences().edit();
		editor.putInt(key, value);
		editor.commit();
		LogManager.i("Setting [" + key + "] set to [" + value + "]");
	}

	public static int getInt(String key) {
		return getSharedPreferences().getInt(key, 0);
	}
}
