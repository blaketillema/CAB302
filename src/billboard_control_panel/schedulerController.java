package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class schedulerController {
    private JButton deleteButton;
    private JButton saveButton;
    private JButton resetButton;
    private JTextField enterScheduleNameTextField;
    public JPanel schedulerPanel;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JRadioButton mondayRadioButton;
    private JRadioButton wednesdayRadioButton;
    private JRadioButton tuesdayRadioButton;
    private JRadioButton fridayRadioButton;
    private JRadioButton saturdayRadioButton;
    private JRadioButton sundayRadioButton;
    private JRadioButton thursdayRadioButton;
    private JButton exitButton;

//    public schedulerController() {
//        setTitle("Login Form");
//    }

    public schedulerController() {
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
}
