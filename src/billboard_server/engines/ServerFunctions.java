package billboard_server.engines;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

import billboard_server.Database;
import billboard_server.Protocol;
import billboard_server.Protocol.*;
import billboard_server.engines.UserInfo;
import billboard_server.exceptions.PermissionException;
import billboard_server.exceptions.ServerException;
import billboard_server.types.ServerResponse;
import billboard_server.tools.UserAuth;

import static billboard_server.engines.Server.*;

/**
 * Server side: Functions for each command defined in Protocol.Cmd and sent by the client
 * @author Max Ferguson
 */
public class ServerFunctions {

    /**
     * Generates a new session ID for a client if the given hash and salt matches the database
     * @param userId The user ID of the client
     * @param hash The password of the client that has been hashed and salted once
     * @return long The new session ID for the client
     * @throws ServerException If the client doesn't exist or credentials are incorrect
     */
    private static long newSessionId(String userId, String hash) throws ServerException {
        String dbSalt = null;
        String dbHash = null;

        if (userId == null || hash == null) {
            throw new ServerException("userId and/or hash not provided");
        }

        // try to get the hash and salt of the user from the database
        try {
            dbHash = database.getHash(userId);
            dbSalt = database.getSalt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        }

        long sessionId;

        // if user provided hash (hashed again) matches the doubly hashed hash in the database
        if (UserAuth.hashAndSalt(hash, dbSalt).equals(dbHash)) {

            // if user already has a session ID in sessionIds, return that
            for (Map.Entry<Long, UserInfo> userInfo : sessionIds.entrySet()) {
                if (userInfo.getValue().userId.equals(userId)) {
                    return userInfo.getKey();
                }
            }

            // otherwise, give them a new session Id
            sessionId = new Random().nextLong();

            UserInfo newUser = new UserInfo();
            newUser.userId = userId;
            newUser.createdAt = System.currentTimeMillis();

            sessionIds.put(sessionId, newUser);
        } else {
            throw new ServerException("couldn't get session id - invalid username or password");
        }

        return sessionId;
    }

    /**
     * Converts a session ID to a user ID
     * @param sessionId The session ID sent by the client
     * @return String The user ID of the client
     * @throws ServerException If the client has no session ID or if the session ID has expired
     */
    private static String sessionToUserId(long sessionId) throws ServerException {

        // check if user is in sessionIds
        if (!sessionIds.containsKey(sessionId)) {
            throw new ServerException("user doesn't have session ID");
        }

        UserInfo info = sessionIds.get(sessionId);

        // check if session id has expired
        if (info.createdAt + ONE_DAY_MS < System.currentTimeMillis()) {
            throw new ServerException("user session ID has expired");
        }

        // return the session ID
        return sessionIds.get(sessionId).userId;
    }

    /**
     * Throws a ServerException if the permission of the user doesn't match the specified requirements
     * @param userId The user ID of the client
     * @param needs The permission(s) needed, as specified in Protocol.Permission
     * @throws ServerException If the permissions are not met
     * @throws SQLException If the database has a fatal error
     */
    private static void checkPermission(String userId, int needs) throws ServerException, SQLException {

        // get the permissions of the user from the database
        int has = database.getPermission(userId);

        // bitwise and the permissions together to see if they have the required one
        if ((has & needs) == 0) {
            throw new PermissionException(userId, has, needs);
        }
    }

    /**
     * Checks if a client has permissions to add/edit/delete a billboard based on their permissions
     * @param userId The user ID of the client
     * @param billboardId The billboard ID to be modified
     * @return String The user ID of the billboard's creator if applicable, null if not
     * @throws ServerException If the client doesn't have the required permission
     * @throws SQLException If the database has a fatal error
     */
    private static String checkBillboardPermission(String userId, String billboardId) throws ServerException, SQLException {
        String boardCreator = null;

        // check if billboard exists
        if (database.doesBillboardExist(billboardId)) {
            // store the billboard creator (user ID)
            boardCreator = database.getBillboardCreator(billboardId);

            // if the user ID given matches the billboard creator's
            if (userId.equals(boardCreator)) {
                // check that they can edit their own
                checkPermission(userId, Permission.CREATE_BILLBOARDS);
            } else {
                // check that they can edit all
                checkPermission(userId, Permission.EDIT_ALL_BILLBOARDS);
            }
        } else {
            boardCreator = userId;
            // check that they can create billboards
            checkPermission(userId, Permission.CREATE_BILLBOARDS);
        }

        return boardCreator;
    }

    /**
     * Fixes a given list of billboard IDs by removing IDs that don't exist and IDs that the client can't edit
     * @param userId The user ID of the client
     * @param billboardIds The list of billboard ID's they want to add/edit/delete
     * @return ArrayList<String> The original list that has been fixed
     * @throws SQLException If the database has a fatal error
     */
    private static ArrayList<String> fixBillboardList(String userId, ArrayList<String> billboardIds) throws SQLException {

        // for each billboard ID in list
        for (Iterator<String> iterator = billboardIds.iterator(); iterator.hasNext(); ) {
            String billboardId = iterator.next();

            // if billboard doesn't exist, remove it
            if (!database.doesBillboardExist(billboardId)) {
                iterator.remove();
            } else {
                try {
                    // if user doesn't have permissions to edit/delete this billboard, delete it
                    checkBillboardPermission(userId, billboardId);
                } catch (ServerException ignored) {
                    iterator.remove();
                }
            }
        }

        return billboardIds;
    }

    /**
     * Fixes a given list of user ID's, removing ID's that don't exist or user ID's that the client doesn't have permission to edit
     * @param userId The user ID of the client
     * @param userIds The list of user ID's they want to add/edit/delete
     * @return ArrayList<String> The original list that has been fixed
     * @throws SQLException If the database has a fatal error
     */
    private static ArrayList<String> fixUserList(String userId, ArrayList<String> userIds) throws SQLException {

        // for each user ID in list
        for (Iterator<String> iterator = userIds.iterator(); iterator.hasNext(); ) {
            String loopUserId = iterator.next();

            // check if user ID exists in database
            if (!database.doesUserExist(loopUserId)) {
                iterator.remove();
            } else {
                // check if user wants to edit someone other than themselves
                if(!loopUserId.equals(userId)) {
                    try {
                        checkPermission(userId, Permission.EDIT_USERS);
                    } catch (ServerException ignored) {
                        iterator.remove();
                    }
                }
            }
        }

        return userIds;
    }

    /*
        SERVERTHREAD CALLS
     */

    /**
     * Creates a new session ID (if possible) for a client
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse The requested sessionId if successful
     * @throws ServerException If the user doesn't exist or has invalid login details
     */
    public static ServerResponse getSessionId(TreeMap<String, Object> data) throws ServerException {
        ServerResponse response = new ServerResponse();

        String userId = (String) data.get(Protocol.USERID);
        String hash = (String) data.get(Protocol.HASH);

        // generate session Id and add it into response.data
        long sessionId = newSessionId(userId, hash);
        response.data.put("sessionId", sessionId);

        return response;
    }

    /**
     * Handles the request to add or edit users
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse The response (success or failure with message)
     * @throws ServerException If user has invalid permissions or provided bad data, or tried to edit admin
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse addUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // get user ID from session ID
        String userId = sessionToUserId(sessionId);

        // for each user in data
        for (Map.Entry<String, Object> user : data.entrySet()) {

            // pull the user details out of the value of each user
            TreeMap<String, Object> userDetails = (TreeMap<String, Object>) user.getValue();

            // pull each value out of the user details
            String newUserId = user.getKey();

            // check if the user is trying to edit someone else
            if(!userId.equals(newUserId)) {
                checkPermission(userId, Protocol.Permission.EDIT_USERS);
            }

            String newUsername = (String) userDetails.get("userName");
            String newSalt = (String) userDetails.get("salt");
            String newHash = (String) userDetails.get("hash");
            String doubleHash = null;

            // hash and salt the provided hash again to be added into the database
            if (newHash != null && newSalt != null) {
                doubleHash = UserAuth.hashAndSalt(newHash, newSalt);
            }
            Integer newPermissions = (Integer) userDetails.get("permissions");

            // if user doesn't exist, make sure all user information has been provided and add it
            if (!database.doesUserExist(user.getKey())) {
                if (newUsername == null || doubleHash == null || newSalt == null || newPermissions == null) {
                    response.status += "attempted to add user without all of the required information. ";
                    continue;
                }
                database.addUser(newUserId, newUsername, doubleHash, newSalt, newPermissions);
            } else {
                if (newUserId == null) {
                    response.status += "attempted to edit user without providing userId. ";
                    continue;
                }
                // prevent admin from being modified
                if (newUserId.equals("b220a053-91f1-48ee-acea-d1a145376e57")) {
                    throw new ServerException("Admin settings cannot be changed");
                }

                try {
                    checkPermission(userId, Permission.EDIT_USERS);
                } catch (ServerException e) {
                    newPermissions = null;
                }
                database.editUser(newUserId, newUsername, doubleHash, newSalt, newPermissions);
            }
        }
        return response;
    }

    /**
     * Handles the request to get users
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Response with data requested
     * @throws ServerException If the user has invalid permissions
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse getUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // get user ID from session ID
        String userId = sessionToUserId(sessionId);

        // if client provided a list of users
        if (data.containsKey("userList")) {
            // fix the list based on permissions and call the database
            ArrayList<String> userIds = fixUserList(userId, (ArrayList<String>) data.get("userList"));
            response.data = database.getUsers(userIds);
        } else {
            // client wants all users
            try {
                // check if they can get all users
                checkPermission(userId, Permission.EDIT_USERS);
                response.data = database.getUsers();
            } catch (ServerException ignored) {
                // if they can't, let them only see themselves
                ArrayList<String> userIdList = new ArrayList<>(1);
                userIdList.add(userId);
                response.data = database.getUsers(userIdList);
            }
        }
        return response;
    }

    /**
     * Handles the request to delete users
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse The response (success or failure with message)
     * @throws ServerException If the user has invalid permissions or provided bad data
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse deleteUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // check if user has permission to delete users
        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.EDIT_USERS);

        // delete users in the provided list
        if (data.containsKey("userList")) {
            database.deleteUsers((ArrayList<String>) data.get("userList"));
        } else {
            throw new ServerException(userId + " attempted to delete users but didn't provide any");
        }

        return response;
    }

    /**
     * Handles the request to add billboards
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse The response (success or failure with message)
     * @throws ServerException If the user has invalid permissions or provided bad data
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse addBillboards(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();
        String userId = sessionToUserId(sessionId);

        boolean addedAny = false;

        // for each billboard in the treemap
        for (Map.Entry<String, Object> billboard : data.entrySet()) {

            // pull the billboard treemap out of the value
            TreeMap<String, Object> billboardDetails = (TreeMap<String, Object>) billboard.getValue();

            // get all of the relevant values and cast to types
            String billboardId = billboard.getKey();
            String name = (String) billboardDetails.get(Protocol.BOARDNAME);
            String creator = (String) billboardDetails.get(Protocol.BOARDCREATOR);
            String message = (String) billboardDetails.get("message");
            String info = (String) billboardDetails.get("information");
            String pictureData = (String) billboardDetails.get("pictureData");
            String pictureUrl = (String) billboardDetails.get("pictureUrl");
            String billboardBackground = (String) billboardDetails.get("billboardBackground");
            String messageColour = (String) billboardDetails.get("messageColour");
            String informationColour = (String) billboardDetails.get("informationColour");

            // get the creator of the billboard and check permissions
            try {
                creator = checkBillboardPermission(userId, billboardId);
            } catch (ServerException e) {
                // if they don;t have the required permissions, skip to the next billboard
                response.status += e + ". ";
                continue;
            }

            // if billboard doesn't exist, add it
            if (!database.doesBillboardExist(billboardId)) {
                if (billboardId == null || name == null || creator == null || message == null || info == null ||
                        pictureData == null || pictureUrl == null || billboardBackground == null || messageColour == null || informationColour == null) {
                    response.status += "couldn't add " + billboardId + ", not all data provided. ";
                    continue;
                }
                database.addBillboard(billboardId, name, creator, message, info, pictureData, pictureUrl, billboardBackground, messageColour, informationColour);
            } else {
                // else, edit it
                if (billboardId == null) {
                    response.status += "attempted to edit billboard without billboardId. ";
                    continue;
                }
                database.editBillboard(billboardId, name, creator, message, info, pictureData, pictureUrl, billboardBackground, messageColour, informationColour);
            }

            addedAny = true;
        }

        if (!addedAny) {
            response.success = false;
        }

        return response;
    }

    /**
     * Handles the request to get billboards
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Response with the data requested
     * @throws ServerException If the user doesn't have the required permissions
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse getBillboards(TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // get all or a list of billboards
        if (data.containsKey("billboardList")) {
            response.data = database.getBillboards((ArrayList<String>) data.get("billboardList"));
        } else {
            response.data = database.getBillboards();
        }

        return response;
    }

    /**
     * Handles the request to delete billboards
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Empty response if successful
     * @throws ServerException If user has invalid permissions or if they didn't specify anything to be deleted
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse deleteBillboards(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        String userId = sessionToUserId(sessionId);

        // check if the user provided a list to delete, and then remove any values from that list they aren't allowed to touch
        if (data.containsKey("billboardList")) {
            ArrayList<String> billboardIds = fixBillboardList(userId, (ArrayList<String>) data.get("billboardList"));
            if(billboardIds.size() == 0) {
                throw new ServerException("user attempted to delete a non-existent billboard, or a billboard without the required permissions");
            }
            database.deleteBillboards(billboardIds);
        } else {
            throw new ServerException("user requested to delete billboards, but didn't say which ones");
        }

        return response;
    }

    /**
     * Handles the request to add schedules
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Response with an error message and success value set
     * @throws ServerException If the user doesn't have the necessary permissions or if the schedule is not valid
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse addSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // check that they can add schedules
        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.SCHEDULE_BILLBOARDS);

        // for each schedule in data
        for (Map.Entry<String, Object> schedule : data.entrySet()) {

            // pull schedule details out of treemap
            TreeMap<String, Object> scheduleDetails = (TreeMap<String, Object>) schedule.getValue();
            String scheduleId = schedule.getKey();
            String billboardId = (String) scheduleDetails.get("billboardId");
            String billboardName = database.billboardIdToName(billboardId);
            OffsetDateTime startTime = (OffsetDateTime) scheduleDetails.get("startTime");
            Integer duration = (Integer) scheduleDetails.get("duration");
            Boolean isRecurring = (Boolean) scheduleDetails.get("isRecurring");
            Integer recurFreqInMins = (Integer) scheduleDetails.get("recurFreqInMins");

            // If schedule DOES NOT exist
            if (!database.doesScheduleExist(scheduleId)) {
                if (scheduleId == null || billboardId == null || startTime == null || duration == null || isRecurring == null || recurFreqInMins == null) {
                    throw new ServerException("all schedule details not provided");
                }

                String successMessage = scheduler.addScheduleCheckIfAllowed( billboardName, startTime, duration, isRecurring, recurFreqInMins, "Creator");
                if ( successMessage.equals("success") ) {
                    // add to DB & set success message
                    database.addSchedule(scheduleId, billboardId, startTime, duration, isRecurring, recurFreqInMins);
                    successMessage = "Successfully added schedule starting at " + startTime + " for " + billboardName;
                }
                else {
                    response.success = false;
                }
                // keep success message from scheduler if unsuccessful
                response.status += successMessage;
            }
            else {
                throw new ServerException("Can't edit schedules");
            }
        }
        return response;
    }

    /**
     * Handles the request to get schedules
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Response with the data requested
     * @throws ServerException If user doesn't have permissions to get schedules
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse getSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // check that they can get schedules
        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Protocol.Permission.SCHEDULE_BILLBOARDS);

        // get all or a list of schedules
        if (data.containsKey("scheduleList")) {
            response.data = database.getSchedules((ArrayList<String>) data.get("scheduleList"));
        } else {
            response.data = database.getSchedules();
        }
        return response;
    }

    /**
     * Handles the request to delete schedules
     * @param sessionId The session ID sent by the client in ClientRequest.sessionId
     * @param data The TreeMap sent by the client in ClientRequest.data
     * @return ServerResponse Empty response if successful
     * @throws ServerException If the user doesn't have the permissions required
     * @throws SQLException If the database has a fatal error
     */
    public static ServerResponse deleteSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        // check that they can delete schedules
        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.SCHEDULE_BILLBOARDS);

        // delete the provided list of schedules
        if (data.containsKey("scheduleList")) {
            database.deleteSchedules((ArrayList<String>) data.get("scheduleList"));
        } else {
            throw new ServerException(userId + " attempted to delete schedules but provided none");
        }

        return response;
    }
}
