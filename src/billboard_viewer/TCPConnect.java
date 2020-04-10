package billboard_viewer;

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
        super.init("viewer");

        while(true)
        {
            TCPClass recv = super.readFromStream();
            System.out.println("from server: " + recv.toString());
        }
    }
}
