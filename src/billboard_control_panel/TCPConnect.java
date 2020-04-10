package billboard_control_panel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import billboard_server.TCPClass;
import billboard_server.TCPClient;

public class TCPConnect extends TCPClient{

    public TCPConnect() { super(); }

    public static void main(String[] args)
    {
        TCPConnect client = new TCPConnect();
        client.talkToServer();
    }

    public void talkToServer()
    {
        try
        {
            super.init("control");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        int counter = 0;

        while(true)
        {
            TCPClass send = new TCPClass();
            send.muteReceiver = true;
            send.num = counter;
            counter++;

            System.out.println("sending to server: " + send.toString());

            try
            {
                super.writeToStream(send);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

            super.sleep(3000);
        }
    }
}

