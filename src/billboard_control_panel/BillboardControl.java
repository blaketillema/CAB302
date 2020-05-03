package billboard_control_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

public class BillboardControl {
    private JTextField textField1;
    private JButton importXMLButton;
    private JButton exportXMLButton;
    private JButton uploadImageButton;
    private JButton textFontButton;
    private JButton textSizeButton;
    private JButton textColourButton;
    private JButton backgroundColourButton;
    private JButton exitButton;
    private JButton saveButton;
    private JButton previewButton;
    private JPanel billboardControl;
    private JEditorPane editorPane1;

    public BillboardControl() {
        importXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        exportXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Preview Billboard");

                //TODO - properly implement preview, currently concept test code

                System.out.println("Import XML");

                // TEST CODE
                TreeMap<String, String> sampleBillboard = new TreeMap<>();

                String billboardBackground = "#0000FF";
                String message = "THIS IS A PREVIEW BUTTON TEST";
                String messageColour = "#FFFF00";
                String pictureUrl = "https://www.w3schools.com/html/pic_trulli.jpg";
                String pictureData = null;
                String information = "Be sure to check out https://example.com/ for more information." +
                        "Longer Information string for testing: Hello world. Hello World. Hello World." +
                        "Hello World. Hello World. Hello World. INFORMATION END";
                String informationColour = "#00FFFF";

                sampleBillboard.put("billboardBackground", billboardBackground);
                sampleBillboard.put("message", message);
                sampleBillboard.put("messageColour", messageColour);
                sampleBillboard.put("pictureUrl", pictureUrl);
                sampleBillboard.put("pictureData", pictureData);
                sampleBillboard.put("information", information);
                sampleBillboard.put("informationColour", informationColour);

                // Pass in TreeMap contents of a billboard to the Billboard class
                billboard_viewer.Billboard previewBillboard = new billboard_viewer.Billboard(sampleBillboard, true);

            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
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
                    new MainControl().main(null);
                } else if (n == JOptionPane.NO_OPTION) {
                }
            }
        });
        textFontButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        textSizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        textColourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        backgroundColourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Builder");
        Main.centreWindow(frame);
        frame.setContentPane(new BillboardControl().billboardControl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
