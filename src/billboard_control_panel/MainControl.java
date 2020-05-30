package billboard_control_panel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import billboard_control_panel.Calendar.*;
import billboard_control_panel.Calendar.CalendarEvent;
import billboard_server.ClientServerInterface;
import billboard_server.exceptions.ServerException;
import billboard_viewer.Billboard;
//import billboard_viewer.DisplayBillboard;
//import connections.ClientServerInterface;
//import connections.Protocol;
//import connections.ServerMainTest;
//import connections.testing.AdminAddUsers;


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
    private JLabel Title;
    private JTabbedPane tabbedPane;
    private JButton deleteBillboardButton;

    public MainControl() {

        Window[] wns = LoginManager.getFrames();
        for (Window wn1 : wns) {
            wn1.setVisible(false);
        }
        // Get Current Users in Database
        //TODO Put users from db into string array to display in GU
        refreshBillboards();
        refreshUsers();


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
                    Main.server.logout();
                    new LoginManager().main(null);

                } else if (n == JOptionPane.NO_OPTION) {
                }
            }
        });

        // User Setting Buttons

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = null;
                try {
                    userId = Main.server.getUserId(usersList.getSelectedValue().toString());
                    int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + usersList.getSelectedValue().toString() +"?");
                    if (n == JOptionPane.YES_OPTION) {
                        Main.server.deleteUser(userId);
                    } else if (n == JOptionPane.NO_OPTION) {
                    }
                } catch (ServerException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                refreshUsers();
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
                new UserControl(null).main(null);
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

                new BillboardControl(null).main(null);
            }
        });

        modifyUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userKey = null;
                TreeMap user = new TreeMap<String, String>();
                System.out.println("Edit selected user: " + usersList.getSelectedValue());

                if (usersList.getSelectedValue() == null) {
                    // No billboard selected
                    throwDialog("No user has been selected to edit, please select a valid user.", "No User Selected");
                } else {

                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }
                    try {
                        userKey = Main.server.getUserId((String) usersList.getSelectedValue());
                        System.out.println("User Key:" + userKey);
                        user = Main.server.getUser(userKey);
                    } catch (ServerException ex) {
                        ex.printStackTrace();
                    }
                    new UserControl(user).main(user); // pass in selected user for edit
                    //new UserControl().main(null);
                }
            }
        });

        editBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String billboardKey = null;
                TreeMap billBoard = new TreeMap<String, String>();

                System.out.println("Edit selected billboard: " + billboardsList.getSelectedValue());

                if (billboardsList.getSelectedValue() == null) {
                    // No billboard selected
                    throwDialog("No Billboard has been selected for edit, please select a valid billboard.", "No Billboard Selected");
                } else {

                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }

                    try {
                        billboardKey = Main.server.getBillboardId((String) billboardsList.getSelectedValue());
                        System.out.println("Billboard Key:" + billboardKey);
                        billBoard = Main.server.getBillboard(billboardKey);
                    } catch (ServerException ex) {
                        ex.printStackTrace();
                    }
                    new BillboardControl(billBoard).main(billBoard); // pass in selected billboard for edit

                }
            }
        });

        previewBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new DisplayBillboard(null).displayCurrentBillboard();

                String billboardKey = null;
                TreeMap billBoard = new TreeMap<String, String>();

                System.out.println("Preview selected billboard: " + billboardsList.getSelectedValue());

                if (billboardsList.getSelectedValue() == null) {
                    // No billboard selected
                    throwDialog("No Billboard has been selected for preview, please select a valid billboard.", "No Billboard Selected");
                } else {

                    try {
                        billboardKey = Main.server.getBillboardId((String) billboardsList.getSelectedValue());
                        System.out.println("Billboard Key:" + billboardKey);
                        billBoard = Main.server.getBillboard(billboardKey);

                    } catch (ServerException ex) {
                        ex.printStackTrace();
                    }

                    //printBillboard debugging
                    System.out.println("Outputing treemap contents:");
                    Set<String> set1 = billBoard.keySet();
                    for (String key : set1) {
                        System.out.println("Billboard Key : " + key + "\t\t" + "Value : " + billBoard.get(key));
                    }
                    // end debug

                    System.out.println("Previewing Billboard...");
                    Billboard previewBillboard = new Billboard(billBoard, true);

                }
            }
        });

        scheduleBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.server.getSchedules();
                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }
                    new CalendarCreator().main(null);
                } catch (ServerException z) {
                    throwDialog("User does not have permission to view schedules", "No permission");
                }
            }
        });

        refreshBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeMap<String, Object> billboards = null;
                try {
                    billboards = Main.server.getBillboards();
                } catch (ServerException z) {
                    z.printStackTrace();
                }
                //TreeMap<String, Object> userDetails = (TreeMap<String, Object>) users.get("userName");
                ArrayList<String> bbList = new ArrayList<String>();
                billboards.forEach((k, v) -> {
                    //System.out.println("Key: " + k + ", Value: " + v);
                    String bbString = v.toString();
                    bbString = bbString.substring(bbString.indexOf("billboardName=") + 14);
                    bbString = bbString.substring(0, bbString.indexOf(","));
                    bbList.add(bbString);
                });
                refresh(billboardsList, bbList);
            }
        });
        refreshUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshUsers();
            }
        });

        deleteBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String billboardID = null;
                if (billboardsList.getSelectedValue() == null) {
                    // No billboard selected
                    throwDialog("No Billboard has been selected to delete, please select a valid billboard.", "No Billboard Selected");
                } else {
                    try {
                        billboardID = Main.server.getBillboardId(billboardsList.getSelectedValue().toString());
                        int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + billboardsList.getSelectedValue().toString() + "?");
                        if (n == JOptionPane.YES_OPTION) {
                            Main.server.deleteBillboard(billboardID);
                        } else if (n == JOptionPane.NO_OPTION) {
                        }
                    } catch (ServerException ex) {
                        ex.printStackTrace();
                    }
                    refreshBillboards();
                }
            }
        });
    }

    //TODO: FIx the bug, sometimes it refreshes, sometimes it doesnt?
    public void refresh(JList list, ArrayList<String> stringArray) {
        list.removeAll();
        list.clearSelection();

        final DefaultListModel model = new DefaultListModel();
        model.clear();
        model.removeAllElements();
        for (int i = 0, n = stringArray.size(); i < n; i++) {
            model.addElement(stringArray.get(i));
            list.setModel(model);
        }
    }

    public void refreshBillboards(){
        TreeMap<String, Object> billboards = null;
        try {
            billboards = Main.server.getBillboards();
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
        refresh(billboardsList, bbList);
    }

    public void refreshUsers(){
        usersList.clearSelection();
        usersList.removeAll();
        TreeMap<String, Object> users = null;
        try {
            users = Main.server.getUsers();
        } catch (ServerException z) {
            z.printStackTrace();
        }

        //TreeMap<String, Object> userDetails = (TreeMap<String, Object>) users.get("userName");
        ArrayList<String> userNameList = new ArrayList<String>();
        TreeMap<String, Object> finalUsers = users;
        users.forEach((k, v) -> {
            //System.out.println("Key: " + k + ", Value: " + v);
            String userString = v.toString().substring(v.toString().lastIndexOf("=") + 1);
            userString = userString.replace("}", "");
            userNameList.add(userString);
        });
        refresh(usersList, userNameList);
        System.out.println(userNameList);
        //refresh(usersList, UserNames);
    }

    private static void throwDialog(String messageText, String title) {
        JOptionPane.showMessageDialog(null, messageText, title, JOptionPane.INFORMATION_MESSAGE);
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        final JTabbedPane tabbedPane1 = new JTabbedPane();
        controlPanel.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 409), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Billboard Settings", panel1);
        billboardsList = new JList();
        panel1.add(billboardsList, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        scheduleBillboardButton = new JButton();
        scheduleBillboardButton.setText("Schedule");
        panel1.add(scheduleBillboardButton, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createBillboardButton = new JButton();
        createBillboardButton.setText("Create");
        panel1.add(createBillboardButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editBillboardButton = new JButton();
        editBillboardButton.setText("Edit");
        panel1.add(editBillboardButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewBillboardButton = new JButton();
        previewBillboardButton.setText("Preview");
        panel1.add(previewBillboardButton, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshBillboardButton = new JButton();
        refreshBillboardButton.setText("Refresh");
        panel1.add(refreshBillboardButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("User Settings", panel2);
        usersList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        usersList.setModel(defaultListModel1);
        panel2.add(usersList, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 237), null, 0, false));
        createUserButton = new JButton();
        createUserButton.setText("Create");
        panel2.add(createUserButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteUserButton = new JButton();
        deleteUserButton.setText("Delete");
        panel2.add(deleteUserButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modifyUserButton = new JButton();
        modifyUserButton.setText("Modify");
        panel2.add(modifyUserButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshUserButton = new JButton();
        refreshUserButton.setText("Refresh");
        panel2.add(refreshUserButton, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logOutButton = new JButton();
        logOutButton.setText("Log Out");
        controlPanel.add(logOutButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        controlPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(11, 11), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("$User$ Control Panel");
        controlPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return controlPanel;
    }
}
