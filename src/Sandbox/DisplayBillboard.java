package Sandbox;

import javax.swing.*;
import java.awt.*;

<<<<<<< HEAD
public class DisplayBillboard extends JFrame {
=======
public class DisplayBillboard extends JFrame{
>>>>>>> billboardViewer
    private JPanel panel;
    private JLabel label01;
    private ImageIcon icon01;

    public DisplayBillboard() {

        setLayout(new FlowLayout());
<<<<<<< HEAD
        setSize(50, 50);
=======
        setSize(50,50);
>>>>>>> billboardViewer

        //label01
        icon01 = new ImageIcon(getClass().getResource("Billboard1200x800.png"));

        label01.setIcon(icon01);
        panel.add(label01);

        add(panel);
        validate();
    }

    public static void main(String[] args) {
        DisplayBillboard gui = new DisplayBillboard();
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(50, 50);
        gui.setVisible(true);
        gui.pack();
        gui.setTitle("Image");
    }
<<<<<<< HEAD

=======
>>>>>>> billboardViewer
}

