package billboard_viewer;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the main class to run the program
 */
public class RunDisplayBillboard {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // get billboard data once initially from server

        // display Current Billboard

        // Every 15 seconds, refresh billboard data


        // Refresh the billboard display (if new data)




        // ------------ TESTING  ----------
        // TEST MAP TO PASS IN
        TreeMap<String, String> testContents = new TreeMap<>();
        testContents.put("message", "Welcome to the ____ Corporation's Annual Fundraiser!");
        testContents.put("base64Image", "\"iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAALHRFWHRDcmVhdGlvbiBUaW1lAE1vbiAxNiBNYX\" +\n" +
                "                    \"IgMjAyMCAxMDowNTo0NyArMTAwMNQXthkAAAAHdElNRQfkAxAABh+N6nQIAAAACXBIWXMAAAsSAAAL\" +\n" +
                "                    \"EgHS3X78AAAABGdBTUEAALGPC/xhBQAAADVJREFUeNp1jkEKADAIwxr//+duIIhumJMUNUWSbU2AyP\" +\n" +
                "                    \"ROFeVqaIH/T7JeRBd0DY+8SrLVPbTmFQ1iRvw3AAAAAElFTkSuQmCC\"");

        // TEST BILLBOARD
        CurrentBillboard billboardNow = new CurrentBillboard(testContents);
        // get contents
        TreeMap<String, String> contentsNow =  billboardNow.getBillboardContents();
        // Print contents of billboard
        System.out.println("Using getBillboard contents, this billboard has: ");
        for (Map.Entry<String, String> entry : contentsNow.entrySet() ){
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(MessageFormat.format("Key: {0} Value: {1}", key, value));
        }

    }

}
