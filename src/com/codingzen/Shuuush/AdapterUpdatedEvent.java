package com.codingzen.Shuuush;

import java.util.EventObject;

@SuppressWarnings("serial")
public class AdapterUpdatedEvent extends EventObject {
	public int NewEventsCount;

	public AdapterUpdatedEvent(Object source, int newCount) {
		super(source);
		this.NewEventsCount = newCount;
	}
}
