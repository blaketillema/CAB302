package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                    new ControlPanel();
                    ControlPanel.main(null);

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });
    }

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frameLP = new JFrame("LoginPanel");
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
