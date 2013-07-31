package com.codingzen.Shuuush;

public class CalendarEvent {
	public static final int AVAILABILITY_BUSY = 0;
	public static final int AVAILABILITY_FREE = 1;
	public static final int AVAILABILITY_TENTATIVE = 2;

	public int EventId;
	public String Title;
	public String Description;
	public long Start;
	public long End;
	public int Availability;
	public Boolean IsAvailabilityBusy;

}
