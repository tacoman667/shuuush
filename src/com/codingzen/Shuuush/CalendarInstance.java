package com.codingzen.Shuuush;

public class CalendarInstance {

	public static final int AVAILABILITY_BUSY = 0;
	public static final int AVAILABILITY_FREE = 1;
	public static final int AVAILABILITY_TENTATIVE = 2;

	public int EventId;
	public String Title;
	public long Begin;
	public long End;
	public String Description;
	public int Availability;
	public Boolean IsAvailabilityBusy;
	public int AllDay;
	public Boolean IsAllDayEvent;

}
