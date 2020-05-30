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

public class ServerFunctions {

    private static long newSessionId(String userId, String hash) throws ServerException {
        String dbSalt = null;
        String dbHash = null;

        if (userId == null || hash == null) {
            throw new ServerException("userId and/or hash not provided");
        }

        try {
            dbHash = database.getHash(userId);
            dbSalt = database.getSalt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        }

        long sessionId;

        if (UserAuth.hashAndSalt(hash, dbSalt).equals(dbHash)) {

            for (Map.Entry<Long, UserInfo> userInfo : sessionIds.entrySet()) {
                if (userInfo.getValue().userId.equals(userId)) {
                    return userInfo.getKey();
                }
            }

            sessionId = new Random().nextLong();

            UserInfo newUser = new UserInfo();
            newUser.userId = userId;
            newUser.createdAt = System.currentTimeMillis();

            sessionIds.put(sessionId, newUser);
        } else {
            System.out.println(hash + ' ' + dbSalt + '\n' + UserAuth.hashAndSalt(hash, dbSalt) + '\n' + dbHash);
            throw new ServerException("couldn't get session id - invalid username or password");
        }

        return sessionId;
    }

    private static String sessionToUserId(long sessionId) throws ServerException {

        if (!sessionIds.containsKey(sessionId)) {
            throw new ServerException("user doesn't have session ID");
        }

        UserInfo info = sessionIds.get(sessionId);

        if (info.createdAt + ONE_DAY_MS < System.currentTimeMillis()) {
            throw new ServerException("user session ID has expired");
        }

        return sessionIds.get(sessionId).userId;
    }

    private static void checkPermission(String userId, int needs) throws ServerException, SQLException {

        int has = database.getPermission(userId);

        System.out.println(Integer.toBinaryString(has) + " " + Integer.toBinaryString(needs));
        if ((has & needs) == 0) {
            throw new PermissionException(userId, has, needs);
        }
    }

    private static String checkBillboardPermission(String userId, String billboardId) throws ServerException, SQLException {

        String boardCreator = null;

        if (database.doesBillboardExist(billboardId)) {
            boardCreator = database.getBillboardCreator(billboardId);

            if (userId.equals(boardCreator)) {
                checkPermission(userId, Permission.CREATE_BILLBOARDS);
            } else {
                checkPermission(userId, Permission.EDIT_ALL_BILLBOARDS);
            }
        } else {
            boardCreator = userId;
            checkPermission(userId, Permission.CREATE_BILLBOARDS);
        }

        return boardCreator;
    }

    private static ArrayList<String> fixBillboardList(String userId, ArrayList<String> billboardIds) throws SQLException {
        for (Iterator<String> iterator = billboardIds.iterator(); iterator.hasNext(); ) {
            String billboardId = iterator.next();
            if (!database.doesBillboardExist(billboardId)) {
                iterator.remove();
            } else {
                try {
                    checkBillboardPermission(userId, billboardId);
                } catch (ServerException ignored) {
                    iterator.remove();
                }
            }
        }

        return billboardIds;
    }

    private static ArrayList<String> fixUserList(String userId, ArrayList<String> userIds) throws SQLException {
        for (Iterator<String> iterator = userIds.iterator(); iterator.hasNext(); ) {

            String loopUserId = iterator.next();

            if (!database.doesUserExist(loopUserId)) {
                iterator.remove();
            } else {
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

    public static ServerResponse getSessionId(TreeMap<String, Object> data) throws ServerException {
        ServerResponse response = new ServerResponse();

        String userId = (String) data.get(Protocol.USERID);
        String hash = (String) data.get(Protocol.HASH);

        long sessionId = newSessionId(userId, hash);
        response.data.put("sessionId", sessionId);

        return response;
    }

    public static ServerResponse addUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();
        String userId = sessionToUserId(sessionId);
        /* make sure user attempting to add new users has permissions */
        checkPermission(userId, Protocol.Permission.EDIT_USERS);
        /* loop through each user in the treemap */
        for (Map.Entry<String, Object> user : data.entrySet()) {
            /* cast the value of the treemap entry and get the details of each user */
            TreeMap<String, Object> userDetails = (TreeMap<String, Object>) user.getValue();
            /* get all of the relevant values and cast to types */
            String newUserId = user.getKey();
            String newUsername = (String) userDetails.get("userName");
            String newSalt = (String) userDetails.get("salt");
            String newHash = (String) userDetails.get("hash");
            String doubleHash = null;
            if (newHash != null && newSalt != null) {
                doubleHash = UserAuth.hashAndSalt(newHash, newSalt);
            }
            System.out.println(newHash + " " + doubleHash);
            Integer newPermissions = (Integer) userDetails.get("permissions");
            /* if user doesn't exist, make sure all user information has been provided and add it */
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
                if (newUserId.equals("b220a053-91f1-48ee-acea-d1a145376e57")) {
                        throw new ServerException("Admin settings cannot be changed");
                }


                database.editUser(newUserId, newUsername, doubleHash, newSalt, newPermissions);
            }
        }
        return response;
    }


    public static ServerResponse getUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();
        String userId = sessionToUserId(sessionId);

        if (data.containsKey("userList")) {
            ArrayList<String> userIds = fixUserList(userId, (ArrayList<String>) data.get("userList"));
            System.out.println("if userList" + userIds.toString());
            response.data = database.getUsers(userIds);
        } else {
            try {
                checkPermission(userId, Permission.EDIT_USERS);
                System.out.println("if has edit" + userId.toString());
                response.data = database.getUsers();
            } catch (ServerException ignored) {
                ArrayList<String> userIdList = new ArrayList<>(1);
                userIdList.add(userId);
                System.out.println("if not has edit " + userIdList.toString());
                response.data = database.getUsers(userIdList);
            }
        }
        return response;
    }

    public static ServerResponse deleteUsers(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.EDIT_USERS);

        if (data.containsKey("userList")) {
            // ArrayList<String> userIds = fixUserList((ArrayList<String>) data.get("userList"));
            database.deleteUsers((ArrayList<String>) data.get("userList"));
        } else {
            throw new ServerException(userId + " attempted to delete users but didn't provide any");
        }

        return response;
    }

    public static ServerResponse addBillboards(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();
        String userId = sessionToUserId(sessionId);

        boolean addedAny = false;

        /* loop through each billboard in the treemap */
        for (Map.Entry<String, Object> billboard : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each billboard */
            TreeMap<String, Object> billboardDetails = (TreeMap<String, Object>) billboard.getValue();

            /* get all of the relevant values and cast to types */
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

            //boolean isAdding = creator == null;

            try {
                creator = checkBillboardPermission(userId, billboardId);
            } catch (ServerException e) {
                response.status += e + ". ";
                continue;
            }

            if (!database.doesBillboardExist(billboardId)) {
                if (billboardId == null || name == null || creator == null || message == null || info == null ||
                        pictureData == null || pictureUrl == null || billboardBackground == null || messageColour == null || informationColour == null) {
                    response.status += "couldn't add " + billboardId + ", not all data provided. ";
                    continue;
                }
                database.addBillboard(billboardId, name, creator, message, info, pictureData, pictureUrl, billboardBackground, messageColour, informationColour);
            } else {
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

    public static ServerResponse getBillboards(TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        if (data.containsKey("billboardList")) {
            // ArrayList<String> billboardIds = fixBillboardList((ArrayList<String>) data.get("billboardList"));
            response.data = database.getBillboards((ArrayList<String>) data.get("billboardList"));
        } else {
            response.data = database.getBillboards();
        }

        return response;
    }

    public static ServerResponse deleteBillboards(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        String userId = sessionToUserId(sessionId);

        if (data.containsKey("billboardList")) {
            ArrayList<String> billboardIds = fixBillboardList(userId, (ArrayList<String>) data.get("billboardList"));
            database.deleteBillboards(billboardIds);
        } else {
            throw new ServerException("user requested to delete billboards, but didn't say which ones");
        }

        return response;
    }

    public static ServerResponse addSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.SCHEDULE_BILLBOARDS);
        System.out.println("Before forloop");
        /*(scheduleId, billboardId, startTime, scheduleDuration, isRecurring, recurFreqInMins)*/
        for (Map.Entry<String, Object> schedule : data.entrySet()) {
            System.out.println("After forloop");
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
                    System.out.println("1");
                    throw new ServerException("all schedule details not provided");
                }

                System.out.println("101010");
                String successMessage = scheduler.addScheduleCheckIfAllowed( billboardName, startTime, duration, isRecurring, recurFreqInMins, "Creator");
                System.out.println("3");
                if ( successMessage.equals("success") ) {
                    System.out.println("4");
                    // add to DB & set success message
                    database.addSchedule(scheduleId, billboardId, startTime, duration, isRecurring, recurFreqInMins);
                    successMessage = "Successfully added schedule starting at " + startTime + " for " + billboardName;
                }
                else {
                    System.out.println("5");
                    response.success = false;
                }
                // keep success message from scheduler if unsuccessful
                response.status += successMessage;
                //database.addSchedule(scheduleId, billboardId, startTime, duration, isRecurring, recurFreqInMins);
            }
            else {
                throw new ServerException("Can't edit schedules");
            }
        }
        return response;
    }

    public static ServerResponse getSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {

        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Protocol.Permission.SCHEDULE_BILLBOARDS);

        ServerResponse response = new ServerResponse();
        if (data.containsKey("scheduleList")) {
            // ArrayList<String> scheduleIds = fixScheduleList((ArrayList<String>) data.get("scheduleList"));
            response.data = database.getSchedules((ArrayList<String>) data.get("scheduleList"));
        } else {
            response.data = database.getSchedules();
        }
        return response;
    }

    public static ServerResponse deleteSchedules(long sessionId, TreeMap<String, Object> data) throws ServerException, SQLException {
        ServerResponse response = new ServerResponse();

        String userId = sessionToUserId(sessionId);
        checkPermission(userId, Permission.SCHEDULE_BILLBOARDS);

        if (data.containsKey("scheduleList")) {
            // ArrayList<String> scheduleIds = fixScheduleList((ArrayList<String>) data.get("scheduleList"));
            database.deleteSchedules((ArrayList<String>) data.get("scheduleList"));
        } else {
            throw new ServerException(userId + " attempted to delete schedules but provided none");
        }

        return response;
    }
}
