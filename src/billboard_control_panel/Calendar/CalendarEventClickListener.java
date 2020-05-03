package billboard_control_panel.Calendar;

import java.text.ParseException;
import java.util.EventListener;

public interface CalendarEventClickListener extends EventListener {
    // Event dispatch methods
    void calendarEventClick(CalendarEventClickEvent e) throws ParseException;
}