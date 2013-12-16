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
        } else {
			ObjectInputStream d = null;			
            try { 
				d = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(FILENAME)));
                contacts = (Set<Contact>) d.readObject();
                meetings = (List<Meeting>) d.readObject();
				d.close(); // remove if a problem
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
		Meeting myMeeting = new MeetingImpl(myCalendar, contacts);
		
		System.out.println(myMeeting.getId() + ":" + DateUtilities.formatDate(myMeeting.getDate()) );
		meetings.add(myMeeting);
	}
	
	public void createContacts() {
		//contacts = new HashSet<Contact>();
		
		try {
			Contact newContact = new ContactImpl("Phil");
			newContact.addNotes("saw Phil today");
			newContact.addNotes("saw Phil again");
			newContact.addNotes("bored of seeing phil already");
			contacts.add(newContact);
			//System.out.println(newContact.getId() + newContact.getName() + newContact.getNotes());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			Contact newContact1 = new ContactImpl("Phil**&oura");
			contacts.add(newContact1);
			//System.out.println(newContact.getId() + newContact.getName() + newContact.getNotes());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			Contact newContact2 = new ContactImpl("Isabelle","new client, treat nicely");
			newContact2.addNotes("saw Issy today");
			newContact2.addNotes("saw Issy again");
			newContact2.addNotes("bored of seeing Issy already");
			contacts.add(newContact2);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
				
		try {
			Contact newContact3 = new ContactImpl("Aimee","young pup");
			newContact3.addNotes("saw Aimee again today");
			contacts.add(newContact3);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public static void main(String[] args) {
		TestHarness script = new TestHarness();
		script.launch();
	}
}