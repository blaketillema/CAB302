package billboard_control_panel.Calendar;

import billboard_control_panel.LoginManager;
import billboard_control_panel.MainControl;
import billboard_control_panel.ScheduleController;
import billboard_control_panel.Scheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarCreator extends Frame {
    static ArrayList<CalendarEvent> events = new ArrayList<>();
    static CalendarWeek tal = new CalendarWeek(events);
    public CalendarCreator(CalendarWeek cal) {
        setLayout(new GridLayout());
        JFrame frm = new JFrame();

        //JPanel
        JPanel schedulerPanel = new JPanel();
        schedulerPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(13, 6, new Insets(0, 0, 0, 0), -1, -1));

        // Title
        final JLabel label2 = new JLabel();
        label2.setText("Billboard Scheduler Editor");
        schedulerPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 16), null, 0, false));

        // Schedule Name
        JTextField enterScheduleNameTextField = new JTextField();
        enterScheduleNameTextField.setText("Enter Schedule Name");
        schedulerPanel.add(enterScheduleNameTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        // Start Time and Spinner
        final JLabel label1 = new JLabel();
        label1.setText("Start time:");
        schedulerPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // Spacers
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(11, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("");
        schedulerPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // Bottom Buttons
        JButton deleteButton = new JButton();
        deleteButton.setText("Delete");
        schedulerPanel.add(deleteButton, new com.intellij.uiDesigner.core.GridConstraints(12, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 24), null, 0, false));
        JButton resetButton = new JButton();
        resetButton.setText("Reset");
        schedulerPanel.add(resetButton, new com.intellij.uiDesigner.core.GridConstraints(12, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 24), null, 0, false));
        JButton exitButton = new JButton();
        exitButton.setText("Exit");
        schedulerPanel.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(12, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JButton saveButton = new JButton();
        saveButton.setText("Save");
        schedulerPanel.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(12, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(69, 24), null, 0, false));
        final JLabel label3 = new JLabel();

        // Reoccuring values
        label3.setText("Duration:");
        schedulerPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Hours:");
        schedulerPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 16), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Minutes:");
        schedulerPanel.add(label5, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 16), null, 0, false));

        JCheckBox recurringCheckBox = new JCheckBox();
        recurringCheckBox.setText("Recurring");
        schedulerPanel.add(recurringCheckBox, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final JRadioButton dailyButton = new JRadioButton();
        dailyButton.setText("Daily");
        dailyButton.setEnabled(false);
        schedulerPanel.add(dailyButton, new com.intellij.uiDesigner.core.GridConstraints(9, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 18), null, 0, false));

        final JRadioButton hourlyButton = new JRadioButton();
        hourlyButton.setText("Hourly");
        hourlyButton.setEnabled(false);
        schedulerPanel.add(hourlyButton, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 18), null, 0, false));

        final JLabel label7 = new JLabel();
        label7.setText("Minutely:");
        label7.setEnabled(false);
        schedulerPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(10, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 16), null, 0, false));

        // Time Spinners
        Date date = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        SpinnerDateModel em = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner endSpinner =new JSpinner(em);
        JSpinner.DateEditor ee=new JSpinner.DateEditor(endSpinner,"EEE MMM ddHH:mm:ss Z yyyy");
        schedulerPanel.add(endSpinner,new com.intellij.uiDesigner.core.GridConstraints(7,2,1,1,com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,null,new Dimension(58,26),null,0,false));

        // Duration Spinners
        final JSpinner durationMinutes = new JSpinner();
        schedulerPanel.add(durationMinutes, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 26), null, 0, false));
        final JSpinner durationHours = new JSpinner();
        schedulerPanel.add(durationHours, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        //SpinnerDateModel em = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner startSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(startSpinner, "EEE MMM dd HH:mm:ss Z yyyy");
        schedulerPanel.add(startSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        int min = 0;
        int max = 10080;
        int val = min;
        int step = 30;
        SpinnerNumberModel mins = new SpinnerNumberModel(val,min,max,step);
        final JSpinner minuteSpinner = new JSpinner(mins);
        minuteSpinner.setEnabled(false);
        schedulerPanel.add(minuteSpinner, new com.intellij.uiDesigner.core.GridConstraints(10, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));



        recurringCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(true);
                    label7.setEnabled(true);
                } else {//checkbox has been deselected
                    dailyButton.setEnabled(false);
                    hourlyButton.setEnabled(false);
                    minuteSpinner.setEnabled(false);
                    label7.setEnabled(false);
                };
            }
        });

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

        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(schedulerPanel, BorderLayout.EAST);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1600, 900);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        // Listeners
        cal.addCalendarEventClickListener(e -> {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStartDateTime().toString());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStartDateTime().plusHours(1).toString());
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
            enterScheduleNameTextField.setText(e.getBillboardName());

        });

        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            System.out.println(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30));
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getDateTime().toString());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getDateTime().plusHours(1).toString());
            System.out.println(startDate);
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
            enterScheduleNameTextField.setText("Enter new schedule name...");
        });


        //TODO: Port saved data to billboard server, database and calendar view
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String billboardName = (String) enterScheduleNameTextField.getText();
                Date startDateTime = (Date) startSpinner.getValue();
                Date endDateTime = (Date) endSpinner.getValue();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(endDateTime);
                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = calendar.get(Calendar.MINUTE);
                // Get msec from each, and subtract.
                long diff = endDateTime.getTime() - startDateTime.getTime();
                int diffMinutes = Math.toIntExact(diff / (60 * 1000));
                System.out.println(diffMinutes);
                int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                int hourDurationValue = (Integer) durationHours.getValue();
                int minutesDurationValue = (Integer) durationMinutes.getValue();
                boolean recurring = (Boolean) recurringCheckBox.isSelected();
                boolean recurringDaily = (Boolean) dailyButton.isSelected();
                boolean recurringHourly = (Boolean) hourlyButton.isSelected();
                int recurringEveryXminutes = 0;
                if (recurringDaily == true){
                    recurringEveryXminutes += 1440;
                }
                else {recurringEveryXminutes += 0;};

                if (recurringHourly == true){
                    recurringEveryXminutes += 60;
                }
                else {recurringEveryXminutes += 0;};
                recurringEveryXminutes = recurringEveryXminutes + minuteSpinnerValue;

                // TODO: CHANGE OFFSETDATETIME VALUE TO NORMAL DATETIME FOR EASE OF USE
                OffsetDateTime offsetDateTime = startDateTime.toInstant().atOffset(ZoneOffset.UTC);

                ScheduleController.commandAddSchedule(billboardName,offsetDateTime,diffMinutes, recurring,recurringEveryXminutes, "Lahiru" );
                // CHECK OUTPUT
                System.out.println("Instructions from GUI for Command:  " + ScheduleController.getCurrentCommandName() + ", the data is:\n"
                        + ScheduleController.getCurrentCommandData().toString() +"\n" );

                // TODO: ADD event according to user selected values
                // TODO: THIS DOESNT DO THE CORRECT JOB, NEED TO FIGURE OUT HOW TO REFRESH SAME WINDOW WITH NEW SCHEDULE
                //events.add(new CalendarEvent(LocalDate.of(2020, 5, 7), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1"));
                //CalendarWeek cal = new CalendarWeek(events);
                // date2 = formatter.parseLocalDate(startDateTime);
                Calendar calendarStart = new GregorianCalendar();
                calendar.setTime(startDateTime);
                int year = calendar.get(Calendar.YEAR);
                //Add one to month {0 - 11}
                int month = calendarStart.get(Calendar.MONTH) + 1;
                int day = calendarStart.get(Calendar.DAY_OF_MONTH);
                int hour = calendarStart.get(Calendar.HOUR_OF_DAY);
                int minute = calendarStart.get(Calendar.MINUTE);
                //int endHour = hour + hourDurationValue;
                //int endMinute = minute + minutesDurationValue;

                System.out.println(startDateTime);
                System.out.println(year);
                System.out.println(month);
                System.out.println(day);
                System.out.println(hour);
                System.out.println(minute);
                System.out.println(endHour);
                System.out.println(endMinute);
                events.add(new CalendarEvent(LocalDate.of(year, month, day), LocalTime.of(hour, minute), LocalTime.of(endHour, endMinute), billboardName));
                cal.goToToday();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new MainControl().main(null);


            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setValues();
                String billboardName = (String) enterScheduleNameTextField.getText();
                Date date1 = (Date) startSpinner.getValue();

                // TODO: CHANGE OFFSETDATETIME VALUE TO NORMAL DATETIME FOR EASE OF USE
                OffsetDateTime offsetDateTime = date1.toInstant().atOffset(ZoneOffset.UTC);
                ScheduleController.commandRemoveSchedule(billboardName,offsetDateTime);
                ScheduleController.commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() );
                System.out.println("Instructions from GUI for Command:  " + ScheduleController.getCurrentCommandName() + ", the data is:\n"
                        + ScheduleController.getCurrentCommandData().toString() +"\n" );
            }
        });

    }


    public static void main(String[] args) {
        // TODO: Store Calendar/Scheduled Events in Database which is where GUI retrieves data to display from
        //i.e for each event in database, events.add(calendarevent ...)
        OffsetDateTime timeNowPlus10Days = OffsetDateTime.now().plusDays(10);
        // TEST DATA
        String billboardName = "Great Billboard!";
        OffsetDateTime schedStart = timeNowPlus10Days;
        Integer duration = 60;
        Boolean recur = true;
        Integer recurFreqMins = 30;
        String creatorName = "John";
        ScheduleController.commandAddSchedule(billboardName, schedStart, duration, recur, recurFreqMins, creatorName);
        ScheduleController.commandGetSchedules();
        System.out.println(ScheduleController.getCurrentCommandData().toString());

        //ArrayList<CalendarEvent> events = new ArrayList<>();
        //CalendarWeek cal = new CalendarWeek(events);
        CalendarCreator edit = new CalendarCreator(tal);
        //events.add(new CalendarEvent(LocalDate.of(2020, 4, 28), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1"));
        //CalendarViewer view = new CalendarViewer();


    }


}
