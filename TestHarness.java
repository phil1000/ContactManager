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
		flush();
		print();
		
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
		
		System.out.println(myMeeting.getId() + ":" + DateUtilities.formatDate(myMeeting.getDate()) );
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