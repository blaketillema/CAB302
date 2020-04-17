package connections;

import billboard_server.Server;
import connections.exceptions.ExistingUserException;
import connections.exceptions.PermissionException;
import connections.tools.UserAuth;
import connections.types.TestDatabase;
import connections.exceptions.ServerException;

import javax.swing.*;
import java.util.*;

public class ServerDatabaseInterface {
    private TestDatabase database = new TestDatabase();

    private boolean hasPermission(String user, String... permissionNeeded) {
        String userPermission = database.getPermission(user);

        for (String needed : permissionNeeded) {
            if (Protocol.Permission.hasOne(userPermission, needed)) {
                return true;
            }
        }

        return false;
    }

    public void addBillboards(String user, TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        //get all entries
        Set<Map.Entry<String, TreeMap<String, String>>> entries = data.entrySet();

        database.currentBillboard = null;

        //using for loop
        for (Map.Entry<String, TreeMap<String, String>> entry : entries) {

            // skip this entry if billboard doesnt exist and user cant create new ones
            if (!doesBillboardExist(entry.getKey()) &&
                    !hasPermission(user, Protocol.Permission.CREATE_BILLBOARDS, Protocol.Permission.EDIT_ALL_BILLBOARDS)) {
                continue;
            } else {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    database.billboards.put(entry.getKey(), new TreeMap<>());
                } else {
                    database.billboards.put(entry.getKey(), entry.getValue());
                }
            }

            // loop through each entry in data
            Set<Map.Entry<String, String>> billboardEntries = entry.getValue().entrySet();

            for (Map.Entry<String, String> billboardEntry : billboardEntries) {
                if (entry.getKey().equals("schedule") &&
                        hasPermission(user, Protocol.Permission.SCHEDULE_BILLBOARDS)) {
                    database.billboards.get(entry.getKey()).put(billboardEntry.getKey(), billboardEntry.getValue());
                }

                if (hasPermission(user,
                        Protocol.Permission.EDIT_ALL_BILLBOARDS, Protocol.Permission.CREATE_BILLBOARDS)) {
                    database.billboards.get(entry.getKey()).put(billboardEntry.getKey(), billboardEntry.getValue());
                }
            }

            // update current billboard
            try {
                String[] unixStartEnd = entry.getValue().get("schedule").split(" ");
                long now = System.currentTimeMillis();
                long start = Long.parseLong(unixStartEnd[0]);
                long end = Long.parseLong(unixStartEnd[1]);

                if (now >= start && now <= end) {
                    database.currentBillboard = entry.getKey();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void removeBillboards(String user, Set<String> keys) throws ServerException {
        if (!hasPermission(user, Protocol.Permission.EDIT_ALL_BILLBOARDS)) {
            throw new PermissionException(user, database.getPermission(user), Protocol.Permission.EDIT_ALL_BILLBOARDS);
        }

        for (String key : keys) {
            database.billboards.remove(key);
        }
    }

    public void addUsers(String user, TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        if (!database.users.isEmpty() && !hasPermission(user, Protocol.Permission.EDIT_USERS)) {
            throw new PermissionException(user, database.getPermission(user), Protocol.Permission.EDIT_USERS);
        }

        //get all entries
        Set<Map.Entry<String, TreeMap<String, String>>> entries = data.entrySet();

        List<String> existingUsers = new ArrayList<>();

        //using for loop
        for (Map.Entry<String, TreeMap<String, String>> entry : entries) {

            if (doesUserExist(entry.getKey())) {
                existingUsers.add(entry.getKey());
            }
            String oldHash = entry.getValue().get("hash");
            String newHash = UserAuth.hashAndSalt(oldHash, entry.getValue().get("salt"));

            data.get(entry.getKey()).replace("hash", newHash);
            database.users.put(entry.getKey(), entry.getValue());
        }

        if (!existingUsers.isEmpty()) {
            throw new ExistingUserException(existingUsers);
        }
    }

    public boolean doesUserExist(String user) {
        return database.users.containsKey(user);
    }

    public boolean doesBillboardExist(String name) {
        return database.billboards.containsKey(name);
    }

    public TreeMap<String, TreeMap<String, String>> getAllUsers(String user) throws ServerException {
        if (!hasPermission(user, Protocol.Permission.EDIT_USERS)) {
            throw new PermissionException(user, database.getPermission(user), Protocol.Permission.EDIT_USERS);
        }

        return database.users;
    }

    public TreeMap<String, TreeMap<String, String>> getAllBillboards() {
        return database.billboards;
    }

    public String getUserValue(String user, String key) throws Exception {
        return database.users.get(user).get(key);
    }

    public TreeMap<String, TreeMap<String, String>> getCurrentBillboard() {
        if (database.currentBillboard == null) {
            return null;
        }

        TreeMap<String, TreeMap<String, String>> response = new TreeMap<>();
        response.put(database.currentBillboard, database.billboards.get(database.currentBillboard));

        return response;
    }
}
