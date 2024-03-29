package billboard_control_panel.Calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * CalendarWeek provides functionality to decide upon the start day, end day, how many days to show, which day is the
 * beginning, and what the previous, next and today buttons do.
 */
public class CalendarWeek extends CalendarViewer {
    private Week week;

    public CalendarWeek(ArrayList<CalendarEvent> events) {
        super(events);
        week = new Week(LocalDate.now());
    }
    @Override
    protected boolean dateInRange(LocalDate date) {
        return Week.getStartOfWeek(date).equals(week.getDay(DayOfWeek.MONDAY));
    }

    @Override
    protected LocalDate getDateFromDay(DayOfWeek day) {
        return week.getDay(day);
    }

    // Adjust to show just weekdays, all days etc
    @Override
    protected int numDaysToShow() {
        return 7;
    }


    @Override
    protected DayOfWeek getStartDay() {
        return DayOfWeek.MONDAY;
    }

    @Override
    protected DayOfWeek getEndDay() {
        return DayOfWeek.SUNDAY;
    }

    @Override
    protected void setRangeToToday() {
        week = new Week(LocalDate.now());
    }

    @Override
    protected double dayToPixel(DayOfWeek dayOfWeek) {
        return TIME_COL_WIDTH + getDayWidth() * (dayOfWeek.getValue() - 1);
    }

    public void nextWeek() {
        week = week.nextWeek();
        repaint();
    }

    public void prevWeek() {
        week = week.prevWeek();
        repaint();
    }

    public void nextMonth() {
        week = new Week(week.getDay(DayOfWeek.MONDAY).plusWeeks(4));
        repaint();
    }

    public void prevMonth() {
        week = new Week(week.getDay(DayOfWeek.MONDAY).minusWeeks(4));
        repaint();
    }
}
