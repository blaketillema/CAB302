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

    public UserControl(TreeMap editUser) {

        if (editUser != null) {
            System.out.println("Input billboard not null.");
            currentUser = editUser;
            refreshFields();
        } else {
            System.out.println("Input billboard is null.");
        }
        //refreshFields();

        // Get newly created user's userID to edit the user's permissions
        String userId = null;
        try {
            userId = Main.server.getUserId(userNameField.getText());
        } catch (ServerException ex) {
            ex.printStackTrace();
        }
        String finalUserId = userId;

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
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start with check if the user exists within the system
                String userIDchecks = null;
                try {
                    userIDchecks = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    //JOptionPane.showMessageDialog(null, ex.getMessage());
                    ex.printStackTrace();
                }
                String finalUserIdCheck = userIDchecks;

                // If new user, create(add) new user
                if (finalUserIdCheck == null){
                    try {
                        Main.server.addUser(userNameField.getText(), passwordField1.getText(), Protocol.Permission.NONE);
                        JOptionPane.showMessageDialog(null, "New user created");
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
                // If existing user, edit existing user
                    // Now check each box and use the editUser function to give certain permissions
                String userIDcheck = null;
                try {
                    userIDcheck = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                finalUserIdCheck = userIDcheck;

                // Add the checked permissions together as an integer
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

                // Edit the user with the saved data
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
        userControl = new JPanel();
        userControl.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 6, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("User Control");
        userControl.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Username:");
        userControl.add(label2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Password:");
        userControl.add(label3, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Permissions");
        userControl.add(label4, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        userNameField = new JTextField();
        userControl.add(userNameField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        passwordField1 = new JPasswordField();
        userControl.add(passwordField1, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        editUsersCheckBox = new JCheckBox();
        editUsersCheckBox.setText("CheckBox");
        userControl.add(editUsersCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(78, 18), null, 0, false));
        ScheduleBBCheckBox = new JCheckBox();
        ScheduleBBCheckBox.setText("CheckBox");
        userControl.add(ScheduleBBCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Credentials");
        userControl.add(label5, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        EditBBCheckBox = new JCheckBox();
        EditBBCheckBox.setText("CheckBox");
        userControl.add(EditBBCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        userControl.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        userControl.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        CreateBBCheckBox = new JCheckBox();
        CreateBBCheckBox.setText("CheckBox");
        userControl.add(CreateBBCheckBox, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Save and Exit");
        userControl.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(6, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(78, 24), null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        userControl.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setText("Remove");
        userControl.add(removeButton, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return userControl;
    }
}
