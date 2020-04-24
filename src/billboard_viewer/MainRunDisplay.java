package billboard_viewer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import connections.*;
import connections.exceptions.ServerException;
import connections.testing.AdminAddUsers;
import connections.testing.User1AddBillboards;
import connections.testing.User3ScheduleBillboards;

import static java.lang.Thread.sleep;

/**
 * This is the main class to run the program
 */
public class MainRunDisplay {
    /**
     *
     * @param args
     */
    public static void main(String[] args) throws ServerException {

        try
        {
            System.out.println("admin adding users: user1 with create billboard permission, user3 with schedule permission:\n");
            connections.testing.AdminAddUsers.main(args);

            System.out.println("\n\nlogin as user1 and add billboards:\n");
            connections.testing.User1AddBillboards.main(args);

            System.out.println("\n\nlogin as user3 and schedule user 1's billboards (first billboard is scheduled to run now)\n");
            connections.testing.User3ScheduleBillboards.main(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        /**
         * Start the Server Connect as a new thread
         * Every 15 seconds, refresh billboard data
         */
        // TODO may need to set this up to run every 15 seconds - thread or something
        ClientServerInterface server = new ClientServerInterface();
        /**
         * Refresh the billboard display (if new data)
         */
        // Add billboard contents to new billboard from server with connect.getBillboard()

        System.out.println("\n\nattempting to get current billboard: should print something like...");
        System.out.println("{createdBy=user1, default=Advertise here!!!, message=......, schedule=1234... 2345...}\n");
        TreeMap billboardNowData = new TreeMap();
        try {
            billboardNowData = server.getCurrentBillboard();
            System.out.println("billboard from server = " + billboardNowData + "\n\n");
        } catch (ServerException e) {
            System.out.println(e.getMessage());
        }



        // TODO Remove old server connect below
        /**
         * Start the Server Connect as a new thread
         * Every 15 seconds, refresh billboard data
         */
        /*
        ServerConnect connect;
        Thread connectThread = new Thread(connect = new ServerConnect()); // Create a thread
        connectThread.start(); // Start the thread, will run every 15 seconds
        */
        /**
         * Refresh the billboard display (if new data)
         */
        // Add billboard contents to new billboard from server with connect.getBillboard()
        //TreeMap billboardNowData = connect.getBillboard();


        // TODO remove print below???
        // connect.printBillboard();

        // TESTING
        // TreeMap billboardNowData = new TreeMap<>();     ;
        // billboardNowData.put("information", "random info");
        // END TESTING


        // NOT TESTING  - KEEP THIS BELOW
        // Add billboard now to new billboard to display
        //Billboard billboardNow = new Billboard( billboardNowData );
        //DisplayBillboard display = new DisplayBillboard( billboardNow );
        //display.displayCurrentBillboard();
        // END

        /*
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
        // --------------------------------
        */

        Billboard newBillboard = new Billboard(billboardNowData);

        /**
         * Loop Every 15 seconds to check for a new billboard from the serverConnect
         * Send TreeMap data to the billboard
         */
        TreeMap billboardTemp;
        boolean billboardClosed = false;
        while(!billboardClosed) {
            try {
                // NEW SERVER
                billboardTemp = server.getCurrentBillboard();
                // TODO remove below
                // OLD SERVER
                //billboardTemp = connect.getBillboard();

                /*
                if (billboardNowData.equals(billboardTemp)) {
                    System.out.println("Debug: Same Billboard");
                    // Same billboard, do nothing
                } else {
                    // New billboard, update billboard contents
                    System.out.println("Debug: New Billboard");
                    billboardNowData = billboardTemp;
                    //billboardNow.updateBillboard(billboardNowData); // Update Billboard Contents
                    newBillboard.updateBillboard(billboardNowData); // Update Billboard Contents
                }
                 */

                billboardNowData = billboardTemp;
                newBillboard.updateBillboard(billboardNowData);

                //connect.printBillboard(); // DEBUG - print connect TreeMap
                //billboardNow.printBillboard(); // DEBUG - print billboard TreeMap
                //newBillboard.printBillboard(); // DEBUG - print billboard TreeMap

                sleep(15000); // Sleep 15 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            billboardClosed = newBillboard.getState(); // Check if billboard has been closed by esc/mouseclick
        }

        System.out.println("DEBUG: End of Main");
        // TODO - Safely terminate/end viewer application once end of main is reached
    }
}