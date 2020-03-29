package billboard_viewer;

import java.util.TreeMap;

public class CurrentBillboard extends Billboard {
    private TreeMap<String, String> billboardContents;

    /**
     *
     * @param billboardContents
     */
    public CurrentBillboard(TreeMap<String, String> billboardContents) {
        this.billboardContents = billboardContents;
    }




    /**
     *
     * @return
     */
    public String getBillboardMessage() {
        if (billboardContents.containsKey("message")) {
            return billboardContents.get("message");
        }
        else return null;
    }


    /**
     *
     * @return
     */
    public TreeMap<String, String> getBillboardContents() {
        return billboardContents;
    }




    /*
    Types of Billboards:
    - Error message Type 1
    - Error message Type 2
    - Message
    - Picture
    - Information
    - Message and Picture
    - Message and Information
    - Picture and Information
    - Message, picture and information
    */

}
