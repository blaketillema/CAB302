package billboard_control_panel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class controlPanel {
    private JButton createBB_Button;
    private JButton editBB_Button;
    private JButton deleteBB_Button;
    private JButton scheduleBB_Button;
    private JButton button5;
    private JList list1;
    private JList list2;
    private JButton createUser_Button;
    private JButton editUsers_Button;
    private JButton deleteUsers_Button;
    private JButton button4;
    private JButton button6;

    public controlPanel() {
        createBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Take to create billboards GUI

            }
        });
        editBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        deleteBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        scheduleBB_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        createUser_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        editUsers_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        deleteUsers_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
