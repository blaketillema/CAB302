package connections.tools;

import java.io.Console;
import java.util.Arrays;
import java.util.TreeMap;

public class Tools
{
    // build functions
    public static TreeMap<String, String> tMap(String key, String value)
    {
        TreeMap<String, String> data = new TreeMap<>();
        data.put(key, value);
        return data;
    }

    // build functions
    public static TreeMap<String, TreeMap<String, String>> tMap(String key, TreeMap<String, String> value)
    {
        TreeMap<String, TreeMap<String, String>> data = new TreeMap<>();
        data.put(key, value);
        return data;
    }

    public static String getPassword() throws Exception
    {
        Console console = System.console();
        if (console == null) {
            throw new Exception("Couldn't get Console instance");
        }
        return Arrays.toString(console.readPassword("Enter admin password"));
    }
}
