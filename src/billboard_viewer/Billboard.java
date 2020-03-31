package billboard_viewer;

import java.util.TreeMap;

public class Billboard {
    private TreeMap<String, String> billboardContents;

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

    public String getBilboardType() {
        if ( billboardContents.containsKey("message") && billboardContents.containsKey("information")
                && (billboardContents.containsKey("pictureUrl") || billboardContents.containsKey("pictureData")) ) {
            // Message, picture and information
            return "messagepictureinformation";
        }
        if (billboardContents.containsKey("information")
                && (billboardContents.containsKey("pictureUrl") || billboardContents.containsKey("pictureData"))) {
            // Picture and information
            return "pictureinformation";
        }
        if (billboardContents.containsKey("information")
                && (billboardContents.containsKey("message"))) {
            // Message and information
            return "messageinformation";
        }
        if (billboardContents.containsKey("message")
                && (billboardContents.containsKey("pictureUrl") || billboardContents.containsKey("pictureData"))) {
            // Message and picture
            return "messagepicture";
        }
        if (billboardContents.containsKey("pictureData")) {
            //  Picture
            return "picture";
        }
        if (billboardContents.containsKey("message")) {
            // Message
            return "message";
        }
        if (billboardContents.containsKey("information")){
            // Information
            return "information";
        }
        // TODO finish implementing getBilboardType() for Error - No Billboard and Error - Billboard server not avail
        if (!billboardContents.containsKey("")){
            // No billboard
            return "Error - No billboard";
        }
        if (!billboardContents.containsKey("server")){
            // Server unavailable
            return "Error - Billboard server not available";
        }
        else {
            return "defaultbillboard";
        }
    }
}

        /*  Types of Billboards:
        - Error - No billboard
        - Error - Billboard server not available
        x Message
        x Picture
        x Information
        x Message and Picture
        x Message and Information
        x Picture and Information
        x Message, picture and information
        */