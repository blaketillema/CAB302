package connections;

import java.util.TreeMap;

public class TestDatabase
{
    TreeMap<String, TreeMap<String, String>> users = null;

    public TestDatabase(){}

    public void addUserInfo(String name, String initialHash, byte[] salt)
    {
        TreeMap<String, String> data = new TreeMap<>();
        data.put("hash", initialHash);

        String newSalt = new String(salt);
        data.put("salt", newSalt);

        this.users.put(name, data);
    }

    private String getFromDb(String user, String object) throws Exception
    {
        return this.users.get(user).get("salt");
    }

    public String getSalt(String user) throws Exception
    {
        return getFromDb(user, "salt");
    }

    public String getHash(String user) throws Exception
    {
        return getFromDb(user, "hash");
    }
}
