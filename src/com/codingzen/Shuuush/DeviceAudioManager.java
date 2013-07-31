package com.codingzen.Shuuush;

import android.content.Context;
import android.media.AudioManager;

public class DeviceAudioManager {

	public void setRingerModeSilent() {
		AudioManager manager = (AudioManager) ShuuushApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
		manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		LogManager.i("Ringer set to SILENT");
	}

	public void setRingerModeNormal() {
		AudioManager manager = (AudioManager) ShuuushApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
		manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		LogManager.i("Ringer set to NORMAL");
	}

	public int getCurrentMode() {
		AudioManager manager = (AudioManager) ShuuushApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
		return manager.getRingerMode();
	}

}
