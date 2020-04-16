package connections;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static connections.ServerSocket.*;

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

        String[] userAndTime = sessionIds.get(sessionId);

        long timeDiff = System.currentTimeMillis() - Long.parseLong(userAndTime[1]);

        if(timeDiff >= ONE_DAY_MS)
        {
            sessionIds.remove(sessionId);
            throw new Exception();
        }

        return userAndTime[0];
    }

    private static boolean checkPermissions(String sessionId, String permissionNeeded)
    {
        try
        {
            String user = checkSessionId(sessionId);
            String permission = database.getPermission(user);

            return permission.equals(permissionNeeded);
        }
        catch(Exception e) {e.printStackTrace();}

        return false;
    }

    private static ServerResponse parseRequest(ClientRequest request)
    {
        ServerResponse response = new ServerResponse();
        response.status = "OK";

        // GET
        if(request.type.equals(Protocol.Type.GET))
        {
            if(request.path.equals(Protocol.Path.NEW_SESSION_ID))
            {
                try
                {
                    String user = request.params.get(Protocol.USER);
                    String hash = request.params.get(Protocol.HASH);
                    return getSessionId(user, hash);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    response.status = "user and/or hash not in database";
                }
            }

            else if(request.path.equals(Protocol.Path.BILLBOARDS))
            {
                if(request.params.containsKey(Protocol.Params.CURRENT_SCHEDULED) &&
                        request.params.get(Protocol.Params.CURRENT_SCHEDULED).equals("true"))
                {
                    try {
                        response.data = database.getCurrentBillboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                }
                else
                {
                    response.data = database.getAllBillboard();
                }
            }
        }

        // POST
        else if(request.type.equals(Protocol.Type.POST))
        {
            if(request.path.equals(Protocol.Path.USERS))
            {
                try
                {
                    database.addUsers(request.data);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.status = e.getMessage();
                }
            }

            else if(request.path.equals(Protocol.Path.BILLBOARDS))
            {
                String sessionId;
                try
                {
                    sessionId = request.params.get(Protocol.Params.SESSION_ID);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    response.status = "missing sessionId";
                    return response;
                }

                if(checkPermissions(sessionId, Protocol.Permission.EDIT_USERS))
                {
                    try
                    {
                        database.addBillboards(request.data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.status = e.getMessage();
                    }
                } else {
                    response.status = "invalid permissions to add billboard";
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
