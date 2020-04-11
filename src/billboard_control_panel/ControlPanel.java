package billboard_control_panel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ControlPanel {
    private JTabbedPane tabbedPane1;
    public JPanel CPPanel;
    private JButton createBB_Button;
    private JButton editBB_Button;
    private JButton previewBB_Button;
    private JList billboardList;
    private JButton createUser_Button;
    private JButton editUser_Button;
    private JButton removeUser_Button;
    private JList userList;
    private JButton logOutButton;

    public ControlPanel() {
        Window[] wns = LoginManager.getFrames();
        for (Window wn1 : wns) {
            wn1.setVisible(false);
        }
        createBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?");
                if (n == JOptionPane.YES_OPTION) {
                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.setVisible(false);
                    }
                    new LoginManager().main(null);
                } else if (n == JOptionPane.NO_OPTION) {
                }
            }
        });
        createBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.setVisible(false);
                }
                new BillboardBuilder().main(null);
            }
        });
    }

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("CP_Panel");
        Main.centreWindow(frame);
        frame.setContentPane(new ControlPanel().CPPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
