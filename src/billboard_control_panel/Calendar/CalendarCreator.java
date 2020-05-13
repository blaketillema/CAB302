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

        // Schedule Name TextField
        JTextField enterScheduleNameTextField = new JTextField();
        enterScheduleNameTextField.setText("Enter Schedule Name");
        schedulerPanel.add(enterScheduleNameTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        // Start Time Label
        Date date = new Date();
        final JLabel label1 = new JLabel();
        label1.setText("Start time:");
        schedulerPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        // Start Time Spinner
        SpinnerDateModel sm = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner startSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(startSpinner, "EEE MMM dd HH:mm:ss Z yyyy");
        schedulerPanel.add(startSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        // End Time Label
        final JLabel label3 = new JLabel();
        label3.setText("End Time:");
        schedulerPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // End Time Spinner
        SpinnerDateModel em = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner endSpinner = new JSpinner(em);
        JSpinner.DateEditor ee =new JSpinner.DateEditor(endSpinner,"EEE MMM ddHH:mm:ss Z yyyy");
        schedulerPanel.add(endSpinner,new com.intellij.uiDesigner.core.GridConstraints(7,2,1,1,com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,null,new Dimension(58,26),null,0,false));

        // Recurring values
        // CheckBox
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

        // Minute Spinner
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

        ////////////////// Listeners /////////////////////////
        recurringCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    //do something...
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                    minuteSpinner.setEnabled(true);
                    minutelyButton.setEnabled(true);
                } else {//checkbox has been deselected
                    //TODO: Make these buttons in a group of buttons
                    dailyButton.setSelected(false);
                    hourlyButton.setSelected(false);
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
                } else {//checkbox has been deselected
                    dailyButton.setEnabled(true);
                    hourlyButton.setEnabled(true);
                }
                minuteSpinner.setEnabled(true);
                minutelyButton.setEnabled(true);
                ;
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
        frm.setExtendedState(frm.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        // Calendar Event Listeners
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

        // User Save, Delete, Reset and Exit Button Listeners
        //TODO: Port saved data to billboard server, database and calendar view
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = new GregorianCalendar();
                // Get Values Entered:
                String billboardName = (String) enterScheduleNameTextField.getText();
                // Start Time Values
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
                int recurringEveryXminutes = 0;
                int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                boolean recurring = (Boolean) recurringCheckBox.isSelected();
                boolean recurringDaily = (Boolean) dailyButton.isSelected();
                boolean recurringHourly = (Boolean) hourlyButton.isSelected();

                // Add recurring values
                if (recurringDaily == true){
                    recurringEveryXminutes += 1440;
                }
                else {recurringEveryXminutes += 0;};

                if (recurringHourly == true){
                    recurringEveryXminutes += 60;
                }
                else {recurringEveryXminutes += 0;};
                recurringEveryXminutes = recurringEveryXminutes + minuteSpinnerValue;

                // Converting DateTime to LocalDateTime to OffsetDateTime for commandAddSchedule function
                LocalDateTime convertedDate = Instant.ofEpochMilli(startDateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                OffsetDateTime offsetDateTime = convertedDate.atOffset(OffsetDateTime.now().getOffset());
                ScheduleController.commandAddSchedule(billboardName,offsetDateTime,diffMinutes, recurring,recurringEveryXminutes, "Lahiru" );

                // CHECK OUTPUT
                System.out.println("Instructions from GUI for Command:  " + ScheduleController.getCurrentCommandName() + ", the data is:\n"
                        + ScheduleController.getCurrentCommandData().toString() +"\n" );

                // ADD Schedule to Calendar View
                events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                cal.goToToday();

                //TODO: Add Recurring schedules to Viewer
                //TODO: Convert total hours, minutes, days into useable format. i.e. >24 hours cant be used in a single day using LocalDate.of
                if (recurringCheckBox.isSelected() && recurringHourly == true){
                    int i =0;
                    while (i <= 672 ){
                        events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour + i, startMinute), LocalTime.of(endHour + i, endMinute), billboardName));
                        i++;
                    }
                    cal.goToToday();
                }
                else if (recurringCheckBox.isSelected() && recurringDaily == true){
                    int i =0;
                    while (i <= 672 ){
                        events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay+i), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                        i++;
                    }
                    cal.goToToday();
                }

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
                // Start Time Values
                Date startDateTime = (Date) startSpinner.getValue();
                calendar.setTime(startDateTime);
                int startMinute = calendar.get(Calendar.MINUTE);
                int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startMonth = calendar.get(Calendar.MONTH) + 1;  //Add one to month {0 - 11}
                int startYear = calendar.get(Calendar.YEAR);
                // Get End Time Values
                Date endDateTime = (Date) endSpinner.getValue();
                calendar.setTime(endDateTime);
                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = calendar.get(Calendar.MINUTE);
                // Converting DateTime to LocalDateTime to OffsetDateTime for commandAddSchedule function
                LocalDateTime convertedDate = Instant.ofEpochMilli(startDateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                OffsetDateTime offsetDateTime = convertedDate.atOffset(OffsetDateTime.now().getOffset());
                ScheduleController.commandRemoveSchedule(billboardName,offsetDateTime);
                ScheduleController.commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() );
                // Remove Schedule to Calendar View
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
        new CalendarCreator(tal);
    }
}
