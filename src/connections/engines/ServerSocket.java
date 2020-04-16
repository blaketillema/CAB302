package connections.engines;

import connections.types.TestDatabase;
import connections.tools.Tools;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.Protocol;
import connections.types.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.TreeMap;

import static connections.engines.ServerSocket.*;

public class ServerSocket
{
    private int port;
    public static TestDatabase database = null;
    public static TreeMap<String, String[]> sessionIds = null;
    public static long ONE_DAY_MS = 86400000;

    public ServerSocket(int port)
    {
        database = new TestDatabase();
        sessionIds = new TreeMap<>();
        this.port = port;
    }

    public void run()
    {
        try {
            java.net.ServerSocket serverSocket = new java.net.ServerSocket(this.port);

            System.out.printf("server running @ localhost, port %d\n", this.port);

            while(true)
            {
                Socket socket = serverSocket.accept();

                ClientThread cThread = new ClientThread(socket);
                Thread thread = new Thread(cThread);
                thread.start();
            }

        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientThread implements Runnable
{
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    public ClientThread(Socket socket) {this.socket = socket;}

    private static String genSessionId()
    {
        return java.util.UUID.randomUUID().toString();
    }

    private static ServerResponse getSessionId(String user, String hash)
    {
        String dbSalt = null;
        String dbHash = null;

        ServerResponse response = new ServerResponse();
        response.status = "OK";

        try {
            dbHash = database.getHash(user);
            dbSalt = database.getSalt(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if(UserAuth.hashAndSalt(hash, dbSalt).equals(dbHash))
        {
            String sessionId = genSessionId();
            String unixTime = String.valueOf(System.currentTimeMillis());
            sessionIds.put(sessionId, new String[] {user, unixTime});
            response.data.put("data", Tools.tMap(Protocol.Params.SESSION_ID, sessionId));
        } else {
            response.status = "invalid username or password";
        }

        return response;
    }

    private static String checkSessionId(String sessionId) throws Exception {

        String[] userAndTime;

        userAndTime = sessionIds.get(sessionId);

        long timeDiff = System.currentTimeMillis() - Long.parseLong(userAndTime[1]);

        if(timeDiff >= ONE_DAY_MS)
        {
            sessionIds.remove(sessionId);
            throw new Exception("session id has expired");
        }

        return userAndTime[0];
    }

    private static ServerResponse parseRequest(ClientRequest request)
    {
        String type = request.type;
        String path = request.path;
        String sessionId = request.sessionId;
        TreeMap<String, String> params = request.params;
        TreeMap<String, TreeMap<String, String>> data = request.data;

        ServerResponse response = new ServerResponse();
        response.status = "OK";

        // GET
        if(type.equals(Protocol.Type.GET))
        {
            // ** get new session id **
            if(path.equals(Protocol.Path.NEW_SESSION_ID))
            {
                try {
                    String user = params.get(Protocol.USER);
                    String hash = params.get(Protocol.HASH);
                    return getSessionId(user, hash);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    response.status = "user and/or hash not in database";
                }
            }

            // ** get billboards (all or current scheduled) **
            else if(path.equals(Protocol.Path.BILLBOARDS))
            {
                if(params != null && params.containsKey(Protocol.Params.CURRENT_SCHEDULED) &&
                        params.get(Protocol.Params.CURRENT_SCHEDULED).equals("true")) {
                    try {
                        response.data = database.getCurrentBillboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                } else {
                    response.data = database.getAllBillboard();
                }
            }
        }

        // POST
        else if(type.equals(Protocol.Type.POST))
        {
            // ** add new user **
            if(path.equals(Protocol.Path.USERS))
            {
                try {
                    String user;
                    if(sessionIds.isEmpty() && data.firstKey().equals("admin")) {
                        user = "admin";
                    } else {
                        user = checkSessionId(sessionId);
                    }
                    database.addUser(user, data.firstKey(), data.get(data.firstKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.status = e.getMessage();
                }
            }

            // ** add new billboard **
            else if(request.path.equals(Protocol.Path.BILLBOARDS))
            {
                try {
                    database.addBillboard(checkSessionId(sessionId), data.firstKey(), data.get(data.firstKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.status = e.getMessage();
                }
            }
        }

        return response;
    }

    @Override
    public void run() {

        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ignored) {return;}

        // read from client
        ClientRequest request = null;
        try {
            request = (ClientRequest) inStream.readObject();
        } catch (IOException ignored) {return;}
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        request.print();

        // parse msg
        ServerResponse response = parseRequest(request);

        // send response
        try {
            outStream.writeObject(response);
        } catch (IOException ignored) {return;}

        // finished
    }
}
