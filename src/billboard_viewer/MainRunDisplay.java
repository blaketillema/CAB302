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

        // TODO fix: connect.getBillboard is not returning billboard
        // Add billboard contents to new billboard from server with connect.getBillboard()
        TreeMap billboardNowData = connect.getBillboard();
        connect.printBillboard();


        // TESTING
        // TreeMap billboardNowData = new TreeMap<>();     ;
        // billboardNowData.put("information", "random info");
        // END TESTING


        // NOT TESTING  - KEEP Below
        // Add billboard now to new billboard to display
        Billboard billboardNow = new Billboard( billboardNowData );
        DisplayBillboard display = new DisplayBillboard( billboardNow );
        display.displayCurrentBillboard();
        // END


        // ------------ TEST OUTPUTS   ----------
        // TEST BILLBOARD USING SERVER CONNECT CLASS
        System.out.println("The billboard boolean has message is: " + billboardNow.hasMessage() );
        System.out.println("The billboard boolean has information is: " + billboardNow.hasInformation() );
        System.out.println("The billboard boolean has imageData is: " + billboardNow.hasImageData() );
        System.out.println("The billboard boolean has imageURL is: " + billboardNow.hasImageURL() );
        System.out.println("The billboard boolean isDefaultL is: " + billboardNow.isDefault() );
        // get contents
        TreeMap<String, String> contentsNow = billboardNow.getBillboardContents();
        // Print contents of billboard
        System.out.println("Using getBillboardContents(), this billboard has: ");
        for (Map.Entry<String, String> entry : contentsNow.entrySet() ){
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(MessageFormat.format("Key: {0} Value: {1}", key, value));
        }
    }

}
