package com.codingzen.Shuuush;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class PrivateTelephonyManager {

	private static ITelephony getManager() {
		TelephonyManager tm = (TelephonyManager) ShuuushApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
		try {
			// Java reflection to gain access to TelephonyManager's
			// ITelephony getter
			LogManager.i("Get getTeleService...");
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephonyService = (ITelephony) m.invoke(tm);
			return telephonyService;
		} catch (Exception e) {
			LogManager.e("Using endCall() from ITelephony private API", e);
		}

		return null;
	}

	public static void endCall() {
		ITelephony manager = getManager();
		if (manager != null) {
			try {
				manager.endCall();
			} catch (RemoteException e) {
				LogManager.e("Tried to call ITelephony.endCall()", e);
			}
		}
	}

	public static void cancelMissedCallsNotification() {
		ITelephony manager = getManager();
		if (manager != null) {
			try {
				manager.cancelMissedCallsNotification();
			} catch (RemoteException e) {
				LogManager.e("Tried to call ITelephony.cancelMissedCallsNotification()", e);
			}
		}
	}
}
