package billboard_control_panel.Calendar;

import java.text.ParseException;
import java.util.EventListener;

public interface CalendarEmptyClickListener extends EventListener {
    // Event dispatch methods
    void calendarEmptyClick(CalendarEmptyClickEvent e) throws ParseException;
}