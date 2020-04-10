package billboard_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static billboard_server.TCPServer.*;

class DisconnectException extends Exception{
    DisconnectException(){
        super();
    }
}

class InitException extends Exception{
    InitException(String s){
        super(s);
    }
}

public class TCPServer
{
    // for the control panel
    private ServerSocket serverSocket = null;
    private int PORT = 1234;
    static final int VIEWER_REFRESH_MS = 3000;
    static volatile TCPDatabase database = new TCPDatabase();
    static volatile Thread cThread = null;
    static volatile Thread vThread = null;

    public TCPServer() {}

    public TCPServer(int port) {this.PORT = port; }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.run();
    }

    public void run() throws IOException
    {
        serverSocket = new ServerSocket(PORT);

        while(true)
        {
            Socket socket = serverSocket.accept();

            ClientThread t = new ClientThread(socket);
            Thread thread = new Thread(t);
            thread.start();
        }
    }
}

class ClientThread implements Runnable
{
    ObjectInputStream inStream = null;
    ObjectOutputStream outStream = null;
    Socket socket;
    boolean isControlPanel;

    public ClientThread(Socket socket)
    {
        this.socket = socket;
    }

    private TCPClass readFromStream() throws DisconnectException
    {
        TCPClass input = null;
        try
        {
            input = (TCPClass) inStream.readObject();
        }
        catch(IOException e)
        {
            String type = this.isControlPanel ? "control panel" : "viewer";
            System.out.println("[" + type + " disconnected]");
            throw new DisconnectException();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return input;
    }

    private void writeToStream(TCPClass object) throws DisconnectException
    {
        try
        {
            outStream.writeObject(object);
        }
        catch (IOException e)
        {
            String type = this.isControlPanel ? "control panel" : "viewer";
            System.out.println("[" + type + " disconnected]");
            throw new DisconnectException();
        }
    }

    private void init() throws InitException
    {
        try
        {
            this.inStream = new ObjectInputStream(socket.getInputStream());
            this.outStream = new ObjectOutputStream(socket.getOutputStream());

            String receive = (String) inStream.readObject();
            System.out.println("[" + receive + " connected]");

            if (receive.equals("control panel"))
            {
                if (cThread == null)
                {
                    cThread = Thread.currentThread();
                }
                else
                {
                    throw new InitException("control thread already running");
                }

                this.isControlPanel = true;
            }
            else
            {
                if (vThread == null)
                {
                    vThread = Thread.currentThread();
                }
                else
                {
                   throw new InitException("viewer thread already running");
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new InitException("see error above");
        }
    }

    private void cleanup()
    {
        if (this.isControlPanel) {cThread = null; }
        else {vThread = null; }
    }

    @Override
    public void run()
    {
        try { init(); }
        catch (InitException e) { return; }

        while(true)
        {
            if(this.isControlPanel)
            {
                // wait for control panel to send object
                TCPClass input = null;
                try
                {
                    input = readFromStream();
                }
                catch(DisconnectException e) {break;}

                // msg
                System.out.println("writing to database: " + input.toString() + " (from control panel)");

                // parse the msg however you want

                // set the database if parse conditions met
                database.set(input);

                // if control panel wants to send more stuff, let it continue
                if(input.muteReceiver) continue;

                // else write something back to control panel
                try
                {
                    writeToStream(input);
                }
                catch(DisconnectException e) {break;}
            }
            else
            {
                TCPClass output = database.get();

                // msg
                System.out.println("sending to viewer: " + output.toString() + " (from database)");
                try
                {
                    writeToStream(output);
                }
                catch(DisconnectException e) {break;}

                try
                {
                    Thread.sleep(VIEWER_REFRESH_MS);
                }
                catch (InterruptedException e) {}
            }
        }
    cleanup();
    }
}
