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
    public TreeMap<String, String> auth = null;
    public TreeMap<String, String> params = null;
    public TreeMap<String, String> data = null;

    public String toString()
    {
        return this.type;
    }

    public static ClientRequest buildRequest(String type, String path, String sessionId,
                                             String[][] params, String[][] data)
    {
        ClientRequest request = new ClientRequest();

        request.type = type;
        request.path = path;
        request.sessionId = sessionId;

        if (params != null)
        {
            request.params = new TreeMap<>();

            for(String[] args : params)
            {
                request.params.put(args[0], args[1]);
            }
        }

        if (data != null)
        {
            request.data = new TreeMap<>();

            for(String[] args : data)
            {
                request.data.put(args[0], args[1]);
            }
        }

        return request;
    }

    public static void printSet(TreeMap<String, String> set)
    {
        try
        {
            //get all entries
            Set<Map.Entry<String, String>> entries = set.entrySet();

            //using for loop
            for(Map.Entry<String, String> entry : entries){
                System.out.println( entry.getKey() + "=>" + entry.getValue() );
            }

        } catch (NullPointerException ignored) {
            System.out.println("none");
        }
    }

    public void print()
    {
        System.out.println("type: " + this.type);
        System.out.println("path: " + this.path);
        System.out.println("sessionId: " + this.sessionId);

        System.out.println("params: ");
        printSet(this.params);

        System.out.println("data: ");
        printSet(this.data);

    }
}
