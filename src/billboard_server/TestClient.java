package billboard_server;

import connections.ClientRequest;
import connections.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.TreeMap;

public class TestClient { // dummy client used for testing network connection

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        TreeMap<String, String> billboard = new TreeMap<>();
        billboard.put("billboardName", "grant mcdonald");
        billboard.put("billboardMessage", null);
        billboard.put("billboardInfo", null);
        billboard.put("billboardImg", "img64");
        billboard.put("billboardBg", null);
        billboard.put("billboardMsgColour", null);
        billboard.put("billboardInfoColour", null);

        while(true){
            Socket socket = new Socket("localhost", 42070);

            ClientRequest cr = new ClientRequest();
            cr.type = "POST";
            cr.path = "billboards";
            cr.params = billboard;

            OutputStream out = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            Scanner scan = new Scanner(System.in);
            scan.nextLine();
            oos.writeObject(cr);
            oos.flush();
            System.out.println("sent");

            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            ServerResponse sr = (ServerResponse) ois.readObject();

            ois.close();
            oos.close();
            socket.close();
        }
    }
}
