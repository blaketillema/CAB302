package billboard_control_panel;

import billboard_control_panel.Calendar.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;



public class schedulerController {
    private JButton deleteButton;
    private JButton saveButton;
    private JButton resetButton;
    private JTextField enterScheduleNameTextField;
    public JPanel schedulerPanel;
    private JSpinner hourSpinner;
    public JSpinner minuteSpinner;
    private JRadioButton mondayRadioButton;
    private JRadioButton wednesdayRadioButton;
    private JRadioButton tuesdayRadioButton;
    private JRadioButton fridayRadioButton;
    private JRadioButton saturdayRadioButton;
    private JRadioButton sundayRadioButton;
    private JRadioButton thursdayRadioButton;
    private JButton exitButton;
    private JSpinner hourlySpinner;
    private JSpinner minutelySpinner;


    int min = 0;
    int max = 10;
    int step = 1;
    int initValue = 5;
    //JSpinner hourSpinner = new JSpinner(value);



    public int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
    public String enterScheduleNameTextFieldValue = (String) enterScheduleNameTextField.getName();
    int hourSpinnerValue = (Integer) hourSpinner.getValue();
    int minutelySpinnerValue = (Integer) minutelySpinner.getValue();
    int hourlySpinnerValue = (Integer) hourlySpinner.getValue();
    //int hourSpinnerValue = (Integer) hourSpinner.getValue();

    public schedulerController(JSpinner hourlySpinner) {
        this.hourlySpinner = hourlySpinner;
    }


    public static String getValues(String TimeString){
        System.out.println("time string is" + TimeString);
        //hourSpinner.setValue(2);
        return TimeString;

    }

    public static void setValues(){
        ArrayList<CalendarEvent> events = new ArrayList<>();
        CalendarWeek cal = new CalendarWeek(events);
        cal.addCalendarEventClickListener(e -> System.out.println(e.getCalendarEvent()));
        cal.addCalendarEmptyClickListener(e -> {
            String dateTimeName = String.valueOf(e.getDateTime());
            //enterScheduleNameTextField.setName(dateTimeName);
        });
    }

    public schedulerController() {
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

        //TODO: Port saved data to billboard server, database and calendar view
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String enterScheduleNameTextFieldValue = (String) enterScheduleNameTextField.getName();
                int minuteSpinnerValue = (Integer) minuteSpinner.getValue();
                int hourSpinnerValue = (Integer) hourSpinner.getValue();
                int minutelySpinnerValue = (Integer) minutelySpinner.getValue();
                int hourlySpinnerValue = (Integer) hourlySpinner.getValue();
                System.out.println(minuteSpinnerValue);
                hourSpinner.setValue(2);
                // TODO: ADD event according to user selected values
                //ArrayList<CalendarEvent> events = new ArrayList<>();
                //events.add(new CalendarEvent(LocalDate.of(2020, 4, 29), LocalTime.of(hourSpinnerValue, minuteSpinnerValue), LocalTime.of(minutelySpinnerValue, hourlySpinnerValue), "BILLBOARD 1"));
            }
        });

        //minutelySpinner(5, 0 ,10,1);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //setValues();
            }
        });
    }
}
