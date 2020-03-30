package billboard_viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This is the main GUI to display the current billboard
 */
// TODO change this back to an abstract class and extend it for each billboard type ???
public class DisplayBillboard {
    JFrame frame = new JFrame("Billboard Frame");
    private double xRes; //Full screen width
    private double yRes; //Full screen height
    // Image Setup
    private String imageFileName = "Billboard640x480.png";
    private String imagePath = System.getProperty("user.dir") + "\\Assets\\" + imageFileName;
    private BufferedImage image = null; // null initialisation
    private Image scaledImage = null; // null initialisation
    // Colour Scheme
    private String backgroundColour = "#ffec82"; // DEFAULT Yellow
    private String messageColour = "#ae82ff"; // DEFAULT purple
    private String informationColour = "#4682b4"; // DEFAULT Steel blue
    // Billboard
    private Billboard billboardNow;


    // TODO add overriding for each different billboard type
    public DisplayBillboard(Billboard billboardNow) {

    }



        /*  Types of Billboards:
        - Error - No billboard
        - Error - Billboard server not available
        - Message
        - Picture
        - Information
        - Message and Picture
        - Message and Information
        - Picture and Information
        - Message, picture and information
        */

    // TODO implement this method for each billboard type / class
    public void displayCurrentBillboard() {
        // TODO implement defaults here
        setupScreen();
        setupImage();
    }

    private void setupScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        xRes = screenSize.getWidth();
        yRes = screenSize.getHeight();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true); // True to remove window bars for true fullscreen
        frame.setVisible(true);
        setBackground();
        setupKeyStroke();
        setupMouseClick();
    }

    // TODO setup so mouse click properly exits program
    private void setupMouseClick() {
        // Close frame on Left mouse click
        frame.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int click = e.getButton();
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

    // TODO fix this ????
    private void setupKeyStroke() {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                // System.out.println("Key Press: "+key);
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

    private void setBackground() {
        // Background colour
        JLabel background = new JLabel();
        background.setBackground(Color.decode(backgroundColour));
        frame.add(background);
    }

    private void setupImage() {
        // Add an image to the panel
        double aspectRatio, scaledHeight, scaledWidth;
        File imageFile = new File(imagePath);
        try {
            image = ImageIO.read(imageFile);

        } catch (IOException ex) {
            // TODO add exception handling
            // Exception handling
        }
        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();
        // TODO Check if scale should start on width or height
        if ( (xRes/2) / sourceWidth * sourceHeight > yRes/2) {
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
        // TODO Fix checks and action proper fixes
        if (scaledWidth > xRes/2 || scaledHeight > yRes/2) {
            System.out.println("Debug: Bad Resolution! Acceptable Maximum: "+xRes/2+"x"+yRes/2);
        }
        scaledImage = image.getScaledInstance((int) scaledWidth,(int) scaledHeight, Image.SCALE_DEFAULT);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        frame.add(imageLabel);
    }

}





