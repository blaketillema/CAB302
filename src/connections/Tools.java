package connections;

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
}
