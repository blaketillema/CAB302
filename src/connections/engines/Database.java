package connections.engines;

import java.util.Map;
import java.util.TreeMap;

import connections.Protocol.*;
import connections.exceptions.PermissionException;
import connections.exceptions.ServerException;

public class Database {

    /* get permission of userId from database */
    private int getPermission(String userId) {

        return 0;
    }

    private void checkPermission(String userId, int permissionNeeded) throws ServerException {

        if (userId == null) {
            throw new ServerException("tried to check permissions for null user");
        }
        if ((getPermission(userId) & permissionNeeded) == 0) {
            throw new PermissionException(userId, getPermission(userId), permissionNeeded);
        }
    }

    /* see if userId is in database */
    private boolean doesUserExist(String userId) {

        return false;
    }

    void addUsers(String userId, TreeMap<String, Object> data) throws ServerException {

        /* make sure user attempting to add new users has permissions */
        checkPermission(userId, Permission.EDIT_USERS);

        /* loop through each user in the treemap */
        for (Map.Entry<String, Object> user : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each user */
            TreeMap<String, Object> userDetails = (TreeMap<String, Object>) user.getValue();

            /* get all of the relevant values and cast to types */
            String newUserId = user.getKey();
            String newUsername = (String) userDetails.get("userName");
            String newHash = (String) userDetails.get("hash");
            String newSalt = (String) userDetails.get("salt");
            int newPermissions = -1;
            if (userDetails.containsKey("permissions"))
                newPermissions = (int) userDetails.get("permissions");

            /* check if the user to be added exists or not. */
            boolean userExists = doesUserExist(user.getKey());

            /* if user doesn't exist, make sure all user information has been provided and add it */
            if (!userExists) {
                if (newUsername == null || newHash == null || newSalt == null || newPermissions == -1) {
                    throw new ServerException("attempting to add user without all of the required information");
                }
                // pstmt.setString(1, newUserId);
                // pstmt.setString(2, newUsername);
                // pstmt.setString(3, newHash);
                // pstmt.setString(4, newSalt);
                // pstmt.setString(5, newUsername);

                /* else if user does exist, somehow modify the existing info for that userId */
            } else {
                // if(username != null) pstmt.setString(2, username);
                // if(hash != null) pstmt.setString(3, hash);
                // if(salt != null) pstmt.setString(4, salt);
                // if(permissions != -1) pstmt.setString(5, username);
            }
        }
    }

    void deleteUsers(String userId, TreeMap<String, Object> data) {

    }

    TreeMap<String, Object> getUsers(String userId) {

        /* new treemap to put database data into */
        TreeMap<String, Object> users = new TreeMap<>();

        /* loop through each user and add to treemap */
        /*
        while(rs.next()) {
            TreeMap<String, Object> user = new TreeMap<>();

            user.put("userName", rs.getString(2));
            user.put("hash", rs.getString(3));
            user.put("salt", rs.getString(4));
            user.put("permissions", rs.getInt(5));

            user.put(rs.getString(1), body);
        } */

        return users;
    }

    void addBillboards(String userId, TreeMap<String, Object> data) {

    }

    void deleteBillboards(String userId, TreeMap<String, Object> data) {

    }

    TreeMap<String, Object> getBillboards(String userId) {

        return null;
    }

    TreeMap<String, Object> getCurrentBillboard() {

        return null;
    }

    void addSchedules(String userId, TreeMap<String, Object> data) {

    }

    void deleteSchedules(String userId, TreeMap<String, Object> data) {

    }

    TreeMap<String, Object> getSchedules(String userId) {

        return null;
    }

    String getUserHash(String userId) {

        return "";
    }

    String getUserSalt(String userId) {

        return "";
    }

    String getUserId(String userName) {

        return "";
    }

}
