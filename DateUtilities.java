import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;


/**
 * A date utilities class to format and to check whether a date is in the past or the future
 */
public class DateUtilities {
	/**
     * Returns today's date as a string.
     *
     * @return a string representation of the current date
     */
	public static String getDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm"); 
		Calendar myCalendar = Calendar.getInstance();
		return dateFormat.format(myCalendar.getTime()).toString();
	}
	
	/**
     * Convert the given date to a string with format yyyy/MM/dd HH:mm
     * @param date the Calendar date to be formatted
     * @return a string representation of the date
     */
	public static String formatDate(Calendar date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return dateFormat.format(date.getTime());
	}
	
	/**
     * Return true if the given date is in the past
     * @param date the Calendar date to be checked
     * @return true if in the past, otherwise false 
     */
	public static boolean dateInPast(Calendar date) {
		Calendar today = Calendar.getInstance();
		
		if (date.get(Calendar.YEAR) < today.get(Calendar.YEAR)) return true;
		if (date.get(Calendar.YEAR) > today.get(Calendar.YEAR)) return false;
		if (date.get(Calendar.DAY_OF_YEAR) >= today.get(Calendar.DAY_OF_YEAR)) return false;
		
		return true;
	}
	
	/**
     * Return true if the given date is in the future
     * @param date the Calendar date to be checked
     * @return true if in the future, otherwise false 
     */
	public static boolean dateInFuture(Calendar date) {
		Calendar today = Calendar.getInstance();

		if (date.get(Calendar.YEAR) > today.get(Calendar.YEAR)) return true;
		if (date.get(Calendar.YEAR) < today.get(Calendar.YEAR)) return false;
		if (date.get(Calendar.DAY_OF_YEAR) <= today.get(Calendar.DAY_OF_YEAR)) return false;
		
		return true;
	}
	
	public static int compareDates(Calendar d1, Calendar d2) {
		if (d1.get(Calendar.YEAR) > d2.get(Calendar.YEAR)) return 1;
		if (d1.get(Calendar.YEAR) < d2.get(Calendar.YEAR)) return -1;
		if (d1.get(Calendar.DAY_OF_YEAR) > d2.get(Calendar.DAY_OF_YEAR)) return 1;
		if (d1.get(Calendar.DAY_OF_YEAR) < d2.get(Calendar.DAY_OF_YEAR)) return -1;
		return 0;
	}
}