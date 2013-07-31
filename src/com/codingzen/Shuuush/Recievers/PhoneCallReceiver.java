package com.codingzen.Shuuush.Recievers;

import java.text.DateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.Contact;
import com.codingzen.Shuuush.ContactsManager;
import com.codingzen.Shuuush.DeviceAudioManager;
import com.codingzen.Shuuush.Event;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.PrivateTelephonyManager;
import com.codingzen.Shuuush.ShuuushApplication;
import com.codingzen.Shuuush.WhitelistItem;
import com.codingzen.Shuuush.WidgetNotificationManager;

public class PhoneCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (ShuuushApplication.isActive()) {
			String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

			String incoming_number = intent.getExtras().getString("incoming_number");

			if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				DeviceAudioManager manager = new DeviceAudioManager();
				if (manager.getCurrentMode() == AudioManager.RINGER_MODE_NORMAL) {
					manager.setRingerModeSilent();
				}
			} else if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				String number = PhoneNumberUtils.formatNumber(incoming_number);
				Contact contact = ContactsManager.getContactsByNumber(number);
				if (contact != null) {
					WhitelistItem item = WhitelistItem.find(contact._Id);
					if (item != null) {
						LogManager.i("isWhitelistEvent: true");
						Preferences.putBoolean(Preferences.IsWhitelistEvent, true);
						new DeviceAudioManager().setRingerModeNormal();
					} else {
						PrivateTelephonyManager.endCall();
					}
				} else {
					PrivateTelephonyManager.endCall();
				}
			} else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				if (incoming_number != null) {
					String number = PhoneNumberUtils.formatNumber(incoming_number);
					Contact contact = ContactsManager.getContactsByNumber(number);
					logCall(number, contact);
				}
				DeviceAudioManager manager = new DeviceAudioManager();
				if (manager.getCurrentMode() == AudioManager.RINGER_MODE_NORMAL) {
					manager.setRingerModeSilent();
					Preferences.putBoolean(Preferences.IsWhitelistEvent, false);
				}
			}

		}

	}

	public void updateWidget() {
		WidgetNotificationManager.updateNotification("Missed Call");
	}

	private void logCall(String number, Contact contact) {
		String name = "";
		if (contact != null) {
			name = contact.Name;
		}

		Event event = new Event();
		event.ContactName = name;
		event.PhoneNumber = number;
		event.Message = "Missed Call";
		event.Type = Event.TYPE_INCOMING_CALL;
		event.TimeStamp = DateFormat.getDateTimeInstance().format(new Date());

		event.save(ShuuushApplication.getAppContext());

		updateWidget();

		Intent updateEventsListviewIntent = new Intent(ApplicationIntentActions.UPDATE_EVENTS_LISTVIEW);
		updateEventsListviewIntent.putExtra("event_id", event.ID);
		ShuuushApplication.getAppContext().sendBroadcast(updateEventsListviewIntent);
	}

}
