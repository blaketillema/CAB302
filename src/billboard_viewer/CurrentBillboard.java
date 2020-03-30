package billboard_viewer;

import java.util.TreeMap;

public class CurrentBillboard {
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
    public TreeMap<String, String> getBillboardContents() {
        return billboardContents;
    }
}
