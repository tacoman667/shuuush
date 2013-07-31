package com.codingzen.Shuuush.Recievers;

import java.text.DateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import com.codingzen.Shuuush.ApplicationIntentActions;
import com.codingzen.Shuuush.Contact;
import com.codingzen.Shuuush.ContactsManager;
import com.codingzen.Shuuush.DeviceAudioManager;
import com.codingzen.Shuuush.Event;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.ShuuushApplication;
import com.codingzen.Shuuush.WhitelistItem;
import com.codingzen.Shuuush.WidgetNotificationManager;
import com.codingzen.Shuuush.Services.SendSmsReplyService;

public class SmsMessageReceivedReceiver extends BroadcastReceiver {

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogManager.i("SMS was recieved.");

		this.context = context;

		if (ShuuushApplication.isActive()) {

			SenderInfo info = getSmsSenderInfo(intent);
			WhitelistItem item = WhitelistItem.find(info.ContactId);

			if (item != null) {
				notifySmsAlert();
			} else {
				addCommunicationToDatabase(info);
				autoReplyIfEnabled(info);
			}
		}

	}

	private void notifySmsAlert() {
		final DeviceAudioManager manager = new DeviceAudioManager();
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}

		LogManager.i("Alert Uri: " + alert);

		manager.setRingerModeNormal();
		try {
			MediaPlayer mMediaPlayer = MediaPlayer.create(ShuuushApplication.getAppContext(), alert);
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					LogManager.i("Alert sound has completed.");
					manager.setRingerModeSilent();
				}
			});
		} catch (Exception e) {
			LogManager.e("Error with SmsMessageReceivedReceiver.notifySmsAlert", e);
		}
	}

	private void autoReplyIfEnabled(SenderInfo info) {
		Boolean isSmsReplyEnabled = Preferences.getBoolean(Preferences.SmsAutoresponder);
		Boolean onlyAutorespondToContacts = Preferences.getBoolean(Preferences.OnlyAutorespondToContacts);

		if (isSmsReplyEnabled) {
			if (onlyAutorespondToContacts) {
				if (info.ContactName != null && !info.ContactName.equals("")) {
					startSendSmsReplyServiceIntent(info);
				}
			} else {
				startSendSmsReplyServiceIntent(info);
			}
		}
	}

	private void startSendSmsReplyServiceIntent(SenderInfo info) {
		Intent sendSmsReplyServiceIntent = new Intent(this.context, SendSmsReplyService.class);
		sendSmsReplyServiceIntent.putExtra("smsSenderNumber", info.OriginatingAddress);
		this.context.startService(sendSmsReplyServiceIntent);
	}

	public void updateWidget() {
		WidgetNotificationManager.updateNotification("Received Text");
	}

	private void addCommunicationToDatabase(final SenderInfo info) {

		Event event = new Event() {
			{
				ContactName = info.ContactName;
				PhoneNumber = info.OriginatingAddress;
				Message = info.Message;
				Type = Event.TYPE_TEXT;
				TimeStamp = DateFormat.getDateTimeInstance().format(new Date());
			}
		};

		event.save(this.context);

		LogManager.i("SMS Event Recorded in database.");

		updateWidget();

		Intent intent = new Intent(ApplicationIntentActions.UPDATE_EVENTS_LISTVIEW);
		intent.putExtra("event_id", event.ID);
		ShuuushApplication.getAppContext().sendBroadcast(intent);

	}

	private SenderInfo getSmsSenderInfo(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null)
			return null;

		Object[] pdus = (Object[]) extras.get("pdus");
		SmsMessage message = null;

		message = SmsMessage.createFromPdu((byte[]) pdus[0]);

		String number = PhoneNumberUtils.formatNumber(message.getOriginatingAddress());
		Contact contact = ContactsManager.getContactsByNumber(number);

		SenderInfo info;

		if (contact == null) {
			info = new SenderInfo(-1, null, number, message.getDisplayMessageBody());
		} else {
			info = new SenderInfo(contact._Id, contact.Name, number, message.getDisplayMessageBody());
		}

		return info;
	}

	private class SenderInfo {

		public long ContactId;
		public String ContactName;
		public String OriginatingAddress;
		public String Message;

		public SenderInfo(long contactId, String contactName, String originatingAddress, String message) {
			this.ContactId = contactId;
			this.OriginatingAddress = originatingAddress;
			this.Message = message;
			this.ContactName = contactName;
		}
	}

}
