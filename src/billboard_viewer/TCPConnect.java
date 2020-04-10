package billboard_viewer;

import java.io.*;
import java.net.Socket;

import billboard_server.TCPClass;
import billboard_server.TCPClient;

public class TCPConnect extends TCPClient
{
    public TCPConnect() {super();}

    public static void main(String[] args)
    {
        TCPConnect viewer = new TCPConnect();
        viewer.serverReceive();
    }

    public void serverReceive()
    {
        try
        {
            super.init("viewer");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        while(true)
        {
            try
            {
                TCPClass recv = super.readFromStream();
                System.out.println("from server: " + recv.toString());
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
