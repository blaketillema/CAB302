package billboard_viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.TreeMap;

/**
 * Create a JPanel based on the input TreeMap and determination of type of billboard,
 * Includes methods to create billboard types and to return the JPanel to the calling billboard class
 * <p>
 * Types of Billboards:
 * 1 - Message, picture and information
 * 2 - Message and Information
 * 3 - Message and Picture
 * 4 - Message
 * 5 - Picture and Information
 * 6 - Picture
 * 7 - Information
 * 8 - Billboard server not available - Default with no applicable billboard above
 */
public class Panel {

    JPanel billboardPanel = new JPanel(); // Initialize The JPanel
    private double xRes; //Full screen width
    private double yRes; //Full screen height

    // Initialize Default colour Values
    String billboardBackground = "#FFFFFF"; // White
    String messageColour = "#000000"; // Black
    String informationColour = "#000000"; // Black

    // Non-default values, either NULL or input value
    String message;
    String pictureUrl;
    String pictureData;
    String information;

    /**
     * Construct the Panel class with the input billboard data from a TreeMap, determines the type of billboard
     * and calls the appropriate JPanel creation class.
     *
     * @param billboard
     */
    public Panel(TreeMap<String, String> billboard) {

        // Get full screen Resolution and set variables
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

        // Set non-default variables - if key does not exist the default value will remain null
        if (billboard.containsKey("message")) {
            message = billboard.get("message");
        }
        if (billboard.containsKey("pictureUrl")) {
            pictureUrl = billboard.get("pictureUrl");
        }
        if (billboard.containsKey("pictureData")) {
            pictureData = billboard.get("pictureData");
        }
        if (billboard.containsKey("information")) {
            information = billboard.get("information");
        }

        // testing code example;
        // createDefault();

        // Determine billboard type and call method to create appropriate panel
        if (message != null) {
            if (information != null && (pictureData != null || pictureUrl != null)) {
                // Settings for 1 - Message, picture and information
                createMPI();
            } else if (information != null) {
                // Settings for 2 - Message and Information
                createMI();
            } else if (pictureData != null || pictureUrl != null) {
                // Settings for 3 - Message and Picture
                createMP();
            } else {
                // Settings for 4 - Message
                createM();
            }
        } else if (pictureData != null || pictureUrl != null) {
            if (information != null) {
                // Settings for 5 - Picture and Information
                createPI();
            } else {
                // Settings for 6 - Picture
                createP();
            }
        } else if (information != null) {
            // Settings for 7 - Information
            createI();
        } else if (true) { // billboardNow.isDefault()
            // Settings for 8 - Billboard server not available
            createDefault();
        }
        //End Non-testing code code

    }

    /**
     * Creates a billboard of type; Message, Picture and Information
     */
    private void createMPI() {
        /**
         * If message, picture and information are all present, all three should be drawn.
         * The picture should be drawn in the centre, but this time at 1/3 of screen width and screen height
         * (once again, scaled to preserve aspect ratio).
         * The message should be sized and centred to fit in the gap between the top of the picture
         * and the top of the screen.
         * The information should be sized and centred to fit in the gap between the bottom of the picture
         * and the bottom of the screen.
         */

        billboardPanel.setLayout(new BorderLayout());

        // Image SETUP
        BufferedImage image = null; // null initialisation
        BufferedImage scaledImage = null; // null initialisation

        try {
            image = produceImageBuffer(pictureUrl, pictureData); // Get image from URL or base 64 Data
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Scale input image to a third
        scaledImage = scaleThird(image);

        // Get scaled image size
        int scaledWidth = scaledImage.getWidth();
        int scaledHeight = scaledImage.getHeight();

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension((int) xRes, (int) yRes/3));

        // Create message text and scale to billboard width
        JLabel topMessage = new JLabel(message, SwingConstants.CENTER);
        topMessage.setPreferredSize(new Dimension((int) xRes, scaledHeight));
        int messageFontSize = (int) (scaleMessageFont(topMessage) * 0.95); // Get font size to width of screen, reduce slightly
        topMessage.setFont(new Font("Serif", Font.BOLD, messageFontSize)); // Set new font size

        // Resolve overlapping text issue
        topMessage.setText("<html><div style='text-align: center;'>" + message + "</div></html>"); // text fix?

        topMessage.setForeground(Color.decode(messageColour)); // Set message colour
        topPanel.add(topMessage); // add message to panel

        // Center Image
        JPanel centrePanel = new JPanel(new GridBagLayout()); // GridBagLayout will center the image
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        centrePanel.setSize(scaledWidth, scaledHeight);
        centrePanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));

        // ADD IMAGE
        centrePanel.add(imageLabel);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        String informationWrap = "<html><div style='text-align: center;'>"+information+"</div></html>";
        JLabel bottomText = new JLabel(informationWrap);

        bottomText.setPreferredSize(new Dimension((int) xRes,(int) yRes/3));

        Font labelFont = bottomText.getFont();
        String labelText = bottomText.getText();

        int componentWidth = (int) xRes;

        int componentHeight = (int) yRes/3;
        int informationFontSize = getFontSizeToFitBoundingRectangle(componentWidth, componentHeight,
                bottomText, labelFont, labelText);

        // Check for information font size to be smaller than the message
        if (informationFontSize >= (messageFontSize * 0.8)) {
            informationFontSize = (int) (messageFontSize * 0.8); // Set Info text to 80% of Message size
        }

        bottomText.setFont(new Font(labelFont.getName(), Font.PLAIN, informationFontSize));

        // Set information text colour
        bottomText.setForeground(Color.decode(messageColour));
        bottomPanel.add(bottomText);

        billboardPanel.add(topPanel, BorderLayout.PAGE_START);
        billboardPanel.add(centrePanel, BorderLayout.CENTER);
        billboardPanel.add(bottomPanel, BorderLayout.PAGE_END);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground)); // test color
        bottomPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

    /**
     * Creates a billboard of type; Message and Information
     */
    private void createMI() {
        /**
         * If only message and information are present,
         * the message text should be sized to fit in the top half of the screen and
         * the information text sized to fit in the bottom half of the screen.
         */

        billboardPanel.setLayout(new BorderLayout());

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension((int) xRes,(int) yRes/2));

        // Create message text and scale to billboard width
        JLabel topMessage = new JLabel(message, SwingConstants.CENTER);
        topMessage.setPreferredSize(new Dimension((int) xRes, (int) yRes/2));
        int messageFontSize = (int) (scaleMessageFont(topMessage) * 0.95); // Get font size to width of screen, reduce slightly
        topMessage.setFont(new Font("Serif", Font.BOLD, messageFontSize)); // Set new font size

        // Resolve overlapping text issue
        topMessage.setText("<html><div style='text-align: center;'>" + message + "</div></html>"); // text fix?

        topMessage.setForeground(Color.decode(messageColour)); // Set message colour
        topPanel.add(topMessage);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        String informationWrap = "<html><div style='text-align: center;'>"+information+"</div></html>";
        JLabel bottomText = new JLabel(informationWrap);

        bottomText.setPreferredSize(new Dimension((int) xRes,(int) yRes/2));

        Font labelFont = bottomText.getFont();
        String labelText = bottomText.getText();

        int componentWidth = (int) xRes;

        int componentHeight = (int) yRes/2;
        int informationFontSize = getFontSizeToFitBoundingRectangle(componentWidth, componentHeight,
                bottomText, labelFont, labelText);

        // Check for information font size to be smaller than the message
        if (informationFontSize >= (messageFontSize * 0.8)) {
            informationFontSize = (int) (messageFontSize * 0.8); // Set Info text to 80% of Message size
        }

        bottomText.setFont(new Font(labelFont.getName(), Font.PLAIN, informationFontSize));

        // Set information text colour
        bottomText.setForeground(Color.decode(messageColour));
        bottomPanel.add(bottomText);

        billboardPanel.add(topPanel, BorderLayout.PAGE_START);
        billboardPanel.add(bottomPanel, BorderLayout.PAGE_END);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        bottomPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

    /**
     * Creates a billboard of type; Message and Picture
     */
    private void createMP() {
        /**
         * If only message and picture are present, the picture should be the same size as before,
         * but instead of being drawn in the centre of the screen,
         * it should be drawn in the middle of the bottom 2/3 of the screen.
         * The message should then be sized to fit in the remaining space between the top of the image
         * and the top of the screen and placed in the centre of that gap.
         */

        billboardPanel.setLayout(new BorderLayout());

        // IMAGE SETUP
        BufferedImage image = null; // null initialisation
        BufferedImage scaledImage = null; // null initialisation
        try {
            image = produceImageBuffer(pictureUrl, pictureData); // Get image from URL or base 64 Data
        } catch (IOException e) {
            e.printStackTrace();
        }

        scaledImage = scaleHalf(image);
        int scaledWidth = scaledImage.getWidth();
        int scaledHeight = scaledImage.getHeight();

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension((int) xRes, (int) yRes/3)); // set one third screen height

        // Create message text and scale to billboard width
        JLabel topMessage = new JLabel(message, SwingConstants.CENTER);
        topMessage.setPreferredSize(new Dimension((int) xRes, (int) yRes/3));
        int fontSize = (int) (scaleMessageFont(topMessage) * 0.95); // Get font size to width of screen, reduce slightly
        topMessage.setFont(new Font("Serif", Font.BOLD, fontSize)); // Set new font size

        // Resolve overlapping text issue
        topMessage.setText("<html><div style='text-align: center;'>" + message + "</div></html>"); // text fix?

        topMessage.setForeground(Color.decode(messageColour)); // Set message colour
        topPanel.add(topMessage);

        // Center Image
        JPanel centrePanel = new JPanel(new GridBagLayout()); // GridBagLayout will center the image
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        centrePanel.setSize(scaledWidth, scaledHeight);
        centrePanel.setPreferredSize(new Dimension(scaledWidth, (int) yRes * 2/3)); // set two third screen height

        // ADD IMAGE
        centrePanel.add(imageLabel);

        billboardPanel.add(topPanel, BorderLayout.PAGE_START);
        billboardPanel.add(centrePanel, BorderLayout.CENTER);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);
    }

    /**
     * Creates a billboard of type; Message
     */
    private void createM() {
        /**
         * If only message is present, the message should be displayed almost as large as possible,
         * within the constraints that the text cannot be broken across multiple lines and it must all fit on the screen.
         */

        billboardPanel.setLayout(new BorderLayout());

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension((int)xRes,(int)yRes));

        // Create message text and scale to billboard width
        JLabel topMessage = new JLabel(message, SwingConstants.CENTER);
        topMessage.setPreferredSize(new Dimension((int) xRes, (int) yRes));
        int fontSize = (int) (scaleMessageFont(topMessage) * 0.95); // Get font size to width of screen, reduce slightly
        topMessage.setFont(new Font("Serif", Font.BOLD, fontSize)); // Set new font size

        // Resolve overlapping text issue
        topMessage.setText("<html><div style='text-align: center;'>" + message + "</div></html>"); // text fix?

        // Set message text colour, add label and panel
        topMessage.setForeground(Color.decode(messageColour));
        topPanel.add(topMessage);
        billboardPanel.add(topPanel, BorderLayout.CENTER);

        // Set background colours
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);
    }

    /**
     * Creates a billboard of type; Picture and Information
     */
    private void createPI() {
        /**
         * If only picture and information are present, the picture should be the same size as before,
         * but instead of being drawn in the centre of the screen,
         * it should be drawn in the middle of the top 2/3 of the screen.
         * The information text should then be sized to fit in the remaining space between the bottom of the image and
         * the bottom of the screen and placed in the centre of that gap
         * (within the constraint that the information text should not fill up more than 75% of the screen’s width.)
         */

        billboardPanel.setLayout(new BorderLayout());

        // IMAGE SETUP
        BufferedImage image = null; // null initialisation
        BufferedImage scaledImage = null; // null initialisation
        try {
            image = produceImageBuffer(pictureUrl, pictureData); // Get image from URL or base 64 Data
        } catch (IOException e) {
            e.printStackTrace();
        }

        scaledImage = scaleHalf(image); // Picture size? 50%? criteria not clear

        int scaledWidth = scaledImage.getWidth();

        // Top Image panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension(scaledWidth, (int) yRes * 2/3)); // set height top 2/3rd
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        topPanel.add(imageLabel);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        String informationWrap = "<html><div style='text-align: center;'>"+information+"</div></html>";
        JLabel bottomText = new JLabel(informationWrap);

        bottomText.setPreferredSize(new Dimension((int) xRes,(int) yRes/3));

        Font labelFont = bottomText.getFont();
        String labelText = bottomText.getText();

        int componentWidth = (int) xRes;

        int componentHeight = (int) yRes/3;
        int informationFontSize = getFontSizeToFitBoundingRectangle(componentWidth, componentHeight,
                bottomText, labelFont, labelText);

        bottomText.setFont(new Font(labelFont.getName(), Font.PLAIN, informationFontSize));

        // Set information text colour
        bottomText.setForeground(Color.decode(messageColour));
        bottomPanel.add(bottomText);

        billboardPanel.add(topPanel, BorderLayout.PAGE_START);
        billboardPanel.add(bottomPanel, BorderLayout.PAGE_END);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        bottomPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);
    }

    /**
     * Creates a billboard of type; Picture
     */
    private void createP() {
        /**
         * If only picture is present, the image should be scaled up to
         * half the width and height of the screen and displayed in the centre.
         *  Note that this scaling up should not distort the aspect ratio of the image.
         * If the screen is 1000 pixels wide and 750 pixels high, a 100x100 image should be displayed at 375x375.
         * On the other hand, a 100x50 image should be displayed at 500x250. In each case the image is scaled,
         * preserving the aspect ratio, to the largest size that can fit in a 500x375 (50% of the screen’s width and height) rectangle.
         */

        billboardPanel.setLayout(new BorderLayout());

        // IMAGE SETUP
        BufferedImage image = null; // null initialisation
        BufferedImage scaledImage = null; // null initialisation
        try {
            image = produceImageBuffer(pictureUrl, pictureData); // Get image from URL or base 64 Data
        } catch (IOException e) {
            e.printStackTrace();
        }

        scaledImage = scaleHalf(image); // Scale picture to 50% screen size

        int scaledWidth = scaledImage.getWidth();
        int scaledHeight = scaledImage.getHeight();

        // Center Image
        JPanel centrePanel = new JPanel(new GridBagLayout()); // GridBagLayout will center the image
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        centrePanel.setSize(scaledWidth, scaledHeight);
        centrePanel.setPreferredSize(new Dimension(scaledWidth, scaledHeight));

        // ADD IMAGE
        centrePanel.add(imageLabel);
        billboardPanel.add(centrePanel, BorderLayout.CENTER);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

    /**
     * Creates a billboard of type; Information
     */
    private void createI() {
        /**
         * If only information is present, the text should be displayed in the centre,
         * with word wrapping and font size chosen so that the text fills up no more than 75% of the screen’s width
         * and 50% of the screen’s height.
         */
        billboardPanel.setLayout(new BorderLayout());
        // Bottom Text
        JPanel centrePanel = new JPanel(new GridBagLayout());

        // Enable text wrapping with the html tags
        String informationWrap = "<html><div style='text-align: center;'>"+information+"</div></html>";
        JLabel bottomText = new JLabel(informationWrap);

        // Text scaling to < 75% width and 50% height
        // Centred, with word wrap
        double maxWidth = xRes * 0.75;
        double maxHeight = yRes * 0.5;

        // Set JLabel size
        bottomText.setPreferredSize(new Dimension((int) maxWidth,(int) maxHeight));

        Font labelFont = bottomText.getFont();
        String labelText = bottomText.getText();

        int stringWidth = bottomText.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = (int) maxWidth;

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        int componentHeight = (int) maxHeight;

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = getFontSizeToFitBoundingRectangle(componentWidth, componentHeight,
                                                                bottomText, labelFont, labelText);
        // Set the label's font size to the newly determined size.
        bottomText.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));

        bottomText.setForeground(Color.decode(messageColour)); // Set text colour
        centrePanel.add(bottomText);
        billboardPanel.add(centrePanel, BorderLayout.CENTER);

        // Set Panel Backgrounds
        billboardPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);
    }

    /**
     *
     * @param rectangleWidth
     * @param rectangleHeight
     * @param label
     * @param font
     * @param text
     * @return fontSizeToSet
     * Useful resources: http://www.java2s.com/Code/Java/Swing-JFC/GetMaxFittingFontSize.htm
     */
    private int getFontSizeToFitBoundingRectangle(int rectangleWidth, int rectangleHeight, JLabel label, Font font, String text){
        int minSize = 0;
        int maxSize = 288;
        int fontSizeToSet = font.getSize(); // init current size
        // iterate through, drawing max and min closer to the required text size
        while (maxSize - minSize > 2) {
            // get font metrics for the current size
            FontMetrics fontMetrics = label.getFontMetrics(new Font(font.getName(), font.getStyle(), fontSizeToSet) );
            // current font & box details
            int fontStringWidth = fontMetrics.stringWidth(text);
            int fontLineHeight = fontMetrics.getHeight(); // line height including leading + ascent to descent
            int numberOfLines = (int) Math.ceil( (double) fontStringWidth / (double) rectangleWidth);
            int totalHeightOfLines = (int) Math.ceil( (double) numberOfLines * (double) fontLineHeight);
            // check if max needs to be decreased or min increased
            if ((fontStringWidth > (rectangleWidth * numberOfLines)) || (totalHeightOfLines > rectangleHeight)) {
                // reduce maxSize to current font size
                maxSize = fontSizeToSet;
            }
            else {
                // increase minSize to current font size
                minSize = fontSizeToSet;
            }
            // new font size set to average max & min
            fontSizeToSet = (maxSize + minSize) / 2;
        }
        return fontSizeToSet;
    }

    /**
     * Get a font size to scale the message text for filling the billboard width
     * @param messageLabel
     * @return newFontSize
     */
    private int scaleMessageFont(JLabel messageLabel) {
        // Scale message font to almost screen width size on one line
        // Messages are Serif styled and BOLD, check based on these factors

        messageLabel.setFont(new Font("Serif", Font.BOLD, 12));
        Font messageFont = messageLabel.getFont();
        String messageText = messageLabel.getText();
        int stringWidth = messageLabel.getFontMetrics(messageFont).stringWidth(messageText);
        double widthRatio = xRes / (double)stringWidth;
        int newFontSize = (int)(messageFont.getSize() * widthRatio);

        return newFontSize; // Return font size
    }

    /**
     * Creates a billboard of type; Billboard for no server connection error
     */
    private void createDefault() {

        billboardPanel.setLayout(new BorderLayout());

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        //topPanel.setBorder(BorderFactory.createTitledBorder("Debug: Top Panel"));
        topPanel.setPreferredSize(new Dimension((int)xRes,(int)yRes));

        // Create message text and scale to billboard width
        JLabel topMessage = new JLabel(message, SwingConstants.CENTER);
        topMessage.setPreferredSize(new Dimension((int) xRes, (int) yRes));
        int fontSize = (int) (scaleMessageFont(topMessage) * 0.95); // Get font size to width of screen, reduce slightly
        topMessage.setFont(new Font("Serif", Font.BOLD, fontSize)); // Set new font size

        // Resolve overlapping text issue
        topMessage.setText("<html><div style='text-align: center;'>No connection to Billboard Server. Attempting to connect...</div></html>");
        topMessage.setForeground(Color.WHITE);
        topPanel.setBackground(Color.decode("#700000"));
        topPanel.add(topMessage);

        billboardPanel.add(topPanel);
    }

    /**
     * Scale image to 50% of screen size
     * @param image
     * @return BufferedImage
     */
    private BufferedImage scaleHalf(BufferedImage image) {

        double aspectRatio, scaledHeight, scaledWidth;
        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();

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

        // Create blank image
        BufferedImage scaledImage = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_ARGB);

        // Scale source image to Buffered Image size
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, (int) scaledWidth, (int) scaledHeight, null);

        return scaledImage;
    }

    /**
     * Scale image to a third of screen size on largest relative axis
     * @param image
     * @return BufferedImage
     */
    private BufferedImage scaleThird(BufferedImage image) {

        double aspectRatio, scaledHeight, scaledWidth;
        double sourceWidth = image.getWidth();
        double sourceHeight = image.getHeight();

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

        // Create blank image
        BufferedImage scaledImage = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_ARGB);

        // Scale source image to Buffered Image size
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, (int) scaledWidth, (int) scaledHeight, null);

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
     * Creates and returns a billboard image from either base64 image data or an Image URL
     * @param pictureUrl
     * @param pictureData
     * @return
     * @throws IOException
     */
    private BufferedImage produceImageBuffer(String pictureUrl, String pictureData) throws IOException {
        BufferedImage imageBuffer = null;

        if (pictureUrl == null) {
            // process base64 image
            byte[] imageBytes = Base64.getDecoder().decode(pictureData);
            ByteArrayInputStream imageBytesStream = new ByteArrayInputStream(imageBytes);
            imageBuffer = ImageIO.read(imageBytesStream);
        }
        else {
            // process image from URL
            URL url = new URL(pictureUrl);
            imageBuffer = ImageIO.read(url);
        }
        // Sample image goes here
        return imageBuffer;
    }

}
