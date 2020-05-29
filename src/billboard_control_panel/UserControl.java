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

    public UserControl() {
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
                    Main.server.deleteUser(finalUserId);
                } catch (ServerException ex) {
                    JOptionPane.showMessageDialog(null, "You do not have permission to remove users.");
                    ex.printStackTrace();
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start with check if the user exists within the system
                String userIDcheck = null;
                try {
                    userIDcheck = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                String finalUserIdCheck = userIDcheck;
                // If new user, create(add) new user
                if (finalUserIdCheck == null){
                    try {
                        Main.server.addUser(userNameField.getText(), passwordField1.getText(), Protocol.Permission.NONE);
                        JOptionPane.showMessageDialog(null, "New user created");
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
                        ex.printStackTrace();
                    }
                }
                // If existing user, edit existing user
                    // Now check each box and use the editUser function to give certain permissions
                try {
                    userIDcheck = Main.server.getUserId(userNameField.getText());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
                finalUserIdCheck = userIDcheck;

                if (ScheduleBBCheckBox.isSelected()){
                    try {
                        Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), Protocol.Permission.SCHEDULE_BILLBOARDS);
                        JOptionPane.showMessageDialog(null, "User successfully edited");
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
                        ex.printStackTrace();
                    }
                }
                if (EditBBCheckBox.isSelected()){
                    try {
                        Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), Protocol.Permission.EDIT_ALL_BILLBOARDS);
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
                        ex.printStackTrace();
                    }
                }
                if (CreateBBCheckBox.isSelected()){
                    try {
                        Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), Protocol.Permission.CREATE_BILLBOARDS);
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
                        ex.printStackTrace();
                    }
                }
                if (editUsersCheckBox.isSelected()){
                    try {
                        Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), Protocol.Permission.EDIT_USERS);
                    } catch (ServerException ex) {
                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
                        ex.printStackTrace();
                    }
                }
//                if (editUsersCheckBox.isSelected() && ScheduleBBCheckBox.isSelected() && CreateBBCheckBox.isSelected() && EditBBCheckBox.isSelected() ){
//                    try {
//                        Main.server.editUser(finalUserIdCheck, userNameField.getText(), passwordField1.getText(), Protocol.Permission.ALL);
//                    } catch (ServerException ex) {
//                        JOptionPane.showMessageDialog(null, "You do not have permission to create users.");
//                        ex.printStackTrace();
//                    }
//                }

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
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard User Builder");
        Main.centreWindow(frame);
        frame.setContentPane(new UserControl().userControl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
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
