import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * THis is a test harness that I used during the development of the code to test out the method 
 * calls and also to try and mimic one or more use cases to test whether the overall program
 * hangs together. The tests embedded here will be formalised within a new test harness and also
 * a JUNIT class. The former to test use cases, the latter to black box test the individual methods
 */
public class TestHarness {
	
	private ContactManager mgr;
	private String[][] validContacts = { {"Phil","Dad"}, {"Isabelle", "Mum"}, {"Isabelle", "Mother"}, {"Aimee", "Big Sis"}, {"Bruno", "Brother"}, {"Flora", "Lil' Sis"} };
	private Set<Contact> contactListA;
	private Set<Contact> contactListB;
	private Set<Contact> contactListC;
	public void launch() {
		mgr = new ContactManagerImpl(); // tests retrieval of prior results from file and general initialisation
		addContacts(); // tests addNewContact()
		//printContactsbyName(); // tests getContacts(String name)
		//printContactsbyId(); // tests getContacts(int... ids)
		populateContactListABC();
		addPastMeetings(); // tests addNewPastMeeting(contacts, date, text)
		//printPastMeetingsbyId(); // tests getPastMeeting(int id)
		//printPastMeetingbyContact(); // tests getPastMeetingList(Contact contact);
		addFutureMeetings(); // tests addFutureMeeting(contact, date)
		//printFutureMeetingsbyContact(); // tests getFutureMeetingList(Contact contact);
		//printFutureMeetingbyDate(); // tests getFutureMeetingList(Calendar date);
		//getFutureMeetingonId();
		addMeetingNotes(); // tests addMeetingNotes(id, text)
		//testWithBadContact(); // tests multiple methods to see if they correctly process a contact that is not found
		mgr.flush(); // tests the writing of results to file
	}
	public void addMeetingNotes() {
		String nullStr = null;
		try {
			mgr.addMeetingNotes(6, nullStr);
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addMeetingNotes with nullStr" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addMeetingNotes with nullStr" + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("calling addMeetingNotes with nullStr" + ex.getMessage());
		}
		
		try {
			mgr.addMeetingNotes(99, "hello");
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addMeetingNotes with not found id" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addMeetingNotes with not found id" + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("calling addMeetingNotes with not found id" + ex.getMessage());
		}
		
		try {
			mgr.addMeetingNotes(3, "hi");
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addMeetingNotes with id for a past meeting" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addMeetingNotes with id for a past meeting" + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("calling addMeetingNotes with id for a past meeting" + ex.getMessage());
		}
		
		try {
			mgr.addMeetingNotes(6, "OK");
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addMeetingNotes but date is in future" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addMeetingNotes but date is in future" + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("calling addMeetingNotes but date is in future" + ex.getMessage());
		}
		
		// id 10 should be a future meeting with today's date and so should be convertible
		Meeting myMeeting=null;
		try {
			myMeeting = mgr.getFutureMeeting(10);
			System.out.println("Future Meeting 10 found");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			mgr.addMeetingNotes(10, "OK");
			System.out.println("successfully converted meeting with id=10");
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addMeetingNotes with id=10" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addMeetingNotes with id=10" + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("calling addMeetingNotes with id=10" + ex.getMessage());
		}
		
		// this should now fail
		try {
			myMeeting = mgr.getFutureMeeting(10);
			System.out.println("Future Meeting 10 found");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
		// and this should succeed
		try {
			myMeeting = mgr.getPastMeeting(10);
			System.out.println("Past Meeting 10 found");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public void testWithBadContact() {
		Set<Contact> badContacts = contactListA;
		Contact badContact = new ContactImpl("Bad Contact",88); // individual contact that will not be found
		badContacts.add(badContact); // contact set containing a non valid contact
		
		Calendar c1 = Calendar.getInstance();
		c1.set(2014, Calendar.MARCH, 30);
		c1.getTime();
		
		//testing addFutureMeeting
		try {
			mgr.addFutureMeeting(badContacts, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addFutureMeeting with bad contact" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addFutureMeeting with bad contact" + ex.getMessage());
		}
		//testing getFutureMeetingList
		List<Meeting> meetings = new ArrayList<Meeting>();
		try {
			meetings = mgr.getFutureMeetingList(badContact);
		}  catch (IllegalArgumentException ex) {
			System.out.println("calling getFutureMeetingList with bad contact" + ex.getMessage());
		}
		//testing getPastMeetingList
		List<PastMeeting> pastMeetings = new ArrayList<PastMeeting>();
		try {
			pastMeetings = mgr.getPastMeetingList(badContact);
		}  catch (IllegalArgumentException ex) {
			System.out.println("calling getPastMeetingList with bad contact" + ex.getMessage());
		}
		//testing getContacts(int...)
		try {
			Set<Contact> contacts = mgr.getContacts(1,2,3,88);
		}  catch (IllegalArgumentException ex) {
			System.out.println("calling getContacts with bad contact" + ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2013, Calendar.MARCH, 30);
		c1.getTime();
		
		try {
			mgr.addNewPastMeeting(badContacts, c1, "Contact A, meeting held on 30/03/2013");
		} catch (IllegalArgumentException ex) {
			System.out.println("calling addNewPastMeeting with bad contact" + ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println("calling addNewPastMeeting with bad contact" + ex.getMessage());
		}
		
	}
	
	public void addContacts() {		
		// Add valid contacts to test that they are inserted ok
		// I have added two Isabelle's to see that getContacts(name) correctly returns multiple values
		for (int i=0; i<validContacts.length;i++) {
			try {
				mgr.addNewContact(validContacts[i][0], validContacts[i][1]);
			} catch (IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			} catch (NullPointerException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		// Add bad contacts to test a) invalid contact name
		String badContactName = "B%$";
		String badContactNotes = "normal notes";
		try {
			mgr.addNewContact(badContactName, badContactNotes);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		}
		
		// Add bad contacts to test b) null contact name
		badContactName = null;
		badContactNotes = "normal notes";
		try {
			mgr.addNewContact(badContactName, badContactNotes);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		}
		
		// Add bad contacts to test c) null notes
		badContactName = "Valid Name";
		badContactNotes = null;
		try {
			mgr.addNewContact(badContactName, badContactNotes);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void printContactsbyName() {
		Set<Contact> contacts = new HashSet<Contact>();
		for (int i=0; i<validContacts.length;i++) {
			Set<Contact> tempContacts = mgr.getContacts(validContacts[i][0]);
			Iterator<Contact> myIterator=tempContacts.iterator();
			while(myIterator.hasNext()) {
				Contact currentContact = myIterator.next();
				contacts.add(currentContact);
			}
		}
		printContactList("BY NAME", contacts);
	}
	
	public void printContactsbyId() {
		Set<Contact> contacts = mgr.getContacts(1,2,3,4,5,6);
		printContactList("BY ID", contacts);
	}
	
	public void populateContactListABC() {
		contactListA = mgr.getContacts(1,2,3);
		contactListB = mgr.getContacts(3,4,5,6);
		contactListC = mgr.getContacts("Isabelle");
	}
	
	public void printContactList(String what, Set<Contact> contacts) {
		Iterator<Contact> myIterator=contacts.iterator();
		while(myIterator.hasNext()) {
			Contact currentContact = myIterator.next();
			System.out.print(what + "," + currentContact.getId() + ",");
			System.out.print(currentContact.getName() + "," + currentContact.getNotes());
			System.out.println();
		}
	}
	
	public void getFutureMeetingonId() {
		Meeting myMeeting=null;
		try {
			myMeeting = mgr.getFutureMeeting(1);
			System.out.println("Future Meeting 1 found");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
		myMeeting=null;
		try {
			myMeeting = mgr.getFutureMeeting(6);
			System.out.println("Future Meeting 6 found");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void addFutureMeetings() {
		// add all future dates except for the last one, which should cause an exception
		Calendar c1 = Calendar.getInstance();
		c1.set(2014, Calendar.MARCH, 30);
		c1.getTime();
		
		try {
			mgr.addFutureMeeting(contactListA, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			mgr.addFutureMeeting(contactListB, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2014, Calendar.JUNE, 30);
		c1.getTime();
		
		try {
			mgr.addFutureMeeting(contactListA, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2014, Calendar.OCTOBER, 30);
		c1.getTime();
		
		try {
			mgr.addFutureMeeting(contactListC, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		try {
			mgr.addFutureMeeting(contactListC, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2013, Calendar.MARCH, 30);
		c1.getTime();
	
		try {
			mgr.addFutureMeeting(contactListC, c1);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	public void addPastMeetings() {
		// add a number of dates that are in the past plus one date that is in the future
		Calendar c1 = Calendar.getInstance();
		c1.set(2013, Calendar.MARCH, 30);
		c1.getTime();
		
		try {
			mgr.addNewPastMeeting(contactListA, c1, "Contact A, meeting held on 30/03/2013");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		try {
			mgr.addNewPastMeeting(contactListB, c1, "Contact B, meeting held on 30/03/2013");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2013, Calendar.JUNE, 30);
		c1.getTime();
		
		try {
			mgr.addNewPastMeeting(contactListA, c1, "Contact A, meeting held on 30/06/2013");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2013, Calendar.OCTOBER, 30);
		c1.getTime();
		
		try {
			mgr.addNewPastMeeting(contactListC, c1, "Contact A, meeting held on 30/10/2013");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		
		c1 = Calendar.getInstance();
		c1.set(2014, Calendar.MARCH, 30);
		c1.getTime();
	
		try {
			mgr.addNewPastMeeting(contactListC, c1, "future date so should throw error");
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void printPastMeetingsbyId() {
		List<Meeting> meetings = new ArrayList<Meeting>();
		for (int i=1;i<7;i++) {
			try {
				Meeting myMeeting = mgr.getPastMeeting(i);
				if (myMeeting!=null) meetings.add(myMeeting);
			} catch (IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			} catch (IllegalStateException ex) {
				System.out.println(ex.getMessage());
			}
		}
		printMeetings(meetings);
	}
	
	public void printFutureMeetingsbyContact() {
	// need to get a single contact to pass as a parameter 
		Iterator<Contact> myIterator=contactListA.iterator();
		Contact currentContact=null;
		while(myIterator.hasNext()) {
			currentContact = myIterator.next(); // have assigned the first contact
			break;
		}
	
		List<Meeting> meetings = new ArrayList<Meeting>();
		System.out.println("*******" + currentContact.getName() + ":" + currentContact.getId());
		meetings = mgr.getFutureMeetingList(currentContact);
		if (meetings!=null) printMeetings(meetings);
		else System.out.println("No meetings for " + currentContact.getName());
	}
	
	public void printFutureMeetingbyDate() {
		Calendar c1 = Calendar.getInstance();
		c1.set(2014, Calendar.MARCH, 30);
		c1.getTime();
		
		List<Meeting> meetings = new ArrayList<Meeting>();
		System.out.println("*******" + c1.getTime());
		meetings = mgr.getFutureMeetingList(c1);
		if (meetings!=null) printMeetings(meetings);
		else System.out.println("No meetings for " + c1.getTime());
		
	}
	
	public void printPastMeetingbyContact() {
		
		// need to get a single contact to pass as a parameter 
		Iterator<Contact> myIterator=contactListA.iterator();
		Contact currentContact=null;
		while(myIterator.hasNext()) {
			currentContact = myIterator.next(); // have assigned the first contact
			break;
		}
	
		List<PastMeeting> meetings = new ArrayList<PastMeeting>();
		System.out.println("*******" + currentContact.getName() + ":" + currentContact.getId());
		try {
			meetings = mgr.getPastMeetingList(currentContact);
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalStateException ex) {
			System.out.println(ex.getMessage());
		}
		if (meetings!=null) printPastMeetings(meetings);
		else System.out.println("No meetings for Isabelle");
	}
	
	public void printPastMeetings(List<PastMeeting> meetings) {
		Iterator<PastMeeting> iter = meetings.iterator();
		while (iter.hasNext()) {
			PastMeeting thisMeeting = iter.next();
			System.out.print("Meeting Id="+thisMeeting.getId()+" Date="+DateUtilities.formatDate(thisMeeting.getDate()));
			System.out.print(" Notes=" + thisMeeting.getNotes());
			System.out.println();
		}		
	}
	
	public void printMeetings(List<Meeting> meetings) {
		Iterator<Meeting> iter = meetings.iterator();
		while (iter.hasNext()) {
			Meeting thisMeeting = iter.next();
			System.out.print("Meeting Id="+thisMeeting.getId()+" Date="+DateUtilities.formatDate(thisMeeting.getDate()));
			if (thisMeeting.getClass() == PastMeetingImpl.class) {
				PastMeetingImpl pastMeeting = (PastMeetingImpl) thisMeeting; 
				System.out.print(" Notes=" + pastMeeting.getNotes());
			}
			System.out.println();
			Set<Contact> myContacts=thisMeeting.getContacts();
			printContactList("Meetings", myContacts);
		}		
	}
	
	public static void main(String[] args) {
		TestHarness script = new TestHarness();
		script.launch();
	}
}