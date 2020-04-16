package connections;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ClientRequest implements Serializable
{
    public String type = null;
    public String path = null;
    public String sessionId = null;
    public TreeMap<String, String> params = null;
    public TreeMap<String, TreeMap<String, String>> data = null;

    // debugging
    public String toString()
    {
        return "type: " + this.type +
                "\npath: " + this.path +
                "\nsessionId: " + this.sessionId +
                "\nparams: " + this.params +
                "\ndata: " + this.data +
                "\n---------------------------";
    }

    public void print()
    {
        System.out.println(this.toString());
    }
}
