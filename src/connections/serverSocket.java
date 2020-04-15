package connections;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.TreeMap;

public class serverSocket
{
    private int port;
    private ServerSocket serverSocket = null;
    public static TestDatabase database = null;

    public serverSocket(int port)
    {
        database = new TestDatabase();
        this.port = port;
    }

    public void run()
    {
        try {
            serverSocket = new ServerSocket(this.port);

            while(true)
            {
                Socket socket = serverSocket.accept();

                clientThread cThread = new clientThread(socket);
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

class clientThread implements Runnable
{
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    Socket socket = null;

    public clientThread(Socket socket) {this.socket = socket;}

    private String genSessionId()
    {
        return "ejsefeafa";
    }

    private serverResponse parseAuthRequest(String user, String hash)
    {
        String dbSalt = null;
        String dbHash = null;

        serverResponse response = null;

        try {
            dbHash = serverSocket.database.getHash(user);
            dbSalt = serverSocket.database.getSalt(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String doubleHash = userAuth.hashAndSalt(hash, dbSalt);

        if(doubleHash.equals(dbHash)){
            response = serverResponse.buildResponse("OK", new String[][] {{"sessionId", genSessionId()}});
        }

        else
        {
            response = serverResponse.buildResponse("invalid username or password", null);
        }

        return response;
    }

    private serverResponse parseRequest(clientRequest request)
    {
        serverResponse response = new serverResponse();

        if(request.type.equals("GET"))
        {
            if(request.path.equals("/cmd/newSessionId"))
            {
                return parseAuthRequest(request.data.get("user"), request.data.get("hash"));
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
        clientRequest request = null;
        try {
            request = (clientRequest) inStream.readObject();
        } catch (IOException ignored) {return;}
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        request.print();

        // parse msg
        serverResponse response = parseRequest(request);

        // send response
        try {
            outStream.writeObject(response);
        } catch (IOException ignored) {return;}

        // finished
    }
}
