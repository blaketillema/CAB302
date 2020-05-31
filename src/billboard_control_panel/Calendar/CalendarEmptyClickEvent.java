package billboard_control_panel.Calendar;

import java.awt.*;
import java.time.LocalDateTime;

/**
 * CalendarEmptyClickEvent retrieves the info from the interface, if, within the calendar viewer, an empty space has been clicked.
 * If so, it will return the clicked spot's corresponding dateTime and use it to enter in as the start datetime in the
 * scheduler panel.
 */
public class CalendarEmptyClickEvent extends AWTEvent {
    private LocalDateTime dateTime;

    public CalendarEmptyClickEvent(Object source, LocalDateTime dateTime) {
        super(source, 0);
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}