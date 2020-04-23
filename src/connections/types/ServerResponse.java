package connections.types;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ServerResponse implements Serializable
{
    public String status = "UNKNOWN";
    public TreeMap<String, TreeMap<String, String>> data;

    public ServerResponse()
    {
        this.data = new TreeMap<>();
    }

    // debugging
    public String toString()
    {
        return String.format("status: %s\ndata: %s\n-------------------------",
                this.status, this.data);
    }

    public void print()
    {
        System.out.println(this.toString());
    }
}
