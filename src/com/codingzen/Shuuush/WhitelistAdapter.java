package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WhitelistAdapter extends ArrayAdapter<WhitelistItem> {

	private final LayoutInflater inflater;

	public WhitelistItem TouchedItem;
	WhitelistAdapter Adapter;

	private final SwipeTouchListener<WhitelistItem> gestureListener;

	private final ArrayList<AdapterUpdatedListener> adapterUpdatedListeners;

	public WhitelistAdapter(Context context, ArrayList<WhitelistItem> items) {
		super(context, -1, items);

		// Caches the LayoutInflater for quicker use
		this.inflater = LayoutInflater.from(context);
		Adapter = this;

		MyGestureDetector detector = new MyGestureDetector(new Runnable() {
			public void run() {
				Adapter.remove(TouchedItem);
			}
		});

		gestureListener = new SwipeTouchListener<WhitelistItem>(new GestureDetector(detector));
		gestureListener.addItemUpdatedFromViewTagListener(new ItemUpdatedFromViewTagListener() {
			public void itemUpdated(ItemUpdatedFromViewTagEvent itemUpdatedEvent) {
				TouchedItem = (WhitelistItem) itemUpdatedEvent.Item;
			}
		});

		this.setNotifyOnChange(true);

		this.adapterUpdatedListeners = new ArrayList<AdapterUpdatedListener>();

		if (super.getCount() == 0) {
			addEmptyItem();
		}
	}

	@Override
	public long getItemId(int position) throws IndexOutOfBoundsException {
		if (position < getCount() && position >= 0) {
			return position;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public void remove(WhitelistItem object) {
		int result = object.delete();
		if (result > 0) {
			super.remove(object);
			this.notifyDataSetChanged();
			AdapterUpdatedEvent item = new AdapterUpdatedEvent(this, this.getCount());
			invokeAdapterUpdatedListeners(item);
			WidgetNotificationManager.updateNotification("Whitelist Item Removed");
		}

		if (super.getCount() == 0) {
			addEmptyItem();
		}
	}

	private void addEmptyItem() {
		super.add(new WhitelistItem() {
			{
				Id = -1;
			}
		});
	}

	@Override
	public WhitelistItem getItem(int position) {
		WhitelistItem item;

		try {
			item = super.getItem(position);
		} catch (Exception e) {
			return null;
		}

		return item;
	}

	public Boolean hasItem(WhitelistItem item) {
		for (int i = 0; i < super.getCount(); i++) {
			WhitelistItem wi = super.getItem(i);
			if (wi.Id == item.Id) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void add(WhitelistItem object) {
		WhitelistItem item = this.getItem(0);
		if (item != null && item.Id == -1) {
			super.remove(item);
		}

		if (!hasItem(object)) {
			super.add(object);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WhitelistItem item = getItem(position);

		if (convertView == null) { // If the View is not cached
			// Inflates the Common View from XML file
			convertView = this.inflater.inflate(R.layout.whitelist_item, null);
		}

		ImageView contactImage = (ImageView) convertView.findViewById(R.id.whitelist_item_image);
		TextView contactName = (TextView) convertView.findViewById(R.id.whitelist_item_contact_name);

		if (item.Id > -1) {
			Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(item.Id));
			Contact contact = ContactsManager.getContactByUri(contactUri);

			if (contact.PhotoId > 0) {
				contactImage.setImageURI(contact.PhotoUri);
			}

			contactName.setText(item.Name);
			contactImage.setVisibility(View.VISIBLE);
			contactName.setTextAppearance(contactName.getContext(), android.R.style.TextAppearance_Large);

			convertView.setTag(item);
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Nothing
				}
			});
			convertView.setOnTouchListener(gestureListener);
		} else {
			contactImage.setVisibility(View.GONE);
			contactName.setTextAppearance(contactName.getContext(), android.R.style.TextAppearance_Small);
			contactName.setText(ShuuushApplication.getAppContext().getString(R.string.whitelist_empty_message));
		}

		parent.requestDisallowInterceptTouchEvent(false);

		return convertView;
	}

	public void addEventsAdapterUpdatedEventListener(AdapterUpdatedListener listener) {
		adapterUpdatedListeners.add(listener);
	}

	public void removeEventsAdapterUpdatedEventListener(AdapterUpdatedListener listener) {
		adapterUpdatedListeners.remove(listener);
	}

	private void invokeAdapterUpdatedListeners(AdapterUpdatedEvent event) {
		for (AdapterUpdatedListener listener : this.adapterUpdatedListeners) {
			listener.adapterUpdated(event);
		}
	}

}
