package com.codingzen.Shuuush.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codingzen.Shuuush.Contact;
import com.codingzen.Shuuush.ContactsManager;
import com.codingzen.Shuuush.LogManager;
import com.codingzen.Shuuush.R;
import com.codingzen.Shuuush.ShuuushApplication;
import com.codingzen.Shuuush.WhitelistAdapter;
import com.codingzen.Shuuush.WhitelistItem;

public class WhitelistActivity extends Activity {

	private static final int RESULT_CODE = 123456789;
	WhitelistAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whitelist_view);

		setupAddButton();

		setupWhitelistListView();

	}

	private void setupAddButton() {
		Button addButton = (Button) this.findViewById(R.id.whitelist_add);
		addButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
				startActivityForResult(contactPickerIntent, RESULT_CODE);
			}
		});
	}

	private void setupWhitelistListView() {
		ArrayList<WhitelistItem> items = WhitelistItem.all(ShuuushApplication.getAppContext());
		ListView lv = (ListView) this.findViewById(R.id.whitelist_items);

		if (lv.getHeaderViewsCount() == 0) {
			TextView view = (TextView) getLayoutInflater().inflate(R.layout.whitelist_list_header, null);
			lv.addHeaderView(view);
		}

		adapter = new WhitelistAdapter(this, items);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_CODE) {
			if (resultCode != RESULT_OK) {
				return;
			}

			Uri uri = data.getData();
			final Contact contact = ContactsManager.getContactByUri(uri);
			LogManager.i("Contact retreived: " + contact.Name);

			WhitelistItem item = new WhitelistItem() {
				{
					Id = contact._Id;
					Name = contact.Name;
					PhoneNumber = contact.PhoneNumber;
				}
			};

			long result = item.save(ShuuushApplication.getAppContext());
			if (result > -1) {
				LogManager.i("Contact saved to Whitelist: " + item.Name);
				adapter.add(item);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
