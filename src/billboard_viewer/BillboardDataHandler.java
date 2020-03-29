package billboard_viewer;


import javax.swing.text.Document;

/**
 * Connects to server and updates the XML file for the current billboard to display
 * The calls to display billboard to refresh the updates
 */

public class BillboardDataHandler{
        private String displayDefault = "<billboard> <message>No Billboard to Display</message> </billboard>";
        private String currentDisplay = displayDefault;
        //private Document currentDisplay;

        // Connect to server to refresh data to currently display
        // (need to run every 15 seconds)
        private String getServerUpdate() {
            String newData = currentDisplay;
            // connect to server and get new data

            // get new data fromm server to replace current display


            return newData;
        }

    /** Saves server update to XML file
     *
     */


}

