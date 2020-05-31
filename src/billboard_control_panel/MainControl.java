package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import billboard_control_panel.Calendar.*;
import billboard_server.exceptions.ServerException;
import billboard_viewer.Billboard;


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

    /**
     * MainControl takes the logged in user as a parameter to display in the GUI, but uses the server's session token
     * to correctly identify the user's permissions and abilities within the Control Panel
     * MainControl displays a clickable list of Users and Billboards, as well as buttons to delete, edit, create,
     * preview, schedule etc. These are all dependent on the user's permissions
     * @param userName
     */
    public MainControl(String userName) {

        Window[] wns = LoginManager.getFrames();
        for (Window wn1 : wns) {
            wn1.setVisible(false);
        }
        // Get Current Users in Database
        refreshBillboards();
        refreshUsers();

        Title.setText(userName + " Control Panel");

        /**
         * Logout button takes the user back to the LoginManager screen and ends that user's session
         */
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

        /**
         * Button functionality related to User Settings
         */
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Main.server.getUsers();
                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }
                    new UserControl(null, userName).main(null,userName);
                } catch (ServerException z) {
                    throwDialog(z.getMessage(), "Error");
                }
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
                        throwDialog(ex.getMessage(),"Error");
                    }
                    new UserControl(user, userName).main(user,userName); // pass in selected user for edit
                }
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = null;
                if (usersList.getSelectedValue() == null) {
                    // No billboard selected
                    throwDialog("No User has been selected to delete, please select a valid user.", "No User Selected");
                }
                else if(usersList.getSelectedValue().toString().equals(userName)){
                    // Trying to delete yourself
                    throwDialog("You cannot remove yourself", "Invalid action");
                }
                else{
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

            }
        });

        refreshUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshUsers();
            }
        });

        /**
         * Button functionality related to Billboard Settings
         * Note - if a user does not have permission to create and/or edit billboards, they will still be able to open
         * the BillboardControl interface, but will not be able to save or make changes ot new/existing billboards
         */
        createBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }

                new BillboardControl(null, userName).main(null, userName);
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
                        throwDialog(ex.getMessage(),"Error");
                    }
                    new BillboardControl(billBoard, userName).main(billBoard, userName); // pass in selected billboard for edit

                }
            }
        });

        previewBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        throwDialog(ex.getMessage(),"Error");
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
                    new CalendarCreator(userName).main(null);
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
                        JOptionPane.showMessageDialog(null,ex.getMessage());
                    }
                    //TODO: ensure refresh works properly
                    refreshBillboards();
                }
            }
        });
    }

    //TODO: BUG -> if there is only one billboard, or user, in the list and I delete it: it will delete from the db,
    // but the list will still show the deleted billboard until one leaves the MainControl and re enters it. Clicking
    // the refresh button doesnt seem to empty the list
    /**
     * The following private functions refresh either the users list or billboards list according to what exists
     * in the DB along with what the currently logged in user's permissions allow him/her to see
     * @param list
     * @param stringArray
     */
    private static void refresh(JList list, ArrayList<String> stringArray) {
        list.removeAll();
        list.clearSelection();

        final DefaultListModel model = new DefaultListModel();

        // Remove all elements within the JList model and add the inputted ArrayList
        model.clear();
        model.removeAllElements();
        for (int i = 0, n = stringArray.size(); i < n; i++) {
            model.addElement(stringArray.get(i));
            list.setModel(model);
        }
    }

    private void refreshBillboards(){
        // Clear the billboard list
        billboardsList.clearSelection();
        billboardsList.removeAll();
        // Retrieve all billboards in db
        TreeMap<String, Object> billboards = null;
        try {
            billboards = Main.server.getBillboards();
        } catch (ServerException z) {
            z.printStackTrace();
        }
        // Retrieve all billboards names and store into an array list
        ArrayList<String> bbList = new ArrayList<String>();
        TreeMap<String, Object> finalBillboards = billboards;
        finalBillboards.forEach((k, v) -> {
            System.out.println("Key: " + k + ", Value: " + v);
            String bbString = v.toString();
            bbString = bbString.substring(bbString.indexOf("billboardName=") + 14);
            bbString = bbString.substring(0, bbString.indexOf(","));
            bbList.add(bbString);
        });
        refresh(billboardsList, bbList);
    }

    private void refreshUsers(){
        // Clear the username list
        usersList.clearSelection();
        usersList.removeAll();
        // Retrieve all usernames in db
        TreeMap<String, Object> users = null;
        try {
            users = Main.server.getUsers();
        } catch (ServerException z) {
            z.printStackTrace();
        }
        // Retrieve all usernames and store into an array list
        ArrayList<String> userNameList = new ArrayList<String>();
        TreeMap<String, Object> finalUsers = users;
        finalUsers.forEach((k, v) -> {
            String userString = v.toString().substring(v.toString().lastIndexOf("=") + 1);
            userString = userString.replace("}", "");
            userNameList.add(userString);
        });
        refresh(usersList, userNameList);
    }

    private static void throwDialog(String messageText, String title) {
        JOptionPane.showMessageDialog(null, messageText, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * MainControl takers in a username argument to refer to when navigating through the main screen of the control panel
     * @param username
     */
    public static void main(String username) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Control Panel");
        Main.centreWindow(frame);
        frame.setContentPane(new MainControl(username).controlPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
