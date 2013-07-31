package com.codingzen.Shuuush;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;

public class ContactsManager {

	private final static String[] properties = new String[] { PhoneLookup._ID, PhoneLookup.DISPLAY_NAME, PhoneLookup.PHOTO_ID };

	public static Contact getContactsByNumber(String phoneNumber) {
		String number = PhoneNumberUtils.formatNumber(phoneNumber);
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Contact contact = getContact(uri);
		return contact;
	}

	private static Contact getContact(Uri uri) {
		ContentResolver resolver = ShuuushApplication.getAppContext().getContentResolver();
		Cursor cursor = resolver.query(uri, properties, null, null, null);
		Contact contact = null;

		if (cursor.moveToFirst()) {
			contact = new Contact();
			contact._Id = cursor.getInt(0);
			contact.Name = cursor.getString(1);
			contact.PhotoId = cursor.getInt(2);
			contact.PhotoUri = getPhotoUri(contact);
		}

		cursor.close();

		return contact;
	}

	public static Uri getPhotoUri(Contact contact) {
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact._Id);
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	public static Contact getContactByUri(Uri uri) {
		Contact contact = getContact(uri);

		ContentResolver resolver = ShuuushApplication.getAppContext().getContentResolver();
		Uri phoneLookupUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, uri.getLastPathSegment());
		Cursor cursor = resolver.query(phoneLookupUri, new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);

		if (cursor.moveToFirst()) {
			contact.PhoneNumber = cursor.getString(0);
		}

		cursor.close();

		return contact;
	}

}
