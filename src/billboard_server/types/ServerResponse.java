package billboard_server.types;

import java.io.Serializable;
import java.util.TreeMap;

public class ServerResponse implements Serializable {
    public boolean success = true;
    public String status = "OK";
    public TreeMap<String, Object> data;

    public ServerResponse() {
        this.data = new TreeMap<>();
    }

    // debugging
    public String toString() {
        return String.format("success: %s, status: %s\ndata: %s\n-------------------------",
                this.success, this.status, this.data);
    }

    public void print()
    {
        System.out.println(this.toString());
    }
}
