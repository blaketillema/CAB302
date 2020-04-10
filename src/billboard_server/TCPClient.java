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
    private int SERVER_PORT = 1234;
    private String SERVER_IP = "localHost";
    private String CLIENT_TYPE = null;

    public TCPClient() {}

    private void _init(String type, String ip, int port) throws IOException
    {

        socket = new Socket(ip, port);
        System.out.println(type + "\nconnected to server\n");
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());

        // send type
        outStream.writeObject(type);
    }

    public void init(String type) throws IOException
    {
        _init(type, this.SERVER_IP, this.SERVER_PORT);
    }

    public void init(String type, int port) throws IOException
    {
        _init(type, this.SERVER_IP, port);
    }

    public void init(String type, String ip, int port) throws IOException
    {
        _init(type, ip, port);
    }

    public TCPClass readFromStream() throws IOException, ClassNotFoundException
    {
        return (TCPClass) inStream.readObject();
    }

    public void writeToStream(TCPClass object) throws IOException
    {
        outStream.writeObject(object);
    }

    public void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException e)
        {

        }
    }

}
