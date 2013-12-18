import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.*;
import java.io.Serializable;

public class ContactImpl implements Contact, Serializable {
 
	private int id;
	private String name;
	private String notes;
	
	public ContactImpl(String name, int id) throws IllegalArgumentException {
		
		try {
			checkText(name);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(ex.getMessage());
		}
		this.name=name; 
		notes="";
		this.id=id;
	}
	
	public ContactImpl(String name, int id, String notes) {
		this(name, id);
		addNotes(notes);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ContactImpl)) return false;
		//if ( (ContactImpl)o.getId().equals(this.getId()) && (ContactImpl)o.getName().equals(this.getName()) ) return true;
		ContactImpl that = (ContactImpl) o;
		if ( that.getId()==this.getId() ) return true;
		else return false;
	}	
	
	@Override
	public int hashCode() {
        return (41 * (41 + getId()) );
	}
	
	private void checkText(String text) throws IllegalArgumentException {	
		// I use regular expressions to check that the contact name is valid i.e. contacts alpha numeric, space or ., 
		// if not I print an error and exit program (will just throw in reality)
		// I appreciate this limits the application to English at the moment but can add to the Regex to include other character types when needed
		Pattern pattern = Pattern.compile("[^a-z0-9 .,@]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(text);
		boolean test = m.find();
		if (test) throw new IllegalArgumentException("invalid characters in name " + text);
	}
	
	@Override
	public int getId() {
		return id;
	}

    @Override
	public String getName() {
		return name;
	}

    @Override
	public String getNotes() {
		return notes;
	}
	
	@Override
    public void addNotes(String note) {
		//each new note is given the current date/time as a create date and also put on a new line
		notes=notes + "\n" + DateUtilities.getDateString() + ":" + note; 
	}
} 