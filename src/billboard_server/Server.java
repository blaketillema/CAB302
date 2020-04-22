package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalTime;
import java.util.Properties;

public class Server {

    public static void main(String[] args) throws IOException{

        ServerSocket svSocket = new ServerSocket(42070); //opens server on port 42070
        Database db;
        try{
            db = new Database();
            System.out.println("Server running on port " + svSocket.getLocalPort());
            Boolean svState = true;

            //test functions
            db.addUser("name", "hash", "salt", "permissions");
            db.deleteUser("name");
            db.addBillboard("bilby name", "12340987asdfhjkLIGFBWf=");
            db.addSchedule("bilby name", LocalTime.parse("20:30"));
            db.deleteSchedule(LocalTime.parse("20:30"));
            db.deleteBillboard("bilby name");


            while (svState) { // this will keep running until the client sends an exit command

                Socket socket = svSocket.accept(); //accept a connection

                InputStream in = socket.getInputStream(); //setup input and output streams
                ObjectInputStream ois = new ObjectInputStream(in);

                in.close(); //close streams and sockets
                ois.close();
                socket.close();
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
        }

    }

}
