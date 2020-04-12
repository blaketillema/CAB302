package Sandbox;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


//XML parsing: java.xml.parsers.DocumentBuilderFactory
//https://docs.oracle.com/en/java/javase/13/docs/api/java.xml/javax/xml/parsers/DocumentBuilderFactory.html

import java.io.File;
import java.io.IOException;

public class readXML {
    // Nothing here

    // Base64 Image String sample
    String base64Image =
            "iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAALHRFWHRDcmVhdGlvbiBUaW1lAE1vbiAxNiBNYX" +
                    "IgMjAyMCAxMDowNTo0NyArMTAwMNQXthkAAAAHdElNRQfkAxAABh+N6nQIAAAACXBIWXMAAAsSAAAL" +
                    "EgHS3X78AAAABGdBTUEAALGPC/xhBQAAADVJREFUeNp1jkEKADAIwxr//+duIIhumJMUNUWSbU2AyP" +
                    "ROFeVqaIH/T7JeRBd0DY+8SrLVPbTmFQ1iRvw3AAAAAElFTkSuQmCC";

    // XML file sample
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<billboard background=\"#0000FF\">\n" +
            "    <message colour=\"#FFFF00\">Welcome to the ____ Corporation's Annual Fundraiser!</message>\n" +
            "    <picture url=\"https://example.com/fundraiser_image.jpg\"/>\n" +
            "    <information colour=\"#00FFFF\">Be sure to check out https://example.com/ for more information.</information>\n" +
            "</billboard>";

    private String sampleFile = "SampleBillboard.xml"; // sample billboard file
    private String xmlPath = System.getProperty("user.dir") + "\\Assets\\" + sampleFile; // test file

    public readXML() {
        System.out.println("ReadXML Start");

        /*
        try {
            openFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        */

        try {
            parseBillboard();
        } catch (NullPointerException e) {
            //
        }


        System.out.println("ReadXML End");
    }

    private void openFile() throws IOException, SAXException, NullPointerException {
        // Open a test XML file
        File imageFile = new File(xmlPath);
        DocumentBuilder builder = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //factory.setValidating(true); // ?
        //factory.setIgnoringElementContentWhitespace(true); // ?

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
           // Exception handling
        }

        File xmlFile = new File(xmlPath);
        Document doc = builder.parse(xmlFile);

        /*
         * https://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
         */
        doc.getDocumentElement().normalize();

        // Root element
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("billboard");

        int temp = 0;
        Node nNode = nList.item(temp);
        System.out.println("\nCurrent Element: " + nNode.getNodeName());

        Element eElement = (Element) nNode;

        // Get Billboard Background
        String background = eElement.getAttribute("background");
        System.out.println("background: "+background);

        String message = eElement.getElementsByTagName("message").item(0).getTextContent();
        System.out.println("message: "+message);

        //String colour = eElement.getNextSibling().getTextContent();

        //String colour = eElement.getAttributeNode();

        String colour = eElement.getAttribute("colour");
        System.out.println("colour: "+colour);


        String picture = eElement.getElementsByTagName("picture").item(0).getTextContent();
        System.out.println("picture: "+picture);

        String information = eElement.getElementsByTagName("information").item(0).getTextContent();
        System.out.println("information: "+information);

        System.out.println("nList Length: " + nList.getLength());

        for (int j = 0; j <= nList.getLength(); j+=1) {
            //
            System.out.println("J: "+j);
            Node nlistNode = nList.item(j);

            printNode(nlistNode);
            printAttributes(nlistNode);

            printChildNode(nlistNode);

            Node firstNode = nlistNode.getFirstChild();
            printNode(firstNode);

            Node lastNode = nlistNode.getLastChild();
            printNode(lastNode);
        }
    }

    private void parseBillboard() {
        // implementation attempt
        // Initialize possible variables;
        String billboardBackground = null;
        String message = null;
        String messageColour = null;
        String pictureUrl = null; // Picture can be a URL or Data
        String pictureData = null;
        String information = null;
        String informationColour = null;

        Document doc= null;

        File imageFile = new File(xmlPath);
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            // Exception handling
        }

        File xmlFile = new File(xmlPath);
        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            System.out.println("LENGTH: " + childList.getLength());
            for (int k = 0; k <= childList.getLength() - 1; k++) {
                // iterate through child nodes and find values
                Node childNode = childList.item(k);

                // Check node
                System.out.println("getNodeName: "+ childNode.getNodeName());
                System.out.println("getNodeValue: "+ childNode.getNodeValue());

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

                // Check attributes
            }
            System.out.println("Billboard Background: " + billboardBackground);
            System.out.println("message: " + message);
            System.out.println("messageColour: " + messageColour);
            System.out.println("pictureUrl: " + pictureUrl);
            System.out.println("pictureData: " + pictureData);
            System.out.println("information: " + information);
            System.out.println("informationColour: " + informationColour);
        }
    }

    private void findAttribute() {

    }

    private void printChildNode(Node node) {
        //
        NodeList childList = node.getChildNodes();

        try {
            System.out.println("getChildList: "+ node.getChildNodes());
        } catch (NullPointerException e) {}

        try {
            System.out.println("childList.getLength: "+ childList.getLength());
        } catch (NullPointerException e) {}

        for (int k = 0; k < childList.getLength(); k+=1) {
            //
            Node childListNode = childList.item(k);
            System.out.println("k: " + k);
            printNode(childListNode);
            printAttributes(childListNode);

        }
    }

    private void printNode(Node node) {
        //
        try {
            System.out.println("getNodeName: "+ node.getNodeName());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getNodeValue: "+ node.getNodeValue());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getTextContent: "+ node.getTextContent());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getChildNodes: "+ node.getChildNodes());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getFirstChild: "+ node.getFirstChild());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getLastChild: "+ node.getLastChild());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getNodeType: "+ node.getNodeType());
        } catch (NullPointerException e) {}

        try {
            System.out.println("getAttributes: "+ node.getAttributes());
        } catch (NullPointerException e) {}

    }

    private void printAttributes(Node node) {
        NamedNodeMap nodeMap = node.getAttributes();

        try {
            System.out.println("ATTRIBUTES - getLength: "+ nodeMap.getLength());
        } catch (NullPointerException e) {}

        try {
            System.out.println("ATTRIBUTES - toString: "+ nodeMap.toString());
        } catch (NullPointerException e) {}

        try {
            System.out.println("ATTRIBUTES - item.GetNodeName: "+ nodeMap.item(0).getNodeName());
        } catch (NullPointerException e) {}

        try {
            System.out.println("ATTRIBUTES - item.GetNodeValue: "+ nodeMap.item(0).getNodeValue());
        } catch (NullPointerException e) {}

        try {
            System.out.println("ATTRIBUTES - item.GetNodeType: "+ nodeMap.item(0).getNodeType());
        } catch (NullPointerException e) {}

    }

        /*

        System.out.println("Doc: "+doc);

        Element eElement;

        System.out.println("Document Element"+doc.getDocumentElement());

        NodeList nodeList = doc.getElementsByTagName("billboard");

        System.out.println(nodeList.getLength());

        Node node = nodeList.item(0);
        System.out.println("NodeName: "+node.getNodeName());

        eElement = (Element) node;

        // billboard Element attribute
        System.out.println("Billboard Background: "+ eElement.getAttribute("background"));

        // Billboard element sub nodes?
        System.out.println("message: "+ eElement.getElementsByTagName("message"));

        //builder = new factory.newDocumentBuilder();
        //Document xmlDoc = convertXML(xmlPath);
        */

    private void openFileV2() throws IOException, SAXException {
        // Attempt two
        DocumentBuilder builder = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // Create Document from file
        File xmlFile = new File(xmlPath);
        Document doc = builder.parse(xmlFile);

        //Extract root element
        Element root = doc.getDocumentElement();

        // Examine Attributes
        doc.getAttributes();

        // Examine sub-elements
        doc.getElementsByTagName("billboard");
        doc.getChildNodes();

        System.out.println();

    }

}
