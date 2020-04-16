package connections;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static connections.ServerSocket.database;

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

    private void addUserInfo(String name, String initialHash, String salt, String permission) throws Exception
    {
        if (name == null || initialHash == null || salt == null || permission == null)
        {
            throw new Exception(name + initialHash + salt + permission);
        }

        TreeMap<String, String> data = new TreeMap<>();
        data.put("hash", UserAuth.hashAndSalt(initialHash, salt));
        data.put("salt", salt);
        data.put("permission", permission);

        this.users.put(name, data);
    }

    public void addUsers(TreeMap<String, TreeMap<String, String>> data) throws Exception
    {
        //get all entries
        Set<Map.Entry<String, TreeMap<String, String>>> entries = data.entrySet();

        //using for loop
        for(Map.Entry<String, TreeMap<String, String>> entry : entries){
            String user = entry.getKey();

            TreeMap<String, String> newData = new TreeMap<>();
            newData.put("hash", UserAuth.hashAndSalt(entry.getValue().get("hash"), entry.getValue().get("salt")));
            newData.put("salt", entry.getValue().get("salt"));
            newData.put("permission", entry.getValue().get("permission"));

            this.users.put(user, newData);
        }
    }

    public void addBillboards(TreeMap<String, TreeMap<String, String>> data) throws Exception
    {
        //get all entries
        Set<Map.Entry<String, TreeMap<String, String>>> entries = data.entrySet();

        //using for loop
        for(Map.Entry<String, TreeMap<String, String>> entry : entries) {
            this.users.put(entry.getKey(), entry.getValue());
            this.currentBillboard = entry.getKey();
        }
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

    public TreeMap<String, TreeMap<String, String>> getCurrentBillboard() throws Exception
    {
        TreeMap<String, TreeMap<String, String>> response = new TreeMap<>();
        response.put("currentBillboard", this.billboards.get(currentBillboard));
        return response;
    }

    public TreeMap<String, TreeMap<String, String>> getBillboard(String name) throws Exception
    {
        TreeMap<String, TreeMap<String, String>> response = new TreeMap<>();
        response.put("requestedBillboard", this.billboards.get(name));
        return response;
    }
}
