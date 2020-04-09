package billboard_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TCPTestControl {

    private Socket socket = null;
    private ObjectOutputStream outStream = null;
    private ObjectInputStream inStream = null;
    private final String IP = "localHost";
    private final int PORT = 1234;

    public TCPTestControl() {}

    public static void main(String[] args)
    {
        TCPTestControl client = new TCPTestControl();
        client.serverSend();
    }

    public void serverSend()
    {
        try
        {
            socket = new Socket(IP, PORT);
            System.out.println("connected to server");
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            // send type
            outStream.writeObject("control");

            // server message
            // System.out.println("server message: " + inStream.readUTF());
            int counter = 0;

            while(true)
            {
                TCPClass send = new TCPClass();
                send.muteReceiver = true;
                send.num = counter;
                counter++;
                System.out.println("sending to server: " + send.toString());
                outStream.writeObject(send);
                Thread.sleep(3000);
            }

        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

