package billboard_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPTestViewer
{
    private Socket socket = null;
    private ObjectInputStream inStream = null;
    private ObjectOutputStream outStream = null;
    private final int SERVER_PORT = 1234;
    private final String SERVER_IP = "localHost";

    public TCPTestViewer() {}

    public static void main(String[] args)
    {
        TCPTestViewer viewer = new TCPTestViewer();
        viewer.serverReceive();
    }

    public void serverReceive()
    {
        try
        {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("connected to server");
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            // send type
            outStream.writeObject("viewer");

            // System.out.println("server message: " + inStream.readUTF());

            while(true)
            {
                TCPClass recv = (TCPClass) inStream.readObject();
                System.out.println("from server: " + recv.toString());
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
