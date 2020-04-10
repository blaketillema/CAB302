package billboard_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TCPClient
{
    private Socket socket = null;
    private ObjectInputStream inStream = null;
    private ObjectOutputStream outStream = null;
    private final int DEFAULT_PORT = 1234;
    private final String DEFAULT_IP = "localHost";

    public TCPClient() {}

    private void _init(String type, String ip, int port) {
        try
        {
            socket = new Socket(ip, port);
            System.out.println(type + "\nconnected to server\n");
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            // send type
            outStream.writeObject(type);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void init(String type)
    {
        _init(type, this.DEFAULT_IP, this.DEFAULT_PORT);
    }

    public void init(String type, int port)
    {
        _init(type, this.DEFAULT_IP, port);
    }

    public void init(String type, String ip, int port)
    {
        _init(type, ip, port);
    }

    public TCPClass readFromStream()
    {
        TCPClass input = null;

        try
        {
            input = (TCPClass) inStream.readObject();
        }
        catch(IOException e)
        {
            System.out.println("[server disconnected]\nclosing down...");
            System.exit(0);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return input;
    }

    public void writeToStream(TCPClass object)
    {
        try
        {
            outStream.writeObject(object);
        }
        catch (IOException e)
        {
            System.out.println("[server disconnected]\nclosing down...");
            System.exit(0);
        }
    }

    public void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ignored) {}
    }

}
