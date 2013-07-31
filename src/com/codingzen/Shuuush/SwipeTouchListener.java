package com.codingzen.Shuuush;

import java.util.ArrayList;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeTouchListener<T> implements View.OnTouchListener {

	private T TouchedItem;
	private final GestureDetector gestureDetector;

	private int padding = 0;
	private int initialx = 0;
	private int currentx = 0;

	private final ArrayList<ItemUpdatedFromViewTagListener> itemUpdatedFromViewTagListeners;

	public T getTouchedItem() {
		return TouchedItem;
	}

	public void setTouchedItem(T touchedItem) {
		TouchedItem = touchedItem;
		fireItemUpdatedFromViewTagListeners();
	}

	public SwipeTouchListener(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
		this.itemUpdatedFromViewTagListeners = new ArrayList<ItemUpdatedFromViewTagListener>();
	}

	@SuppressWarnings("unchecked")
	public boolean onTouch(View v, MotionEvent event) {
		setTouchedItem((T) v.getTag());

		setItemPadding(v, event);

		return gestureDetector.onTouchEvent(event);
	}

	private void setItemPadding(View v, MotionEvent event) {
		int eventAction = event.getAction();
		if (eventAction == MotionEvent.ACTION_DOWN) {
			padding = 0;
			initialx = (int) event.getX();
			currentx = (int) event.getX();
		}
		if (eventAction == MotionEvent.ACTION_MOVE) {
			currentx = (int) event.getX();
			padding = currentx - initialx;
		}

		if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL) {
			padding = 0;
			initialx = 0;
			currentx = 0;
		}

		v.setPadding(padding, 0, (padding * -1), 0);
	}

	public void addItemUpdatedFromViewTagListener(ItemUpdatedFromViewTagListener l) {
		itemUpdatedFromViewTagListeners.add(l);
	}

	private void fireItemUpdatedFromViewTagListeners() {
		ItemUpdatedFromViewTagEvent e = new ItemUpdatedFromViewTagEvent(this, TouchedItem);

		for (ItemUpdatedFromViewTagListener l : itemUpdatedFromViewTagListeners) {
			l.itemUpdated(e);
		}
	}
}
