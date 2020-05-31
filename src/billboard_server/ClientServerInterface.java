package billboard_server;

import java.io.*;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;

import billboard_server.Protocol.*;
import billboard_server.engines.ServerClientConnection;
import billboard_server.exceptions.ServerException;
import billboard_server.tools.UserAuth;
import billboard_server.types.ClientRequest;
import billboard_server.types.ServerResponse;

/**
 * Class to be used by clients to easily login, send to and receive data from the server
 * @author Max Ferguson
 */
public class ClientServerInterface {

    private String ip;
    private int port;
    private long sessionId;
    private static final String saltMapPath =
            Paths.get(System.getProperty("user.dir"), "salts.map").toString();

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "network.props").toString();

    /**
     * Initialiser that reads assets/network.props and stores the salt of admin in assets/salts.map
     */
    public ClientServerInterface() {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(networkPath);
            props.load(in);
            in.close();
            this.ip = props.getProperty("ip");
            this.port = Integer.parseInt(props.getProperty("port"));

            // saves the salt of admin into assets/salts.map
            saveUserSalt("b220a053-91f1-48ee-acea-d1a145376e57", "2219d4ec595ce93cabfe7c7941d7e274");
        } catch (Exception ioe) { //if network.props can't be found
            this.ip = "localHost";
            this.port = 1234;
            ioe.printStackTrace();
        }
    }

    /**
     * Logout the user by clearing the sessionId given by the server
     */
    public void logout(){
        this.sessionId = 0;
    }

    /**
     * Requests a sessionId from the server for the specified user, and stores the sessionId if successful
     * @param userName The username to login with (e.g. "max")
     * @param password The password to login with (e.g. "1234")
     * @throws ServerException If the user couldn't be logged in (invalid details or doesn't exist)
     */
    public void login(String userName, String password) throws ServerException {
        System.out.printf("\nrequesting to login user: %s ... ", userName);

        // get the user ID from the server given the username
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.NAME_TO_ID;
        request.data = new TreeMap<>();
        request.data.put(Protocol.USERNAME, userName);

        ServerResponse response;

        response = ServerClientConnection.request(this.ip, this.port, request);

        String userId;

        try {
            userId = (String) response.data.get(Protocol.USERID);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

        if(userId == null) {
            throw new ServerException("user doesn't exist");
        }

        // try to get the salt of the user ID from salts.map
        String salt;

        try {
            salt = getUserSalt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // hash and salt the password with the salt from salts.map
        String hash = UserAuth.hashAndSalt(password, salt);

        // send user ID and hash to server to login and get session ID
        request = new ClientRequest();

        request.cmd = Cmd.GET_SESSION_ID;
        request.data = new TreeMap<>();
        request.data.put(Protocol.USERID, userId);
        request.data.put(Protocol.HASH, hash);

        response = ServerClientConnection.request(this.ip, this.port, request);

        try {
            this.sessionId = (long) response.data.get(Protocol.SESSIONID);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

        System.out.println("done");
    }

    /**
     * Get a user ID from the database, given a username
     * @param userName The username of the user
     * @return String The user ID of the user
     * @throws ServerException If the user doesn't exist
     */
    public String getUserId(String userName) throws ServerException {
        System.out.printf("\nrequesting to get userId of %s ...", userName);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.NAME_TO_ID;
        request.data.put("userName", userName);
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return (String) response.data.get("userId");
    }

    /**
     * Adds a user to the database (permissions allowing)
     * @param userName The username to add (e.g. "max")
     * @param password The password to add (e.g. "1234")
     * @param permission The permission(s) to add (e.g. Protocol.Permission.EDIT_USERS | Protocol.Permission.CREATE_BILLBOARDS)
     * @throws ServerException If the user calling this doesn't have the EDIT_USERS permission, or if bad data sent
     */
    public void addUser(String userName, String password, int permission) throws ServerException {
        System.out.printf("\nrequesting to add user: %s, with permission: %s ... ", userName, Permission.toString(permission));

        // generate a salt and then hash the password with the new salt
        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);

        // generate a new user ID
        String userId = java.util.UUID.randomUUID().toString();

        // add the new user to the database
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_USERS;

        TreeMap<String, Object> data = new TreeMap<>();
        data.put(Protocol.USERNAME, userName);
        data.put(Protocol.HASH, hash);
        data.put(Protocol.SALT, salt);
        data.put(Protocol.PERMISSION, permission);

        request.data.put(userId, data);

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        // if server successfully added the user to the database, save the salt into assets/salts.map
        try {
            saveUserSalt(userId, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("done");
    }

    /**
     * Edits a user already in the database (permissions allowing)
     * @param userId The user ID of the user to edit
     * @param userName The new username for the user (or null if unchanged)
     * @param password The new password for the user (or null if unchanged)
     * @param permission The new permission(s) for the user ((or null if unchanged)
     * @throws ServerException If the user calling this doesn't have the EDIT_USERS permission
     */
    public void editUser(String userId, String userName, String password, Integer permission) throws ServerException {
        System.out.printf("\nrequesting to edit user: %s, with permission: %d ... ", userName, permission);

        // send the edited user info to the server
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_USERS;
        request.data = new TreeMap<>();

        TreeMap<String, Object> data = new TreeMap<>();
        data.put(Protocol.USERNAME, userName);
        data.put(Protocol.PERMISSION, permission);

        String salt = null;
        String hash = null;

        // if user wants to change the password, regenerate a new hash and salt
        if (password != null) {
            salt = UserAuth.generateSalt();
            hash = UserAuth.hashAndSalt(password, salt);
            data.put(Protocol.HASH, hash);
            data.put(Protocol.SALT, salt);
        }

        request.data.put(userId, data);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        // if server successfully edited user and password was changed, add the new salt to assets/salts.map
        if (password != null) {
            try {
                saveUserSalt(userId, salt);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        System.out.println("done");
    }

    /**
     * Deletes users based on a list of user ID's (permissions allowing)
     * @param userIds A list of user ID's
     * @throws ServerException If nothing in the list was deleted (invalid ID and/or bad permission)
     */
    public void deleteUsers(ArrayList<String> userIds) throws ServerException {
        System.out.printf("\nrequesting to delete user: %s... ", userIds.toString());

        // send off a list of user IDs to delete
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_USERS;
        request.data = new TreeMap<>();
        request.data.put("userList", userIds);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Deletes a user based on their user ID (permissions allowing)
     * @param userId The user ID to be deleted
     * @throws ServerException If the user wasn't deleted (invalid ID or bad permission)
     */
    public void deleteUser(String userId) throws ServerException {
        ArrayList<String> userList = new ArrayList<>();
        userList.add(userId);
        deleteUsers(userList);
    }

    /**
     * Gets a TreeMap of all users from the database (permissions allowing)
     * @return A TreeMap<String, Object> of users, e.g. {"userID1":{"username":"max", "permission":0b1100}, "userID2":{...}}
     * @throws ServerException Shouldn't be thrown
     */
    public TreeMap<String, Object> getUsers() throws ServerException {
        System.out.print("\nrequesting to get all users ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_USERS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets a TreeMap of the users specified from the database (permissions allowing)
     * @param userIds A list of user ID's to get data for
     * @return A TreeMap<String, Object> of requested users, e.g. {"userID1":{"username":"max", "permission":0b1100}, "userID2":{...}}
     * @throws ServerException If nothing in the list was retrieved
     */
    public TreeMap<String, Object> getUsers(List<String> userIds) throws ServerException {

        System.out.printf("\nrequesting to get users %s ... ", userIds.toString());

        // send a list of user IDs to get in request.data
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_USERS;
        request.sessionId = this.sessionId;
        request.data = new TreeMap<>();

        request.data.put("userList", userIds);

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets a single user from the database (permissions allowing)
     * @param userId The user ID of the user to retrieve
     * @return A TreeMap<String, Object> of the user, e.g. {"username":"max", "permission":0b1100}
     * @throws ServerException If user couldn't be retrieved
     */
    public TreeMap<String, Object> getUser(String userId) throws ServerException {
        ArrayList<String> userList = new ArrayList<>();
        userList.add(userId);

        // pull user out of server response
        return (TreeMap<String, Object>) getUsers(userList).get(userId);
    }

    /**
     * Gets a billboard ID from the database given a billboard name
     * @param boardName The name of the billboard
     * @return String The ID of the billboard
     * @throws ServerException If the billboard doesn't exist
     */
    public String getBillboardId(String boardName) throws ServerException {
        System.out.printf("\nrequesting to get billboardId of %s ...", boardName);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.BOARD_TO_ID;
        request.data.put("billboardName", boardName);
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        // pull billboard ID out of server response
        return (String) response.data.get("billboardId");
    }

    /**
     * Adds a billboard to the database (permissions allowing)
     * @param billboardName The new name of the billboard
     * @param data The billboard xml TreeMap, e.g. {"message":"new message", :"information","..."}
     * @throws ServerException If the user doesn't have the CREATE_BILLBOARDS permission, or if bad data sent
     */
    public void addBillboard(String billboardName, TreeMap<String, String> data) throws ServerException {
        System.out.printf("\nrequesting to add billboard: %s ... ", billboardName);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_BILLBOARDS;
        request.data = new TreeMap<>();

        data.put(Protocol.BOARDNAME, billboardName);

        // generate a new billboard ID
        request.data.put(UUID.randomUUID().toString(), data);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Edits billboards in the database given their existing billboard IDs (permissions allowing)
     * @param data A TreeMap<String, Object> to send, e.g. {"billboardID1":{"message":"new message"}, "billboardID2":{...}}
     * @throws ServerException If the user doesn't have the right permissions to edit the billboards, or if bad data sent
     */
    public void editBillboards(TreeMap<String, Object> data) throws ServerException {
        System.out.printf("\nrequesting to edit billboards: %s ... ", data.toString());

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_BILLBOARDS;

        request.data = data;
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Edits a billboard in the database given an existing billboard ID (permissions allowing)
     * @param billboardId The billboard ID of the billboard to be edited
     * @param newData The new billboard xml TreeMap to use
     * @throws ServerException If the user doesn't have the right permissions to edit the billboard, or if bad data sent
     */
    public void editBillboard(String billboardId, TreeMap<String, String> newData) throws ServerException {
        TreeMap<String, Object> editedBoard = new TreeMap<>();
        editedBoard.put(billboardId, newData);
        editBillboards(editedBoard);
    }

    /**
     * Deletes billboards in the database given a list of billboard IDs (permissions allowing)
     * @param billboardIds A list of billboard IDs of billboards to be deleted
     * @throws ServerException If the user doesn't have the required permissions, or if bad data sent
     */
    public void deleteBillboards(ArrayList<String> billboardIds) throws ServerException {
        System.out.printf("\nrequesting to delete billboards: %s... ", billboardIds.toString());
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_BILLBOARDS;
        request.data = new TreeMap<>();
        request.data.put("billboardList", billboardIds);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Deletes a billboard in the database given a billboard ID (permissions allowing)
     * @param billboardId The billboard ID of the billboard to be deleted
     * @throws ServerException If the billboard couldn't be deleted (bad permissions and/or bad data)
     */
    public void deleteBillboard(String billboardId) throws ServerException {
        ArrayList<String> billboardList = new ArrayList<>();
        billboardList.add(billboardId);
        deleteBillboards(billboardList);
    }

    /**
     * Gets all billboards in the database
     * @return TreeMap<String, Object> Of all billboards, e.g. {"billboardID1":{"message":"new message"}, "billboardID2":{...}}
     * @throws ServerException If fatal error occurs in server
     */
    public TreeMap<String, Object> getBillboards() throws ServerException {
        System.out.print("\nrequesting to get all billboards ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_BILLBOARDS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets billboards in the database given a list of billboard IDs
     * @param billboardIds A list of billboard IDs to retrieve data for
     * @return TreeMap<String, Object> Of the requested billboards, e.g. {"billboardID1":{"message":"new message"}, "billboardID2":{...}}
     * @throws ServerException If nothing in the list was retrieved
     */
    public TreeMap<String, Object> getBillboards(ArrayList<String> billboardIds) throws ServerException {
        System.out.printf("\nrequesting to get billboards: %s ... ", billboardIds.toString());

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_BILLBOARDS;
        request.sessionId = this.sessionId;
        request.data.put("billboardList", billboardIds);

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets a billboard from the database
     * @param billboardId The billboard ID of the billboard to be retrieved
     * @return TreeMap<String, String> of the billboard, e.g. {"message":"new message", "information":"..."}
     * @throws ServerException If the billboard doesn't exist
     */
    public TreeMap<String, String> getBillboard(String billboardId) throws ServerException {
        ArrayList<String> billboardList = new ArrayList<>(1);
        billboardList.add(billboardId);

        // pulls the billboard out of the server response
        return (TreeMap<String, String>) getBillboards(billboardList).get(billboardId);
    }

    /**
     * Gets the current billboard from the database
     * @return TreeMap<String, String> of the billboard, e.g. {"message":"new message", "information":"..."}
     * @throws ServerException If fatal error occurs in server
     */
    public TreeMap<String, String> getCurrentBillboard() throws ServerException {
        System.out.print("\nrequesting to get current billboard ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_CURRENT_BILLBOARD;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        // pulls the billboard out of the server response
        return (TreeMap<String, String>) response.data.get( response.data.firstKey() );
    }

    /**
     * Gets the schedule ID given the billboard ID it's associated with (permissions allowing)
     * @param billboardId The billboard ID associated with the schedule
     * @return String The schedule ID of the billboard
     * @throws ServerException If the schedule doesn't exist or user doesn't have SCHEDULE_BILLBOARDS permission
     */
    public String getScheduleId(String billboardId) throws ServerException {
        System.out.printf("\nrequesting to get scheduleId of %s ...", billboardId);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.BOARD_TO_SCHEDULE;
        request.data.put("billboardId", billboardId);
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        // pulls the schedule ID out of the server response
        return (String) response.data.get("scheduleId");
    }

    /**
     * Adds a schedule to the database given a formatted TreeMap
     * @param data The TreeMap of the schedule to add, e.g. {"message":"new message", "information":"..."}
     * @throws ServerException If the user doesn't have the SCHEDULE_BILLBOARDS permission
     */
    public void addSchedule(TreeMap<String, Object> data) throws ServerException {
        System.out.printf("\nrequesting to add schedule: %s... ", data.toString());

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_SCHEDULES;

        // generates a new schedule ID
        request.data.put(UUID.randomUUID().toString(), data);

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Adds a schedule to the database (permissions allowing)
     * @param billboardName The name of the billboard to associate the schedule with
     * @param schedStart The start time of the schedule
     * @param schedDurationInMins The duration of the schedule
     * @param isRecurring If the schedule is recurring ...
     * @param recurFreqInMins ... and when the schedule recurs
     * @param creatorName The name of the schedule's creator
     * @throws ServerException If user doesn't have the SCHEDULE_BILLBOARDS permission or if the schedule is invalid
     */
    public void addSchedule(String billboardName, OffsetDateTime schedStart, Integer schedDurationInMins,
                            Boolean isRecurring, Integer recurFreqInMins, String creatorName) throws ServerException {
        System.out.printf("\nrequesting to add schedule: %s... ", billboardName);


        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_SCHEDULES;

        // build the schedule treemap to send to the server
        TreeMap<String, Object> body = new TreeMap<>();

        body.put("billboardId", getBillboardId(billboardName));
        body.put("startTime", schedStart);
        body.put("duration", schedDurationInMins);
        body.put("isRecurring", isRecurring);
        body.put("recurFreqInMins", recurFreqInMins);
        body.put("creatorName", creatorName);

        // generates a schedule ID
        request.data.put(UUID.randomUUID().toString(), body);

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    @Deprecated
    public TreeMap<String, Object> scheduleCommand(String command, ArrayList<Object> data) throws ServerException {
        System.out.printf("\nrequesting schedule command: %s... ", command);
        ClientRequest request = new ClientRequest();

        TreeMap<String, Object> body = new TreeMap<>();

        body.put("command", command);
        body.put("data", data);

        // schedule below ID not needed for commands
        request.data.put(UUID.randomUUID().toString(), body);

        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        TreeMap<String, Object> clientData = (TreeMap<String, Object>) response.data.get( response.data.firstKey() );

        System.out.println("done");
        return clientData;
    }


    /**
     * Edits a schedule in the database based on it's ID (permissions allowing)
     * @param scheduleId The schedule ID of the schedule to be edited
     * @param data The TreeMap of the new schedule
     * @throws ServerException
     */
    @Deprecated
    public void editSchedule(String scheduleId, TreeMap<String, Object> data) throws ServerException {
        System.out.printf("\nrequesting to edit schedule: %s... ", scheduleId);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_SCHEDULES;
        request.data.put(scheduleId, data);

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Deletes schedule based on a list of schedule IDs (permissions allowing)
     * @param scheduleIds A list of schedule IDs of schedule to be deleted
     * @throws ServerException If nothing in the list was deleted (invalid IDs or missing SCHEDULE_BILLBOARDS permission)
     */
    public void deleteSchedules(ArrayList<String> scheduleIds) throws ServerException {
        System.out.printf("\nrequesting to delete schedules: %s ... ", scheduleIds.toString());
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_SCHEDULES;
        request.data = new TreeMap<>();
        request.data.put("scheduleList", scheduleIds);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * Deletes a schedule based on a schedule ID (permissions allowing)
     * @param scheduleId The schedule ID of the schedule to be deleted
     * @throws ServerException If the schedule doesn't exist or the user doesn't have SCHEDULE_BILLBOARDS permission
     */
    public void deleteSchedule(String scheduleId) throws ServerException {
        ArrayList<String> scheduleIds = new ArrayList<>(1);
        scheduleIds.add(scheduleId);
        deleteSchedules(scheduleIds);
    }

    /**
     * Deletes a schedule based on the billboard name and schedule start time (permissions allowing)
     * @param billboardName The name of the billboard associated with the schedule
     * @param startTime The start time of the schedule
     * @throws ServerException If nothing was deleted (schedule doesn't exist or missing SCHEDULE_BILLBOARDS permission)
     */
    public void deleteSchedule(String billboardName, OffsetDateTime startTime) throws ServerException {

        // get all schedules from database
        TreeMap<String, Object> schedules = getSchedules();

        // for each schedule
        for(Map.Entry<String, Object> schedule : schedules.entrySet()) {

            // pull schedule details from treemap
            TreeMap<String, Object> scheduleData = (TreeMap<String, Object>) schedule.getValue();

            // get values
            String scheduleId = schedule.getKey();
            String billboardNameToCheck = (String) scheduleData.get("billboardName");
            OffsetDateTime startTimeToCheck = (OffsetDateTime) scheduleData.get("startTime");

            // check if this schedule is the one to delete
            if(billboardName.equals(billboardNameToCheck) && startTime.compareTo(startTimeToCheck) == 0){
                deleteSchedule(scheduleId);
            }
        }
    }

    /**
     * Gets all schedules from the database (permissions allowing)
     * @return TreeMap<String, Object> of all schedules, e.g. {"scheduleID1":{"billboardID":"billboardID", "duration":60, ...}}
     * @throws ServerException If user doesn't have SCHEDULE_BILLBOARDS permissions
     */
    public TreeMap<String, Object> getSchedules() throws ServerException {
        System.out.print("\nrequesting to get all schedules ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_SCHEDULES;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets a list of schedules from the database (permissions allowing)
     * @param scheduleIds A list of schedule IDs to be retrieved
     * @return TreeMap<String, Object> of requested schedules, e.g. {"scheduleID1":{"billboardId":"billboardID1", "duration":60, ...}}
     * @throws ServerException If user doesn't have SCHEDULE_BILLBOARDS permissions
     */
    public TreeMap<String, Object> getSchedules(ArrayList<String> scheduleIds) throws ServerException {
        System.out.printf("\nrequesting to get schedules: %s ... ", scheduleIds);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_SCHEDULES;
        request.sessionId = this.sessionId;
        request.data = new TreeMap<>();
        request.data.put("scheduleList", scheduleIds);

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    /**
     * Gets a schedule from the database given a schedule ID (permissions allowing)
     * @param scheduleId The schedule ID of the schedule
     * @return TreeMap<String, Object> of the requested schedule, e.g. {"billboardId":"billboardID1", "duration":60, ...}
     * @throws ServerException
     */
    public TreeMap<String, Object> getSchedule(String scheduleId) throws ServerException {
        ArrayList<String> scheduleIds = new ArrayList<>(1);
        scheduleIds.add(scheduleId);

        // pull the schedule from the server response
        return (TreeMap<String, Object>) getSchedules(scheduleIds).get(scheduleId);
    }


    /**********************
     private functions
     **********************/

    /**
     * Get's the salt of a user based on a user ID from assets/salts.map
     * @param userId The user ID of the salt to be retrieved
     * @return String The salt of the user
     * @throws IOException If the file assets/salts.map isn't found
     * @throws ClassNotFoundException If the data in the file can't be cast back
     */
    private static String getUserSalt(String userId) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(saltMapPath);
        ObjectInputStream ois = new ObjectInputStream(fis);

        // read assets/salts.map into a TreeMap
        TreeMap<String, String> fileMap;
        fileMap = (TreeMap<String, String>) ois.readObject();

        ois.close();
        fis.close();

        // check if the user is in there
        if (fileMap.containsKey(userId)) {
            return fileMap.get(userId);
        } else {
            throw new IOException("user salt not in salts.map");
        }
    }

    /**
     * Adds or overwrites an existing salt for a user ID in assets/salts.map
     * @param userId The user ID associated with the salt
     * @param salt The salt to be added or to overwrite with
     * @throws IOException If the file assets/salts.map isn't found
     * @throws ClassNotFoundException If the data in the file can't be cast back
     */
    private static void saveUserSalt(String userId, String salt)
            throws IOException, ClassNotFoundException {
        TreeMap<String, String> fileMap = new TreeMap<>();

        try {
            FileInputStream fis = new FileInputStream(saltMapPath);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // read assets/salts.map into a TreeMap
            fileMap = (TreeMap<String, String>) ois.readObject();

            ois.close();
            fis.close();

            // if fileMap contains the user ID, replace the old salt
            if (fileMap.containsKey(userId)) {
                fileMap.replace(userId, salt);
            } else {
                // else, add the new salt
                fileMap.put(userId, salt);
            }
        } catch (EOFException ignored) {
            fileMap.put(userId, salt);
        }

        FileOutputStream fos = new FileOutputStream(saltMapPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        // write the edited fileMap back into salts.map
        oos.writeObject(fileMap);
        oos.flush();
        oos.close();
        fos.close();
    }

}
