package events;

public class DatePickedEvent {
	public int type;
	public int year;
	public int month;
	public int day;

	public DatePickedEvent(int type, int year, int month, int day) {
		this.type = type;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public DatePickedEvent(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
}
