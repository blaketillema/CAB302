package connections;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import connections.Protocol.*;
import connections.engines.ServerClientConnection;
import connections.exceptions.PermissionException;
import connections.exceptions.ServerException;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.types.ServerResponse;

public class ClientServerInterface {

    private String ip;
    private int port;
    private long sessionId;
    private static final String saltMapPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "salts.map").toString();

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "network.props").toString();

    /****************
     * initialiser (reads network.prop)
     ****************/
    public ClientServerInterface()
    {
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

    public void addUser(String userName, String password, int permission) throws ServerException {

        System.out.printf("\nrequesting to add user: %s, with permission: %d ... ", userName, permission);

        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);
        String userId = java.util.UUID.randomUUID().toString();

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_USERS;
        request.data = new TreeMap<>();

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

    public void deleteUser(String userId) throws ServerException {

        System.out.printf("\nrequesting to delete user: %s... ", userId);
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_USERS;
        request.data = new TreeMap<>();
        request.data.put(userId, null);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public TreeMap<String, Object> getUsers() throws ServerException {

        System.out.print("\nrequesting to get all users ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_USERS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    public TreeMap<String, Object> getUsers(List<String> userIds) throws ServerException {

        System.out.print("\nrequesting to get all users ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_USERS;
        request.sessionId = this.sessionId;
        request.data = new TreeMap<>();

        for (String userId : userIds) {
            request.data.put(userId, null);
        }

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

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

    public void editBillboards(TreeMap<String, Object> data) throws ServerException {
        System.out.print("\nrequesting to edit billboards ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_BILLBOARDS;
        // request.data = new TreeMap<>();

        request.data = data;
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public void deleteBillboard(String billboardId) throws ServerException {
        System.out.printf("\nrequesting to delete billboard: %s... ", billboardId);
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_USERS;
        request.data = new TreeMap<>();
        request.data.put(billboardId, null);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public TreeMap<String, Object> getBillboards() throws ServerException {

        System.out.print("\nrequesting to get all billboards ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_BILLBOARDS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    public TreeMap<String, Object> getCurrentBillboard() throws ServerException {

        System.out.print("\nrequesting to get current billboard ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_CURRENT_BILLBOARD;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }

    public void addSchedules(TreeMap<String, Object> data) throws ServerException {

        System.out.print("\nrequesting to add schedules ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.ADD_SCHEDULES;
        request.data = data;

        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public void deleteSchedule(String billboardId) throws ServerException {

        System.out.printf("\nrequesting to delete schedule associated with billboardId: %s ... ", billboardId);
        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.DELETE_SCHEDULES;
        request.data = new TreeMap<>();
        request.data.put(billboardId, null);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public TreeMap<String, Object> getSchedules() throws ServerException {

        System.out.print("\nrequesting to get all schedules ... ");

        ClientRequest request = new ClientRequest();

        request.cmd = Cmd.GET_SCHEDULES;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");

        return response.data;
    }


    /**********************

     private functions
     **********************/
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
