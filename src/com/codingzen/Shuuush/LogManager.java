package com.codingzen.Shuuush;

import android.util.Log;

public class LogManager {

	private static final String packageName = "com.codingzen.shuuush";

	public static void i(String text) {
		if (ShuuushApplication.isDebugMode()) {
			Log.i(packageName, text);
		}
	}

	public static void e(String message, Exception ex) {
		if (ShuuushApplication.isDebugMode()) {
			Log.e(packageName, message);
			e(ex);
		}
	}

	public static void e(Exception ex) {
		if (ShuuushApplication.isDebugMode()) {

			Log.e(packageName, ex.getMessage());

			ex.printStackTrace();

		}
	}
}
