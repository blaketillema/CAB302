package billboard_control_panel;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.EndDocument;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Base64;
import java.util.Set;
import java.util.TreeMap;

import billboard_viewer.Billboard; // Viewer billboard class required for previewing the billboard

import billboard_server.ClientMainTests;
import billboard_server.ClientServerInterface;
import billboard_server.exceptions.ServerException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import org.w3c.dom.*;

// TODO - cleanup and documentation of BillboardControl class

public class BillboardControl {
    private JTextField billboardNameArea;
    private JButton importXMLButton;
    private JButton exportXMLButton;
    private JButton uploadImageButton;
    private JButton applyButton;
    private JButton exitButton;
    private JButton saveButton;
    private JButton previewButton;
    private JPanel billboardControl;
    private JEditorPane editorPane1;
    private JTextArea messageArea;
    private JTextArea messageColourArea;
    private JTextArea informationArea;
    private JTextArea informationColourArea;
    private JTextArea backgroundColourArea;
    private JTextArea pictureUrlArea;
    private JTextArea pictureDataArea;
    private JLabel xmlLabel;
    private JLabel messageColourLabel;
    private JLabel messageColourPreviewLabel;
    private JLabel informationColourPreviewLabel;
    private JLabel backgroundColourPreviewLabel;

    TreeMap<String, String> currentBillboard = new TreeMap<>(); // Initialize an empty TreeMap

    public BillboardControl(TreeMap editBillboard, String userName) {

        if (editBillboard != null) {
            System.out.println("Input billboard not null.");
            currentBillboard = editBillboard;
            refreshFields();
            printBillboard(currentBillboard);
        } else {
            System.out.println("Input billboard is null.");
        }
        refreshFields();

        /**
         * Button functionality
         */
        importXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String paneText = null;
                paneText = editorPane1.getText();
                System.out.println("paneText: " + paneText);

                //TreeMap billboardMap = new TreeMap<String, String>();

                // Convert string to document
                Document doc = null; // Initialize document

                DocumentBuilder documentBuilder = null;
                try {
                    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    doc = documentBuilder.parse(new ByteArrayInputStream(paneText.getBytes("UTF-8")));
                } catch (ParserConfigurationException | UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //billboardMap = parseBillboard(doc);
                currentBillboard = parseBillboard(doc);

                // Refresh text fields
                refreshFields();

                printBillboard(currentBillboard);

            }
        });

        exportXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Call XML creation method
                editorPane1.setText(createXML());

            }
        });

        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Preview Billboard");

                applyChanges();
                refreshFields();

                Billboard previewBillboard = new Billboard(currentBillboard, true);

            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeMap<String, String> body = new TreeMap<>();

                //Apply changed fields
                boolean valid = applyChanges();
                if (!valid) {
                    // Not valid - Do nothing. applyChanges will have popped an error dialog.
                    System.out.println("Billboard not saved due to an invalid format.");

                } else {
                    String nullString = ""; // for easily changing between null or "" depending on implementation elsewhere (DB side)

                    if (messageArea.getText().length() > 0) {
                        body.put("message", "" + messageArea.getText());
                    } else {
                        body.put("message", nullString);
                    }

                    if (informationArea.getText().length() > 0) {
                        body.put("information", "" + informationArea.getText());
                    } else {
                        body.put("information", nullString);
                    }

                    if (pictureDataArea.getText().length() > 0) {
                        body.put("pictureData", "" + pictureDataArea.getText());
                    } else {
                        body.put("pictureData", nullString);
                    }

                    if (pictureUrlArea.getText().length() > 0) {
                        body.put("pictureUrl", "" + pictureUrlArea.getText());
                    } else {
                        body.put("pictureUrl", nullString);
                    }

                    if (backgroundColourArea.getText().length() > 0) {
                        body.put("billboardBackground", "" + backgroundColourArea.getText());
                    } else {
                        body.put("billboardBackground", nullString);
                    }

                    if (messageColourArea.getText().length() > 0) {
                        body.put("messageColour", "" + messageColourArea.getText());
                    } else {
                        body.put("messageColour", nullString);
                    }

                    if (informationColourArea.getText().length() > 0) {
                        body.put("informationColour", "" + informationColourArea.getText());
                    } else {
                        body.put("informationColour", nullString);
                    }

                    System.out.println(body);

                    printBillboard(body);

                    // Check for a billboard Name
                    if (billboardNameArea.getText().length() < 1) {
                        // Throw error warning for no name
                        System.out.println("Billboard Name length smaller than 1!");
                        throwDialog("Please enter a Billboard Name", "No Billboard Name");
                    } else {

                        try {
                            // Check if the billboard exists
                            String existingID = Main.server.getBillboardId(billboardNameArea.getText());

                            if (existingID == null) {
                                // Billboard with this name does not exist, create new billboard
                                System.out.println("Creating new billboard...");
                                Main.server.addBillboard(billboardNameArea.getText(), body);
                                throwDialog("Newly created Billboard " + billboardNameArea.getText() + " has been saved.", "Billboard Created");
                            } else {
                                // This billboard name already exists, edit existing billboard
                                body.put("billboardName", billboardNameArea.getText());
                                System.out.println("Editing existing billboard with ID: " + existingID + " ...");
                                Main.server.editBillboard(existingID, body);
                                throwDialog("Existing Billboard with name " + billboardNameArea.getText() + " has been saved.", "Billboard Edited");
                            }
                        } catch (ServerException ex) {
                            System.out.println("Exception getting billboard ID");
                            throwDialog(ex.getMessage(), "Error");
                        }
                    }
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] wns = LoginManager.getFrames();
                for (Window wn1 : wns) {
                    wn1.dispose();
                    wn1.setVisible(false);
                }
                new MainControl(userName).main(userName);
            }
        });

        /**
         * Convert provided image file (png,jpg) to BASE64 Format
         */
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Upload an image");
                // Browse for an image
                JFileChooser fileChooser = new JFileChooser();
                int chooserState = fileChooser.showOpenDialog(null);

                if (chooserState == JFileChooser.APPROVE_OPTION) {
                    File imageFile = fileChooser.getSelectedFile();

                    String pictureData = null;

                    try {
                        FileInputStream streamReader = new FileInputStream(imageFile);
                        byte[] imageBytes = new byte[(int) imageFile.length()];
                        streamReader.read(imageBytes);

                        pictureData = Base64.getEncoder().encodeToString(imageBytes);

                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    pictureDataArea.setText(pictureData);

                } else {
                    // File chooser encountered an error
                    System.out.println("File Chooser was not approved");
                }
            }
        });
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Apply Changes
                applyChanges();
            }
        });
    }

    public void main(TreeMap inputBillboard, String userName) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Builder");
        Main.centreWindow(frame);

        frame.setContentPane(new BillboardControl(inputBillboard, userName).billboardControl);
        frame.setMinimumSize(new Dimension(500, 400)); // Set minimum size for scaling
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        refreshFields();
    }

    private static void throwDialog(String messageText, String title) {
        JOptionPane.showMessageDialog(null, messageText, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private String createXML() {
        // parse billboard treemap to XML

        applyChanges(); // Apply changes from fields, will set empty fields to null
        String xmlExport = null;

        try {
            // Setup builders
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Create root element for billboard xml
            Element billboardRoot = document.createElement("billboard");
            document.appendChild(billboardRoot);

            // Handle background colour attribute
            if (currentBillboard.get("billboardBackground") != null) {
                // Add attribute background colour to root element
                Attr backgroundAttr = document.createAttribute("background");
                backgroundAttr.setValue(currentBillboard.get("billboardBackground"));
                billboardRoot.setAttributeNode(backgroundAttr);
            }

            // Handle message and messageColour attribute
            if (currentBillboard.get("message") != null) { // Check if message exists
                // Add message element
                Element message = document.createElement("message");
                message.appendChild(document.createTextNode(currentBillboard.get("message")));

                if (currentBillboard.get("messageColour") != null) { // Check if message colour exists
                    // Add colour attribute
                    Attr messageColour = document.createAttribute("colour");
                    messageColour.setValue(currentBillboard.get("messageColour"));
                    message.setAttributeNode(messageColour);
                }
                billboardRoot.appendChild(message); // add message
            }

            // Handle Picture (URL or Data)
            if (currentBillboard.get("pictureUrl") != null) { // Add picture url attribute
                Element picture = document.createElement("picture");
                Attr pictureUrl = document.createAttribute("url");
                pictureUrl.setValue(currentBillboard.get("pictureUrl"));
                picture.setAttributeNode(pictureUrl);
                billboardRoot.appendChild(picture);
            } else {
                if (currentBillboard.get("pictureData") != null) { // Add picture data attribute
                    Element picture = document.createElement("picture");
                    Attr pictureUrl = document.createAttribute("data");
                    pictureUrl.setValue(currentBillboard.get("pictureData"));
                    picture.setAttributeNode(pictureUrl);
                    billboardRoot.appendChild(picture);
                }
            }

            // Handle information and information colour attribute
            if (currentBillboard.get("information") != null) { // Check if message exists
                // Add message element
                Element information = document.createElement("information");
                information.appendChild(document.createTextNode(currentBillboard.get("information")));

                if (currentBillboard.get("informationColour") != null) { // Check if message colour exists
                    // Add colour attribute
                    Attr informationColour = document.createAttribute("colour");
                    informationColour.setValue(currentBillboard.get("informationColour"));
                    information.setAttributeNode(informationColour);
                }
                billboardRoot.appendChild(information); // add message
            }
            // End XML elements

            // Transform XML document into a string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            xmlExport = writer.getBuffer().toString(); // assign to string

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        if (xmlExport == null) {
            xmlExport = "Error: Invalid data";
        }

        return xmlExport;
    }
    private TreeMap parseBillboard(Document doc) {
        // implementation attempt
        // Initialize possible variables;
        String billboardBackground = null;
        String message = null;
        String messageColour = null;
        String pictureUrl = null; // Picture can be a URL or Data
        String pictureData = null;
        String information = null;
        String informationColour = null;

        NodeList nodeList = doc.getElementsByTagName("billboard");

        for (int i = 0; i <= nodeList.getLength() - 1; i++) {
            // Iterate nodes in NodeList (should only be one)
            Node node = nodeList.item(i);

            // Start with billboard background attribute
            NamedNodeMap attributes = node.getAttributes();

            // Check if billboard background is set, else leave null
            if (node.getAttributes().item(0).getNodeName() == "background") {
                // Background attribute name found
                billboardBackground = node.getAttributes().item(0).getNodeValue();
            } else {
                billboardBackground = null;
            }

            // Iterate child nodes
            NodeList childList = node.getChildNodes();
            //System.out.println("LENGTH: " + childList.getLength());
            for (int k = 0; k <= childList.getLength() - 1; k++) {
                // iterate through child nodes and find values
                Node childNode = childList.item(k);

                // Check node
                //System.out.println("getNodeName: "+ childNode.getNodeName());
                //System.out.println("getNodeValue: "+ childNode.getNodeValue());

                if (childNode.getNodeName() == "message") {
                    //
                    message = childNode.getTextContent();
                    // Check for message colour attribute
                    if (childNode.getAttributes().item(0).getNodeName() == "colour") {
                        // Assign colour
                        messageColour = childNode.getAttributes().item(0).getNodeValue();
                    }
                } else {
                    if (childNode.getNodeName() == "picture") {
                        //
                        //System.out.println("PICTURE");
                        //System.out.println("PICTURE value: "+ childNode.getNodeValue());

                        if (childNode.getAttributes().item(0).getNodeName() == "url") {
                            // Assign picture URL
                            pictureUrl = childNode.getAttributes().item(0).getNodeValue();
                        } else {
                            if (childNode.getAttributes().item(0).getNodeName() == "data") {
                                // assign picture Data
                                pictureData = childNode.getAttributes().item(0).getNodeValue();
                            }
                        }
                    } else {
                        if (childNode.getNodeName() == "information") {
                            //
                            information = childNode.getTextContent();

                            if (childNode.getAttributes().item(0).getNodeName() == "colour") {
                                // Assign colour
                                informationColour = childNode.getAttributes().item(0).getNodeValue();
                            }
                        }
                    }
                }
            }
        }

        // Parse XML into HashMap
        TreeMap xmlTreeMap = new TreeMap<String, String>();

        xmlTreeMap.put("billboardBackground", billboardBackground);
        xmlTreeMap.put("message", message);
        xmlTreeMap.put("messageColour", messageColour);
        xmlTreeMap.put("pictureUrl", pictureUrl);
        xmlTreeMap.put("pictureData", pictureData);
        xmlTreeMap.put("information", information);
        xmlTreeMap.put("informationColour", informationColour);

        return xmlTreeMap;
    }

    /**
     * Refreshes text area fields when changes are made or applied to the billboard data
     */
    private void refreshFields() {
        // Refresh all text fields
        messageArea.setText(currentBillboard.get("message"));
        messageColourArea.setText(currentBillboard.get("messageColour"));
        informationArea.setText(currentBillboard.get("information"));
        informationColourArea.setText(currentBillboard.get("informationColour"));
        backgroundColourArea.setText(currentBillboard.get("billboardBackground"));
        pictureUrlArea.setText(currentBillboard.get("pictureUrl"));
        pictureDataArea.setText(currentBillboard.get("pictureData"));

        // If billboard name set, such as editing a billboard
        if (currentBillboard.containsKey("billboardName")) {
            //
            billboardNameArea.setText(currentBillboard.get("billboardName"));
        }
    }

    /**
     * Validates all input HTML colour codes, returns true if colour codes are valid else will return false
     *
     * @return
     */
    private boolean validateColours() {

        if (messageColourArea.getText().length() > 0) {
            //Check message colour is valid
            try {
                Color.decode(messageColourArea.getText());
            } catch (Exception NumberFormatException) {
                ///
                System.out.println("Caught NumberFormatException...");
                throwDialog("Message Colour code format is not valid, please enter a valid HTML Colour code", "Invalid Message Colour");
                return false;
            }
        }

        if (informationColourArea.getText().length() > 0) {
            //Check information colour is valid
            try {
                Color.decode(informationColourArea.getText());
            } catch (Exception NumberFormatException) {
                ///
                System.out.println("Caught NumberFormatException...");
                throwDialog("Information Colour code format is not valid, please enter a valid HTML Colour code", "Invalid Information Colour");
                return false;
            }
        }

        if (backgroundColourArea.getText().length() > 0) {
            //Check background colour is valid
            try {
                Color.decode(backgroundColourArea.getText());
            } catch (Exception NumberFormatException) {
                ///
                System.out.println("Caught NumberFormatException...");
                throwDialog("Background Colour code format is not valid, please enter a valid HTML Colour code", "Invalid Background Colour");
                return false;
            }
        }

        return true;
    }

    /**
     * Applies the changes from JTextFields to the currentBillboard treemap,
     * Applies variables based on text field contents, assigning null if empty.
     *
     * @return
     */
    private boolean applyChanges() {
        // Initialize billboard variables as null
        String message = null;
        String messageColour = null;
        String information = null;
        String informationColour = null;
        String billboardBackground = null;
        String pictureUrl = null; // Picture can be a URL or Data
        String pictureData = null;

        if (validateColours() == false) {
            return false; // Colours are not valid, return false
        }

        // Check if text boxes have content and assign variables, else leave variable as null
        if (messageArea.getText().length() > 0) { // Check not empty
            message = messageArea.getText();
        } // else leave as null

        if (messageColourArea.getText().length() > 0) { // Check not empty
            messageColour = messageColourArea.getText();
            // Apply validated colour to preview box
            messageColourPreviewLabel.setBackground(Color.decode(messageColour));
            messageColourPreviewLabel.setForeground(Color.decode(messageColour));
            messageColourPreviewLabel.setOpaque(true);
        } // else leave as null

        if (informationArea.getText().length() > 0) { // Check not empty
            information = informationArea.getText();
        } // else leave as null

        if (informationColourArea.getText().length() > 0) { // Check not empty
            informationColour = informationColourArea.getText();
            // Apply validated colour to preview box
            informationColourPreviewLabel.setBackground(Color.decode(informationColour));
            informationColourPreviewLabel.setForeground(Color.decode(informationColour));
            informationColourPreviewLabel.setOpaque(true);
        } // else leave as null

        if (backgroundColourArea.getText().length() > 0) { // Check not empty
            billboardBackground = backgroundColourArea.getText();
            // Apply validated colour to preview box
            backgroundColourPreviewLabel.setBackground(Color.decode(billboardBackground));
            backgroundColourPreviewLabel.setForeground(Color.decode(billboardBackground));
            backgroundColourPreviewLabel.setOpaque(true);
        } // else leave as null

        if (pictureUrlArea.getText().length() > 0) { // Check not empty
            pictureUrl = pictureUrlArea.getText();
        } // else leave as null

        if (pictureDataArea.getText().length() > 0) { // Check not empty
            pictureData = pictureDataArea.getText();
        } // else leave as null

        // Apply changes into to the TreeMap object
        currentBillboard.put("billboardBackground", billboardBackground);
        currentBillboard.put("message", message);
        currentBillboard.put("messageColour", messageColour);
        currentBillboard.put("pictureUrl", pictureUrl);
        currentBillboard.put("pictureData", pictureData);
        currentBillboard.put("information", information);
        currentBillboard.put("informationColour", informationColour);

        return true;
    }

    /**
     * TEST CLASS FOR PRINTING TREEMAP / BILLBOARD CONTENTS
     *
     * @param billboardMap
     */
    public void printBillboard(TreeMap<String, String> billboardMap) {
        System.out.println("Connect: Printing Billboard...");
        Set<String> set1 = billboardMap.keySet();
        for (String key : set1) {
            System.out.println("Connect Key : " + key + "\t\t" + "Value : " + billboardMap.get(key));
        }
    }
}
