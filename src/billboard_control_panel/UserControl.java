package billboard_control_panel;

//import connections.ClientServerInterface;
//import connections.Protocol;
//import connections.exceptions.ServerException;

import billboard_server.Protocol;
import billboard_server.exceptions.ServerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;


public class UserControl {
    private JTextField userNameField;
    private JPasswordField passwordField1;
    private JCheckBox ScheduleBBCheckBox;
    private JCheckBox editUsersCheckBox;
    private JCheckBox EditBBCheckBox;
    private JCheckBox CreateBBCheckBox;
    private JButton removeButton;
    private JButton saveButton;
    private JButton exitButton;
    private JPanel userControl;

    TreeMap<String, Object> currentUser = new TreeMap<>(); // Initialize an empty TreeMap

    /**
     * UserControl is where a user can create, edit and/or delete users depending on their permissions.
     * If a null editUser tree map is inputted from the MainControl, this means the "Create User" button was selected,
     * leaving the fields blank. If a non-null editUser tree map was inputted, this means a user was selected in the
     * MainControl along with the "Edit User" button. This then populates the UserControl fields with the user's username
     * and elected permissions.
     * @param editUser
     */
    public UserControl(TreeMap editUser) {

        if (editUser != null) {
            System.out.println("Input billboard not null.");
            currentUser = editUser;
            refreshFields();
        } else {
            System.out.println("Input billboard is null.");
        }

        // Get newly created user's userID to edit the user's permissions
        String userId = null;
        try {
            userId = Main.server.getUserId(userNameField.getText());
        } catch (ServerException ex) {
            ex.printStackTrace();
        }
        String finalUserId = userId;

        /**
         * Button functionality
         */
        // Deletes a user
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + userNameField.getText() + "?");
                    if (n == JOptionPane.YES_OPTION) {
                        Main.server.deleteUser(finalUserId);
                        userNameField.setText("");
                        passwordField1.setText("");
                        ScheduleBBCheckBox.setSelected(false);
                        editUsersCheckBox.setSelected(false);
                        CreateBBCheckBox.setSelected(false);
                        EditBBCheckBox.setSelected(false);
                    } else if (n == JOptionPane.NO_OPTION) {
                    }
                } catch (ServerException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        //Saves a user
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start with check if the user exists within the system
                String userIDchecks = null;
                try {
                    userIDchecks = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                String finalUserIdCheck = userIDchecks;

                // If user does not exist in db, create(add) new user
                if (finalUserIdCheck == null){
                    try {
                        Main.server.addUser(userNameField.getText(), passwordField1.getText(), Protocol.Permission.NONE);
                        JOptionPane.showMessageDialog(null, "New user created");
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }

                // Now check again if user exist (which it should)
                String userIDcheck = null;
                try {
                    userIDcheck = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                finalUserIdCheck = userIDcheck;

                // Tally the checked permissions together as an integer
                Integer newPermission = 0;
                if (ScheduleBBCheckBox.isSelected()){
                    newPermission += 0b100;
                }
                if (EditBBCheckBox.isSelected()){
                    newPermission += 0b1;
                }
                if (CreateBBCheckBox.isSelected()){
                    newPermission += 0b10;
                }
                if (editUsersCheckBox.isSelected()){
                    newPermission += 0b1000;
                }

                // Edit the newly created or existing user with the saved data using the server.editUser function
                try {
                    Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), newPermission);
                    if (userIDchecks != null){
                        JOptionPane.showMessageDialog(null, "Edited user: " + userNameField.getText());
                    }
                } catch (ServerException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
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
                new MainControl(null).main(null);
            }
        });
    }

    public static void main(TreeMap inputUser) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard User Builder");
        Main.centreWindow(frame);
        frame.setContentPane(new UserControl(inputUser).userControl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Refreshes text area fields when changes are made or applied to the billboard data
     */
    private void refreshFields() {
        // Refresh username field, leave password field blank
        userNameField.setText(currentUser.get("userName").toString());

        // Check the integer of the permissions and check the appropriate boxes
        Integer userPermission = Integer.parseInt(currentUser.get("permissions").toString());
        System.out.println(Protocol.Permission.toString(userPermission));

        if ((userPermission & Protocol.Permission.EDIT_ALL_BILLBOARDS) != 0) {
            EditBBCheckBox.setSelected(true);
        }

        if ((userPermission & Protocol.Permission.CREATE_BILLBOARDS) != 0) {
            CreateBBCheckBox.setSelected(true);
        }

        if ((userPermission & Protocol.Permission.SCHEDULE_BILLBOARDS) != 0) {
            ScheduleBBCheckBox.setSelected(true);
        }

        if ((userPermission & Protocol.Permission.EDIT_USERS) != 0) {
            editUsersCheckBox.setSelected(true);
        }


        // If billboard name set, such as editing a billboard
        if (currentUser.containsKey("userName")) {
            //
            userNameField.setText(currentUser.get("userName").toString());
        }
    }


}
