import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactManagerImpl implements ContactManager {

	private Set<Contact> contacts;
	private List<Meeting> meetings;
	private int latestMeetingId;
	private int latestCustomerId;
	private static final String FILENAME = "contacts.txt";
	
	public ContactManagerImpl() {

		if (!new File(FILENAME).exists()) {
            contacts = new HashSet<Contact>();
            meetings = new ArrayList<Meeting>();
			latestMeetingId=1;
			latestCustomerId=1;
        } else {
			ObjectInputStream d = null;			
            try { 
				d = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(FILENAME)));
                contacts = (Set<Contact>) d.readObject();
                meetings = (List<Meeting>) d.readObject();
				LatestIDs storedIds = (LatestIDs) d.readObject();
				this.latestMeetingId=storedIds.latestMeetingId++;
				this.latestCustomerId=storedIds.latestCustomerId++;
				d.close(); 
            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("On read error " + ex);
            } finally {
				try {
					if (d != null) {
						d.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}	
		}
	}
	
    @Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) throws IllegalArgumentException {
	
		int meetingId=0;
		if (DateUtilities.dateInPast(date)) { 
			throw new IllegalArgumentException("Date in the past"); 
		}
		if ( (contacts==null) || (!this.contacts.containsAll(contacts)) ) {
			throw new IllegalArgumentException("Contact list empty or contains one or more unknown contacts"); 
		}
		
		Meeting newMeeting = new FutureMeetingImpl(date, this.latestMeetingId, contacts);
		meetingId = this.latestMeetingId;
		this.latestMeetingId++;
		
		meetings.add(newMeeting); // do I want to add here or into a new future meeting set?
		return meetingId;
	}
	
	@Override
    public PastMeeting getPastMeeting(int id) {
		// first Collections.sort(meetings), then do a Collections.binarySearch(meetings,id);
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
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) throws IllegalArgumentException {

		if ( (contacts==null) || (!this.contacts.containsAll(contacts)) ) {
			throw new IllegalArgumentException("Contact list empty or contains one or more unknown contacts"); 
		}
		
		Meeting newMeeting = new PastMeetingImpl(date, this.latestMeetingId, contacts, text);
		this.latestMeetingId++;
		
		meetings.add(newMeeting); // do I want to add here or into a new past meeting set?
	}
	
	@Override
    public void addMeetingNotes(int id, String text) {
	}
	
	@Override
    public void addNewContact(String name, String notes) throws NullPointerException, IllegalArgumentException {
	
		if ( (name==null) || (notes==null) ) {
			throw new NullPointerException("contact name or notes are null");
		}
		try {
			Contact newContact = new ContactImpl(name, this.latestCustomerId, notes);
			contacts.add(newContact);
			this.latestCustomerId++;
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
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
		ObjectOutputStream encode = null;
		LatestIDs storedIds = new LatestIDs();
		storedIds.latestMeetingId=this.latestMeetingId;
		storedIds.latestCustomerId=this.latestCustomerId;
		try {
				encode = new ObjectOutputStream(
					new BufferedOutputStream(
                        new FileOutputStream(FILENAME)));
        } catch (FileNotFoundException ex) {
            System.err.println("encoding... " + ex);
		} catch (IOException ex) {
            ex.printStackTrace();
        }    

        try {
            encode.writeObject(contacts);
            encode.writeObject(meetings);
			encode.writeObject(storedIds);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            encode.close();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
	}
}