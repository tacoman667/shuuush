package com.codingzen.Shuuush;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ItemUpdatedFromViewTagEvent extends EventObject {

	Object Item;

	public ItemUpdatedFromViewTagEvent(Object source, Object item) {
		super(source);
		this.Item = item;
	}

}
