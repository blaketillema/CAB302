package Sandbox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Java Swing
/*
Swing:
    javax.swing - https://docs.oracle.com/en/java/javase/13/docs/api/java.desktop/javax/swing/package-summary.html
    java.awt - https://docs.oracle.com/en/java/javase/13/docs/api/java.desktop/java/awt/package-summary.html

Visual Guide to Swing Components - https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html
 */

/*
Types of Billboards:
    - Message
    - Picture
    - Information
    - Message and Picture
    - Message and Information
    - Picture and Information
    - Message, picture and information
 */

public class gui extends JFrame implments ActionListener{

    JFrame frame = new JFrame("Billboard Frame");

    //private String testImage = "Billboard1200x800.png";
    //private String testImage = "Billboard1200x1800.png";
    //private String testImage = "Billboard1800x1200.png";
    private String testImage = "Billboard640x480.png";

    private double xRes; //Full screen width
    private double yRes; //Full screen height
    boolean windowed = false; // set to true for Windowed (dev)
    private String imagePath = System.getProperty("user.dir") + "\\Assets\\" + testImage; // test file

    BufferedImage image = null; // null initialisation
    Image scaledImage = null; // null initialisation

    public gui() {
        //GUI Starts here
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        xRes = screenSize.getWidth();
        yRes = screenSize.getHeight();

        setupFrame();
        setupImage();
        //setBackground();

        System.out.println(imagePath);
        System.out.println("Screen Size: x="+xRes+" y="+yRes);

        frame.setVisible(true);

        keyStroke();
        mouseClick();

        System.out.println("End GUI.");
    }

    private void mouseClick() {
        // Close frame on Left mouse click
        frame.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int click = e.getButton();
                System.out.println("Mouse Click: "+click);
                if (click == 1) {
                    frame.setVisible(false); // close frame
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseDragged(MouseEvent e) {}
            @Override
            public void mouseMoved(MouseEvent e) {}
        });

    }

    private void keyStroke() {
        // this function gave me a stroke

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                System.out.println("Key Press: "+key);
                if (key == '') {
                    frame.setVisible(false); // Alternatively, hide but reserve the frame?
                    //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)); // Close window
                }
        }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    private void setupFrame() {
        //frame.setSize(600,600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        if (!windowed) {
            frame.setUndecorated(true); // Set to True to remove window bars for true fullscreen
        }

        // Test background colour
        JLabel background = new JLabel();
        background.setBackground(Color.BLUE); // To do: implement color code
        frame.add(background);

    }

    private void setBackground() {
        // Test background colour
        //frame.getContentPane().setLayout(new FlowLayout());
        JLabel background = new JLabel();
        background.setBackground(Color.BLUE); // To do: implement color code
        frame.add(background);

    }

    private void setupImage() {
        // Add an image to the panel
        double aspectRatio, scaledHeight, scaledWidth;

        File imageFile = new File(imagePath);

        try {
            image = ImageIO.read(imageFile);

        } catch (IOException ex) {
            // Exception handling
        }

        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();
        System.out.println("Original Image Size: x="+sourceWidth+" y="+sourceHeight);

        if ( (xRes/2) / sourceWidth * sourceHeight > yRes/2) { // Check if scale should start on width or height
            // Scale on Height
            scaledHeight = (int) (yRes / 2);
            aspectRatio = scaledHeight / sourceHeight;
            scaledWidth = sourceWidth * aspectRatio;
        } else {
            // Scale on Width
            scaledWidth = (int) (xRes / 2);
            aspectRatio = scaledWidth / sourceWidth;
            scaledHeight = sourceHeight * aspectRatio;
        }

        // Checks
        if (scaledWidth > xRes/2 || scaledHeight > yRes/2) {
            System.out.println("Debug: Bad Resolution! Acceptable Maximum: "+xRes/2+"x"+yRes/2);
        }

        System.out.println("Scaled Image Size: x="+scaledWidth+" y="+scaledHeight);
        scaledImage = image.getScaledInstance((int) scaledWidth,(int) scaledHeight, Image.SCALE_DEFAULT);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        frame.add(imageLabel);

    }

}
