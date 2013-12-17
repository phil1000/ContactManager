import java.util.Set;
import java.util.Calendar;

public class FutureMeetingImpl extends MeetingImpl implements FutureMeeting {
	public FutureMeetingImpl(Calendar date, int id, Set<Contact> contacts) {
		super(date, id, contacts);
    }
}
