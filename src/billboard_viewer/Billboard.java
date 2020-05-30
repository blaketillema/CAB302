package billboard_viewer;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.TreeMap;


/**
 * Billboard class is called from main, taking Input TreeMap billboard data,
 * handling the JFrame presented to the screen and calling the Panel class
 * from input TreeMap to retrieve JPanels of a visual Billboard in full screen.
 */
public class Billboard {
    private TreeMap<String, String> billboardContents;
    private double xRes; //Full screen width
    private double yRes; //Full screen height
    private boolean hasMessage = false;
    private boolean hasInformation = false;
    private boolean hasImageData = false;
    private boolean hasImageURL = false;
    private boolean isDefault = false;
    private JFrame billboardFrame = new JFrame("Billboard Frame");
    private boolean billboardClosed = false;
    private boolean preview = false; // Indicates if called from the control panel to preview a billboard. Default value false.

    /**
     * Default constructor (No billboard to display)
     */
    public Billboard() {
        // Start billboard as default if given no input TreeMap
        isDefault = true;
        TreeMap<String, String> defaultBillboard = new TreeMap<>();
        defaultBillboard.put("default", "Advertise Here!!!");
        this.billboardContents = defaultBillboard;
        frameSetup();
    }

    /**
     * Construct billboard with input TreeMap
     *
     * @param billboardContents
     */
    public Billboard(TreeMap<String, String> billboardContents) {
        this.billboardContents = billboardContents;
        frameSetup();
    }

    /**
     * Used for previewing billboard from control panel.
     * Construct billboard with input TreeMap, with boolean overload for control panel preview
     *
     * @param billboardContents takes contents of a billboard from the billboard creator
     */
    public Billboard(TreeMap<String, String> billboardContents, boolean preview) {
        this.preview = true;
        this.billboardContents = billboardContents;
        frameSetup();
    }

    /**
     * Setup JFrame
     */
    private void frameSetup() {
        // Handle billboard
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Get screen Resolution
        xRes = screenSize.getWidth(); // Set resolution x
        yRes = screenSize.getHeight(); // Set resolution y
        billboardFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set Frame to Full Screen
        billboardFrame.setUndecorated(true); // Remove window bars for true full screen

        // Check if the billboard is a control panel preview
        if (preview) {
            Panel panel = new Panel(billboardContents);
            JPanel billboardPanel = panel.getPanel();
            billboardFrame.add(billboardPanel);
        } else {
            billboardFrame.add(noBillboardPanel());
        }
        // Add listeners for left mouse or ESC key to exist program
        addMouseListener();
        addKeyListener();
        billboardFrame.setVisible(true);
    }

    /**
     * Creates and returns the default connection error JPanel
     * @return JPanel
     */
    private JPanel noBillboardPanel(){
        JPanel defaultPanel = new JPanel(new GridBagLayout());

        String defaultMessage = "No connection to Billboard Server. Attempting to connect...";
        JLabel label = new JLabel(defaultMessage);
        label.setForeground(Color.WHITE);
        defaultPanel.setBackground(Color.decode("#700000"));
        defaultPanel.add(label);

        return defaultPanel;
    }

    /**
     * Update Billboard Contents
     * @param newBillboard
     */
    public void updateBillboard(TreeMap<String, String> newBillboard) {
        // Check if billboard is the same as previous
        if (billboardContents.equals(newBillboard)) {
            // Same Billboard, do nothing

        } else {
            // New Billboard
            this.billboardContents = newBillboard;
            // Check type of billboard
            if ( billboardContents.containsKey("message") ) {
                hasMessage = true;
            }
            if ( billboardContents.containsKey("information") ) {
                hasInformation = true;
            }
            if ( billboardContents.containsKey("pictureData") ) {
                hasImageData = true;
            }
            if ( billboardContents.containsKey("pictureURL") ) {
                hasImageURL = true;
            }
            // Create a new billboard JPanel
            Panel panel = new Panel(newBillboard);
            JPanel billboardPanel = panel.getPanel();
            // Clear frame contents for new panel
            billboardFrame.getContentPane().removeAll();
            // Add billboard Panel to frame
            billboardFrame.add(billboardPanel);
            // Reload Frame
            billboardFrame.revalidate();
            billboardFrame.repaint();
            billboardFrame.setVisible(true);
        }
    }

    /**
     * Add a Mouse listener for left mouse click to exit billboard
     */
    private void addMouseListener() {
        // Close frame on Left mouse click
        billboardFrame.addMouseListener(new MouseInputListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int click = e.getButton();
                if (click == 1) {
                    exitBillboard();
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

    /**
     * Add a Key listener for escape key to exit billboard
     */
    private void addKeyListener() {
        // this function gave me a stroke
        billboardFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                if (key == '') {
                    exitBillboard();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    /**
     * Close the billboard window and change boolean signal for main to read application is to close
     */
    private void exitBillboard() {
        billboardFrame.setVisible(false); // Hide but reserve the frame?
        billboardFrame.dispose(); // Dispose of JFrame
        billboardClosed = true;
    }

    /**
     * Check if billboard has been closed by user
     * @return state of viewer if closed
     */
    public boolean getState() {
        return billboardClosed;
    }
}
