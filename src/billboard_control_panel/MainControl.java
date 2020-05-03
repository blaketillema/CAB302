package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import billboard_control_panel.Calendar.*;
import billboard_control_panel.Calendar.CalendarEvent;
import connections.ClientServerInterface;
import connections.Protocol;
import connections.ServerMainTest;
import connections.testing.AdminAddUsers;


public class MainControl {
    private JTabbedPane mainControl;
    private JList usersList;
    private JButton createUserButton;
    private JButton modifyUserButton;
    private JButton deleteUserButton;
    private JButton logOutButton;
    private JList billboardsList;
    private JButton scheduleBillboardButton;
    private JButton createBillboardButton;
    private JButton editBillboardButton;
    private JButton previewBillboardButton;
    private JPanel controlPanel;
    private JButton refreshUserButton;
    private JButton refreshBillboardButton;

    public MainControl() {
        Window[] wns = LoginManager.getFrames();
        for (Window wn1 : wns) {
            wn1.setVisible(false);
        }
        String[] BillboardNames = {"Bill1", "BillTwo", "BillThree"};
        String[] UserNames = {"Lahiru", "Blake", "Max"};

        refresh(billboardsList, BillboardNames);
        refresh(usersList, UserNames);

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
                CalendarCreator calendarCreator = new CalendarCreator(null);
                CalendarCreator.main(null);
                //openCalendar();
            }
        });

        refreshBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(billboardsList, BillboardNames);
            }
        });
        refreshUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(usersList, UserNames);
            }
        });
    }

    public void refresh(JList list, String[] stringArray) {
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0, n = stringArray.length; i < n; i++) {
            model.addElement(stringArray[i]);
            list.setModel(model);
        }
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
