package connections;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class serverSocket
{
    private int port;
    private ServerSocket serverSocket = null;

    public serverSocket(int port)
    {
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

    private serverResponse parseRequest(clientRequest request)
    {
        serverResponse response = new serverResponse();

        if(request.type.equals("GET"))
        {
            if(request.path.equals("/cmd/newSessionId"))
            {
                response.data.put("sessionId", genSessionId());
                response.status = "OK";
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
