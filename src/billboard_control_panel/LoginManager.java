package billboard_control_panel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
                String username = usernameField1.getText();
                String password = passwordField1.getText();

                Component frame = null;
                if (username.equals("admin") && password.equals("admin")) {
                    new ControlPanel().main(null);

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });
    }

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frameLP = new JFrame("LoginPanel");
        frameLP.setContentPane(new LoginManager().LoginPanel);
        frameLP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLP.pack();
        frameLP.setVisible(true);


    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
