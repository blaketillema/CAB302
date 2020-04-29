package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Properties;

public class Server {

    public static void main(String[] args) throws IOException, SQLException {

        ServerSocket svSocket = new ServerSocket(42070); //opens server on port 42070
        Server sv = new Server(); //creates a new server object TODO: will i even need this anymore
        System.out.println("Server running on port " + svSocket.getLocalPort());
        Boolean svState = true;

        while(svState){ // this will keep running until the client sends an exit command

            Socket socket = svSocket.accept(); //accept a connection
            System.out.println("Connected to " + socket.getInetAddress());

            InputStream in = socket.getInputStream(); //setup input and output streams
            ObjectInputStream ois = new ObjectInputStream(in);
            String inString = ois.readUTF(); //read input bytes to a string
            System.out.println("Client: " + inString);

            if(inString.equals("exit")){ //if the string reads exit, close the server TODO: handle more than one command
                System.out.println("Server shutting down. Goodbye.");
                svState = false;
            }

            in.close(); //close streams and sockets
            ois.close();
            socket.close();

        }

    }

    private String url;
    private String schema;
    private String username;
    private String password;
    private Connection conn;
    private Statement statement;

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( userId INT NOT NULL PRIMARY KEY AUTO_INCREMENT, userName VARCHAR(255) NOT NULL ); ";
    private static final String BILLBOARDS_TABLE = "CREATE TABLE IF NOT EXISTS billboards ( billboardId INT NOT NULL PRIMARY KEY AUTO_INCREMENT, image MEDIUMTEXT CHARACTER SET BINARY )"; //TODO: write a proper statement to create the db
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedule ( scheduleId INT NOT NULL PRIMARY KEY AUTO_INCREMENT )";

    public Server() throws SQLException{ // Reads the db.props file and sets the variables for the server to those props
        try{
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("./db.props");
            props.load(in);
            in.close();

            this.url = props.getProperty("jdbc.url");
            this.schema = props.getProperty("jdbc.schema");
            this.username = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");

            setup();
        }
        catch(IOException ioe){ //if db.props can't be found
            System.out.println("No properties file found");
            ioe.printStackTrace();
        }
    }

    private void connect() throws SQLException{
        try{
            conn = DriverManager.getConnection(url + "/" + schema, username, password);
            statement = conn.createStatement();
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }

    private void setup() throws SQLException { //Runs a setup SQL statement, creating the users table. This is run during construction TODO: actually make the right tables
        connect();
        statement.executeQuery(USERS_TABLE);
        statement.executeQuery(BILLBOARDS_TABLE);
        statement.executeQuery(SCHEDULE_TABLE);
        conn.close();
    }

    public void addUser(String name) throws SQLException{ //adds a user to the db
        connect();
        statement.executeQuery("INSERT INTO users (userName) VALUES (\"" + name + "\")");
        conn.close();
    }

    //TODO: maybe use some prepared statements to simplify code. both getTables and getUsers have VERY similar code, just different SQL statements.
    //may not even need these in the final product
    public String getTables() throws SQLException{ //returns the tables in the db
        connect();
        ResultSet rs = statement.executeQuery("SHOW TABLES");
        String tables = "";
        while(rs.next()){
            tables = tables.concat(rs.getString(1) + " ");
        }
        return tables; //formatted like "table table table " NOTE the extra space at the end TODO: maybe put the tables into a string[]
    }

    public String getUsers() throws SQLException{ //returns a string of users
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users");
        String users = "";
        while(rs.next()){
            users = users.concat(rs.getString(2) + " ");
        }
        return users; //formatted like "user user user " NOTE the extra space at the end TODO: maybe put the user into a string[]
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
