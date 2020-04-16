package connections;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ServerResponse implements Serializable
{
    String status = "UNHANDLED ERROR";
    TreeMap<String, TreeMap<String, String>> data;

    public ServerResponse()
    {
        this.data = new TreeMap<>();
    }

    public String toString()
    {
        try{
            // Map.Entry<String, TreeMap<String, String>> head = data.firstEntry();
            return String.format("status = %s, data head = %s", this.status, this.data);
        } catch (Exception e) {
            return String.format("status = %s, data = null", this.status);
        }
    }

    public void print()
    {
        System.out.println("status: " + this.status);

        System.out.println("data: ");

        try
        {
            //get all entries
            Set<Map.Entry<String, TreeMap<String, String>>> entries = this.data.entrySet();


            //using for loop
            for(Map.Entry<String, TreeMap<String, String>> entry : entries){
                System.out.println( entry.getKey() + "=>" + entry.getValue() );
            }
        }
        catch (NullPointerException ignored)
        {
            System.out.println("none");
        }
    }

    public static ServerResponse buildResponse(String status, String[][] data)
    {
        ServerResponse response = new ServerResponse();
        response.status = status;

        if (data != null)
        {
            TreeMap<String, String> map = new TreeMap<>();

            for (String[] args : data)
            {
                map.put(args[0], args[1]);
            }
            response.data.put("data", map);
        }

        return response;
    }
}
