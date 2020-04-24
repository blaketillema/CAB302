package billboard_server;

import java.io.*;
import java.net.Socket;

public class TestClient { // dummy client used for testing network connection

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 42070);

        OutputStream out = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);

        oos.writeUTF("exit");
        oos.flush();

        socket.close();
    }

}
