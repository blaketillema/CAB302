package connections.types;

import connections.tools.UserAuth;
import connections.Protocol;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TestDatabase {
    public TreeMap<String, TreeMap<String, String>> users = null;
    public TreeMap<String, TreeMap<String, String>> billboards = null;
    public String currentBillboard = null;

    public TestDatabase() {
        users = new TreeMap<>();
        billboards = new TreeMap<>();
    }

    public String getPermission(String user) {
        try {
            return users.get(user).get("permission");
        } catch (Exception e) {
            return Protocol.Permission.NONE;
        }
    }
}
