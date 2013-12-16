import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

public class MeetingImpl implements Meeting, Serializable {

	private static int globalMeetingIdSeed=0;
	private int id;
	private Calendar date;
	private Set<Contact> contacts;
	
	public MeetingImpl(Calendar date, Set<Contact> contacts) {
		id=++globalMeetingIdSeed; // I always want a meeting id > 0
		this.date=date;
		this.contacts=contacts;
	}
	
    @Override
	public int getId() {
		return id;
	}

	@Override
    public Calendar getDate() {
		return date;
	}

	@Override
    public Set<Contact> getContacts() {
		return contacts;
	}
}