package billboard_server.types;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Encapsulation class to send data from the server to the client
 *
 * @author Max Ferguson
 */
public class ServerResponse implements Serializable {
    public boolean success = true;
    public String status = "";
    public TreeMap<String, Object> data;

    public ServerResponse() {
        this.data = new TreeMap<>();
    }

    // debugging
    public String toString() {
        return String.format("success: %s, status: %s\ndata: %s\n-------------------------",
                this.success, this.status, this.data);
    }

    public void print() {
        System.out.println(this.toString());
    }
}
