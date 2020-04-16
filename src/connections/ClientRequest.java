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
        StringBuilder sb = new StringBuilder();

        sb.append("type: ").append(this.type);
        sb.append("path: ").append(this.path);
        sb.append("sessionId: ").append(this.sessionId);
        sb.append("params: ").append(this.params);
        sb.append("data: ").append(this.data);

        return sb.toString();
    }

    public void print()
    {
        this.toString();
    }
}
