package connections;

import connections.exceptions.*;
import connections.tools.UserAuth;
import connections.types.TestDatabase;

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

    private void addBillboard(String user, Map.Entry<String, TreeMap<String, String>> data, String intention) throws ServerException {

        boolean canEditAll = hasPermission(user, Protocol.Permission.EDIT_ALL_BILLBOARDS);
        boolean canEditOwn = hasPermission(user, Protocol.Permission.CREATE_BILLBOARDS);
        boolean canEditSchedule = hasPermission(user, Protocol.Permission.SCHEDULE_BILLBOARDS);

        boolean intentEdit = intention.equals(Protocol.Params.Intent.EDIT_BILLBOARD);
        boolean intentAdd = intention.equals(Protocol.Params.Intent.ADD_BILLBOARD);
        boolean intentSchedule = intention.equals(Protocol.Params.Intent.EDIT_SCHEDULE);


        // if billboard exists
        if (doesBillboardExist(data.getKey())) {

            // if user has schedule permission and entry has schedule parameter
            if (intentSchedule) {
                if (canEditSchedule) {
                    if (data.getValue().containsKey("schedule")) {
                        database.billboards.get(data.getKey()).put("schedule", data.getValue().get("schedule"));
                    } else {
                        throw new ServerException("user intends to edit schedule but hasn't provided any values");
                    }
                } else {
                    throw new IntentionException(user,
                            database.getPermission(user),
                            Protocol.Permission.SCHEDULE_BILLBOARDS,
                            Protocol.Params.Intent.EDIT_SCHEDULE);
                }
            }

            // if user has create billboards permission and billboard is theirs, or if they have edit all permission
            if (intentEdit || intentAdd) {
                if (canEditAll ||
                        (canEditOwn &&
                                data.getValue().containsKey("createdBy") &&
                                data.getValue().get("createdBy").equals(user))) {
                    if (data.getValue().containsKey("renameTo")) {
                        String newTitle = data.getValue().get("renameTo");
                        database.billboards.put(newTitle, data.getValue());
                        database.billboards.get(newTitle).remove("renameTo");
                        database.billboards.remove(data.getKey());
                    } else {
                        database.billboards.put(data.getKey(), data.getValue());
                    }
                } else {
                    throw new IntentionException(user,
                            database.getPermission(user),
                            Protocol.Permission.combine(Protocol.Permission.EDIT_ALL_BILLBOARDS, Protocol.Permission.CREATE_BILLBOARDS),
                            Protocol.Params.Intent.EDIT_BILLBOARD + " or " + Protocol.Params.Intent.ADD_BILLBOARD);
                }
            }

        } else {

            // if user has create billboards permission
            if (intentAdd) {
                if (canEditOwn) {
                    database.billboards.put(data.getKey(), data.getValue());
                } else {
                    throw new IntentionException(user,
                            database.getPermission(user),
                            Protocol.Permission.CREATE_BILLBOARDS,
                            Protocol.Params.Intent.ADD_BILLBOARD);
                }
            }
        }
    }

    public void addBillboards(String user, TreeMap<String, TreeMap<String, String>> data, String intention) throws ServerException {

        switch (data.size()) {
            case 0:
                throw new ServerException("no billboard provided");

            case 1:
                addBillboard(user, data.firstEntry(), intention);
                break;

            default:
                for (Map.Entry<String, TreeMap<String, String>> entry : data.entrySet()) {
                    addBillboard(user, entry, intention);
                }
                break;
        }
    }

    @Deprecated
    public void _addBillboards(String user, TreeMap<String, TreeMap<String, String>> data) throws ServerException {

        //using for loop
        for (Map.Entry<String, TreeMap<String, String>> entry : data.entrySet()) {
            // skip this entry if billboard doesnt exist and user cant create new ones
            if (!doesBillboardExist(entry.getKey()) &&
                    !hasPermission(user, Protocol.Permission.CREATE_BILLBOARDS, Protocol.Permission.EDIT_ALL_BILLBOARDS)) {
                continue;
            } else {
                if (entry.getValue() == null) {
                    database.billboards.put(entry.getKey(), new TreeMap<>());
                } else {
                    database.billboards.put(entry.getKey(), entry.getValue());
                }
            }

            String title = entry.getKey();

            if (entry.getValue().containsKey("renameTo") && hasPermission(user, Protocol.Permission.EDIT_ALL_BILLBOARDS)) {
                title = entry.getValue().get("renameTo");
                database.billboards.put(title, entry.getValue());
                database.billboards.remove(entry.getKey());
            }

            for (Map.Entry<String, String> billboardEntry : entry.getValue().entrySet()) {
                if (entry.getKey().equals("schedule") &&
                        hasPermission(user, Protocol.Permission.SCHEDULE_BILLBOARDS)) {
                    database.billboards.get(title).put(billboardEntry.getKey(), billboardEntry.getValue());
                }

                if (hasPermission(user,
                        Protocol.Permission.EDIT_ALL_BILLBOARDS, Protocol.Permission.CREATE_BILLBOARDS)) {
                    database.billboards.get(title).put(billboardEntry.getKey(), billboardEntry.getValue());
                }
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

        //using for loop
        for (Map.Entry<String, TreeMap<String, String>> entry : data.entrySet()) {

            if (doesUserExist(entry.getKey())) {
                throw new UserException(entry.getKey());
            }
            String oldHash = entry.getValue().get("hash");
            String newHash = UserAuth.hashAndSalt(oldHash, entry.getValue().get("salt"));

            data.get(entry.getKey()).put("hash", newHash);
            database.users.put(entry.getKey(), entry.getValue());
        }
    }

    public void modifyUsers(String user, TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        if (!hasPermission(user, Protocol.Permission.EDIT_USERS)) {
            throw new PermissionException(user, database.getPermission(user), Protocol.Permission.EDIT_USERS);
        }

        //using for loop
        for (Map.Entry<String, TreeMap<String, String>> entry : data.entrySet()) {

            if (!doesUserExist(entry.getKey())) {
                throw new ServerException("tried to modify non-existent user");
            }

            String oldHash = entry.getValue().get("hash");
            String newHash = UserAuth.hashAndSalt(oldHash, entry.getValue().get("salt"));

            data.get(entry.getKey()).put("hash", newHash);

            if (entry.getValue().containsKey("renameTo")) {
                String oldUserName = entry.getKey();
                String newUserName = entry.getValue().get("renameTo");

                database.users.put(newUserName, entry.getValue());
                database.users.get(newUserName).remove("renameTo");
                database.users.put(newUserName, entry.getValue());
                database.users.remove(oldUserName);

                // make sure created by tag on each billboard is updated
                for (Map.Entry<String, TreeMap<String, String>> billboard : database.billboards.entrySet()) {
                    database.billboards.get(billboard.getKey()).put("createdBy", newUserName);
                }
            }
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

    public TreeMap<String, TreeMap<String, String>> getCurrentBillboard() throws ServerException {
        refreshCurrentBillboard();

        if (database.currentBillboard == null) {
            throw new ServerException("no billboard currently scheduled");
        }

        TreeMap<String, TreeMap<String, String>> response = new TreeMap<>();
        response.put(database.currentBillboard, database.billboards.get(database.currentBillboard));

        return response;
    }

    private void refreshCurrentBillboard() {
        database.currentBillboard = null;

        for (Map.Entry<String, TreeMap<String, String>> billboard : database.billboards.entrySet()) {
            try {
                String[] unixStartEnd = billboard.getValue().get("schedule").split(" ");
                long now = System.currentTimeMillis();
                long start = Long.parseLong(unixStartEnd[0]);
                long end = Long.parseLong(unixStartEnd[1]);

                if (now >= start || now <= end) {
                    database.currentBillboard = billboard.getKey();
                    return;
                }
            } catch (Exception ignored) {
            }
        }
    }
}
