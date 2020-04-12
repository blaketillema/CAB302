package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BillboardBuilder {
    private JTextField enterBuildboardNameTextField;
    private JPanel BB_BuilderPanel;
    private JButton importXMLButton;
    private JButton exportXMLButton;
    private JButton textColourButton;
    private JButton textSizeButton;
    private JButton importImageButton;
    private JButton backgroundColourButton;
    private JEditorPane editorPane1;
    private JButton textFontButton;
    private JButton previewButton;
    private JButton saveButton;
    private JButton exitButton;

    public BillboardBuilder() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit without saving?");
                if (n == JOptionPane.YES_OPTION) {
                    Window[] wns = LoginManager.getFrames();
                    for (Window wn1 : wns) {
                        wn1.dispose();
                        wn1.setVisible(false);
                    }
                    new ControlPanel().main(null);
                } else if (n == JOptionPane.NO_OPTION) {
                }
            }
        });
    }
    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Builder");
        Main.centreWindow(frame);
        frame.setContentPane(new BillboardBuilder().BB_BuilderPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
