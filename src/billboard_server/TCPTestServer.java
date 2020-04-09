package billboard_server;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static billboard_server.TCPTestServer.*;

public class TCPTestServer
{
    // for the control panel
    private ServerSocket serverSocket = null;
    private final int PORT = 1234;
    private ObjectInputStream inStream = null;
    private ObjectOutputStream outStream = null;
    static final int VIEWER_REFRESH_MS = 3000;
    static volatile TCPDatabase database = new TCPDatabase();

    public TCPTestServer() {}

    public static void main(String[] args) throws IOException {
        TCPTestServer server = new TCPTestServer();
        server.run();
    }

    public void run() throws IOException
    {
        serverSocket = new ServerSocket(PORT);

        while(true) {

            try
            {
                System.out.println("accepting clients...");
                Socket socket = serverSocket.accept();
                System.out.println("client has connected -> " + socket);

                TCPClientThread t = new TCPClientThread(socket);
                Thread thread = new Thread(t);
                thread.start();
                System.out.println("client thread started");
            }
            catch (EOFException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}

class TCPClientThread implements Runnable
{
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;
    boolean isControlPanel;

    public TCPClientThread(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            this.inStream = new ObjectInputStream(socket.getInputStream());
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        try
        {
            String receive = (String) inStream.readObject();
            System.out.println("connected: " + receive);

            if (receive.equals("control"))
            {
                this.isControlPanel = true;
            }

            String msg = "welcome to server";
            // outStream.writeUTF(msg);

            while(true)
            {
                if(this.isControlPanel)
                {
                    // wait for control panel to send object
                    TCPClass input = (TCPClass) inStream.readObject();

                    // msg
                    System.out.println("received from control: " + input.toString() + ", writing to database");

                    // parse the msg however you want

                    // set the database if parse conditions met
                    database.set(input);

                    // if control panel wants to send more stuff, let it continue
                    if(input.muteReceiver) continue;

                    // else write something back to control panel
                    outStream.writeObject(input);
                }
                else
                {
                    TCPClass test = database.get();
                    outStream.writeObject(test);
                    try
                    {
                        Thread.sleep(VIEWER_REFRESH_MS);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
