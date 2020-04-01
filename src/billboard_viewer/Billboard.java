package billboard_viewer;

import java.util.TreeMap;

public class Billboard {
    private TreeMap<String, String> billboardContents;
    private boolean hasMessage = false;
    private boolean hasInformation = false;
    private boolean hasImageData = false;
    private boolean hasImageURL = false;
    private boolean isDefault = false;

    /**
     *  Default constructor (No billboard to display)
     */
    public Billboard() {
        isDefault = true;
        TreeMap<String, String> defaultBillboard = new TreeMap<>();
        defaultBillboard.put("default", "Advertise Here!!!");
        this.billboardContents = defaultBillboard;
    }
    /**
     *
     * @param billboardContents
     */
    public Billboard(TreeMap<String, String> billboardContents) {
        this.billboardContents = billboardContents;
    }

    // TODO - fix this - something is not working -  still returns false
    //  (possibly  only when retrieving data from server in Main with connect.getBillboard();
    public final void initialise() {
        // Set attributes for this billboard
        if ( billboardContents.containsKey("message") ) {
            hasMessage = true;
        }
        if ( billboardContents.containsKey("information") ) {
            hasInformation = true;
        }
        if ( billboardContents.containsKey("pictureData") ) {
            hasImageData = true;
        }
        if ( billboardContents.containsKey("pictureURL") ) {
            hasImageURL = true;
        }
    }

    /**
     *
     * @return
     */
    public TreeMap<String, String> getBillboardContents() {
        return billboardContents;
    }

    /**
     *
     * @return
     */
    public boolean hasMessage() {
        return hasMessage;
    }

    /**
     *
     * @return
     */
    public boolean hasInformation() {
        return hasInformation;
    }

    /**
     *
     * @return
     */
    public boolean hasImageData() {
        return hasImageData;
    }

    /**
     *
     * @return
     */
    public boolean hasImageURL() {
        return hasImageURL;
    }

    /**
     *
     * @return
     */
    public boolean isDefault() {
        return isDefault;
    }

}
