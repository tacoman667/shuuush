package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class EventsAdapter extends ArrayAdapter<Event> {

	private final LayoutInflater inflater;

	public Event TouchedEvent;
	private final EventsAdapter Adapter;

	private final SwipeTouchListener<Event> gestureListener;

	private final ArrayList<AdapterUpdatedListener> adapterUpdatedListeners;

	public EventsAdapter(Context context, ArrayList<Event> events) {
		super(context, -1, events);

		// Caches the LayoutInflater for quicker use
		this.inflater = LayoutInflater.from(context);
		Adapter = this;

		MyGestureDetector detector = new MyGestureDetector(new Runnable() {
			public void run() {
				Adapter.remove(TouchedEvent);
			}
		});

		gestureListener = new SwipeTouchListener<Event>(new GestureDetector(detector));
		gestureListener.addItemUpdatedFromViewTagListener(new ItemUpdatedFromViewTagListener() {
			public void itemUpdated(ItemUpdatedFromViewTagEvent itemUpdatedEvent) {
				TouchedEvent = (Event) itemUpdatedEvent.Item;
			}
		});

		this.setNotifyOnChange(true);

		this.adapterUpdatedListeners = new ArrayList<AdapterUpdatedListener>();

		if (super.getCount() == 0) {
			addEmptyItem();
		}
	}

	private OnClickListener createOnClickListener() {
		return new OnClickListener() {

			public void onClick(View v) {
				Event event = (Event) v.getTag();

				AlertDialog.Builder builder = createDialog(v.getContext(), event);

				AlertDialog dialog = builder.create();
				dialog.show();

			}

			private AlertDialog.Builder createDialog(final Context context, final Event event) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				if (event.Type.equals(Event.TYPE_INCOMING_CALL)) {
					// Phone Call
					builder.setMessage("Would you like to call them back now?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent smsIntent = new Intent(Intent.ACTION_DIAL);
							smsIntent.setData(Uri.parse("tel:" + event.PhoneNumber));
							context.startActivity(smsIntent);
						}
					});
				} else {
					// SMS Text
					builder.setMessage("Would you like to text them back now?");
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent smsIntent = new Intent(Intent.ACTION_VIEW);
							smsIntent.setType("vnd.android-dir/mms-sms");
							smsIntent.putExtra("address", event.PhoneNumber);
							context.startActivity(smsIntent);
						}
					});
				}

				builder.setCancelable(true);

				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				return builder;
			}
		};
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
	public Event getItem(int position) {
		Event item;

		try {
			item = super.getItem(position);
		} catch (Exception e) {
			return null;
		}

		return item;
	}

	@Override
	public void insert(Event object, int index) {
		Event item = this.getItem(0);
		if (item != null && item.Type.equals(Event.TYPE_NONE)) {
			super.remove(item);
		}

		if (!hasItem(object)) {
			super.insert(object, index);
		}
	}

	public Boolean hasItem(Event item) {
		for (int i = 0; i < super.getCount(); i++) {
			Event event = super.getItem(i);
			if (event.ID == item.ID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void remove(Event object) {
		super.remove(object);
		object.delete();
		LogManager.i("Events is deleted from DB");
		this.notifyDataSetChanged();
		AdapterUpdatedEvent event = new AdapterUpdatedEvent(this, this.getCount());
		invokeAdapterUpdatedListeners(event);
		WidgetNotificationManager.updateNotification("Event Removed");

		if (super.getCount() == 0) {
			addEmptyItem();
		}
	}

	private void addEmptyItem() {
		super.add(new Event() {
			{
				Type = Event.TYPE_NONE;
				Message = ShuuushApplication.getAppContext().getString(R.string.missed_events_empty_message);
			}
		});
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Event event = getItem(position);

		if (convertView == null) { // If the View is not cached
			// Inflates the Common View from XML file
			convertView = this.inflater.inflate(R.layout.events_list_item, null);
		}

		TextView contactName = (TextView) convertView.findViewById(R.id.eventContact);
		TextView message = (TextView) convertView.findViewById(R.id.eventMessage);
		TextView timeStamp = (TextView) convertView.findViewById(R.id.eventTimeStamp);
		QuickContactBadge contactBadge = (QuickContactBadge) convertView.findViewById(R.id.quickContactBadge1);

		// if there are no events to show this is the placeholder
		if (event.Type.equals(Event.TYPE_NONE)) {
			message.setText(event.Message);
			contactName.setVisibility(View.GONE);
			timeStamp.setVisibility(View.GONE);
			contactBadge.setVisibility(View.GONE);
		} else {
			timeStamp.setText(event.TimeStamp);
			message.setText(event.Message);

			contactBadge.assignContactFromPhone(event.PhoneNumber, false);
			contactBadge.setMode(ContactsContract.QuickContact.MODE_SMALL);

			if (event.ContactName != null && !event.ContactName.equals("")) {
				contactName.setText(event.ContactName);
				Contact contact = ContactsManager.getContactsByNumber(event.PhoneNumber);
				if (contact.PhotoId > 0) {
					contactBadge.setImageURI(contact.PhotoUri);
				}
			} else {
				contactName.setText(PhoneNumberUtils.formatNumber(event.PhoneNumber));
			}

			contactName.setVisibility(View.VISIBLE);
			timeStamp.setVisibility(View.VISIBLE);
			contactBadge.setVisibility(View.VISIBLE);

			convertView.setTag(event);
			convertView.setOnClickListener(createOnClickListener());
			convertView.setOnTouchListener(gestureListener);
		}

		return convertView;
	}

	public void addAdapterUpdatedListener(AdapterUpdatedListener listener) {
		adapterUpdatedListeners.add(listener);
	}

	public void removeAdapterUpdatedListener(AdapterUpdatedListener listener) {
		adapterUpdatedListeners.remove(listener);
	}

	private void invokeAdapterUpdatedListeners(AdapterUpdatedEvent event) {
		for (AdapterUpdatedListener listener : this.adapterUpdatedListeners) {
			listener.adapterUpdated(event);
		}
	}

}
