import java.util.Calendar;
import java.util.Set;
import java.io.Serializable;

/* I created this class to speed up the retrieval of all meetings
 * for a contact or all meetings on a specific date
 *
 *
 */
public class MeetingContactIntersect implements Serializable {
	private Meeting meeting;
	private Contact contact;
	private Calendar date;
	
	public MeetingContactIntersect(Meeting meeting, Contact contact, Calendar date) {
		this.meeting = meeting;
		this.contact = contact;
		this.date = date;
	}
	
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
	public Meeting getMeeting() {
		return this.meeting;
	}
	public Contact getContact() {
		return this.contact;
	}
	public Calendar getDate() {
		return this.date;
	}
}