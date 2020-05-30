package billboard_viewer;

import billboard_server.ClientServerInterface;

import java.rmi.ServerException;
import java.util.TreeMap;
import static java.lang.Thread.sleep;

/**
 * This is the main class to run the program
 */
public class MainRunDisplay {

    /**
     * Set up a connection to the server using common interface class
     */
    public static ClientServerInterface server = new ClientServerInterface();

    /**
     * Set up a Billboard and connection to server, includes a loop for getting a billboard every 15 seconds.
     *
     * @param args
     */
    public static void main(String[] args) throws ServerException {

        // Initialise Billboard class and Billboard TreeMap
        TreeMap billboardNowData = new TreeMap();
        Billboard currentBillboard = new Billboard(billboardNowData);

        /** Get current billboard from server **/
        try {
            billboardNowData = server.getCurrentBillboard();
        } catch (billboard_server.exceptions.ServerException e) {
            e.printStackTrace();
        }

        /**
         * Loop Every 15 seconds to check for a new billboard from the serverConnect
         * Send TreeMap data to the billboard
         */
        boolean billboardClosed = false;
        while (!billboardClosed) {
            try {
                // Get the currently scheduled billboard from the server
                try {
                    billboardNowData = server.getCurrentBillboard();
                } catch (billboard_server.exceptions.ServerException e) {
                    e.printStackTrace();
                }

                //billboardNowData = billboardTemp;
                currentBillboard.updateBillboard(billboardNowData); // Update the displayed billboard

                sleep(15000); // Sleep 15 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            billboardClosed = currentBillboard.getState(); // Check if billboard has been closed by esc/mouseclick
        }

        System.out.println("DEBUG: End of Main");
        System.exit(0); // Signal proper termination of running application after frame is closed
    }
}