package com.codingzen.Shuuush.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codingzen.Shuuush.DeviceAudioManager;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;

public class RingerModeChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogManager.i(context.getClass() + ".onReceive called");

		Boolean isShuuushEnabled = Preferences.getBoolean(Preferences.IsShuuushEnabled);
		Boolean isWhitelistEvent = Preferences.getBoolean(Preferences.IsWhitelistEvent);

		if (isShuuushEnabled && !isWhitelistEvent) {
			new DeviceAudioManager().setRingerModeSilent();
		}
	}

}
