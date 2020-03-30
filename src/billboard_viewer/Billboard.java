package billboard_viewer;

import java.util.TreeMap;

public class Billboard {
    private TreeMap<String, String> billboardContents;

    public Billboard(){
    }
    /**
     *
     * @param billboardContents
     */
    public Billboard(TreeMap<String, String> billboardContents) {
        this.billboardContents = billboardContents;
    }
    /**
     *
     * @return
     */
    public TreeMap<String, String> getBillboardContents() {
        return billboardContents;
    }

    // TODO finish implementing getBilboardType()
    public String getBilboardType() {
        if ( billboardContents.containsKey("message") && billboardContents.containsKey("information")
                && (billboardContents.containsKey("pictureUrl") || billboardContents.containsKey("pictureData")) ) {
            // Message, picture and information
            return "messagepictureinformation" ;
        }
        if (billboardContents.containsKey("information")
                && (billboardContents.containsKey("pictureUrl") || billboardContents.containsKey("pictureData"))) {
            // Picture and information
            return "pictureinformation";
        }
        else {
            return "defaultbillboard";
        }
    }
}

        /*  Types of Billboards:
        - Error - No billboard
        - Error - Billboard server not available
        - Message
        - Picture
        - Information
        - Message and Picture
        - Message and Information
        - Picture and Information
        - Message, picture and information
        */