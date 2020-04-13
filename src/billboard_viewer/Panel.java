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

// TODO - Message and information text font size scaling, information word wrap.

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
        boolean testing = true; // DEBUG/TESTING to call billboard being developed
        if (testing) {
            // TODO - Remove testing/debug code when all panel types implemented
            //call a billboard type here to test it
            createI();
        } else {
            if (message != null) {
                if (information != null && (pictureData != null || pictureUrl != null)) {
                    // Settings for 1 - Message, picture and information
                    System.out.println("DEBUG: Panel Type 1, MPI");
                    createMPI();
                } else if (information != null) {
                    // Settings for 2 - Message and Information
                    System.out.println("DEBUG: Panel Type 2, MI");
                    createMI();
                } else if (pictureData != null || pictureUrl != null) {
                    // Settings for 3 - Message and Picture
                    System.out.println("DEBUG: Panel Type 3, MP");
                    createMP();
                } else {
                    // Settings for 4 - Message
                    System.out.println("DEBUG: Panel Type 4, M");
                    createM();
                }
            } else if (pictureData != null || pictureUrl != null) {
                if (information != null) {
                    // Settings for 5 - Picture and Information
                    System.out.println("DEBUG: Panel Type 5, PI");
                    createPI();
                } else {
                    // Settings for 6 - Picture
                    System.out.println("DEBUG: Panel Type 6, P");
                    createP();
                }
            } else if (information != null) {
                // Settings for 7 - Information
                System.out.println("DEBUG: Panel Type 7, I");
                createI();
            } else if (true) { // billboardNow.isDefault()
                // Settings for 8 - Default - No billboard to display "Advertise Here!!!"
                System.out.println("DEBUG: Panel Type 8, default");
                createDefault();
            }
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
        /**
         * If message, picture and information are all present, all three should be drawn.
         * The picture should be drawn in the centre, but this time at 1/3 of screen width and screen height
         * (once again, scaled to preserve aspect ratio).
         * The message should be sized and centred to fit in the gap between the top of the picture
         * and the top of the screen.
         * The information should be sized and centred to fit in the gap between the bottom of the picture
         * and the bottom of the screen.
         */
        // TODO - Message and information font sizing, should this scale on screen and message size
        // TODO - Input image from network location or base64 - this should be handled elsewhere in a new function?

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
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        centrePanel.setBackground(Color.decode(billboardBackground)); // test color
        bottomPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

    /**
     * Message and Information
     */
    private void createMI() {
        /**
         * If only message and information are present,
         * the message text should be sized to fit in the top half of the screen and
         * the information text sized to fit in the bottom half of the screen.
         */
        // TODO - Scale information text

        billboardPanel.setLayout(new BorderLayout());

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        //topPanel.setBorder(BorderFactory.createTitledBorder("Debug: Top Panel"));
        topPanel.setPreferredSize(new Dimension((int) xRes/2,(int) yRes/2));
        JLabel topMessage = new JLabel(message);
        topMessage.setFont(new Font("Serif", Font.BOLD, 50)); // Set font and size
        topMessage.setForeground(Color.decode(messageColour));
        topPanel.add(topMessage);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension((int) xRes/2,(int) yRes/2));
        //bottomPanel.setBorder(BorderFactory.createTitledBorder("Debug: Bottom Panel"));
        JLabel bottomText = new JLabel(information);
        bottomText.setFont(new Font("Serif", Font.PLAIN, 40)); // Set font and size
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
     * Message and Picture
     */
    private void createMP() {
        /**
         * If only message and picture are present, the picture should be the same size as before,
         * but instead of being drawn in the centre of the screen,
         * it should be drawn in the middle of the bottom 2/3 of the screen.
         * The message should then be sized to fit in the remaining space between the top of the image
         * and the top of the screen and placed in the centre of that gap.
         */
        // TODO - Scale message text

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
        topPanel.setPreferredSize(new Dimension(scaledWidth, (int) yRes/3)); // set one third screen height
        JLabel topMessage = new JLabel(message);
        topMessage.setFont(new Font("Serif", Font.BOLD, 50)); // Set font and size
        topMessage.setForeground(Color.decode(messageColour));
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
     * Message
     */
    private void createM() {
        /**
         * If only message is present, the message should be displayed almost as large as possible,
         * within the constraints that the text cannot be broken across multiple lines and it must all fit on the screen.
         */
        // TODO - Implement message scale for 'almost as large as possible' on one line

        billboardPanel.setLayout(new BorderLayout());

        // Top Message
        JPanel topPanel = new JPanel(new GridBagLayout());
        //topPanel.setBorder(BorderFactory.createTitledBorder("Debug: Top Panel"));
        topPanel.setPreferredSize(new Dimension((int)xRes,(int)yRes));

        JLabel topMessage = new JLabel(message);

        // Font Scaling?
        String fontName = "Serif";
        //int fontSize = 0;
        //int messageWidth;
        //int componentWidth;

        //double widthRatio;
        Font labelFont = topMessage.getFont();
        String labelText = topMessage.getText();

        int stringWidth = topMessage.getFontMetrics(labelFont).stringWidth(labelText);
        System.out.println("StringWidth: "+stringWidth);

        // Find out how much the font can grow in width.
        double widthRatio = xRes / (double)stringWidth;
        System.out.println("widthRatio: "+widthRatio);

        int newFontSize = (int)(labelFont.getSize() * widthRatio);

        System.out.println("FONT SIZE: "+newFontSize);
        // Set the label's font size to the newly determined size.
        topMessage.setFont(new Font("Serif", Font.BOLD, newFontSize));

        /*
        int componentHeight = topMessage.getHeight();
        System.out.println("newFontSize: "+newFontSize);
        System.out.println("componentHeight: "+componentHeight);
        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);
        fontSizeToUse = newFontSize;
         */
        //topMessage.setFont(new Font("Serif", Font.BOLD, 50)); // Set font and size

        topMessage.setForeground(Color.decode(messageColour));
        topPanel.add(topMessage);

        billboardPanel.add(topPanel, BorderLayout.CENTER);

        // Set background colours
        billboardPanel.setBackground(Color.decode(billboardBackground));
        topPanel.setBackground(Color.decode(billboardBackground));
        billboardPanel.setOpaque(true);

    }

    /**
     * Picture and Information
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
        // TODO - Information text size scaling

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
        int scaledHeight = scaledImage.getHeight();

        // Top Image panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setPreferredSize(new Dimension(scaledWidth, (int) yRes * 2/3)); // set height top 2/3rd
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        topPanel.add(imageLabel);

        // Bottom Text
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension(scaledWidth, (int) yRes * 1/3)); // set height bottom 1/3rd
        //bottomPanel.setBorder(BorderFactory.createTitledBorder("Debug: Bottom Panel"));
        JLabel bottomText = new JLabel(information);
        bottomText.setFont(new Font("Serif", Font.PLAIN, 40)); // Set font and size
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
     * Picture
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
        // TODO - Implement

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
     * Information
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
        //bottomPanel.setPreferredSize(new Dimension(scaledWidth,scaledHeight));
        //bottomPanel.setBorder(BorderFactory.createTitledBorder("Debug: Bottom Panel"));

        //bottomText.setFont(new Font("Serif", Font.PLAIN, 40)); // Set font and size

        // Enable text wrapping with the html tags
        String informationWrap = "<html>"+information+"</html>";
        JLabel bottomText = new JLabel(informationWrap);

        /*
        String labelText = bottomText.getText();

        double maxWidth = xRes * 0.75;
        double maxHeight = yRes * 0.5;
        System.out.println("Max Width: "+maxWidth);
        System.out.println("Max Height: "+maxHeight);

        bottomText.setPreferredSize(new Dimension((int) maxWidth,(int) maxHeight));

        Font labelFont = bottomText.getFont();
        int stringWidth = bottomText.getFontMetrics(labelFont).stringWidth(labelText);
        double widthRatio = maxWidth / (double)stringWidth;

        //int newFontSize = (int)(bottomText.getWidth() * widthRatio);
        int newFontSize = (int)(bottomText.getWidth() * widthRatio);

        System.out.println("widthRatio: "+widthRatio);
        System.out.println("bottomText.getWidth(): "+(int)(bottomText.getWidth()));
        System.out.println("newFontSize: "+newFontSize);

        bottomText.setFont(new Font("Serif", Font.PLAIN, newFontSize)); // Set font and size
        */

        // Text scaling to < 75% width and 50% height
        // Centred, with word wrap

        double maxWidth = xRes * 0.75;
        double maxHeight = yRes * 0.5;
        System.out.println("Max Width: "+maxWidth);
        System.out.println("Max Height: "+maxHeight);

        // Set JLabel size
        bottomText.setPreferredSize(new Dimension((int) maxWidth,(int) maxHeight));

        Font labelFont = bottomText.getFont();
        String labelText = bottomText.getText();
        System.out.println("labelFont: "+labelFont);
        System.out.println("labelText: "+labelText);

        int stringWidth = bottomText.getFontMetrics(labelFont).stringWidth(labelText);
        int componentWidth = (int) maxWidth;
        System.out.println("stringWidth: "+stringWidth);
        System.out.println("componentWidth: "+componentWidth);

        // Find out how much the font can grow in width.
        double widthRatio = (double)componentWidth / (double)stringWidth;

        int newFontSize = (int)(labelFont.getSize() * widthRatio);
        System.out.println("newFontSize: "+newFontSize);
        int componentHeight = (int) maxHeight;
        System.out.println("componentHeight: "+componentHeight);

        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = getFontSizeToFitBoundingRectangle(componentWidth, componentHeight,
                                                                bottomText, labelFont, labelText);
        // Set the label's font size to the newly determined size.
        bottomText.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        bottomText.setBackground(Color.red);
        bottomText.setOpaque(true);

        /*
        // Font Scaling?
        String fontName = "Serif";
        //int fontSize = 0;
        //int messageWidth;
        //int componentWidth;

        //double widthRatio;
        Font labelFont = topMessage.getFont();
        String labelText = topMessage.getText();

        int stringWidth = topMessage.getFontMetrics(labelFont).stringWidth(labelText);
        System.out.println("StringWidth: "+stringWidth);

        // Find out how much the font can grow in width.
        double widthRatio = xRes / (double)stringWidth;
        System.out.println("widthRatio: "+widthRatio);

        int newFontSize = (int)(labelFont.getSize() * widthRatio);

        System.out.println("FONT SIZE: "+newFontSize);
        // Set the label's font size to the newly determined size.
        topMessage.setFont(new Font("Serif", Font.BOLD, newFontSize));

        int componentHeight = topMessage.getHeight();
        System.out.println("newFontSize: "+newFontSize);
        System.out.println("componentHeight: "+componentHeight);
        // Pick a new font size so it will not be larger than the height of label.
        int fontSizeToUse = Math.min(newFontSize, componentHeight);
        fontSizeToUse = newFontSize;
         */
        //topMessage.setFont(new Font("Serif", Font.BOLD, 50)); // Set font and size


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
     * @return
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
     * Billboard for no server connection error
     */
    private void createDefault() {
        // TODO - tidy up the default to a better looking error message
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
            System.out.println("DEBUG: Picture type - BASE64");
            byte[] imageBytes = Base64.getDecoder().decode(pictureData);
            ByteArrayInputStream imageBytesStream = new ByteArrayInputStream(imageBytes);
            imageBuffer = ImageIO.read(imageBytesStream);
        }
        else {
            // process image from URL
            System.out.println("DEBUG: Picture type - URL");
            URL url = new URL(pictureUrl);
            imageBuffer = ImageIO.read(url);
        }
        // Sample image goes here
        return imageBuffer;
    }

}
