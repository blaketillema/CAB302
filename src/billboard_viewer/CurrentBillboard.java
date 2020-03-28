package billboard_viewer;

public class CurrentBillboard extends Billboard {
    String message;
    String picture;

    /** Billboard with message only
     *
     * @param message
     */
    public void billboard(String message){
        this.message = message;
        this.picture = null;
    }

    /** Billboard with message & picture
     * @param message
     * @param picture
     */
    public void billboard(String message, String picture){
        this.message = message;
        this.picture = picture;
    }

    // billboard with...

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
