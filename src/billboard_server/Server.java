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

        TreeMap<String, String> user = new TreeMap<>();
        user.put("userName", "jeff");
        user.put("hash", "brown");
        user.put("salt", "pepper");
        user.put("permission", "none");

        ServerSocket svSocket = new ServerSocket(42070); //opens server on port 42070
        Database db;
        try{
            db = new Database();
            System.out.println("Server running on port " + svSocket.getLocalPort());
            Boolean svState = true;

            db.addUser(user);

            while (svState) { // this will keep running until the client sends an exit command
                Socket socket = svSocket.accept(); //accept a connection

                InputStream in = socket.getInputStream(); //setup input and output streams
                ObjectInputStream ois = new ObjectInputStream(in);

                ClientRequest cr = (ClientRequest) ois.readObject();
                if(db.processRequest(cr)){
                    OutputStream out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    ServerResponse sr = new ServerResponse();
                    oos.writeObject(sr);
                    oos.flush();
                    oos.close();
                }

                ois.close();
                in.close(); //close streams and sockets
                socket.close();
            }
        }
        catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    
    public void resetTables() throws SQLException{ //resets the tables. mostly for testing. TODO: probably remove this in the final product
        connect();
        statement.executeQuery("DROP table users");
        statement.executeQuery("DROP table billboards");
        statement.executeQuery("DROP table schedule");
        conn.close();
    }




    // SCHEDULER DB COMMANDS
    // TODO figure out best place to execute commands for scheduler
    public void executeSQL(String SQLCommand) throws SQLException {
        connect();
        statement.executeQuery(SQLCommand);
        conn.close();
    }

    public ResultSet getSQLResult(String SQLCommand) throws SQLException {
        ResultSet rs = statement.executeQuery(SQLCommand);
        return rs;
    }
    // END Scheduler DB commands

}
