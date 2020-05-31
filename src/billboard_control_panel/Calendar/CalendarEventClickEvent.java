package billboard_control_panel.Calendar;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * CalendarEventClickEvent retrieves the info from the interface, if, within the calendar viewer, an empty space has been clicked.
 * If so, it will return the clicked event's start and endtime along with its name and use it to populate the scheduler panel.
 */
public class CalendarEventClickEvent extends AWTEvent{
    private CalendarEvent calendarEvent;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String billboardName;

    public CalendarEventClickEvent(Object source, CalendarEvent calendarEvent) {
        super(source, 0);
        this.calendarEvent = calendarEvent;
        this.startDateTime = calendarEvent.getStartDateTime();
        this.endDateTime = calendarEvent.getEndDateTime();
        this.billboardName = calendarEvent.getText();
    }

    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public String getBillboardName() {
        return billboardName;
    }
}
