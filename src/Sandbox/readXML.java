package Sandbox;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

        try {
            openFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
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
    }

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
