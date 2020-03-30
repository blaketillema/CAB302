package billboard_viewer;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the main class to run the program
 */
public class MainRunDisplay {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        /**
         * Start the Server Connect as a new thread
         * Every 15 seconds, refresh billboard data
         */
        ServerConnect connect;
        Thread connectThread = new Thread(connect = new ServerConnect()); // Create a thread
        connectThread.start(); // Start the thread, will run every 15 seconds

        /**
         * Refresh the billboard display (if new data)
         */
        // TODO set this screen to update along with thread
        // Add billboard contents to new billboard from server with connect.getBillboard()
        Billboard billboardNow = new Billboard( connect.getBillboard() );
        // TODO update this with conditionals for choosing class with overridden methods
        DisplayBillboard display = new DisplayBillboard(billboardNow);
        display.displayCurrentBillboard();



        // ------------ TESTING  ----------
        // TEST BILLBOARD USING NEW SERVER CONNECT CLASS

        // get contents
        TreeMap<String, String> contentsNow = billboardNow.getBillboardContents();
        // Print contents of billboard
        System.out.println("Using getBillboardContents(), this billboard has: ");
        for (Map.Entry<String, String> entry : contentsNow.entrySet() ){
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(MessageFormat.format("Key: {0} Value: {1}", key, value));
        }

        TreeMap<String, String> billboardMap = new TreeMap<String, String>();
        billboardMap = connect.getBillboard(); // get the current TreeMap
        connect.printBillboard(); // Print the current TreeMap to terminal

    }

}
