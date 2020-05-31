package billboard_control_panel.Calendar;

import java.text.ParseException;
import java.util.EventListener;
/**
 * CalendarEmptyClickListener checks if, within the calendar viewer, and empty space has been clicked.
 * If so, it will send the information to the CalendarEmptyClickEvent.
 */
public interface CalendarEmptyClickListener extends EventListener {
    // Event dispatch methods
    void calendarEmptyClick(CalendarEmptyClickEvent e) throws ParseException;
}