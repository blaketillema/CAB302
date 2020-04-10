package billboard_control_panel;

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
        super.init("control panel");

        int counter = 0;

        while(true)
        {
            TCPClass send = new TCPClass();
            send.muteReceiver = true;
            send.num = counter;
            counter++;

            System.out.println("sending to server: " + send.toString());

            super.writeToStream(send);

            super.sleep(3000);
        }
    }
}

