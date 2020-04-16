package connections.types;

import connections.tools.UserAuth;
import connections.Protocol;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static connections.engines.ServerSocket.database;

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

    private void checkPermissions(String user, String permissionNeeded) throws Exception
    {
        String permission = database.getPermission(user);

        if(!Protocol.Permission.hasOne(permission, permissionNeeded))
        {
            throw new Exception(String.format("invalid permissions: user=%s, permissions=%s, needed=%s",
                    user, permission, permissionNeeded));
        }
    }

    public void addUser(String currentUser, String newUser, TreeMap<String, String> data) throws Exception
    {
        if(!currentUser.equals("admin") || this.users.containsKey("admin")) {
            checkPermissions(currentUser, Protocol.Permission.EDIT_USERS);
        }

        TreeMap<String, String> newData = new TreeMap<>();
        newData.put("hash", UserAuth.hashAndSalt(data.get("hash"),data.get("salt")));
        newData.put("salt", data.get("salt"));
        newData.put("permission", data.get("permission"));

        this.users.put(newUser, newData);
    }

    public void addBillboard(String user, String name, TreeMap<String, String> data) throws Exception
    {
        checkPermissions(user, Protocol.Permission.CREATE_BILLBOARDS);

        this.billboards.put(name, data);
        this.currentBillboard = name;
    }

    @Deprecated
    public void addBillboards(String user, TreeMap<String, TreeMap<String, String>> data) throws Exception
    {
        checkPermissions(user, Protocol.Permission.CREATE_BILLBOARDS);

        //get all entries
        Set<Map.Entry<String, TreeMap<String, String>>> entries = data.entrySet();

        //using for loop
        for(Map.Entry<String, TreeMap<String, String>> entry : entries) {
            this.billboards.put(entry.getKey(), entry.getValue());
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

    public String getPermission(String user)
    {
        try {
            return users.get(user).get("permission");
        } catch (Exception e) {
            return Protocol.Permission.NONE;
        }
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
