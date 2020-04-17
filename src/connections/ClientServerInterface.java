package connections;

import connections.engines.ServerClientConnection;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.exceptions.ServerException;
import connections.types.ServerResponse;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TreeMap;

/************************************************

    ClientServerInterface
    - must be instantiated... new ClientServerInterface()
    - if new user, use addNewUser() function (but only once - or until database is wiped)
        - (currently it throws an exception if the server gets asked this more than once - ill fix this eventually)
    - must use login() if you want to do more than just view billboards

************************************************/
public class ClientServerInterface
{
    private String ip;
    private int port;
    private String sessionId;
    private static final String saltMapPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "salts.map").toString();

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "network.props").toString();

    /****************
     * initialiser
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
        }
        catch(IOException ioe){ //if network.props can't be found
            this.ip = "localHost";
            this.port = 1234;
            System.out.println("network properties file not found");
            ioe.printStackTrace();
        }
    }

    /*********************

     PUBLIC FUNCTIONS
     ********************/
    public void addNewUser(String user, String password, String permission) throws ServerException {
        System.out.printf("requesting to add user: %s, with permission: %s ... ", user, permission);

        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);

        // add to client
        try {
            saveUserSalt(user, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.USERS;
        request.data = new TreeMap<>();

        TreeMap<String, String> data = new TreeMap<>();
        data.put(Protocol.HASH, hash);
        data.put(Protocol.SALT, salt);
        data.put(Protocol.PERMISSION, permission);

        request.data.put(user, data);
        request.sessionId = this.sessionId;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /****************
     * server login
     ****************/
    public void login(String user, String password) throws ServerException {
        System.out.printf("requesting to login user: %s ... ", user);

        String salt;

        try {
            salt = getUserSalt(user);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String hash = UserAuth.hashAndSalt(password, salt);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.NEW_SESSION_ID;
        request.params = new TreeMap<>();
        request.params.put(Protocol.USER, user);
        request.params.put(Protocol.HASH, hash);

        ServerResponse response;

        response = ServerClientConnection.request(this.ip, this.port, request);

        try {
            this.sessionId = response.data.get("data").get(Protocol.Params.SESSION_ID);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

        System.out.println("done");
    }

    /****************
     * get current billboard
     ****************/
    public TreeMap<String, String> getCurrentBillboard() throws ServerException {
        System.out.println("requesting to get current billboard ... ");

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.BILLBOARDS;
        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.CURRENT_SCHEDULED, "true");
        request.sessionId = this.sessionId;

        ServerResponse response;

        response = ServerClientConnection.request(this.ip, this.port, request);

        try {
            return response.data.firstEntry().getValue();
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    /****************
     * get all billboards
     ****************/
    public TreeMap<String, TreeMap<String, String>> getAllBillboards() throws ServerException {
        System.out.println("requesting to get all billboards ... ");

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);
        return response.data;
    }

    /****************
     * get all users
     ****************/
    public TreeMap<String, TreeMap<String, String>> getAllUsers() throws ServerException {
        System.out.println("requesting to get all users ... ");

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.USERS;
        request.sessionId = this.sessionId;

        ServerResponse response = ServerClientConnection.request(this.ip, this.port, request);
        return response.data;
    }

    /****************
     * add billboard/s
     ****************/
    public void addBillboard(String name, TreeMap<String, String> data) throws ServerException {
        System.out.printf("requesting to add billboard %s ... ", name);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.data = new TreeMap<>();
        request.data.put(name, data);

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    public void addBillboards(TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        System.out.printf("requesting to add billboards %s ... ", data);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.data = data;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /****************
     * remove billboard/s
     ****************/
    public void removeBillboards(TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        System.out.printf("requesting to remove billboards %s ... ", data.keySet());

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.DELETE;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.data = data;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }


    /**********************

     PRIVATE FUNCTIONS
     **********************/
    private static String getUserSalt(String user) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(saltMapPath);
        ObjectInputStream ois = new ObjectInputStream(fis);

        TreeMap<String, String> fileMap;
        fileMap = (TreeMap<String,String>) ois.readObject();

        ois.close();
        fis.close();

        if(fileMap.containsKey(user)) {
            return fileMap.get(user);
        } else {
            throw new IOException("user salt not in salts.map");
        }
    }

    private static void saveUserSalt(String user, String salt)
            throws IOException, ClassNotFoundException
    {
        TreeMap<String,String> fileMap = new TreeMap<>();

        try
        {
            FileInputStream fis = new FileInputStream(saltMapPath);
            ObjectInputStream ois = new ObjectInputStream(fis);

            fileMap = (TreeMap<String,String>) ois.readObject();

            ois.close();
            fis.close();

            if(fileMap.containsKey(user)) {
                fileMap.replace(user, salt);
            } else {
                fileMap.put(user, salt);
            }
        } catch (EOFException ignored) {
            fileMap.put(user, salt);
        }

        FileOutputStream fos = new FileOutputStream(saltMapPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(fileMap);
        oos.flush();
        oos.close();
        fos.close();
    }
}
