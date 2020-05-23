package billboard_control_panel.Calendar;

import billboard_control_panel.LoginManager;
import billboard_control_panel.MainControl;
import billboard_control_panel.ScheduleController;
import billboard_control_panel.Scheduler;
import connections.ClientMainTests;
import connections.exceptions.ServerException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    public CalendarCreator() {

        //<editor-fold desc="Schedule Controller Panel (Right Panel of GUI)">
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
//        JTextField enterScheduleNameTextField = new JTextField();
//        enterScheduleNameTextField.setText("Enter Schedule Name");
//        schedulerPanel.add(enterScheduleNameTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension( 150, -1), null, 0, false));

        // Billboard Drop down
        TreeMap<String, Object> billboards = null;
        try {
            billboards = LoginManager.server.getBillboards();
        } catch (ServerException z) {
            z.printStackTrace();
        }
        //TreeMap<String, Object> userDetails = (TreeMap<String, Object>) users.get("userName");
        ArrayList<String> bbList = new ArrayList<String>();
        billboards.forEach((k, v) -> {
            System.out.println("Key: " + k + ", Value: " + v);
            String bbString = v.toString();
            bbString = bbString.substring(bbString.indexOf("billboardName=") + 14);
            bbString = bbString.substring(0, bbString.indexOf(","));
            bbList.add(bbString);
        });
        // Convert ArrayList to String Array (This could be done better tbh)
        String bbArray[] = new String[bbList.size()];
        for (int j = 0; j < bbList.size(); j++) {

            // Assign each value to String array
            bbArray[j] = bbList.get(j);
        }
        JComboBox billboardDropdown = new JComboBox(bbArray);
        schedulerPanel.add(billboardDropdown, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

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
        //</editor-fold>

        //<editor-fold desc="Print existing schedules from db">
        //Ok
        ArrayList<CalendarEvent> events = new ArrayList<>();
        CalendarWeek cal = new CalendarWeek(events);

        TreeMap<String, Object> confirmed = null;
        try {
            confirmed = LoginManager.server.getSchedules();
        } catch (ServerException e) {
            e.printStackTrace();
        }

        //System.out.println(confirmed);
        for(Map.Entry<String,Object> entry : confirmed.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            TreeMap<String, Object> scheduleDetails = (TreeMap<String, Object>) entry.getValue();
            System.out.println(key + " => " + value);
            OffsetDateTime offsetStartDateTime = (OffsetDateTime) scheduleDetails.get("startTime");
            Integer duration = (Integer) scheduleDetails.get("duration");
            Boolean isRecurring = (Boolean) scheduleDetails.get("isRecurring");
            Integer recurFreqInMins = (Integer) scheduleDetails.get("recurFreqInMins");
            String scheduleId = (String) scheduleDetails.get("scheduleId");
            String billboardId = (String) scheduleDetails.get("billboardId");

            try {
                TreeMap<String, String> billboard = LoginManager.server.getBillboard(billboardId);
                //System.out.println(billboard.toString());
                String billboardName = (String) billboard.get("billboardName");
                int startMinute = offsetStartDateTime.getMinute();
                int startHour = offsetStartDateTime.getHour();
                int startDay = offsetStartDateTime.getDayOfMonth();
                int startMonth = offsetStartDateTime.getMonthValue();
                int startYear = offsetStartDateTime.getYear();
                int endHour = startHour + 1;
                int endMinute = startMinute;


                // Minutely Recurrence
                if (isRecurring == true) {
                    int months = 0;
                    int Rminute = recurFreqInMins;
                    int freqHours = Rminute / 60 % 24;
                    int remMins = (Rminute % 60);
                    // TODO: Reschedules for a year (can specify this with a user-input value)
                    while (months <= 1) {
                        // Add the first event
                        events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                        // For the next event: Add the user inputted minutes e.g. 90 minutes, to Start Time (2.30pm) + (userinputted) 90 min = (4.00pm)
                        startMinute = (startMinute + remMins);
                        startHour = (startHour + freqHours);
                        endMinute = (endMinute + remMins);
                        endHour = (endHour + freqHours);
                        // First add remainder minutes (90 minutes -> 30minutes and 1 hour)
                        if (startMinute >= 60) {
                            startMinute = startMinute % 60;
                            startHour++;
                        }
                        if (endMinute >= 60) {
                            endMinute = endMinute % 60;
                            endHour++;
                        }
                        if (startHour > 23) {
                            startHour = startHour % 24;
                            startDay++;
                        }

                        if (endHour > 23) {
                            endHour = endHour % 24;
                        }

                        // Months with 31 days
                        if ((startDay == 32) && (startMonth == 1 || startMonth == 3 || startMonth == 5 || startMonth == 7 || startMonth == 8 || startMonth == 10 || startMonth == 12)) {
                            startDay = 1;
                            startMonth++;
                            months++;
                        }
                        // Months with 30 days
                        if ((startDay >= 31) && (startMonth == 4 || startMonth == 6 || startMonth == 9 || startMonth == 11)) {
                            startDay = 1;
                            startMonth++;
                            months++;
                        }

                        // Year
                        if (startMonth == 13) {
                            startMonth = 1;
                            startYear++;
                        }
                        // February Non-Leap Year
                        if (startMonth == 2 && startYear % 4 != 0 && startDay == 29) {
                            startDay = 1;
                            startMonth++;
                            months++;
                        }
                        // February Leap Year
                        if (startMonth == 2 && startYear % 4 == 0 && startDay == 30) {
                            startDay = 1;
                            startMonth++;
                            months++;
                        }
                    }
                }
                else{
                    events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                }

            } catch (ServerException e) {
                e.printStackTrace();
            }
        }


        //System.out.println(confirmed.values().toArray(Object[0]));
        //TODO: Paint existing schedules
        //Pseudo code - for number of objects in schedule table, add new calendar event. If reoccuring is true, make proper adjustments
        //add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
        //</editor-fold>


        //<editor-fold desc="Calendar Viewer w/ buttons">
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
        //</editor-fold>

        //<editor-fold desc="Adding to form and setting JFrame">
        //// CREATE ENTIRE SCHEDULE + CALENDAR VIEW ///////
        frm.add(weekControls, BorderLayout.NORTH);
        frm.add(schedulerPanel, BorderLayout.EAST);
        frm.add(cal, BorderLayout.CENTER);
        frm.setSize(1600, 900);
        frm.setExtendedState(frm.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frm.setVisible(true);
        frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //</editor-fold>

        //<editor-fold desc="Calendar Viewer Listeners">
        cal.addCalendarEventClickListener(e -> {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStartDateTime().toString());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getEndDateTime().toString());
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
            //enterScheduleNameTextField.setText(e.getBillboardName());
            billboardDropdown.setSelectedItem(e.getBillboardName());
        });

        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            System.out.println(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30));
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getDateTime().toString());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getDateTime().plusHours(1).toString());
            System.out.println(startDate);
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
//            if (enterScheduleNameTextField.getText() != "Enter new schedule name..."){
//                //TODO: optional - check if ScheduleName exists in db
//            } else enterScheduleNameTextField.setText("Enter new schedule name...");

        });
        //</editor-fold>


        //<editor-fold desc="Scheduler Option Listeners">
        endSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Date endDateTime = (Date) endSpinner.getValue();
                Date startDateTime = (Date) startSpinner.getValue();
                if (endDateTime.compareTo(startDateTime) <= 0){
                    endSpinner.setValue(startSpinner.getNextValue());
                    JOptionPane.showMessageDialog(null, "End DateTime can't be before Start DateTime",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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
        //</editor-fold>

        //TODO: Port saved data to billboard server, database and calendar view
        //<editor-fold desc="Scheduler User Save, Delete, Exit and Reset Listeners">
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = new GregorianCalendar();
                // Get Values Entered:
                //String scheduleName = (String) enterScheduleNameTextField.getText();
                String billboardName = (String) billboardDropdown.getSelectedItem().toString();
                //String billboardName = ((JTextField)billboardDropdown.getEditor().getEditorComponent()).getText();
                System.out.println(billboardName);
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
                System.out.println("diff " + diff);
                System.out.println("diffMinutes: " + diffMinutes);
                // Get recurring values
                int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                boolean recurring = (Boolean) recurringCheckBox.isSelected();
                boolean recurringDaily = (Boolean) dailyButton.isSelected();
                boolean recurringHourly = (Boolean) hourlyButton.isSelected();
                boolean recurringMinutely = (Boolean) minutelyButton.isSelected();

                // If recurring checkbox is selected but no values are inputted:
                if (recurring == true && recurringDaily == false && recurringHourly == false && minuteSpinnerValue == 0){
                    JOptionPane.showMessageDialog(null, "Must input recurring value",
                            "Fail",
                            JOptionPane.PLAIN_MESSAGE);
                    return;
                }

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


                // Converting DateTime to LocalDateTime to OffsetDateTime for commandAddSchedule function
                LocalDateTime convertedDate = Instant.ofEpochMilli(startDateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                OffsetDateTime offsetDateTime = convertedDate.atOffset(OffsetDateTime.now().getOffset());
                String billboardId = null;
                try {
                    billboardId = LoginManager.server.getBillboardId(billboardName);
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                //TODO: Add Schedule to DB.
                TreeMap<String, Object> body = new TreeMap<>();
                try {
                    body.put("billboardId", billboardId);
                    body.put("startTime", offsetDateTime);
                    body.put("duration", diffMinutes);
                    body.put("isRecurring", recurring);
                    body.put("recurFreqInMins", recurringEveryXminutes);
                    LoginManager.server.addSchedule(body);
                    //LoginManager.server.addSchedule(ClientMainTests.randomNewSchedule(billboardId));
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }

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
                        int days = 0;
                        // TODO: Reschedules for a year (can specify this with a user-input value)
                        while (days <= 365 ){
                            events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                            startDay = (startDay + 1)%32;
                            days++;
                            if (startDay == 0){
                                startDay++;
                                startMonth++;
                            }
                            // Year
                            if (startMonth == 13){
                                startMonth = 1;
                                startYear++;
                            }
                            // Months with 30 days
                            if ((startDay == 31) && (startMonth == 4 || startMonth  == 6 || startMonth == 9|| startMonth == 11)){
                                startDay = 1;
                                startMonth++;
                            }
                            // February Non-Leap Year
                            if (startMonth == 2 && startYear%4 != 0 && startDay==29){
                                startDay = 1;
                                startMonth++;
                            }
                            // February Leap Year
                            if (startMonth == 2 && startYear%4 != 0 && startDay==30){
                                startDay = 1;
                                startMonth++;
                            }
                        }
                        cal.goToToday();
                    }
                    // Hourly Recurrence
                    else if (recurringHourly == true){
                        int days = 0;
                        while (days <= 7 ){
                            events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour , startMinute), LocalTime.of(endHour, endMinute), billboardName));
                            startHour = (startHour + 1);
                            endHour = (endHour +1);
                            if (startHour > 23){
                                startHour = startHour%24;
                                startDay++;
                                days++;
                            }
                            if (endHour > 23){
                                endHour = endHour%24;
                            }
                            // Months with 31 days
                            if ((startDay == 32) && (startMonth == 1 || startMonth  == 3 || startMonth == 5|| startMonth == 7|| startMonth == 8|| startMonth == 10|| startMonth == 12)){
                                startDay = 1;
                                startMonth++;
                            }
                            // Months with 30 days
                            if ((startDay >= 31) && (startMonth == 4 || startMonth  == 6 || startMonth == 9|| startMonth == 11)){
                                startDay = 1;
                                startMonth++;
                            }
                            // Year
                            if (startMonth == 13){
                                startMonth = 1;
                                startYear++;
                            }
                            // February Non-Leap Year
                            if (startMonth == 2 && startYear%4 != 0 && startDay==29){
                                startDay = 1;
                                startMonth++;
                            }
                            // February Leap Year
                            if (startMonth == 2 && startYear%4 == 0 && startDay==30){
                                startDay = 1;
                                startMonth++;
                            }
                        }
                        cal.goToToday();
                    }
                    // Minutely Recurrence
                    else if (recurringMinutely == true){
                        int months = 0;
                        int Rminute = minuteSpinnerValue;
                        int freqHours = Rminute/60%24;
                        int remMins = (Rminute%60);
                        // TODO: Reschedules for a year (can specify this with a user-input value)
                        while (months <= 12 ){
                            // Add the first event
                            events.add(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                            // For the next event: Add the user inputted minutes e.g. 90 minutes, to Start Time (2.30pm) + (userinputted) 90 min = (4.00pm)
                            startMinute = (startMinute + remMins);
                            startHour = (startHour + freqHours);
                            endMinute = (endMinute + remMins);
                            endHour = (endHour + freqHours);
                            // First add remainder minutes (90 minutes -> 30minutes and 1 hour)
                            if (startMinute >= 60){
                                startMinute = startMinute%60;
                                startHour++;
                            }
                            if (endMinute >= 60){
                                endMinute = endMinute%60;
                                endHour++;
                            }
                            if (startHour > 23){
                                startHour = startHour%24;
                                startDay++;
                            }

                            if (endHour > 23){
                                endHour = endHour%24;
                            }

                            // Months with 31 days
                            if ((startDay == 32) && (startMonth == 1 || startMonth  == 3 || startMonth == 5|| startMonth == 7|| startMonth == 8|| startMonth == 10|| startMonth == 12)){
                                startDay = 1;
                                startMonth++;
                                months++;
                            }
                            // Months with 30 days
                            if ((startDay >= 31) && (startMonth == 4 || startMonth  == 6 || startMonth == 9|| startMonth == 11)){
                                startDay = 1;
                                startMonth++;
                                months++;
                            }

                            // Year
                            if (startMonth == 13){
                                startMonth = 1;
                                startYear++;
                            }
                            // February Non-Leap Year
                            if (startMonth == 2 && startYear%4 != 0 && startDay==29){
                                startDay = 1;
                                startMonth++;
                                months++;
                            }
                            // February Leap Year
                            if (startMonth == 2 && startYear%4 == 0 && startDay==30){
                                startDay = 1;
                                startMonth++;
                                months++;
                            }
                        }
                        cal.goToToday();
                    }
                    //enterScheduleNameTextField.setText("Enter new schedule name...");
                }


            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar calendar = new GregorianCalendar();
                // Get Values Entered:
                String billboardName = (String) billboardDropdown.getSelectedItem().toString();
                //String billboardName = (String) enterScheduleNameTextField.getText();
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


                //enterScheduleNameTextField.setText("Enter new schedule name...");
                events.remove(new CalendarEvent(LocalDate.of(startYear, startMonth, startDay), LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute), billboardName));
                cal.goToToday();

                ScheduleController.commandRemoveSchedule(billboardName,offsetDateTime);
                Scheduler.commandParser( ScheduleController.getCurrentCommandName(), ScheduleController.getCurrentCommandData() );

                // Deleting Billboard using ClientServer Interface
                //TODO: Ensure this is deleting a schedule of a billboard, not a billboard
                String scheduleId = null;
                try {
                    scheduleId = LoginManager.server.getScheduleId(LoginManager.server.getBillboardId(billboardName));
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                try {
                    LoginManager.server.deleteSchedule(scheduleId);
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, Scheduler.getCurrentCommandData().toString(),
                        "Success",
                        JOptionPane.PLAIN_MESSAGE);
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
        //</editor-fold>

    }


    public static void main(String[] args) {
        // Either keep one at the top, or this one below.
        // CalendarCreator edit = new CalendarCreator(tal);
    }
}
