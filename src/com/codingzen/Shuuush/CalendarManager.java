package com.codingzen.Shuuush;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

public class CalendarManager {

	private final Context Context;
	private final Uri instancesUri = Uri.parse("content://com.android.calendar/instances/when");
	private final Uri calendarsUri = Uri.parse("content://com.android.calendar/calendars");
	private final Uri eventsUri = Uri.parse("content://com.android.calendar/events");

	public CalendarManager(Context context) {
		this.Context = context;
	}

	public void getCalendars() {
		Uri.Builder builder = calendarsUri.buildUpon();
		ContentResolver contentResolver = this.Context.getContentResolver();
		final Cursor cursor = contentResolver.query(builder.build(), null, null, null, null);

		LogManager.i("----  Calendar -----");
		logColumnsForDebug(cursor);

		cursor.close();

	}

	public ArrayList<CalendarEvent> getEvents() {
		ArrayList<CalendarEvent> events = null;

		Uri.Builder builder = eventsUri.buildUpon();
		long now = new Date().getTime();

		String whereClause = "dtend > 0 AND dtstart > " + now + " AND dtstart < " + (now * DateUtils.MINUTE_IN_MILLIS * 15);

		final Cursor cursor = getCursor(builder.build(), null, whereClause, null, "dtstart asc");

		int rowCount = cursor.getCount();

		if (rowCount > 0) {
			events = new ArrayList<CalendarEvent>();
		}

		if (cursor.moveToFirst()) {
			LogManager.i("----  Event -----");
			logColumnsForDebug(cursor);
			CalendarEvent event = new CalendarEvent() {
				{
					EventId = getColumnIntValue(cursor, "_id");
					Title = getColumnStringValue(cursor, "title");
					Start = getColumnLongValue(cursor, "dtstart");
					End = getColumnLongValue(cursor, "dtend");
					Description = getColumnStringValue(cursor, "description");
					Availability = getColumnIntValue(cursor, "availability");
					IsAvailabilityBusy = Availability == CalendarEvent.AVAILABILITY_BUSY;
				}
			};

			// Availability is missing from private calendar provider apis. set
			// them
			// to busy status so that calendar will integrate.
			if (event.Availability == -1) {
				event.IsAvailabilityBusy = true;
			}

			events.add(event);
		}

		cursor.close();

		return events;
	}

	public ArrayList<CalendarInstance> getInstances() {
		ArrayList<CalendarInstance> instances = null;

		Uri.Builder builder = instancesUri.buildUpon();
		long now = new Date().getTime();

		ContentUris.appendId(builder, now - DateUtils.MINUTE_IN_MILLIS * 0);
		ContentUris.appendId(builder, now + DateUtils.MINUTE_IN_MILLIS * 15);

		final Cursor cursor = getCursor(builder.build(), null, "allDay != 1 and begin >= " + now, null, "begin asc");

		if (cursor == null) {
			ToastManager.showToast(this.Context, "You do not have any calendars set up to integrate with.");
			Preferences.putBoolean(Preferences.ShouldEnableFromCalendarEvents, false);
			return null;
		}

		int rowCount = cursor.getCount();

		if (rowCount > 0) {
			instances = new ArrayList<CalendarInstance>();
		}

		while (cursor.moveToNext()) {
			LogManager.i("----  Instance -----");
			logColumnsForDebug(cursor);

			CalendarInstance instance = new CalendarInstance() {
				{
					EventId = getColumnIntValue(cursor, "event_id");
					Title = getColumnStringValue(cursor, "title");
					Begin = getColumnLongValue(cursor, "begin");
					End = getColumnLongValue(cursor, "end");
					Description = getColumnStringValue(cursor, "description");
					Availability = getColumnIntValue(cursor, "availability");
					IsAvailabilityBusy = Availability == CalendarEvent.AVAILABILITY_BUSY;
					AllDay = getColumnIntValue(cursor, "allDay");
					IsAllDayEvent = (AllDay == 1);
				}
			};

			// Availability is missing from private calendar provider apis. set
			// them
			// to busy status so that calendar will integrate.
			if (instance.Availability == -1) {
				instance.IsAvailabilityBusy = true;
			}

			instances.add(instance);
		}

		cursor.close();

		return instances;
	}

	private int getColumnIntValue(Cursor cursor, String column) {
		try {
			return cursor.getInt(cursor.getColumnIndex(column));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return -1;
	}

	private long getColumnLongValue(Cursor cursor, String column) {
		try {
			return cursor.getLong(cursor.getColumnIndex(column));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return -1;
	}

	private String getColumnStringValue(Cursor cursor, String column) {
		try {
			return cursor.getString(cursor.getColumnIndex(column));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";
	}

	private Cursor getCursor(Uri contentUri, String[] columns, String selection, ArrayList<String> selectionArgs, String sortOrder) {
		ContentResolver contentResolver = this.Context.getContentResolver();
		return contentResolver.query(contentUri, null, selection, null, sortOrder);
	}

	private void logColumnsForDebug(Cursor cursor) {
		for (int i = 0; i < cursor.getColumnCount(); i++) {
			String value = "";
			try {
				value = cursor.getString(i);
			} catch (Exception e) {
				// TODO: handle exception
			}
			LogManager.i("Column: " + cursor.getColumnName(i) + " -- Value: " + value);
		}
		LogManager.i("--------------------------------------------");
	}
}
