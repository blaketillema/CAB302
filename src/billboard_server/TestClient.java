package billboard_server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient { // dummy client used for testing network connection

    public static void main(String[] args) throws IOException {
        while(true){
            Socket socket = new Socket("localhost", 42070);

            OutputStream out = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);

            Scanner in = new Scanner(System.in);
            String s = in.nextLine();

            oos.writeUTF(s);
            if(s.equals("close")){
                break;
            }
            oos.flush();

            socket.close();
        }

    }

}
