package billboard_control_panel.Calendar;

import java.text.ParseException;
import java.util.EventListener;
/**
 * CalendarEventClickListener checks if, within the calendar viewer, an exisitng event has been clicked.
 * If so, it will send the information to the CalendarEventClickEvent.
 */
public interface CalendarEventClickListener extends EventListener {
    // Event dispatch methods
    void calendarEventClick(CalendarEventClickEvent e) throws ParseException;
}