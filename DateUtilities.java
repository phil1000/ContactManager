import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
 
public class DateUtilities {
	
	public static String getDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); 
		Calendar myCalendar = Calendar.getInstance();
		return dateFormat.format(myCalendar.getTime()).toString();
	}
	
	public static String formatDate(Calendar date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return dateFormat.format(date.getTime());
	}
	
	public static boolean dateInPast(Calendar date) {
		Calendar today = Calendar.getInstance();
		return ( (today.getTime().compareTo(date.getTime())) > 0 ) ?  true : false;
	}
	
}