package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class WhitelistProvider {

	static final String TABLE_NAME_WHITELIST = DbOpenHelper.TABLE_NAME_WHITELIST;
	final String[] columns = new String[] { "Id", "Name", "PhoneNumber" };

	public long save(WhitelistItem item) {
		SQLiteDatabase db = openDb();

		// check if item exists already and return -1 if nothing is saved.
		if (find(item.Id) != null) {
			LogManager.i("Contact found in DB already, will not be inserted to DB - Name: " + item.Name + " Id: " + item.Id);
			return -1;
		}

		String INSERT = "insert into " + TABLE_NAME_WHITELIST + " (Id, Name, PhoneNumber) values (?, ?, ?)";
		SQLiteStatement insertItemStmt = db.compileStatement(INSERT);

		insertItemStmt.bindLong(1, item.Id);
		insertItemStmt.bindString(2, item.Name);
		if (item.PhoneNumber == null) {
			insertItemStmt.bindNull(3);
		} else {
			insertItemStmt.bindString(3, item.PhoneNumber);
		}

		long result = insertItemStmt.executeInsert();

		closeDb(db);

		LogManager.i("Contact saves as item to DB - Name: " + item.Name + " Id: " + item.Id);

		return result;
	}

	public ArrayList<WhitelistItem> all() {

		SQLiteDatabase db = openDb();

		ArrayList<WhitelistItem> list = new ArrayList<WhitelistItem>();

		Cursor cursor = db.query(TABLE_NAME_WHITELIST, columns, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				WhitelistItem item = new WhitelistItem();
				item.Id = cursor.getLong(0);
				item.Name = cursor.getString(1);
				item.PhoneNumber = cursor.getString(2);
				list.add(item);
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

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
		db.delete(TABLE_NAME_WHITELIST, null, null);
		closeDb(db);
	}

	public int count() {
		SQLiteDatabase db = openDb();

		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_WHITELIST, null);
		int count = 0;

		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		cursor.close();

		closeDb(db);

		return count;
	}

	public int remove(WhitelistItem item) {
		SQLiteDatabase db = openDb();
		int affectedRows = db.delete(TABLE_NAME_WHITELIST, "Id = ?", new String[] { String.valueOf(item.Id) });
		LogManager.i("" + affectedRows);
		closeDb(db);
		return affectedRows;
	}

	public WhitelistItem find(long id) {
		SQLiteDatabase db = openDb();
		WhitelistItem item = null;
		Cursor cursor = db.query(TABLE_NAME_WHITELIST, columns, "Id = ?", new String[] { String.valueOf(id) }, null, null, "Name asc");

		if (cursor.moveToFirst()) {
			item = new WhitelistItem();
			item.Id = cursor.getLong(0);
			item.Name = cursor.getString(1);
			item.PhoneNumber = cursor.getString(2);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		cursor.close();

		closeDb(db);

		return item;
	}
}
