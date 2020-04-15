package connections;

import java.util.TreeMap;

public class TestDatabase
{
    TreeMap<String, TreeMap<String, String>> users = null;
    TreeMap<String, TreeMap<String, String>> billboards = null;
    String currentBillboard = null;

    public TestDatabase()
    {
        users = new TreeMap<>();
        billboards = new TreeMap<>();
    }

    public void addUserInfo(String name, String initialHash, String salt, String permission)
    {
        TreeMap<String, String> data = new TreeMap<>();
        data.put("hash", UserAuth.hashAndSalt(initialHash, salt));
        data.put("salt", salt);
        data.put("permission", permission);

        this.users.put(name, data);
    }

    public void addBillboard(String name, TreeMap<String, String> billboard)
    {
        this.billboards.put(name, billboard);
        this.currentBillboard = name;
    }

    private String getFromDb(String user, String object) throws Exception
    {
        return this.users.get(user).get(object);
    }

    public String getSalt(String user) throws Exception
    {
        return getFromDb(user, "salt");
    }

    public String getHash(String user) throws Exception
    {
        return getFromDb(user, "hash");
    }

    public String getPermission(String user) throws Exception
    {
        return getFromDb(user, "permission");
    }

    public TreeMap<String, TreeMap<String, String>> getAllBillboard()
    {
        return this.billboards;
    }

    public TreeMap<String, String> getCurrentBillboard() throws Exception
    {
        return this.billboards.get(currentBillboard);
    }

    public TreeMap<String, String> getBillboard(String name) throws Exception
    {
        return this.billboards.get(name);
    }
}
