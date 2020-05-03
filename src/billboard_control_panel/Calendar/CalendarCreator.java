package billboard_control_panel.Calendar;

import billboard_control_panel.schedulerController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalendarCreator extends Frame {
    //private static Object schedulerController;

    public CalendarCreator(CalendarWeek cal) {
        setLayout(new GridLayout());
        JFrame frm = new JFrame();

        schedulerController scheduler = new schedulerController();
        JPanel schedulerPanel = new schedulerController().schedulerPanel;
        //TODO: Connect event adding with scheduler functions and buttons e.g. port the user select variables into the following function
        //TODO: Create a function to account for recurring schedules
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
        JButton resetBtn = new JButton("Reset");
        JButton saveBtn = new JButton("Save");
        //goToTodayBtn.addActionListener(e -> cal.goToToday());

        JButton deleteBtn = new JButton("Delete");
        //nextWeekBtn.addActionListener(e -> cal.nextWeek());

        JButton exitBtn = new JButton("Exit");
        //prevWeekBtn.addActionListener(e -> cal.prevWeek());

        // create a object of JTextField with 16 columns and a given initial text
        JTextField scheduleName = new JTextField("Enter Schedule Name", 16);

        // Spinners
        Integer value = 3;
        Integer min = 0;
        Integer max = 24;
        Integer step = 1;
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner hourSpinner = new JSpinner(model);
        JSpinner minuteSpinner = new JSpinner(model);
        // Add the buttons into a JPanel
        JPanel schedulePanel = new JPanel();

        JPanel scheduleControls = new JPanel();
        scheduleControls.add(scheduleName);
        scheduleControls.add(deleteBtn);
        scheduleControls.add(saveBtn);
        scheduleControls.add(exitBtn);
        scheduleControls.add(hourSpinner);
        scheduleControls.add(minuteSpinner);

        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(scheduleControls, BorderLayout.EAST);
        //frm.add(schedulerPanel, BorderLayout.EAST);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1000, 900);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        cal.addCalendarEventClickListener(e -> {
            //System.out.println(e.getCalendarEvent());
            scheduleName.setText("K");
            String dateTimeName = String.valueOf(e.getCalendarEvent());
            //schedulerController.getValues(dateTimeName);
        });

        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            //int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
            System.out.println(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30));
            //scheduleName.setText(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30).toString());
            //scheduler.minuteSpinnerValue = 30;
            //scheduler.minuteSpinner.setValue(30);

            String dateTimeName = String.valueOf(e.getDateTime());
        });

    }



    public static void main(String[] args) {

        ArrayList<CalendarEvent> events = new ArrayList<>();
        CalendarWeek cal = new CalendarWeek(events);
        CalendarCreator edit = new CalendarCreator(cal);
        events.add(new CalendarEvent(LocalDate.of(2020, 4, 28), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1"));


    }
}
