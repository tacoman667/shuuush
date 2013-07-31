package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class EventsProvider {

	static final String TABLE_NAME_EVENTS = DbOpenHelper.TABLE_NAME_EVENTS;
	private static final String[] columns = new String[] { "Id", "ContactName", "PhoneNumber", "Message", "Type", "TimeStamp" };

	public EventsProvider() {
	}

	public long save(Event event) {
		SQLiteDatabase db = openDb();

		String INSERT = "insert into " + TABLE_NAME_EVENTS + " (ContactName, PhoneNumber, Message, Type, TimeStamp) values (?, ?, ?, ?, ?)";
		SQLiteStatement insertEventStmt = db.compileStatement(INSERT);

		if (event.ContactName != null) {
			insertEventStmt.bindString(1, event.ContactName);
		} else {
			insertEventStmt.bindNull(1);
		}

		insertEventStmt.bindString(2, event.PhoneNumber);
		insertEventStmt.bindString(3, event.Message);
		insertEventStmt.bindString(4, event.Type);
		insertEventStmt.bindString(5, event.TimeStamp);

		long result = insertEventStmt.executeInsert();

		closeDb(db);

		event.ID = result;

		return result;
	}

	public ArrayList<Event> all() {

		SQLiteDatabase db = openDb();

		ArrayList<Event> list = new ArrayList<Event>();

		Cursor cursor = db.query(TABLE_NAME_EVENTS, columns, null, null, null, null, "TimeStamp desc");
		int x = 0;
		if (cursor.moveToFirst()) {
			do {
				Event event = new Event();
				event.ID = cursor.getInt(0);
				event.ContactName = cursor.getString(1);
				event.PhoneNumber = cursor.getString(2);
				event.Message = cursor.getString(3);
				event.Type = cursor.getString(4);
				event.TimeStamp = cursor.getString(5);
				list.add(event);
				x = x + 1;
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();

		closeDb(db);

		return list;

	}

	private SQLiteDatabase openDb() {
		DbOpenHelper openHelper = new DbOpenHelper();
		SQLiteDatabase db = openHelper.getWritableDatabase();
		return db;
	}

	private void closeDb(SQLiteDatabase db) {
		if (db.isOpen()) {
			db.close();
		}
	}

	public void removeAll() {
		SQLiteDatabase db = openDb();
		db.delete(TABLE_NAME_EVENTS, null, null);
		closeDb(db);
	}

	public int count() {
		SQLiteDatabase db = openDb();

		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_EVENTS, null);
		int count = 0;

		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		cursor.close();

		closeDb(db);

		return count;
	}

	public void remove(Event event) {
		SQLiteDatabase db = openDb();
		db.delete(TABLE_NAME_EVENTS, "Id = ?", new String[] { String.valueOf(event.ID) });
		closeDb(db);
	}

	public Event find(long id) {
		SQLiteDatabase db = openDb();
		Event event = null;
		Cursor cursor = db.query(TABLE_NAME_EVENTS, columns, "Id = ?", new String[] { String.valueOf(id) }, null, null, "TimeStamp desc");

		if (cursor.moveToFirst()) {
			event = new Event();
			event.ID = cursor.getInt(0);
			event.ContactName = cursor.getString(1);
			event.PhoneNumber = cursor.getString(2);
			event.Message = cursor.getString(3);
			event.Type = cursor.getString(4);
			event.TimeStamp = cursor.getString(5);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();

		closeDb(db);

		return event;
	}

}
