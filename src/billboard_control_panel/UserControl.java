package billboard_control_panel;

import connections.ClientServerInterface;
import connections.Protocol;
import connections.exceptions.ServerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserControl {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JButton removeButton;
    private JButton saveButton;
    private JButton saveAndExitButton;
    private JPanel userControl;

    public UserControl() {
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientServerInterface server = new ClientServerInterface();
                try {
                    server.addNewUser(textField1.getText(), passwordField1.getText(), Protocol.Permission.ALL);
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }

            }
        });
        saveAndExitButton.addActionListener(new ActionListener() {
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
}
