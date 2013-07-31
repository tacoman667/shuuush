package com.codingzen.Shuuush.Services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.Preferences;
import com.codingzen.Shuuush.R;

public class SendSmsReplyService extends IntentService {

	public SendSmsReplyService() {
		super("SendSmsReplyService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LogManager.i("SendSmsReplyService onHandleIntent fired.");

		sendSmsReply(intent);

		this.stopSelf();
	}

	private void sendSmsReply(Intent intent) {

		TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String thisPhoneNumber = tMgr.getLine1Number();

		String originatingAddress = intent.getExtras().getString("smsSenderNumber");

		if (thisPhoneNumber.equals(originatingAddress)) {
			LogManager.i("No Sms Sent. Cannot Sms Self");
			return;
		}

		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

		String text = "";

		if (Preferences.getBoolean(Preferences.ShouldUseDefaultMessage)) {
			text = getString(R.string.DefaultSmsReply);
		} else {
			text = Preferences.getString(Preferences.SmsAutoresponderText);
		}

		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(originatingAddress, null, text, sentPI, deliveredPI);

		LogManager.i("SMS Reply Sent");
	}

}
