package billboard_viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;


// TODO - pictureUrl download from http/https source to image
// TODO - pictureData conversion from Base64 to image

/**
 * Create a JPanel based on the input TreeMap and determination of type
 */
public class Panel {

    JPanel billboardPanel = new JPanel();

    private double xRes; //Full screen width
    private double yRes; //Full screen height

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

        // Set Resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        xRes = screenSize.getWidth();
        yRes = screenSize.getHeight();

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
    private void createMPI() {
        // TODO - Message and information font sizing, should this scale on screen and message size
        // TODO - Input image from network location or base64 - this should be handled elsewhere in a new function?

        //createPlaceholder();

        billboardPanel.setLayout(new BorderLayout());
        //billboardPanel.setLayout(new BorderLayout());

        //CardLayout card =
        //billboardPanel.setLayout(card);

        /*
        JLabel messageLabel = new JLabel("Message text: Hello World");
        billboardPanel.add(messageLabel);
         */

        // TEST IMAGE SETUP
        BufferedImage image = null; // null initialisation
        BufferedImage scaledImage = null; // null initialisation
        String testImage = "Billboard640x480.png";
        String imagePath = System.getProperty("user.dir") + "\\Assets\\" + testImage; // test file

        File imageFile = new File(imagePath);
        try {
            image = ImageIO.read(imageFile);

        } catch (IOException ex) {
            // Exception handling
        }

        // Scale input image to a third
        scaledImage = scaleThird(image);

        // Get scaled image size
        int scaledWidth = scaledImage.getWidth();
        int scaledHeight = scaledImage.getHeight();

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        //topPanel.setBorder(BorderFactory.createTitledBorder("Debug: Top Panel"));
        topPanel.setPreferredSize(new Dimension(scaledWidth,scaledHeight));
        JLabel topMessage = new JLabel(message);
        topMessage.setFont(new Font("Serif", Font.BOLD, 50)); // Set font and size
        topMessage.setForeground(Color.decode(messageColour));
        topPanel.add(topMessage);

        // Center Image
        JPanel centrePanel = new JPanel(new GridBagLayout()); // GridBagLayout will center the image
        //centrePanel.setBorder(BorderFactory.createTitledBorder("Debug: Centre Panel"));
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        centrePanel.setSize(scaledWidth, scaledHeight);
        centrePanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));
        //centrePanel.set

        // ADD IMAGE
        centrePanel.add(imageLabel);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension(scaledWidth,scaledHeight));
        //bottomPanel.setBorder(BorderFactory.createTitledBorder("Debug: Bottom Panel"));
        JLabel bottomText = new JLabel(information);
        bottomText.setFont(new Font("Serif", Font.PLAIN, 40)); // Set font and size
        bottomText.setForeground(Color.decode(messageColour));
        bottomPanel.add(bottomText);

        billboardPanel.add(topPanel, BorderLayout.PAGE_START);
        billboardPanel.add(centrePanel, BorderLayout.CENTER);
        billboardPanel.add(bottomPanel, BorderLayout.PAGE_END);

        // Set Panel Backgrounds
        //billboardBackground = "#a3a375"; // test colour yellow

        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground)); // test color
        bottomPanel.setBackground(Color.decode(billboardBackground));

        //billboardPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

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
     * Scale image to 50% of screen size
     * @param image
     * @return
     */
    private BufferedImage scaleHalf(BufferedImage image) {

        double aspectRatio, scaledHeight, scaledWidth;
        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();

        System.out.println("Original Image Size: x="+sourceWidth+" y="+sourceHeight);

        // Get scaled resolution
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

        // Checks - clean this up
        if (scaledWidth > xRes/2 || scaledHeight > yRes/2) {
            System.out.println("Debug: Bad Resolution! Acceptable Maximum: "+xRes/2+"x"+yRes/2);
        }
        System.out.println("Scaled Image Size: x="+scaledWidth+" y="+scaledHeight);

        // Create blank image
        BufferedImage scaledImage = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_ARGB);

        // Scale source image to Buffered Image size
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, (int) scaledWidth, (int) scaledHeight, null);

        //scaledImage = image.getScaledInstance()
        //scaledImage = image.getScaledInstance((int) scaledWidth,(int) scaledHeight, Image.SCALE_DEFAULT);

        return scaledImage;
    }

    /**
     * Scale image to a third of screen size on largest relative axis
     * @param image
     * @return
     */
    private BufferedImage scaleThird(BufferedImage image) {

        double aspectRatio, scaledHeight, scaledWidth;
        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();

        System.out.println("Original Image Size: x="+sourceWidth+" y="+sourceHeight);

        // Get scaled resolution
        if ( (xRes*1/3) / sourceWidth * sourceHeight > yRes*1/3) { // Check if scale should start on width or height
            // Scale on Height
            scaledHeight = (int) (yRes * 1/3);
            aspectRatio = scaledHeight / sourceHeight;
            scaledWidth = sourceWidth * aspectRatio;
        } else {
            // Scale on Width
            scaledWidth = (int) (xRes * 1/3);
            aspectRatio = scaledWidth / sourceWidth;
            scaledHeight = sourceHeight * aspectRatio;
        }

        // Checks - clean this up
        if (scaledWidth > xRes*1/3 || scaledHeight > yRes*1/3) {
            System.out.println("Debug: Bad Resolution! Acceptable Maximum: "+xRes*1/3+"x"+yRes*1/3);
        }
        System.out.println("Scaled Image Size: x="+scaledWidth+" y="+scaledHeight);

        // Create blank image
        BufferedImage scaledImage = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_ARGB);

        // Scale source image to Buffered Image size
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, (int) scaledWidth, (int) scaledHeight, null);

        //scaledImage = image.getScaledInstance()
        //scaledImage = image.getScaledInstance((int) scaledWidth,(int) scaledHeight, Image.SCALE_DEFAULT);

        return scaledImage;
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
