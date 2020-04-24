package billboard_viewer;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import connections.*;
import connections.exceptions.ServerException;

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

        // TODO may need to set this up to run every 15 seconds - thread or something
        //ClientServerInterface server;
        //Thread connectThread = new Thread((Runnable) (server = new ClientServerInterface()));
        //connectThread.start();
        ClientServerInterface server = new ClientServerInterface();
        /**
         * Refresh the billboard display (if new data)
         */

        System.out.println("\n\nattempting to get current billboard: should print something like...");
        System.out.println("{createdBy=user1, default=Advertise here!!!, message=......, schedule=1234... 2345...}\n");
        TreeMap billboardNowData = new TreeMap();
        try {
            billboardNowData = server.getCurrentBillboard();
            System.out.println("billboard from server = " + billboardNowData + "\n\n");
        } catch (ServerException e) {
            System.out.println(e.getMessage());
        }

        Billboard newBillboard = new Billboard(billboardNowData);

        /**
         * Loop Every 15 seconds to get the current billboard from the server by calling the
         * client server interface, then send TreeMap data to the billboard
         */
        TreeMap billboardTemp;
        boolean billboardClosed = false;
        while(!billboardClosed) {
            try {
                // NEW SERVER
                billboardTemp = server.getCurrentBillboard();

                billboardNowData = billboardTemp;
                newBillboard.updateBillboard(billboardNowData);

                sleep(15000); // Sleep 15 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            billboardClosed = newBillboard.getState(); // Check if billboard has been closed by esc/mouseclick
        }

        System.out.println("DEBUG: End of Main, terminating Viewer JVM");

        System.exit(0); // Terminate viewer JVM
    }
}