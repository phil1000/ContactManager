import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class ContactManagerImpl implements ContactManager {

    @Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
		return 0;
	}
	
	@Override
    public PastMeeting getPastMeeting(int id) {
		return null;
	}
	
	@Override
    public FutureMeeting getFutureMeeting(int id) {
		return null;
	}
	
	@Override
    public Meeting getMeeting(int id) {
		return null;
	}
	
	@Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
		return null;
	}
	
	@Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
		return null;
	}
	
	@Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
		return null;
	}
	
	@Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
	}
	
	@Override
    public void addMeetingNotes(int id, String text) {
	}
	
	@Override
    public void addNewContact(String name, String notes) {
	}
	
	@Override
    public Set<Contact> getContacts(int... ids) {
		return null;
	}
	
	@Override
    public Set<Contact> getContacts(String name) {
		return null;
	}
	
	@Override 
    public void flush() {
	}
}