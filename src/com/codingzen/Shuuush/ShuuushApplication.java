package com.codingzen.Shuuush;

import android.app.Application;
import android.content.Context;

public class ShuuushApplication extends Application {

	private static Context context;
	private static Boolean isDebugMode;

	@Override
	public void onCreate() {
		super.onCreate();

		ShuuushApplication.context = getApplicationContext();

		isDebugMode = Boolean.valueOf(getAppContext().getString(R.string.isDebugMode));

		// new WhitelistProvider().removeAll();
		// new EventsProvider().removeAll();
	}

	public static Context getAppContext() {
		return ShuuushApplication.context;
	}

	public static Boolean isActive() {
		return Preferences.getBoolean(Preferences.IsShuuushEnabled);
	}

	public static Boolean isDebugMode() {
		return isDebugMode;
	}

}
