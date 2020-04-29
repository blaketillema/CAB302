package billboard_control_panel.Calendar;

import billboard_control_panel.schedulerController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalendarCreator {
    public static void event() {

    }
    public static void main(String[] args) {
        JFrame frm = new JFrame();
        ArrayList<CalendarEvent> events = new ArrayList<>();

        //TODO: Connect event adding with scheduler functions and buttons e.g. port the user select variables into the following function
        events.add(new CalendarEvent(LocalDate.of(2020, 4, 28), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1"));
        //TODO: Create a function to account for recurring schedules

        CalendarWeek cal = new CalendarWeek(events);

        cal.addCalendarEventClickListener(e -> {
            //System.out.println(e.getCalendarEvent());
            String dateTimeName = String.valueOf(e.getCalendarEvent());
            schedulerController.getValues(dateTimeName);
        });

        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            //int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
            System.out.println(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30));
            String dateTimeName = String.valueOf(e.getDateTime());


            //schedulerController.
        });

        // Calendar Week Controls (NORTH)
        JButton goToTodayBtn = new JButton("Today");
        goToTodayBtn.addActionListener(e -> cal.goToToday());

        JButton nextWeekBtn = new JButton(">");
        nextWeekBtn.addActionListener(e -> cal.nextWeek());

        JButton prevWeekBtn = new JButton("<");
        prevWeekBtn.addActionListener(e -> cal.prevWeek());

        JButton nextMonthBtn = new JButton(">>");
        nextMonthBtn.addActionListener(e -> cal.nextMonth());

        JButton prevMonthBtn = new JButton("<<");
        prevMonthBtn.addActionListener(e -> cal.prevMonth());

        // Add the buttons into a JPanel
        JPanel weekControls = new JPanel();
        weekControls.add(prevMonthBtn);
        weekControls.add(prevWeekBtn);
        weekControls.add(goToTodayBtn);
        weekControls.add(nextWeekBtn);
        weekControls.add(nextMonthBtn);

        // TODO Add functionality to these buttons, linking with database and server
        // Calendar Week Controls (EAST)
        JButton saveBtn = new JButton("Save");
        //goToTodayBtn.addActionListener(e -> cal.goToToday());

        JButton deleteBtn = new JButton("Delete");
        //nextWeekBtn.addActionListener(e -> cal.nextWeek());

        JButton cancelBtn = new JButton("Cancel");
        //prevWeekBtn.addActionListener(e -> cal.prevWeek());

        // create a object of JTextField with 16 columns and a given initial text
        JTextField scheduleName = new JTextField("enter billboard schedule name", 16);

        // Add the buttons into a JPanel
        JPanel schedulePanel = new JPanel();

        JPanel scheduleControls = new JPanel();
        scheduleControls.add(scheduleName);
        scheduleControls.add(deleteBtn);
        scheduleControls.add(saveBtn);
        scheduleControls.add(cancelBtn);

        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(new schedulerController().schedulerPanel, BorderLayout.EAST);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1000, 900);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
