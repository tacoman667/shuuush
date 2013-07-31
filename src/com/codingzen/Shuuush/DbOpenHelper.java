package com.codingzen.Shuuush;

import java.lang.reflect.Field;
import java.util.Hashtable;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Shuuush.db";
	public static final int DATABASE_VERSION = 5;
	public static final String TABLE_NAME_EVENTS = "Events";
	public static final String TABLE_NAME_WHITELIST = "Whitelist";

	private static final String TEXT = "TEXT";
	private static final String DATETIME = "DATETIME";
	private static final String INTEGER = "INTEGER";

	public DbOpenHelper() {
		super(ShuuushApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createEventsTable(db);
		createWhitelistTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EVENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WHITELIST);
		onCreate(db);
	}

	private void createEventsTable(SQLiteDatabase db) {
		Field[] fields = Event.class.getDeclaredFields();
		Hashtable<String, String> eventColumns = getColumnsFrom(fields);

		createTable(db, TABLE_NAME_EVENTS, eventColumns);

	}

	private void createWhitelistTable(SQLiteDatabase db) {
		Field[] fields = WhitelistItem.class.getDeclaredFields();
		Hashtable<String, String> whitelistItemColumns = getColumnsFrom(fields);

		createTable(db, TABLE_NAME_WHITELIST, whitelistItemColumns);

	}

	public static Hashtable<String, String> getColumnsFrom(Field[] fields) {
		Hashtable<String, String> columns = new Hashtable<String, String>();

		for (Field field : fields) {
			String type = field.getType().getSimpleName();
			String colName = field.getName();
			String colType = "";

			if (type.equals("String")) {
				colType = TEXT;
			} else if (type.equals("int")) {
				colType = INTEGER;
			} else if (type.equals("Date")) {
				colType = DATETIME;
			} else if (type.equals("long")) {
				colType = INTEGER;
			}

			if (colName.toLowerCase().equals("id")) {
				colType += " PRIMARY KEY";
			}

			columns.put(colName, colType);
		}
		return columns;
	}

	private void createTable(SQLiteDatabase db, String tableName, Hashtable<String, String> columns) {

		String cols = "";
		for (String key : columns.keySet()) {
			if (cols.length() > 0) {
				cols += ", ";
			}
			cols += key + " " + columns.get(key);
		}

		db.execSQL("CREATE TABLE " + tableName + " (" + cols + ")");
	}

}
