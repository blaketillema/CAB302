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

    public String toString()
    {
        return this.type;
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

    public static void printBigSet(TreeMap<String, TreeMap<String, String>> set)
    {
        try
        {
            //get all entries
            Set<Map.Entry<String, TreeMap<String, String>>> entries = set.entrySet();

            //using for loop
            for(Map.Entry<String, TreeMap<String, String>> entry : entries){
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
        printBigSet(this.data);

    }
}
