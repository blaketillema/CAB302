package billboard_viewer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * Set up client-server connection settings and attempt to connect and pull billboard
 *
 * Currently simulating functionality as the client-server functionality is not built
 * Will create a TreeMap from an xml - this would be part of the control panel when built
 * When built will get a billboard TreeMap from the server.
 */

public class ServerConnect implements Runnable{

    String serverAddress = null;
    String serverPort = null;

    TreeMap billboardMap = new TreeMap<String, String>(); // Store last billboard

    /**
     * Run serverConnect as a thread
     * Attempt connect to server every 15 seconds
     */
    public void run() {
        // running
        //System.out.println("Debug: runnable start");
        connect();
        while(true) {
            try {
                Thread.sleep(15000); // 15 second sleep
                connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerConnect() {
        //Initialize client server connection settings
        // Read from .props file connection settings
        //System.out.println("Debug: ServerConnect start");

    }

    /**
     * Returns the current billBoard TreeMap
     * @return
     */
    public TreeMap<String, String> getBillboard() {
        return billboardMap;
    }

    /**
     * Print the billboard treemap for debug purposes
     */
    public void printBillboard() {
        System.out.println("Connect: Printing Billboard...");
        Set<String> set1 = billboardMap.keySet();
        for (String key: set1) {
            System.out.println("Connect Key : "  + key + "\t\t" + "Value : "  + billboardMap.get(key));
        }
    }

    /**
     * Connect to the server - runs every 15 seconds
     * Stub for unimplemented feature currently simulating a server by parsing an XML file
     * Intended functionality will get a Billboard TreeMap from the server
     * Return true/false if the billboard is new
     */
    private boolean connect() {
        //System.out.println("Debug: Connect to server");
        boolean equals = false;

        // Unimplemented test
        Document newBillboardDoc = sampleInput(); // Get a sample billboard file
        TreeMap newBillboardMap = parseBillboard(newBillboardDoc); // Create TreeMap for new billboard

        // Compare TreeMaps
        if (newBillboardMap.equals(billboardMap)) {
            // Same map - do nothing
            // System.out.println("Debug: Same Billboard");
            return false; // same billboard
        } else {
            // New map - update billboardMap
            //System.out.println("Debug: New Billboard");
            billboardMap = newBillboardMap;
            return true; // New billboard
        }
    }

    /**
     * method below is for simulating functionality of not yet built client-server input
     */
    private Document sampleInput() {
        // Create a File from sample xml
        String sampleFile = "SampleBillboard.xml"; // sample billboard file
        String xmlPath = System.getProperty("user.dir") + "\\Assets\\" + sampleFile; // test file
        File newBillboardFile = new File(xmlPath);

        // Create doc from file
        Document doc = null;
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            // Exception
        }
        try {
            doc = builder.parse(newBillboardFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Parse an XML billboard Document and extract variables, create a TreeMap from variables
     * Returns the TreeMap
     * NOTE: This functionality will be moved to the Control Panel, it is in the viewer for now
     */
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

        // Debug: print variables
        /*
        System.out.println("Billboard Background: " + billboardBackground);
        System.out.println("message: " + message);
        System.out.println("messageColour: " + messageColour);
        System.out.println("pictureUrl: " + pictureUrl);
        System.out.println("pictureData: " + pictureData);
        System.out.println("information: " + information);
        System.out.println("informationColour: " + informationColour);
        */

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

}