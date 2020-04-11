package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserBuilder {
    private JPanel UBPanel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JRadioButton createBillboardsRadioButton;
    private JRadioButton editAllBillboardsRadioButton;
    private JRadioButton scheduleBillboardsRadioButton;
    private JRadioButton editUsersRadioButton;
    private JButton removeButton;
    private JButton saveButton;
    private JButton saveAndExitButton;

    public UserBuilder() {
        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new ControlPanel().main(null);
            }
        });
    }

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("UBPanel");
        Main.centreWindow(frame);
        frame.setContentPane(new UserBuilder().UBPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
