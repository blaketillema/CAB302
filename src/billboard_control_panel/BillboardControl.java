package billboard_control_panel;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.TreeMap;

//import billboard_viewer.*;

import connections.ClientMainTests;
import connections.ClientServerInterface;
import connections.exceptions.ServerException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import org.w3c.dom.*;


// TODO - cleanup and documentation of BillboardControl class
// TODO - cleanup of unused UI elements and appropriate element wrapping/visual presentation

public class BillboardControl {
    private JTextField billboardNameArea;
    private JButton importXMLButton;
    private JButton exportXMLButton;
    private JButton unused1;
    private JButton unused2;
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

    TreeMap<String, String> currentBillboard = new TreeMap<>(); // Initialize an empty TreeMap

    public BillboardControl() {
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

                //Billboard previewBillboard = new Billboard(currentBillboard, true);

            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LoginManager.server.addBillboard(billboardNameArea.getText(), ClientMainTests.randomNewBillboard());
                } catch (ServerException ex) {
                    ex.printStackTrace();
                }
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
        unused1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        unused2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // TODO - Implement image upload and conversion to BASE64
        /**
         * Convert provided image file (png,jpg) to BASE64 Format
         */
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


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

    public static void main(String[] args) {
        /* Create and display the form */
        JFrame frame = new JFrame("Billboard Builder");
        Main.centreWindow(frame);
        frame.setContentPane(new BillboardControl().billboardControl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
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
    }

    /**
     * Applies the changes from JTextFields to the currentBillboard treemap,
     * Applies variables based on text field contents, assigning null if empty.
     */
    private void applyChanges() {
        // Initialize billboard variables as null
        String message = null;
        String messageColour = null;
        String information = null;
        String informationColour = null;
        String billboardBackground = null;
        String pictureUrl = null; // Picture can be a URL or Data
        String pictureData = null;

        // Check if text boxes have content and assign variables, else leave variable as null
        if (messageArea.getText().length() > 0) { // Check not empty
            message = messageArea.getText();
        } // else leave as null
        if (messageColourArea.getText().length() > 0) { // Check not empty
            messageColour = messageColourArea.getText();
        } // else leave as null
        if (informationArea.getText().length() > 0) { // Check not empty
            information = informationArea.getText();
        } // else leave as null
        if (informationColourArea.getText().length() > 0) { // Check not empty
            informationColour = informationColourArea.getText();
        } // else leave as null
        if (backgroundColourArea.getText().length() > 0) { // Check not empty
            billboardBackground = backgroundColourArea.getText();
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        billboardControl = new JPanel();
        billboardControl.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 6, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Billboard Name:");
        billboardControl.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        billboardControl.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        billboardNameArea = new JTextField();
        billboardControl.add(billboardNameArea, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        importXMLButton = new JButton();
        importXMLButton.setText("Import XML");
        billboardControl.add(importXMLButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportXMLButton = new JButton();
        exportXMLButton.setText("Export XML");
        billboardControl.add(exportXMLButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        unused1 = new JButton();
        unused1.setText("n/a");
        billboardControl.add(unused1, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(126, 30), null, 0, false));
        unused2 = new JButton();
        unused2.setText("n/a");
        billboardControl.add(unused2, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(153, 30), null, 0, false));
        uploadImageButton = new JButton();
        uploadImageButton.setText("Upload Image");
        billboardControl.add(uploadImageButton, new com.intellij.uiDesigner.core.GridConstraints(7, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(128, 30), null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        billboardControl.add(applyButton, new com.intellij.uiDesigner.core.GridConstraints(7, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 30), null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Exit");
        billboardControl.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        billboardControl.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        previewButton = new JButton();
        previewButton.setText("Preview");
        billboardControl.add(previewButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editorPane1 = new JEditorPane();
        editorPane1.setText("");
        billboardControl.add(editorPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 6, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Message Text");
        billboardControl.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        messageArea = new JTextArea();
        billboardControl.add(messageArea, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        messageColourArea = new JTextArea();
        billboardControl.add(messageColourArea, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        informationArea = new JTextArea();
        billboardControl.add(informationArea, new com.intellij.uiDesigner.core.GridConstraints(2, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        informationColourArea = new JTextArea();
        billboardControl.add(informationColourArea, new com.intellij.uiDesigner.core.GridConstraints(3, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        backgroundColourArea = new JTextArea();
        billboardControl.add(backgroundColourArea, new com.intellij.uiDesigner.core.GridConstraints(4, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        pictureUrlArea = new JTextArea();
        billboardControl.add(pictureUrlArea, new com.intellij.uiDesigner.core.GridConstraints(5, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Information Text");
        billboardControl.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Message Colour");
        billboardControl.add(label4, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Information Colour");
        billboardControl.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Background Colour");
        billboardControl.add(label6, new com.intellij.uiDesigner.core.GridConstraints(4, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Picture URL");
        billboardControl.add(label7, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 16), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Picture Data");
        billboardControl.add(label8, new com.intellij.uiDesigner.core.GridConstraints(6, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pictureDataArea = new JTextArea();
        billboardControl.add(pictureDataArea, new com.intellij.uiDesigner.core.GridConstraints(6, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return billboardControl;
    }
}
