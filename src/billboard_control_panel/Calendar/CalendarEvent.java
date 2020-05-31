package billboard_control_panel.Calendar;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * CalendarEvent is responsible for getting and setting a billboard's schedule's details.
 *
 */
public class CalendarEvent {
    private static final Color DEFAULT_COLOR = Color.ORANGE;

    private LocalDate date;
    private LocalTime start;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalTime end;
    private String text;
    private Color color;

    public CalendarEvent(LocalDate date, LocalTime start, LocalTime end, String text) {
        this(date, start, end, text, DEFAULT_COLOR);
    }

    public CalendarEvent(LocalDate date, LocalTime start, LocalTime end, String text, Color color) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.text = text;
        this.color = color;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalDateTime getStartDateTime() {
        LocalDateTime startDateTime = start.atDate(date);
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        LocalDateTime endDateTime = end.atDate(date);
        return endDateTime;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return getDate() + " " + getStart() + "-" + getEnd() + ". " + getText();
    }

    public Color getColor() {
        return color;
    }

    /**
     * Checks if the clicked object is equal to an existing calendar event, irrespective of the exact cursor position
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent that = (CalendarEvent) o;

        if (!date.equals(that.date)) return false;
        if (!start.equals(that.start)) return false;
        return end.equals(that.end);

    }

    // Returns the integer hash code value of the date object considering 31 unique days.
    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
