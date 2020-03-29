package Sandbox;

import billboard_viewer.*;

import java.util.Map;
import java.util.TreeMap;

public class sandboxMain {

    public static void main(String[] args) {
        System.out.println("----- SANDBOX START -----");

        System.out.println(System.getProperty("user.dir")); // Output 'working directory' e.g. C:\java\project

        //new gui(); // GUI class for testing billboard features
        //new readXML(); // Class for figuring out parsing XML
        //billboard_viewer.serverConnect serverBillboard = new billboard_viewer.serverConnect();
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        TreeMap<String, String> billboardMap = new TreeMap<String, String>();

        /**
         * Start the Server Connect as a new thread
         */
        ServerConnect connect;
        Thread connectThread = new Thread(connect = new ServerConnect()); // Create a thread
        connectThread.start(); // Start the thread, will run every 15 seconds

        /**
         * Pull billboard TreeMap information from running thread
         */
        billboardMap = connect.getBillboard(); // get the current TreeMap
        connect.printBillboard(); // Print the current TreeMap to terminal

        /**
         * Get billboard every 15 seconds
         */
        while(true) {
            //
            treeMap = connect.getBillboard();
            //System.out.println("The value for the background key is " + treeMap.get("billboardBackground"));
            //System.out.println("The value for the message key is " + treeMap.get("message"));
            connect.printBillboard();
            try {
                Thread.sleep(15000);
                //
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("----- SANDBOX END -----");
    }


}
