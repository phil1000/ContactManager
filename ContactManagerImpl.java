public class ContactManagerImpl implements ContactManager {

    @override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
	}
	
	@override
    public PastMeeting getPastMeeting(int id) {
	}
	
	@override
    public FutureMeeting getFutureMeeting(int id) {
	}
	
	@override
    public Meeting getMeeting(int id) {
	}
	
	@override
    public List<Meeting> getFutureMeetingList(Contact contact) {
	}
	
	@override
    public List<Meeting> getFutureMeetingList(Calendar date) {
	}
	
	@override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
	}
	
	@override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
	}
	
	@override
    public void addMeetingNotes(int id, String text) {
	}
	
	@override
    public void addNewContact(String name, String notes) {
	}
	
	@override
    public Set<Contact> getContacts(int... ids) {
	}
	
	@override
    public Set<Contact> getContacts(String name) {
	}
	
	@override 
    public void flush() {
	}
}