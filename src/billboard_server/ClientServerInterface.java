package billboard_server;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import billboard_server.Protocol.*;
import billboard_server.engines.ServerClientConnection;
import billboard_server.exceptions.ServerException;
import billboard_server.tools.UserAuth;
import billboard_server.types.ClientRequest;
import billboard_server.types.ServerResponse;

/**
 * @author Max Ferguson
 */
public class ClientServerInterface {

    private String ip;
    private int port;
    private long sessionId;
    private static final String saltMapPath =
            Paths.get(System.getProperty("user.dir"), "src", "billboard_server", "assets", "salts.map").toString();

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "billboard_server", "assets", "network.props").toString();

    /**
     *
     */
    public ClientServerInterface() {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(networkPath);
            props.load(in);
            in.close();

            this.ip = props.getProperty("ip");
            this.port = Integer.parseInt(props.getProperty("port"));
        } catch (IOException ioe) { //if network.props can't be found
            this.ip = "localHost";
            this.port = 1234;
            System.out.println("network properties file not found");
            ioe.printStackTrace();
        }
    }

    /**
     * @param userName the username of the user
     * @param password the password of the user
     * @throws ServerException if username/password are invalid
     */
    public void login(String userName, String password) throws ServerException {
        System.out.printf("\nrequesting to login user: %s ... ", userName);

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

        System.out.println(userId);
        String salt;

        try {
            salt = getUserSalt(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String hash = UserAuth.hashAndSalt(password, salt);

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
     * @param userName username of user
     * @return String              user ID of user
     * @throws ServerException if user doesn't exist
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
     * @param userName   username of user
     * @param password   password of user
     * @param permission permissions of user, as specified in Protocol.Permission
     * @throws ServerException if user couldn't be added (permission denied or other)
     */
    public void addUser(String userName, String password, int permission) throws ServerException {

        System.out.printf("\nrequesting to add user: %s, with permission: %d ... ", userName, permission);

        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);
        String userId = java.util.UUID.randomUUID().toString();

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

        // add salt to salts.map
        try {
            saveUserSalt(userId, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("done");
    }

    /**
     * @param userId     user ID of user (must not be null)
     * @param userName   new username for user ID (null = unchanged)
     * @param password   new password for user ID (null = unchanged)
     * @param permission new permissions for user ID (null = unchanged)
     * @throws ServerException if user couldn't be edited (permission denied or user doesn't exist)
     */
    public void editUser(String userId, String userName, String password, Integer permission) throws ServerException {
        System.out.printf("\nrequesting to edit user: %s, with permission: %d ... ", userName, permission);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_USERS;
        request.data = new TreeMap<>();

        TreeMap<String, Object> data = new TreeMap<>();
        data.put(Protocol.USERNAME, userName);
        data.put(Protocol.PERMISSION, permission);

        String salt = null;
        String hash = null;
        if (password != null) {
            salt = UserAuth.generateSalt();
            hash = UserAuth.hashAndSalt(password, salt);
            data.put(Protocol.HASH, hash);
            data.put(Protocol.SALT, salt);
        }

        request.data.put(userId, data);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        if (password != null) {
            // add salt to salts.map
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
     * @param userIds user ID's to be deleted
     * @throws ServerException If every user in list couldn't be deleted
     */
    public void deleteUsers(ArrayList<String> userIds) throws ServerException {

        System.out.printf("\nrequesting to delete user: %s... ", userIds.toString());
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_USERS;
        request.data = new TreeMap<>();
        request.data.put("userList", userIds);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * @param userId user ID of user to be deleted
     * @throws ServerException if user couldn't be deleted (permission denied or doesn't exist)
     */
    public void deleteUser(String userId) throws ServerException {
        ArrayList<String> userList = new ArrayList<>();
        userList.add(userId);
        deleteUsers(userList);
    }

    /**
     * @return TreeMap<String, Object>     treemap of user treemaps for all users in the database, with userId as the key
     * @throws ServerException if unknown error occurs
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
     * @param userIds user ID's to be retrieved
     * @return TreeMap<String, Object>     treemap of user treemaps for requested users in the database, with userId as the key
     * @throws ServerException if none of the requested users were found
     */
    public TreeMap<String, Object> getUsers(List<String> userIds) throws ServerException {

        System.out.printf("\nrequesting to get users %s ... ", userIds.toString());

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
     * @param userId user ID of user
     * @return TreeMap<String, Object>     treemap of user data for user requested
     * @throws ServerException if user doesn't exist
     */
    public TreeMap<String, Object> getUser(String userId) throws ServerException {
        ArrayList<String> userList = new ArrayList<>();
        userList.add(userId);
        return (TreeMap<String, Object>) getUsers(userList).get(userId);
    }

    /**
     * @param boardName name of billboard
     * @return String              ID of billboard
     * @throws ServerException if billboard doesn't exist
     */
    public String getBillboardId(String boardName) throws ServerException {
        System.out.printf("\nrequesting to get billboardId of %s ...", boardName);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.BOARD_TO_ID;
        request.data.put("billboardName", boardName);
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return (String) response.data.get("billboardId");
    }

    /**
     * @param billboardName name of billboard to be added
     * @param data          billboard data treemap
     * @throws ServerException if billboard couldn't be added (permission denied or unknown error)
     */
    public void addBillboard(String billboardName, TreeMap<String, String> data) throws ServerException {
        System.out.printf("\nrequesting to add billboard: %s ... ", billboardName);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_BILLBOARDS;
        request.data = new TreeMap<>();

        data.put(Protocol.BOARDNAME, billboardName);
        request.data.put(UUID.randomUUID().toString(), data);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * @param data treemap of billboard treemaps, with billboardId as the key
     * @throws ServerException if billboards couldn't be edited (permission denied or unknown error)
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
     * @param billboardId billboard ID to be edited
     * @param newData     billboard treemap of values to overwrite the old data with
     * @throws ServerException if billboard couldn't be edited (permission denied or unknown error)
     */
    public void editBillboard(String billboardId, TreeMap<String, String> newData) throws ServerException {
        TreeMap<String, Object> editedBoard = new TreeMap<>();
        editedBoard.put(billboardId, newData);
        editBillboards(editedBoard);
    }

    /**
     * @param billboardIds billboard ID's to be deleted
     * @throws ServerException if billboards couldn't be deleted (permission error)
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
     * @param billboardId billboard ID to be deleted
     * @throws ServerException if billboard couldn't be deleted
     */
    public void deleteBillboard(String billboardId) throws ServerException {
        ArrayList<String> billboardList = new ArrayList<>();
        billboardList.add(billboardId);
        deleteBillboards(billboardList);
    }

    /**
     * @return TreeMap<String, Object>     treemap of all billboard treemaps, with billboardId as key
     * @throws ServerException if unknown error occurs
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
     * @param billboardIds billboard ID's to be retrieved
     * @return TreeMap<String, Object>     treemap of requested billboard treemaps, with billboardId as key
     * @throws ServerException if all billboards requested don't exist
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
     * @param billboardId billboard ID to be retrieved
     * @return TreeMap<String, String>     billboard treemap of requested billboard
     * @throws ServerException if billboard doesn't exist
     */
    public TreeMap<String, String> getBillboard(String billboardId) throws ServerException {
        ArrayList<String> billboardList = new ArrayList<>(1);
        billboardList.add(billboardId);
        return (TreeMap<String, String>) getBillboards(billboardList).get(billboardId);
    }

    /**
     * @return TreeMap<String, String>     billboard treemap of current billboard
     * @throws ServerException if no billboard is scheduled
     */
    public TreeMap<String, String> getCurrentBillboard() throws ServerException {
        System.out.print("\nrequesting to get current billboard ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_CURRENT_BILLBOARD;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return (TreeMap<String, String>) response.data.firstEntry();
    }

    /**
     * @param billboardId billboard ID associated with schedule
     * @return String              schedule ID
     * @throws ServerException if schedule doesn't exist
     */
    public String getScheduleId(String billboardId) throws ServerException {
        System.out.printf("\nrequesting to get scheduleId of %s ...", billboardId);

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.BOARD_TO_SCHEDULE;
        request.data.put("billboardId", billboardId);
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return (String) response.data.get("scheduleId");
    }

    /**
     * @param data schedule treemap (with all values)
     * @throws ServerException if schedule couldn't be added (permission error)
     */
    public void addSchedule(TreeMap<String, Object> data) throws ServerException {
        System.out.printf("\nrequesting to add schedule: %s... ", data.toString());

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_SCHEDULES;
        request.data.put(UUID.randomUUID().toString(), data);

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /**
     * @param scheduleId schedule ID to be edited
     * @param data       schedule treemap (with only values to be overwritten)
     * @throws ServerException if schedule couldn't be edited (permission error)
     */
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
     * @param scheduleIds schedule ID's to be deleted
     * @throws ServerException if all requested schedule's weren't deleted (permission or don't exist)
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
     * @param scheduleId schedule ID to be deleted
     * @throws ServerException if schedule couldn;t be deleted (permission or doesn't exists)
     */
    public void deleteSchedule(String scheduleId) throws ServerException {
        ArrayList<String> scheduleIds = new ArrayList<>(1);
        scheduleIds.add(scheduleId);
        deleteSchedules(scheduleIds);
    }

    /**
     * @return TreeMap<String, Object>     treemap of schedule treemaps, with scheduleId as key
     * @throws ServerException if unknown error occurs
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
     * @param scheduleIds schedule ID's to be retrieved
     * @return TreeMap<String, Object>     treemap of requested schedule treemaps, with scheduleId as key
     * @throws ServerException if no schedule requested exists
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
     * @param scheduleId schedule ID to be retrieved
     * @return TreeMap<String, Object>     schedule treemap
     * @throws ServerException if schedule doesn't exist
     */
    public TreeMap<String, Object> getSchedule(String scheduleId) throws ServerException {
        ArrayList<String> scheduleIds = new ArrayList<>(1);
        scheduleIds.add(scheduleId);
        return (TreeMap<String, Object>) getSchedules(scheduleIds).get(scheduleId);
    }


    /**********************
     private functions
     **********************/

    /**
     * @param userId user ID
     * @return String                      salt of user ID from assets/salts.map
     * @throws IOException            if salts.map doesn't exist, or if user doesn't exist within it
     * @throws ClassNotFoundException ObjectInputStream exception if class to cast to isn't found
     */
    private static String getUserSalt(String userId) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(saltMapPath);
        ObjectInputStream ois = new ObjectInputStream(fis);

        TreeMap<String, String> fileMap;
        fileMap = (TreeMap<String, String>) ois.readObject();

        ois.close();
        fis.close();

        if (fileMap.containsKey(userId)) {
            return fileMap.get(userId);
        } else {
            throw new IOException("user salt not in salts.map");
        }
    }

    /**
     * @param userId user ID
     * @param salt   salt of user ID
     * @throws IOException            if assets/salts.map doesn't exist
     * @throws ClassNotFoundException ObjectInputStream exception if class to cast to isn't found
     */
    private static void saveUserSalt(String userId, String salt)
            throws IOException, ClassNotFoundException {
        TreeMap<String, String> fileMap = new TreeMap<>();

        try {
            FileInputStream fis = new FileInputStream(saltMapPath);
            ObjectInputStream ois = new ObjectInputStream(fis);

            fileMap = (TreeMap<String, String>) ois.readObject();

            ois.close();
            fis.close();

            if (fileMap.containsKey(userId)) {
                fileMap.replace(userId, salt);
            } else {
                fileMap.put(userId, salt);
            }
        } catch (EOFException ignored) {
            fileMap.put(userId, salt);
        }

        FileOutputStream fos = new FileOutputStream(saltMapPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(fileMap);
        oos.flush();
        oos.close();
        fos.close();
    }

}
