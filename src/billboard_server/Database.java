package billboard_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.time.LocalTime;

public class Database {

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( userName VARCHAR(255) PRIMARY KEY NOT NULL, hash VARCHAR(255) NOT NULL, salt VARCHAR(255) NOT NULL, permission VARCHAR(255) ) ";
    private static final String BILLBOARDS_TABLE = "CREATE TABLE IF NOT EXISTS billboards ( billboardId INT NOT NULL PRIMARY KEY AUTO_INCREMENT, billboardName VARCHAR(255), image MEDIUMTEXT CHARACTER SET BINARY ) "; //TODO: write a proper statement to create the db
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( billboardId INT, time DATETIME NOT NULL, FOREIGN KEY (billboardId) REFERENCES billboards(billboardId) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userName, hash, salt, permission) VALUES (?, ?, ?, ?)";
    private static final String deluserStatement = "DELETE FROM users WHERE userName=?";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardName, image) VALUES (?, ?)";
    private static final String delbilbStatement = "DELETE FROM billboards WHERE billboardName=?";
    private static final String addschedStatement = "INSERT INTO schedules (billboardId, time) VALUES (?, ?)";
    private static final String delschedStatement = "DELETE FROM schedules WHERE billboardId=?";

    private String url;
    private String schema;
    private String username;
    private String password;
    private Connection conn;
    private Statement statement;
    PreparedStatement pstmt;

    public Database() throws SQLException{ // Reads the db.props file and sets the variables for the server to those props
        try{
            Properties props = new Properties();
            FileInputStream in = new FileInputStream("../db.props");
            props.load(in);
            in.close();

            this.url = props.getProperty("jdbc.url");
            this.schema = props.getProperty("jdbc.schema");
            this.username = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");

            setup();

        } catch(IOException ioe){ //if db.props can't be found
            System.out.println("No properties file found");
            ioe.printStackTrace();
        }

    }

    private void connect() throws SQLException{ // used to connect to the db before sending a query (this is mostly to cut down on repetitive code)

        conn = DriverManager.getConnection(url + "/" + schema, username, password);
        statement = conn.createStatement();

    }

    private void setup() throws SQLException{ //Runs a setup SQL statement, creating the users table. This is run during construction TODO: actually make the right tables

        connect();
        statement.executeQuery(USERS_TABLE);
        statement.executeQuery(BILLBOARDS_TABLE);
        statement.executeQuery(SCHEDULE_TABLE);
        conn.close();

    }

    private String timeToDateTimeString(LocalTime time){ // convert a LocalTime object to a String of Date + Time
        String timeString = time.toString();
        LocalDate todayDate = LocalDate.now();
        String dateString = todayDate.toString();
        return dateString + ' ' + timeString;
    }

    public void addUser(String name, String hash, String salt, String permissions) throws SQLException { // adds a user to the DB
        connect();
        pstmt = conn.prepareStatement(adduserStatement);
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setString(2, hash);
        pstmt.setString(3, salt);
        pstmt.setString(4, permissions);
        pstmt.execute();
    }

    public String[][] getUsers() throws SQLException{ // gets a 2D array of [users][name, hash, salt, perms]
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users");
        int len = 0;
        while(rs.next()){
            len++;
        }
        String[][] users = new String[len][4];
        rs.beforeFirst();
        int i = 0;
        while(rs.next()){
            users[i][0] = rs.getString(1);
            users[i][1] = rs.getString(2);
            users[i][2] = rs.getString(3);
            users[i][3] = rs.getString(4);
            i++;
        }
        return users;
    }

    public void deleteUser(String name) throws SQLException{ // deletes a user
        connect();
        pstmt = conn.prepareStatement(deluserStatement);
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.execute();
    }

    public void addBillboard(String billboardName, String image) throws SQLException { // adds a billboard
        connect();
        pstmt = conn.prepareStatement(addbilbStatement);
        pstmt.setString(1, billboardName);
        pstmt.setString(2, image);
        pstmt.execute();
    }

    public String[][] getBillboards() throws SQLException{ // basically the same as getUsers but [billboard][billboardId (probably not needed), billboard name, image base64 or url]
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM billboards");
        int len = 0;
        while(rs.next()){
            len++;
        }
        String[][] billboards = new String[len][3];
        rs.beforeFirst();
        int i = 0;
        while(rs.next()){
            billboards[i][0] = rs.getString(1);
            billboards[i][1] = rs.getString(2);
            billboards[i][2] = rs.getString(3);
        }
        return billboards;
    }

    public void deleteBillboard(String billboardName) throws SQLException{ // deletes a billboard
        connect();
        pstmt = conn.prepareStatement(delbilbStatement);
        pstmt.setString(1, billboardName);
        pstmt.execute();
    }

    public void addSchedule(String billboardName, LocalTime time) throws SQLException { // adds a schedule using a billboard name to find a billboardId and using that as a foreign key
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId FROM billboards WHERE billboardName=\""+billboardName+"\"");
        rs.next();
        pstmt = conn.prepareStatement(addschedStatement);
        pstmt.setInt(1, rs.getInt(1));
        String schedTime = timeToDateTimeString(time);
        pstmt.setString(2, schedTime);
        pstmt.execute();
    }

    public String[][] getSchedule() throws SQLException { // returns a 2D array of [schedule][billboardId, time]
        connect();
        ResultSet rs = statement.executeQuery("SELECT * from schedules");
        int len = 0;
        while(rs.next()){
            len++;
        }
        rs.beforeFirst();
        String[][] schedules = new String[len][2];
        int i = 0;
        while(rs.next()){
            schedules[i][0] = rs.getString(1);
            schedules[i][1] = rs.getString(2);
            i++;
        }
        return schedules;
    }

    public void deleteSchedule(LocalTime time) throws SQLException{ // deletes a schedule based off time
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId from schedules WHERE time=\""+timeToDateTimeString(time)+"\"");
        rs.next();
        pstmt = conn.prepareStatement(delschedStatement);
        pstmt.setString(1, timeToDateTimeString(time));
        pstmt.execute();
    }

    public void dropDb() throws SQLException { //TODO: REMOVE LATER THIS IS JUST FOR TESTING
        connect();
        statement.executeQuery("DROP TABLE IF EXISTS users");
        statement.executeQuery("DROP TABLE IF EXISTS schedules");
        statement.executeQuery("DROP TABLE IF EXISTS billboards");
        conn.close();
    }

}
