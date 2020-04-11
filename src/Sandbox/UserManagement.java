package Sandbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class UserManagement {
    private JPanel LoginPanel;
    private JPasswordField passwordField1;
    private JTextField usernameField1;
    private JButton loginButton;

    // TODO: clean up GUI
    public UserManagement() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField1.getText();
                String password = passwordField1.getText();

                Component frame = null;
                if (username.equals("Lahiru") && password.equals("password")) {
                    JOptionPane.showMessageDialog(null, "Successfully logged in");

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserLoginPanel");
        frame.setContentPane(new UserManagement().LoginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
