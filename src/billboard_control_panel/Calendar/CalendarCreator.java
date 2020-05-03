package billboard_control_panel.Calendar;

import billboard_control_panel.LoginManager;
import billboard_control_panel.MainControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarCreator extends Frame {
    public CalendarCreator(CalendarWeek cal) {
        setLayout(new GridLayout());
        JFrame frm = new JFrame();
        //JPanel guiPanel = setupUi();

        JPanel schedulerPanel = new JPanel();
        schedulerPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(14, 6, new Insets(0, 0, 0, 0), -1, -1));
        JTextField enterScheduleNameTextField = new JTextField();
        enterScheduleNameTextField.setText("Enter Schedule Name");
        schedulerPanel.add(enterScheduleNameTextField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        JRadioButton mondayRadioButton = new JRadioButton();
        mondayRadioButton.setText("Monday");
        schedulerPanel.add(mondayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 18), null, 0, false));
        JRadioButton tuesdayRadioButton = new JRadioButton();
        tuesdayRadioButton.setText("Tuesday");
        schedulerPanel.add(tuesdayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(9, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 18), null, 0, false));
        JRadioButton fridayRadioButton = new JRadioButton();
        fridayRadioButton.setText("Friday");
        schedulerPanel.add(fridayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 18), null, 0, false));
        JRadioButton saturdayRadioButton = new JRadioButton();
        saturdayRadioButton.setText("Saturday");
        schedulerPanel.add(saturdayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(10, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 18), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Daily Recurrance:");
        schedulerPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Start time:");
        schedulerPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        // Time Spinners
        Date date = new Date();
        SpinnerDateModel sm = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        SpinnerDateModel em = new SpinnerDateModel(date,null,null, Calendar.HOUR_OF_DAY);
        JSpinner startSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(startSpinner, "EEE MMM dd HH:mm:ss Z yyyy");
        schedulerPanel.add(startSpinner, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        JSpinner endSpinner = new JSpinner(em);
        JSpinner.DateEditor ee = new JSpinner.DateEditor(endSpinner, "EEE MMM dd HH:mm:ss Z yyyy");
        schedulerPanel.add(endSpinner, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        JSpinner occuranceSpinner = new JSpinner();
        //de = new JSpinner.DateEditor(occuranceSpinner, "HH:MM");
        schedulerPanel.add(occuranceSpinner, new com.intellij.uiDesigner.core.GridConstraints(11, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));

        //JSpinner minutelySpinner = new JSpinner(sm);
        //JSpinner.DateEditor de = new JSpinner.DateEditor(minutelySpinner, "HH:MM");
        //minutelySpinner.setEditor(de);


        //schedulerPanel.add(minutelySpinner, new com.intellij.uiDesigner.core.GridConstraints(11, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 26), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        JButton deleteButton = new JButton();
        deleteButton.setText("Delete");
        schedulerPanel.add(deleteButton, new com.intellij.uiDesigner.core.GridConstraints(13, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 24), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Hourly recurrance:");
        schedulerPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(72, 16), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Billboard Scheduler Editor");
        schedulerPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(58, 16), null, 0, false));
        JRadioButton wednesdayRadioButton = new JRadioButton();
        wednesdayRadioButton.setText("Wednesday");
        schedulerPanel.add(wednesdayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 18), null, 0, false));
        JRadioButton sundayRadioButton = new JRadioButton();
        sundayRadioButton.setText("Sunday");
        schedulerPanel.add(sundayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(10, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 18), null, 0, false));
        JRadioButton thursdayRadioButton = new JRadioButton();
        thursdayRadioButton.setText("Thursday");
        schedulerPanel.add(thursdayRadioButton, new com.intellij.uiDesigner.core.GridConstraints(9, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(69, 18), null, 0, false));
        final JLabel label7 = new JLabel();
        JButton resetButton = new JButton();
        resetButton.setText("Reset");
        schedulerPanel.add(resetButton, new com.intellij.uiDesigner.core.GridConstraints(13, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(32, 24), null, 0, false));
        JButton exitButton = new JButton();
        exitButton.setText("Exit");
        schedulerPanel.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(13, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JButton saveButton = new JButton();
        saveButton.setText("Save");
        schedulerPanel.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(13, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(69, 24), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        schedulerPanel.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(12, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("End time:");
        schedulerPanel.add(label8, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        //schedulerController scheduler = new schedulerController();
        //JPanel schedulerPanel = new schedulerController().schedulerPanel;
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
            //System.out.println(e.getCalendarEvent());
            //System.out.println(e.getStartDateTime().toString());
//            scheduleName.setText("K");
//            enterScheduleNameTextField.setText("Enter Schedule Name");
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStartDateTime().toString());
            startSpinner.setValue(startDate);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getEndDateTime().toString());
            endSpinner.setValue(endDate);
            enterScheduleNameTextField.setText(e.getBillboardName());
            //String dateTimeName = String.valueOf(e.getCalendarEvent());
            //Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getStart().toString());
            //startSpinner.setValue(startDate);
            //schedulerController.getValues(dateTimeName);
        });

        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            System.out.println(CalendarViewer.roundTime(e.getDateTime().toLocalTime(), 30));
            Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(e.getDateTime().toString());
            //LocalDateTime endDateUnformatted = e.getDateTime().plusHours(Long.parseLong(occuranceSpinner.getValue().toString()));
            LocalDateTime endDateUnformatted = e.getDateTime().plusHours(1);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(endDateUnformatted.toString());
            System.out.println(endDate);
            startSpinner.setValue(startDate);
            endSpinner.setValue(endDate);
            enterScheduleNameTextField.setText("Enter new schedule name...");
            //String dateTimeName = String.valueOf(e.getDateTime());
        });


        //TODO: Port saved data to billboard server, database and calendar view
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enterScheduleNameTextFieldValue = (String) enterScheduleNameTextField.getName();
                //int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                //int hourSpinnerValue = (Integer) hourSpinner.getValue();
                //int minutelySpinnerValue = (Integer) minutelySpinner.getValue();
                //int hourlySpinnerValue = (Integer) hourlySpinner.getValue();
                //System.out.println(minuteSpinnerValue);
                //hourSpinner.setValue(2);
                // TODO: ADD event according to user selected values
                //ArrayList<CalendarEvent> events = new ArrayList<>();
                //events.add(new CalendarEvent(LocalDate.of(2020, 4, 29), LocalTime.of(hourSpinnerValue, minuteSpinnerValue), LocalTime.of(minutelySpinnerValue, hourlySpinnerValue), "BILLBOARD 1"));
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
            }
        });

    }

    public static void main(String[] args) {

        ArrayList<CalendarEvent> events = new ArrayList<>();
        CalendarWeek cal = new CalendarWeek(events);
        CalendarCreator edit = new CalendarCreator(cal);
        events.add(new CalendarEvent(LocalDate.of(2020, 4, 28), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1"));


    }
}
