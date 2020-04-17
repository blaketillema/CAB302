package connections;

import connections.engines.ServerClientConnection;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.exceptions.ServerException;
import connections.types.ServerResponse;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
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

    /****************
     * add a new user
     *
     * input: username, password and permission (use Protocol.Permission class)
     ****************/
    public void addNewUser(String user, String password, String permission) throws ServerException {

        System.out.printf("requesting to add user: %s, with permission: %s ... ", user, permission);

        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);

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

        // add salt to salts.map
        try {
            saveUserSalt(user, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("done");
    }

    /****************
     * modify a user
     *
     * input: username, password and permission (use Protocol.Permission class)
     ****************/
    public void modifyUser(String originalUser, String user, String password, String permission) throws ServerException {

        System.out.printf("requesting to modify user: %s -> %s with new permission: %s ... ", originalUser, user, permission);

        String salt = UserAuth.generateSalt();
        String hash = UserAuth.hashAndSalt(password, salt);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.USERS;
        request.data = new TreeMap<>();

        TreeMap<String, String> data = new TreeMap<>();
        data.put(Protocol.HASH, hash);
        data.put(Protocol.SALT, salt);
        data.put(Protocol.PERMISSION, permission);
        data.put("renameTo", user);

        request.data.put(originalUser, data);
        request.sessionId = this.sessionId;

        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.INTENT, Protocol.Params.Intent.EDIT_USERS);

        ServerClientConnection.request(this.ip, this.port, request);

        // add salt to salts.map
        try {
            saveUserSalt(user, salt);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("done");
    }

    /****************
     * server login
     *
     * input: username and password
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
     *
     * returns: <billboard treemap>
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
     *
     * returns: <billboard title/s, <billboard treemap/s>>
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
     *
     * returns: <username/s, <keys, values>>
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
     * send one new billboard
     *
     * input: user that created it, billboard title and billboard treemap
     ****************/
    public void sendNewBillboard(String user, String title, TreeMap<String, String> data) throws ServerException {
        TreeMap<String, TreeMap<String, String>> body = new TreeMap<>();
        data.put("createdBy", user);
        body.put(title, data);
        sendNewBillboards(body);
    }

    /****************
     * send many new billboards
     *
     * input: lists of users, titles and billboard treemaps
     ****************/
    public void sendNewBillboards(List<String> users, List<String> titles, List<TreeMap<String, String>> data) throws ServerException {
        if (users.size() != titles.size() || users.size() != data.size()) {
            throw new ServerException("list sizes are not equal");
        }

        for (int i = 0; i < users.size(); i++) {
            TreeMap<String, TreeMap<String, String>> body = new TreeMap<>();
            data.get(i).put("createdBy", users.get(i));
            body.put(titles.get(i), data.get(i));
            sendNewBillboards(body);
        }
    }

    /****************
     * send many new billboards
     *
     * input: treemap (see the function above to see what is required for this treemap)
     ****************/
    public void sendNewBillboards(TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        System.out.printf("requesting to add billboards %s ... ", data);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.data = data;

        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.INTENT, Protocol.Params.Intent.ADD_BILLBOARD);

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /****************
     * send billboards to overwrite the ones in the database
     *
     * input: treemap
     ****************/
    public void sendEditedBillboards(TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        System.out.printf("requesting to modify billboards %s ... ", data);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.data = data;

        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.INTENT, Protocol.Params.Intent.EDIT_BILLBOARD);

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /****************
     * send schedules for billboards currently in database
     *
     * input: treemap
     ****************/
    public void sendSchedules(TreeMap<String, TreeMap<String, String>> data) throws ServerException {
        System.out.printf("requesting to modify schedule %s ... ", data);

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.sessionId = this.sessionId;

        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.INTENT, Protocol.Params.Intent.EDIT_SCHEDULE);

        request.data = data;

        ServerClientConnection.request(this.ip, this.port, request);

        System.out.println("done");
    }

    /****************
     * remove billboard/s
     ****************/
    @Deprecated
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

     private functions
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
