package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeMap;


import billboard_server.exceptions.ServerException;

/**
 *  The LoginManager class constructs, displays and provides functionality as the first GUI users see to login to the
 *  control panel. The user can login with their credentials which are authenticated through the server’s ‘login’
 *  function. If this is the first time the GUI is ran on the initiated server, the only user that is created and
 *  stored by the server is the admin. The admin can login using the username ‘admin’ and password ‘cab302’
 */
public class LoginManager extends JFrame {
    public JPanel LoginPanel;
    public JButton loginButton;
    public JPasswordField passwordField1;
    public JTextField usernameField1;


    public LoginManager() {

        setTitle("Login Form");

        // Login Button Action Listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameField1.getText(), passwordField1.getText());
            }
        });

        // Pressing 'Enter/Return' acts as clicking the Login Button
        passwordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login(usernameField1.getText(), passwordField1.getText());
                }
            }
        });
        usernameField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login(usernameField1.getText(), passwordField1.getText());
                }
            }
        });
    }

    /**
     * Login function which calls upon the server.login function
     * Username: admin
     * Password: cab203 (not cab302)
     * If successfully logged in as admin or any user stored in the DB, the MainControl() class will be initialised
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        boolean success = true;
            try {
                Main.server.login(username,password);
                // TODO: bug when logging in as someone who doesnt exist,
                //  if the following is called, it will attempt to run regardless if exception is thrown or not
                //new MainControl(usernameField1.getText()).main(usernameField1.getText());

            } catch (ServerException e) {
                JOptionPane.showMessageDialog(null, "Invalid username or password");
                success = false;
            }
            // TODO: this keeps successs as true
        boolean successs = success;
            System.out.println(successs);
            if (successs == true){
                new MainControl(usernameField1.getText()).main(usernameField1.getText());
            }
    }

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frameLP = new JFrame("Billboard Control Panel Login");
        Main.centreWindow(frameLP);
        frameLP.setContentPane(new LoginManager().LoginPanel);
        frameLP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLP.pack();
        frameLP.setVisible(true);
    }
}
