package billboard_server;

import connections.ClientRequest;
import connections.ServerResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalTime;
import java.util.Properties;
import java.util.TreeMap;

public class Server {

    public static void main(String[] args) throws IOException{

        ServerSocket svSocket = new ServerSocket(42070); //opens server on port 42070
        Database db;
        try{
            db = new Database();
            System.out.println("Server running on port " + svSocket.getLocalPort());
            Boolean svState = true;

            while (svState) { // this will keep running until the client sends an exit command

                Socket socket = svSocket.accept(); //accept a connection

                InputStream in = socket.getInputStream(); //setup input and output streams
                ObjectInputStream ois = new ObjectInputStream(in);
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);

                ClientRequest cr = (ClientRequest) ois.readObject();
                if(db.processRequest(cr)){
                    //ServerResponse
                }

                in.close(); //close streams and sockets
                ois.close();
                out.close();
                oos.close();
                socket.close();
            }
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

}
