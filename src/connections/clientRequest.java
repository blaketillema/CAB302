package connections;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class clientRequest implements Serializable
{
    public String type = null;
    public String path = null;
    public String sessionId = null;
    public TreeMap<String, String> params = null;
    public TreeMap<String, String> data = null;

    public String toString()
    {
        return this.type;
    }

    public void print()
    {
        System.out.println("type: " + this.type);
        System.out.println("path: " + this.path);
        System.out.println("sessionId: " + this.sessionId);

        System.out.println("params: ");

        try
        {
            //get all entries
            Set<Map.Entry<String, String>> param_entries = this.params.entrySet();

            //using for loop
            for(Map.Entry<String, String> entry : param_entries){
                System.out.println( entry.getKey() + "=>" + entry.getValue() );
            }

        } catch (NullPointerException ignored) {
            System.out.println("none");
        }

        System.out.println("data: ");

        try
        {
            //get all entries
            Set<Map.Entry<String, String>> data_entries = this.data.entrySet();

            //using for loop
            for(Map.Entry<String, String> entry : data_entries){
                System.out.println( entry.getKey() + "=>" + entry.getValue() );
            }
        } catch (NullPointerException ignored) {
            System.out.println("none");
        }


    }
}
