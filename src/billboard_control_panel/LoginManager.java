package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginManager extends JFrame {
    public JPanel LoginPanel;
    public JButton loginButton;
    public JPasswordField passwordField1;
    public JTextField usernameField1;


    public LoginManager() {

        setTitle("Login Form");


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login(usernameField1.getText(),passwordField1.getText());
            }
        });
        passwordField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    login(usernameField1.getText(),passwordField1.getText());
                }
            }
        });
        usernameField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    login(usernameField1.getText(),passwordField1.getText());
                }
            }
        });
    }

    public void login(String username, String password){
        Component frame = null;
        if (username.equals("admin") && password.equals("admin")) {
            new MainControl().main(null);

        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password");
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
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
