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

public class TestHarness {
	
	private Set<Contact> contacts;
	private List<Meeting> meetings;
	private int latestMeetingId;
	private int latestCustomerId;
	private static final String FILENAME = "contacts.txt";
	
	public void launch() {
		openfile();

		createContacts();
		createMeetings();
		testcontainsAll();
		Set<Contact> returnedList=null;
		try {
			returnedList = getContacts(1,2,3,4,5,6);
			Iterator<Contact> myIterator = returnedList.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				System.out.println("contact id list=" + currentContact.getId() + currentContact.getName() + currentContact.getNotes());
			}
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} 
		
		try {
			returnedList = getContacts("Phil");
			Iterator<Contact> myIterator = returnedList.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				System.out.println("contact phil list=" + currentContact.getId() + currentContact.getName() + currentContact.getNotes());
			}
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			returnedList = getContacts("Bobby");
			Iterator<Contact> myIterator = returnedList.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				System.out.println("contact phil list=" + currentContact.getId() + currentContact.getName() + currentContact.getNotes());
			}
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		}  catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			String myStr = null;
			returnedList = getContacts(myStr);
			Iterator<Contact> myIterator = returnedList.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				System.out.println("contact phil list=" + currentContact.getId() + currentContact.getName() + currentContact.getNotes());
			}
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		}  catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		flush();
		print();
		
		/*Calendar c1 = Calendar.getInstance();
		c1.set(2000, Calendar.JANUARY, 30);  //January 30th 2000
		System.out.println(c1.getTime() + ":" + DateUtilities.dateInPast(c1));
		c1.set(2014, Calendar.JANUARY, 30);  //January 30th 2000
		System.out.println(c1.getTime() + ":" + DateUtilities.dateInPast(c1));
		c1.set(2013, Calendar.DECEMBER, 16);  //January 30th 2000
		System.out.println(c1.getTime() + ":" + DateUtilities.dateInPast(c1));*/
	}
	
	public void testcontainsAll() {
		Set<Contact> newContacts = new HashSet<Contact>();;
		try {
			Contact newContact = new ContactImpl("Phil", 1);
			newContacts.add(newContact);
			//System.out.println(newContact.getId() + newContact.getName() + newContact.getNotes());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			Contact newContact2 = new ContactImpl("Isabelle",2);
			newContacts.add(newContact2);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		if (!this.contacts.containsAll(newContacts)) System.out.println("one or more not found");
		else System.out.println("first two found ok");
		
		try {
			Contact newContact2 = new ContactImpl("Isabelle",13);
			newContacts.add(newContact2);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		if (!this.contacts.containsAll(newContacts)) System.out.println("last one a problem");
		else System.out.println("first three found ok");
		
		Contact newContacttemp=null;
		try {
			newContacttemp = new ContactImpl("Aimee",3);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		if (this.contacts.contains(newContacttemp)) System.out.println("Aimee 3 found");
		else System.out.println("Aimee 3 not found");
		
		newContacttemp=null;
		try {
			newContacttemp = new ContactImpl("Aimee",17);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		if (this.contacts.contains(newContacttemp)) System.out.println("Aimee 17 found");
				else System.out.println("Aimee 17 not found");
	}
	
	public void openfile() {
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
	
	public void print() {
		Iterator<Meeting> iter = meetings.iterator();
		while (iter.hasNext()) {
			Meeting thisMeeting=iter.next();
			System.out.println("Meeting Id="+thisMeeting.getId()+" Date="+DateUtilities.formatDate(thisMeeting.getDate()));
			Set<Contact> myContacts=thisMeeting.getContacts();
			Iterator<Contact> myIterator = myContacts.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				System.out.println(currentContact.getId() + currentContact.getName() + currentContact.getNotes());
			}
		}		
	}
	
	public void printMeetings() {
		Iterator<Meeting> iter = meetings.iterator();
		while (iter.hasNext()) {
			Meeting thisMeeting=iter.next();
			System.out.print("Meeting Id="+thisMeeting.getId()+" Date="+DateUtilities.formatDate(thisMeeting.getDate()));
			if (thisMeeting.getClass() == PastMeetingImpl.class) {
				PastMeetingImpl pastMeeting = (PastMeetingImpl) thisMeeting; 
				System.out.print(" Notes=" + pastMeeting.getNotes());
			}
			System.out.println();
		}		
	}
	
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
	
	public void createMeetings() {
		Calendar myCalendar = Calendar.getInstance();
		Meeting myMeeting = new MeetingImpl(myCalendar, this.latestMeetingId, contacts);
		this.latestMeetingId++;
		
		//System.out.println(myMeeting.getId() + ":" + DateUtilities.formatDate(myMeeting.getDate()) );
		meetings.add(myMeeting);
		
		myMeeting = new PastMeetingImpl(myCalendar, this.latestMeetingId, contacts, "random notes");
		this.latestMeetingId++;
		
		//System.out.println(myMeeting.getId() + ":" + DateUtilities.formatDate(myMeeting.getDate()) );
		meetings.add(myMeeting);
		
		myMeeting = new FutureMeetingImpl(myCalendar, this.latestMeetingId, contacts);
		this.latestMeetingId++;
		
		//System.out.println(myMeeting.getId() + ":" + DateUtilities.formatDate(myMeeting.getDate()) );
		meetings.add(myMeeting);
	}
	
	public void createContacts() {
		//contacts = new HashSet<Contact>();
		
		try {
			Contact newContact = new ContactImpl("Phil", this.latestCustomerId);
			contacts.add(newContact);
			this.latestCustomerId++;
			//System.out.println(newContact.getId() + newContact.getName() + newContact.getNotes());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			Contact newContact1 = new ContactImpl("Phil**&oura",this.latestCustomerId);
			contacts.add(newContact1);
			this.latestCustomerId++;
			//System.out.println(newContact.getId() + newContact.getName() + newContact.getNotes());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			Contact newContact2 = new ContactImpl("Isabelle",this.latestCustomerId);
			contacts.add(newContact2);
			this.latestCustomerId++;
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
				
		try {
			Contact newContact3 = new ContactImpl("Aimee",this.latestCustomerId);
			contacts.add(newContact3);
			this.latestCustomerId++;
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public static void main(String[] args) {
		TestHarness script = new TestHarness();
		script.launch();
	}
}