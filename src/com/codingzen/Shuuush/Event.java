package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.content.Context;
import android.telephony.PhoneNumberUtils;

public class Event {

	public long ID;
	public String ContactName;
	public String PhoneNumber;
	public String Message;
	public String TimeStamp;
	public String Type;

	private static final int MAX_MESSAGE_CHARS = 22;

	public static final String TYPE_TEXT = "sms";
	public static final String TYPE_INCOMING_CALL = "call";
	public static final String TYPE_NONE = "none";

	public Event() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (this.ContactName != null && !this.ContactName.equals("")) {
			builder.append(this.ContactName);
		} else {
			builder.append(PhoneNumberUtils.formatNumber(this.PhoneNumber));
		}

		if (this.Type.equals(TYPE_TEXT)) {
			builder.append("\n");
			if (this.Message.length() > MAX_MESSAGE_CHARS) {
				builder.append(this.Message.substring(0, MAX_MESSAGE_CHARS));
				builder.append(" (...)");
			} else {
				builder.append(this.Message);
			}
		} else if (this.Type.equals(TYPE_INCOMING_CALL)) {
			builder.append("\nMissed Call");
		}

		return builder.toString();
	}

	public long save(Context context) {
		return new ProviderFactory().resolve(EventsProvider.class).save(this);
	}

	public static ArrayList<Event> all(Context context) {
		return new ProviderFactory().resolve(EventsProvider.class).all();
	}

	public static void removeAll(Context context) {
		new ProviderFactory().resolve(EventsProvider.class).removeAll();
	}

	public static int count(Context context) {
		return new ProviderFactory().resolve(EventsProvider.class).count();
	}

	public void delete() {
		new ProviderFactory().resolve(EventsProvider.class).remove(this);
	}

	public static Event find(long id) {
		return new ProviderFactory().resolve(EventsProvider.class).find(id);
	}

}
