package billboard_control_panel.Calendar;

import java.awt.*;
import java.time.LocalDateTime;

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