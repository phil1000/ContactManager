import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
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

/* This class (and all other implementation classes) will not repeat any JavaDocumentation contained within the
 * interface as there would then be a possibility that the documentation will get out of line.
 * However, I will include JavaDocs for classes or methods that are not explicitly defined within the interface specs.
 */ 
public class ContactManagerImpl implements ContactManager {

	private Set<Contact> contacts;
	private List<Meeting> meetings;
	// the following list contains a single entry for each intersect/mapping between contacts, meeting and date.
	// it has been created to facilitate the bulk querying of meetings by contact or by date
	private List<MeetingContactIntersect> meetingContactIntersects;
	private int latestMeetingId; // this is the seed for the meeting id used/set throughout the application 
	private int latestCustomerId; // this is the seed for the customer ids used/set throughout the application
	private static final String FILENAME = "contacts.txt";
	
	public ContactManagerImpl() {

		if (!new File(FILENAME).exists()) {
            contacts = new HashSet<Contact>();
            meetings = new ArrayList<Meeting>();
			meetingContactIntersects = new ArrayList<MeetingContactIntersect>();
			latestMeetingId=1; // first time through so initialise the meeting id seeds to 1
			latestCustomerId=1;
        } else {
			ObjectInputStream d = null;			
            try { 
				d = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(FILENAME)));
                contacts = (Set<Contact>) d.readObject();
                meetings = (List<Meeting>) d.readObject();
				meetingContactIntersects = (List<MeetingContactIntersect>) d.readObject();
				LatestIDs storedIds = (LatestIDs) d.readObject();
				this.latestMeetingId=storedIds.latestMeetingId++; // set the meeting id seeds to the value stored previously
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
		
		meetings.add(newMeeting);
		insertIntersect(date, newMeeting, contacts); // create one to many entries in Intersect table to map meeting to each attendee
		return meetingId;
	}

	@Override
    public PastMeeting getPastMeeting(int id) throws IllegalArgumentException {
		// I have made a design decision that a meeting doesn't automatically become a past meeting
		// based on the meeting date i.e. a future meeting has to be consciously converted to a past meeting
		// this is because a meeting might not have occurred even though it's past its meeting date.
		Meeting thisMeeting = getMeeting(id);
		if (thisMeeting==null) return null;
		// getMeeting could return Meeting, PastMeeting or FutureMeeting so check we have a PastMeeting
		if (thisMeeting.getClass() == PastMeetingImpl.class) {  
			PastMeeting pastMeeting = (PastMeeting) thisMeeting;
			if (DateUtilities.dateInFuture(thisMeeting.getDate()) ) {
				throw new IllegalArgumentException("Meeting:" + thisMeeting.getId() + " - past meeting has a future date");
			}
			return pastMeeting;
		} else {
			throw new IllegalArgumentException(id + " is not a past meeting"); 
		}
	}
	
	@Override
    public FutureMeeting getFutureMeeting(int id) throws IllegalArgumentException {
		Meeting thisMeeting = getMeeting(id);
		if (thisMeeting==null) return null;
		if (thisMeeting.getClass() == FutureMeetingImpl.class) {
			if (DateUtilities.dateInPast(thisMeeting.getDate()) ) {
				throw new IllegalArgumentException("Meeting:" + thisMeeting.getId() + " - future meeting has a past date");
			}
			FutureMeeting futureMeeting = (FutureMeeting) thisMeeting; 
			return futureMeeting;
		} else {
			throw new IllegalArgumentException(id + " is not a future meeting"); 
		}
	}
	
	@Override
    public Meeting getMeeting(int id) {
		Meeting meetingFound=null;
		Iterator<Meeting> iter = meetings.iterator();
		while (iter.hasNext()) {
			Meeting thisMeeting=iter.next();
			if (id==thisMeeting.getId()) {
				meetingFound=thisMeeting;
				break;
			}
		}
		return meetingFound;
	}
	
	@Override
    public List<Meeting> getFutureMeetingList(Contact contact) throws IllegalArgumentException {
		List<Meeting> futureMeetings = new ArrayList<Meeting>();
		int count=0;
		
		if ( (contact==null) || (!this.contacts.contains(contact)) ) {
			throw new IllegalArgumentException(contact.getName() + " not found"); 
		}
		
		Iterator<MeetingContactIntersect> iter = meetingContactIntersects.iterator();
		while (iter.hasNext()) {
			MeetingContactIntersect thisIntersect=iter.next();
			if ( contact.getId()==thisIntersect.getContact().getId() ) { 
				if (thisIntersect.getMeeting().getClass() == FutureMeetingImpl.class) {
					futureMeetings.add(thisIntersect.getMeeting());
					count++;
				}
			}
		}
		
		if ( count > 0 ) {
			Collections.sort(futureMeetings, new SortbyDate());
			return futureMeetings;
		}
		else return null; 
	}
	
	@Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
		List<Meeting> futureMeetings = new ArrayList<Meeting>();
		// the MeetingContactIntersect list contains one entry for each contact that attends a meeting 
		// and so a meeting with 4 contacts will have 4 entries. I only want to return one entry for
		// each meeting and so create a set to discard duplicate entries
		Set<Meeting> uniqueMeetings = new HashSet<Meeting>();
		int count=0;
		
		Iterator<MeetingContactIntersect> iter = meetingContactIntersects.iterator();
		while (iter.hasNext()) {
			MeetingContactIntersect thisIntersect=iter.next();
			if ( (date.get(Calendar.YEAR) == thisIntersect.getDate().get(Calendar.YEAR)) &&
				 (date.get(Calendar.DAY_OF_YEAR) == thisIntersect.getDate().get(Calendar.DAY_OF_YEAR)) ) {
				if (thisIntersect.getMeeting().getClass() == FutureMeetingImpl.class) {
					uniqueMeetings.add(thisIntersect.getMeeting()); // will only add first meeting found, will discard subsequent meetings
					count++;
				}
			}
		}

		if ( count > 0 ) {
			Iterator<Meeting> tempIter = uniqueMeetings.iterator();
			while (tempIter.hasNext()) { // now transfer unique meetings from the set to the arraylist to be returned
				Meeting newMeeting = tempIter.next();
				futureMeetings.add(newMeeting);
			}
			Collections.sort(futureMeetings, new SortbyId());
			return futureMeetings;
		}
		else return null;  
	}
	
	class SortbyId implements Comparator<Meeting> {
		@Override
		public int compare(Meeting a, Meeting b) {
			if ( a.getId() > b.getId() ) return 1;
			if ( a.getId() < b.getId() ) return -1;
			else return 0;
		}
	}
	
	class SortbyDate implements Comparator<Meeting> {
		@Override
		public int compare(Meeting a, Meeting b) {
			return (DateUtilities.compareDates(a.getDate(), b.getDate()));
		}
	}
	
	@Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
		List<PastMeeting> pastMeetings = new ArrayList<PastMeeting>();
		int count=0;
		
		if ( (contact==null) || (!this.contacts.contains(contact)) ) {
			throw new IllegalArgumentException(contact.getName() + " not found"); 
		}
		
		Iterator<MeetingContactIntersect> iter = meetingContactIntersects.iterator();
		while (iter.hasNext()) {
			MeetingContactIntersect thisIntersect=iter.next();
			if ( contact.getId()==thisIntersect.getContact().getId() ) {
				if (thisIntersect.getMeeting().getClass() == PastMeetingImpl.class) {
					pastMeetings.add( (PastMeeting) thisIntersect.getMeeting());
					count++;
				}
			}
		}
		
		if ( count > 0 ) {
			Collections.sort(pastMeetings, new SortbyDate());
			return pastMeetings;
		}
		else return null;
	}
	
	@Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) throws IllegalArgumentException {

		if ( (contacts==null) || (!this.contacts.containsAll(contacts)) ) {
			throw new IllegalArgumentException("Contact list empty or contains one or more unknown contacts"); 
		}
		
		if (text==null) {
			throw new NullPointerException("No notes for meeting ");
		}
		
		if (date==null) {
			throw new NullPointerException("No date for meeting ");
		}
		
		Meeting newMeeting = new PastMeetingImpl(date, this.latestMeetingId, contacts, text);
		this.latestMeetingId++;
		
		meetings.add(newMeeting);
		insertIntersect(date, newMeeting, contacts); // create one to many entries in Intersect table to map meeting to each attendee
	}
	
	@Override
    public void addMeetingNotes(int id, String text) throws NullPointerException, IllegalArgumentException, IllegalStateException {
		if (text==null) {
			throw new NullPointerException("No notes for meeting " + id);
		}
		
		Meeting meetingFound=null;
		ListIterator<Meeting> iter = meetings.listIterator(); // need a list iterator as I want to replace an item if found
		while (iter.hasNext()) {
			Meeting thisMeeting=iter.next();
			if (id==thisMeeting.getId()) {
				meetingFound=thisMeeting;
				break;
			}
		}
		if (meetingFound==null) {
			throw new IllegalArgumentException("meeting not found :" + id);
		}
		
		if (meetingFound.getClass() != FutureMeetingImpl.class) {
			throw new IllegalArgumentException(id + " is not a future meeting"); 
		}
		
		FutureMeeting futureMeeting = (FutureMeeting) meetingFound;
		
		if ( DateUtilities.dateInFuture(futureMeeting.getDate()) ) {
			throw new IllegalStateException("meeting date is in the future");
		}
		
		Meeting pastMeeting = new PastMeetingImpl(futureMeeting, text);
		// now replace what was a future meeting with the newly created past meeting
		// The iterator was declared outside the while loop so will still be pointing 
		// at the appropriate index in the array
		iter.set(pastMeeting);
		updateIntersect(pastMeeting); // replace the existing entry in Intersect table to reflect the meeting is now a past meeting
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
    public Set<Contact> getContacts(int... ids) throws IllegalArgumentException {
		Set<Contact> returnedContacts = new HashSet<Contact>();
		boolean found;
		Iterator<Contact> myIterator=null;
		
		for (int i : ids) {
			myIterator = contacts.iterator();
			found=false;
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				if (i==currentContact.getId()) {
					returnedContacts.add(currentContact);
					found=true;
					break;
				}
			}
			if (!found) throw new IllegalArgumentException("id not found :" + i);
		}
		return returnedContacts;
	}
	
	@Override
    public Set<Contact> getContacts(String name) throws NullPointerException, IllegalArgumentException {
		// I have added an illegalArgumentException even though not asked for one because it makes
		// sense to tell the calling program that nothing has been found - the alternative is to 
		// send a null pointer.
		if (name==null) throw new NullPointerException("name is null");
		
		Set<Contact> returnedContacts = new HashSet<Contact>();
		boolean found=false;
		Iterator<Contact> myIterator=contacts.iterator();
		while(myIterator.hasNext()) {
			Contact currentContact = myIterator.next();
			if (name.equals(currentContact.getName())) {
				returnedContacts.add(currentContact);
				found=true;
			}
		}
		if (!found) throw new IllegalArgumentException("name not found :" + name);
		else return returnedContacts;
	}
	
	/**
     * (I appreciate this is a private method but still feel it's worthwhile to generate Java Docs for it._
     * Inserts an entry into the MeetingContractIntersect list to map contact to meeting for every newly created meeting
	 *
     * @param date the Date for the meeting
	 * @param meeting the Meeting to be mapped
	 * @param contacts the contacts that attended the meeting
     */
	private void insertIntersect(Calendar date, Meeting meeting, Set<Contact> contacts) {
			Iterator<Contact> myIterator = contacts.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				MeetingContactIntersect intersect = new MeetingContactIntersect(meeting, currentContact, date);
				meetingContactIntersects.add(intersect);
			}
	}
	
	/**
     * Replaces an existing future meeting in the MeetingContractIntersect list with a newly created meeting past meeting
	 *
	 * @param meeting the Meeting to be converted from a future to a past meeting
     */
	private void updateIntersect(Meeting meeting) {			
		ListIterator<MeetingContactIntersect> iter = meetingContactIntersects.listIterator();
		while (iter.hasNext()) {
				MeetingContactIntersect thisIntersect=iter.next();
				if (meeting.getId()==thisIntersect.getMeeting().getId()) {
					thisIntersect.setMeeting(meeting);
					iter.set(thisIntersect);
				}
		}
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
			encode.writeObject(meetingContactIntersects);
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