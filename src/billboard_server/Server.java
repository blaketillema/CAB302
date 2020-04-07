package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Server {

    private String url;
    private String schema;
    private String username;
    private String password;

    private static final String SETUP_SQL = "CREATE TABLE IF NOT EXISTS users ( userId INT NOT NULL PRIMARY KEY AUTO_INCREMENT, userName VARCHAR(255) NOT NULL )"; //TODO: write a proper statement to create the db

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
        catch(IOException ioe){
            System.out.println("No properties file found");
            ioe.printStackTrace();
        }
    }

    private void setup() throws SQLException { //Runs a setup SQL statement, creating the users table. This is run during construction TODO: actually make the right tables
        Connection conn = DriverManager.getConnection(url + "/" + schema, username, password);
        Statement statement = conn.createStatement();
        statement.executeQuery(SETUP_SQL);
        conn.close();
    }

    public void addUser(String name) throws SQLException{
        Connection conn = DriverManager.getConnection(url + "/" + schema, username, password);
        Statement statement = conn.createStatement();
        statement.executeQuery("INSERT INTO users (userName) VALUES (\"" + name + "\")");
        conn.close();
    }
}
