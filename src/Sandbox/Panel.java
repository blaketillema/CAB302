package Sandbox;

import javax.swing.*;
import java.util.TreeMap;

/**
 * Create a JPanel based on the input TreeMap and determination of type
 */
public class Panel {

    JPanel billboardPanel = new JPanel();

    // Initialize Default colour Values
    String billboardBackground = "#FFFFFF"; // White
    String messageColour = "#000000"; // Black
    String informationColour = "#000000"; // Black

    // Non-default values
    String message;
    String pictureUrl;
    String pictureData;
    String information;

    public Panel(TreeMap<String, String> billboard) {

        // Set Variables from TreeMap
        if (billboard.get("billboardBackground") != null) {
            //not null
            billboardBackground = billboard.get("billboardBackground");
        } else {
            //null, keep default
        }

        if (billboard.get("messageColour") != null) {
            //not null
            messageColour = billboard.get("messageColour");
        } else {
            //null, keep default
        }

        if (billboard.get("informationColour") != null) {
            //not null
            informationColour = billboard.get("informationColour");
        } else {
            //null, keep default
        }
        // Set non-default variables
        message = billboard.get("message");
        pictureUrl = billboard.get("pictureUrl");
        pictureData = billboard.get("pictureData");
        information = billboard.get("information");

        // Determine billboard type and call method to create appropriate panel
        if (message != null) {
            if (information != null && ( pictureData != null || pictureUrl != null )) {
                // Settings for 1 - Message, picture and information
                System.out.println("DEBUG: Panel Type 1, MPI");
                createMPI();
            }
            else if (information != null) {
                // Settings for 2 - Message and Information
                System.out.println("DEBUG: Panel Type 2, MI");
                createMI();
            }
            else if (pictureData != null || pictureUrl != null) {
                // Settings for 3 - Message and Picture
                System.out.println("DEBUG: Panel Type 3, MP");
                createMP();
            }
            else {
                // Settings for 4 - Message
                System.out.println("DEBUG: Panel Type 4, M");
                createM();
            }
        }
        else if (pictureData != null || pictureUrl != null) {
            if (information != null) {
                // Settings for 5 - Picture and Information
                System.out.println("DEBUG: Panel Type 5, PI");
                createPI();
            } else {
                // Settings for 6 - Picture
                System.out.println("DEBUG: Panel Type 6, P");
                createP();
            }
        }
        else if (information != null) {
            // Settings for 7 - Information
            System.out.println("DEBUG: Panel Type 7, I");
            createI();
        }
        else if (true) { // billboardNow.isDefault()
            // Settings for 8 - Default - No billboard to display "Advertise Here!!!"
            System.out.println("DEBUG: Panel Type 8, default");
            createDefault();
        }
        // TODO - check if 9 Server not available needed in addition to 8
        /*
        Types of Billboards:
        1 - Message, picture and information
        2 - Message and Information
        3 - Message and Picture
        4 - Message
        5 - Picture and Information
        6 - Picture
        7 - Information
        8 - Default - No billboard to display "Advertise Here!!!"
        9 - Billboard server not available (Not sure if this & last are same message?)
            From the specifications: If there is no billboard scheduled at a particular time,
            the Server should send back something else for the Viewer to display in the meantime
        */


    }

    /**
     * Message, Picture and Information
     */
    private void createMPI() {createPlaceholder();}

    /**
     * Message and Information
     */
    private void createMI() {createPlaceholder();}

    /**
     * Message and Picture
     */
    private void createMP() {createPlaceholder();}

    /**
     * Message
     */
    private void createM() {createPlaceholder();}

    /**
     * Picture and Information
     */
    private void createPI() {createPlaceholder();}

    /**
     * Picture
     */
    private void createP() {createPlaceholder();}

    /** Information
     *
     */
    private void createI() {createPlaceholder();}

    /**
     * Billboard for no server connection error
     */
    private void createDefault() {
        JLabel label = new JLabel("ERROR! No Connection to Billboard Server. Attempting to Connect...");
        billboardPanel.add(label);
    }

    /**
     * Return the created JPanel
     * @return JPanel
     */
    public JPanel getPanel(){
        return billboardPanel;
    }

    /**
     * Unimplemented PlaceHolder JPanel
     */
    private void createPlaceholder() {
        //JPanel billboardPanel = new JPanel();
        JLabel label = new JLabel("Hello World - PlaceHolder JPanel");
        billboardPanel.add(label);
    }

}
