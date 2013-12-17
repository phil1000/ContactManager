import java.util.Set;
import java.util.Calendar;

public class PastMeetingImpl extends MeetingImpl implements PastMeeting {
	private String notes;
	
	public PastMeetingImpl(Calendar date, int id, Set<Contact> contacts) {
		super(date, id, contacts);
		this.notes="";
	}
	
	public PastMeetingImpl(Calendar date, int id, Set<Contact> contacts, String notes) {
		super(date, id, contacts);
		this.notes="";
		addNotes(notes);
	}
	
	public PastMeetingImpl(FutureMeeting heldMeeting, String notes){
		super(heldMeeting.getDate(), heldMeeting.getId(), heldMeeting.getContacts());
		this.notes = notes;
    }
	
	@Override
	public String getNotes() {
		return notes;
	}
	
	public void addNotes(String notes) {
		//each new note is given the current date/time as a create date and also put on a new line
		this.notes=this.notes + "\n" + DateUtilities.getDateString() + ":" + notes; 
	}
}