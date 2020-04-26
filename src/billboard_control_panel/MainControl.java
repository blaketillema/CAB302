package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import billboard_control_panel.SwingCalendar.Calendar;
import billboard_control_panel.SwingCalendar.CalendarEvent;
import billboard_viewer.*;


public class MainControl {
    private JTabbedPane mainControl;
    private JList billboardList;
    private JButton createUserButton;
    private JButton modifyUserButton;
    private JButton deleteUserButton;
    private JButton logOutButton;
    private JList userList;
    private JButton scheduleBillboardButton;
    private JButton createBillboardButton;
    private JButton editBillboardButton;
    private JButton previewBillboardButton;
    private JPanel controlPanel;

    public MainControl() {
        Window[] wns = LoginManager.getFrames();
        for (Window wn1 : wns) {
            wn1.setVisible(false);
        }

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?");
                if (n == JOptionPane.YES_OPTION) {
                    // TODO: Make this a function (Hides and disposes Frames)
                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }
                    new LoginManager().main(null);
                } else if (n == JOptionPane.NO_OPTION) {
                }
            }
        });

        // User Setting Buttons

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new UserControl().main(null);
            }
        });

        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new UserControl().main(null);
            }
        });
        modifyUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new UserControl().main(null);
            }
        });

        // User Setting Buttons

        createBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new BillboardControl().main(null);
            }
        });
        editBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new BillboardControl().main(null);
            }
        });

        previewBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new billboard_viewer.DisplayBillboard(null).displayCurrentBillboard();
            }
        });
        scheduleBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                openCalendar();
            }
        });

    }

    // Creates calendar and schedule view
    private void openCalendar(){
        JFrame frm = new JFrame();

        ArrayList<CalendarEvent> events = new ArrayList<>();
        events.add(new CalendarEvent(LocalDate.of(2020, 4, 8), LocalTime.of(14, 0), LocalTime.of(14, 20), "BILLBOARD 1 11/11 14:00-14:20"));

        CalendarWeek cal = new CalendarWeek(events);

        cal.addCalendarEventClickListener(e -> System.out.println(e.getCalendarEvent()));
        cal.addCalendarEmptyClickListener(e -> {
            System.out.println(e.getDateTime());
            System.out.println(Calendar.roundTime(e.getDateTime().toLocalTime(), 30));
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

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Control Panel");
        Main.centreWindow(frame);
        frame.setContentPane(new MainControl().controlPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
