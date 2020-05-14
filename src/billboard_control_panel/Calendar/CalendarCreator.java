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

        // Start Time Label
        final JLabel label1 = new JLabel();
        label1.setText("Start time:");
        schedulerPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        // Start Time Spinner
        Date date = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner startSpinner = new JSpinner(sm);
        //JSpinner.DateEditor de = new JSpinner.DateEditor(startSpinner, "EEE MMM dd HH:mm:ss Z yyyy");
        schedulerPanel.add(startSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        // End Time Label
        final JLabel label3 = new JLabel();
        label3.setText("End Time:");
        schedulerPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // End Time Spinner
        SpinnerDateModel em = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner endSpinner =new JSpinner(em);
        //JSpinner.DateEditor ee=new JSpinner.DateEditor(endSpinner,"EEE MMM ddHH:mm:ss Z yyyy");
        schedulerPanel.add(endSpinner,new com.intellij.uiDesigner.core.GridConstraints(7,2,1,1,com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,null,new Dimension(58,26),null,0,false));

        // Recurring Checkbox and buttons
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

        final JRadioButton minutelyButton = new JRadioButton();
        minutelyButton.setText("Per Minute(s):");
        minutelyButton.setEnabled(false);
        schedulerPanel.add(minutelyButton, new com.intellij.uiDesigner.core.GridConstraints(10, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 18), null, 0, false));

        int min = 0, max = 10080, val = min, step = 30;
        SpinnerNumberModel mins = new SpinnerNumberModel(val,min,max,step);
        final JSpinner minuteSpinner = new JSpinner(mins);
        minuteSpinner.setEnabled(false);
        schedulerPanel.add(minuteSpinner, new com.intellij.uiDesigner.core.GridConstraints(10, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

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

        ////////////////// Calendar Week Controls (NORTH) ////////////////////////////
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

        //// CREATE ENTIRE SCHEDULE + CALENDAR VIEW ///////
        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(schedulerPanel, BorderLayout.EAST);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1600, 900);
        frm.setExtendedState(frm.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        ///// Calendar Viewer Listeners /////
        cal.addCalendarEventClickListener(e -> {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStartDateTime().toString());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getEndDateTime().toString());
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
            if (enterScheduleNameTextField.getText() != "Enter new schedule name..."){
                //TODO: optional - check if ScheduleName exists in db
            } else enterScheduleNameTextField.setText("Enter new schedule name...");

        });

        /// Button Listeners ////
        recurringCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(true);
                    minutelyButton.setEnabled(true);
                    minuteSpinner.setEnabled(false);
                } else {//checkbox has been deselected
                    dailyButton.setSelected(false);
                    hourlyButton.setSelected(false);
                    minuteSpinner.setValue(0);
                    minutelyButton.setSelected(false);
                    dailyButton.setEnabled(false);
                    hourlyButton.setEnabled(false);
                    minuteSpinner.setEnabled(false);
                    minutelyButton.setEnabled(false);
                };
            }
        });

        dailyButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(false);
                    minuteSpinner.setEnabled(false);
                    minutelyButton.setEnabled(false);
                } else {//checkbox has been deselected
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(true);
                    minutelyButton.setEnabled(true);
                    minuteSpinner.setEnabled(false);
                };
            }
        });

        hourlyButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(false);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(false);
                    minutelyButton.setEnabled(false);
                } else {//checkbox has been deselected
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(true);
                    minutelyButton.setEnabled(true);
                    minuteSpinner.setEnabled(false);
                };
            }
        });

        minutelyButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(false);
                    hourlyButton.setEnabled(false);
                    minuteSpinner.setEnabled(true);
                } else {//checkbox has been deselected
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(false);
                }
//                minuteSpinner.setEnabled(true);
//                minutelyButton.setEnabled(true);
            }
        });

        //TODO: Port saved data to billboard server, database and calendar view
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = new GregorianCalendar();
                // Get Values Entered:
                String billboardName = (String) enterScheduleNameTextField.getText();
                // Get Start Time Values
                Date startDateTime = (Date) startSpinner.getValue();
                calendar.setTime(startDateTime);
                int startMinute = calendar.get(Calendar.MINUTE);
                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startMonth = calendar.get(Calendar.MONTH) + 1; //Add one to month {0 - 11}
                int startYear = calendar.get(Calendar.YEAR);
                // Get End Time Values
                Date endDateTime = (Date) endSpinner.getValue();
                calendar.setTime(endDateTime);
                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = calendar.get(Calendar.MINUTE);
                // Get seconds from each, and subtract.
                long diff = endDateTime.getTime() - startDateTime.getTime();
                int diffMinutes = Math.toIntExact(diff / (60 * 1000));
                // Get recurring values
                int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                boolean recurring = (Boolean) recurringCheckBox.isSelected();
                boolean recurringDaily = (Boolean) dailyButton.isSelected();
                boolean recurringHourly = (Boolean) hourlyButton.isSelected();
                boolean recurringMinutely = (Boolean) minutelyButton.isSelected();

                // Add recurring values
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

                // ADD Schedule to Calendar View
                if (startDateTime.compareTo(endDateTime) > 0){
                    System.out.println("Invalid input");
                }
                enterScheduleNameTextField.setText("Enter new schedule name...");

                // Converting DateTime to LocalDateTime to OffsetDateTime for commandAddSchedule function
                LocalDateTime convertedDate = Instant.ofEpochMilli(startDateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                OffsetDateTime offsetDateTime = convertedDate.atOffset(OffsetDateTime.now().getOffset());

                // TODO: Grab Current Signed-in User and input into function below
                ScheduleController.commandAddSchedule(billboardName,offsetDateTime,diffMinutes, recurring,recurringEveryXminutes, "Lahiru" );
                Scheduler.commandParser( ScheduleController.getCurrentCommandName(), ScheduleController.getCurrentCommandData() );
                String CommandNow = Scheduler.getCurrentCommandName();
                if (CommandNow == "schedule-response:error"){
                    JOptionPane.showMessageDialog(null,
                            ScheduleController.commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() ),
                            "Error",
                            JOptionPane.WARNING_MESSAGE);

                }
                else {
                    //TODO: Add case where checkbox is ticked but no other buttons are selected
                    if (!recurringCheckBox.isSelected()){
                        events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                        cal.goToToday();
                    }
                    // Daily Recurrence
                    else if (recurringDaily == true){
                        int day = startDay;
                        int month = startMonth;
                        int days = 0;
                        // TODO: Reschedules for a year (can specify this with a user-input value)
                        while (days <= 365 ){
                            events.add(new CalendarEvent(LocalDate.of(startYear, month, day), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                            day = (day + 1)%32;
                            days++;
                            if (day == 0){
                                day++;
                                month++;
                            }
                            // Year
                            if (month == 13){
                                month = 1;
                                startYear++;
                            }
                            // Months with 30 days
                            if ((day == 31) && (month == 4 || month  == 6 || month == 9|| month == 11)){
                                day = 1;
                                month++;
                            }
                            // February Non-Leap Year
                            if (month == 2 && startYear%4 != 0 && day==29){
                                day = 1;
                                month++;
                            }
                            // February Leap Year
                            if (month == 2 && startYear%4 != 0 && day==30){
                                day = 1;
                                month++;
                            }
                        }
                        cal.goToToday();
                    }
                    // Hourly Recurrence
                    else if (recurringHourly == true){
                        int Shour = startHour;
                        int Ehour = endHour;
                        int day = startDay;
                        int month = startMonth;
                        int hours = 0;

                        while (hours <= 8760 ){
                            events.add(new CalendarEvent(LocalDate.of(startYear, month, day), LocalTime.of(Shour , startMinute), LocalTime.of(Ehour, endMinute), billboardName));
                            //day = (day+1)%31;
                            Shour = (Shour + 1)%24;
                            Ehour = (Ehour +1)%24;
                            if (day == 0){
                                day++;
                                month++;
                            }
                            if (Ehour == 0){
                                day++;
                                Ehour++;
                            }
                        }
                        cal.goToToday();
                    }

                    // Minutely Recurrence
                    else if (recurringMinutely == true){
                        int Shour = startHour;
                        int Ehour = endHour;
                        int Smin = startMinute;
                        int Emin = endMinute;
                        int day = startDay;
                        int month = startMonth;
                        int hours = 0;
                        int days = 0;
                        int Rminute = minuteSpinnerValue;
                        int freqHours = Rminute/60%24;
                        int remMins = (Rminute%60);
                        int duration = diffMinutes;
                        // TODO: Reschedules for a year (can specify this with a user-input value)
                        while (days <= 365 ){
                            events.add(new CalendarEvent(LocalDate.of(startYear, month, day), LocalTime.of(Shour, Smin), LocalTime.of(Ehour, Emin), billboardName));

                            Smin = (Smin + remMins);
                            if (Smin >= 60){
                                Smin = Smin%60;
                                Shour++;
                            }
                            Shour = (Shour + freqHours);
                            if (Shour > 23){
                                Shour = Shour%24;
                                Smin = Smin%60;
                                //day++;
                            }
                            Emin = (Emin + remMins);
                            if (Emin >= 60){
                                Emin = Emin%60;
                                Ehour++;
                            }
                            Ehour = (Ehour + freqHours);
                            if (Ehour > 23){
                                Ehour = duration/60%24;
                                Emin = duration%60;
                                day++;
                            }

                            // Year
                            if (month == 13){
                                month = 1;
                                startYear++;
                            }
                            // Months with 31 days
                            if ((day == 32) && (month == 1 || month  == 3 || month == 5|| month == 7|| month == 8|| month == 10|| month == 12)){
                                day = 1;
                                month++;
                            }
                            // Months with 30 days
                            if ((day >= 31) && (month == 4 || month  == 6 || month == 9|| month == 11)){
                                day = 1;
                                month++;
                            }
                            // February Non-Leap Year
                            if (month == 2 && startYear%4 != 0 && day==29){
                                day = 1;
                                month++;
                            }
                            // February Leap Year
                            if (month == 2 && startYear%4 != 0 && day==30){
                                day = 1;
                                month++;
                            }
                            System.out.println(Rminute/60%24); // Gives total hours
                            System.out.println(Rminute%60); // Gives remaining minutes after total hours
                            //System.out.println(Ehour);
                            days++;

                        }
                        cal.goToToday();
                    }
//                    JOptionPane.showMessageDialog(null,
//                            ScheduleController.commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() ),
//                            "Success",
//                            JOptionPane.PLAIN_MESSAGE);
                }


                //TODO: Add Recurring schedules to Viewer
                //TODO: Convert total hours, minutes, days into useable format. i.e. >24 hours cant be used in a single day using LocalDate.of


                //TODO: Ensure schedules can't reoccur longer than its frequency

                //TODO: Add Schedule to DB
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = new GregorianCalendar();
                // Get Values Entered:
                String billboardName = (String) enterScheduleNameTextField.getText();
                // Get Start Time Values (Hour and Minute)
                Date startDateTime = (Date) startSpinner.getValue();
                calendar.setTime(startDateTime);
                int startMinute = calendar.get(Calendar.MINUTE);
                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startMonth = calendar.get(Calendar.MONTH) + 1;  //Add one to month {0 - 11}
                int startYear = calendar.get(Calendar.YEAR);
                // Get End Time Values (Hour and Minute)
                Date endDateTime = (Date) endSpinner.getValue();
                calendar.setTime(endDateTime);
                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = calendar.get(Calendar.MINUTE);
                // Converting DateTime to LocalDateTime to OffsetDateTime for commandAddSchedule function
                LocalDateTime convertedDate = Instant.ofEpochMilli(startDateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                OffsetDateTime offsetDateTime = convertedDate.atOffset(OffsetDateTime.now().getOffset());
                ScheduleController.commandRemoveSchedule(billboardName,offsetDateTime);
                ScheduleController.commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() );
                enterScheduleNameTextField.setText("Enter new schedule name...");
                events.remove(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
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

    }


    public static void main(String[] args) {
        CalendarCreator edit = new CalendarCreator(tal);
    }
}
