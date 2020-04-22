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

    private void connect() throws SQLException{

        conn = DriverManager.getConnection(url + "/" + schema, username, password);
        statement = conn.createStatement();

    }

    private void setup() throws SQLException{ //Runs a setup SQL statement, creating the users table. This is run during construction TODO: actually make the right tables

        dropDb(); //TODO TESTING REMOVE THIS LATER
        connect();
        statement.executeQuery(USERS_TABLE);
        statement.executeQuery(BILLBOARDS_TABLE);
        statement.executeQuery(SCHEDULE_TABLE);
        conn.close();

    }

    private String timeToDateTimeString(LocalTime time){
        String timeString = time.toString();
        LocalDate todayDate = LocalDate.now();
        String dateString = todayDate.toString();
        return dateString + ' ' + timeString;
    }

    public void addUser(String name, String hash, String salt, String permissions) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(adduserStatement);
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setString(2, hash);
        pstmt.setString(3, salt);
        pstmt.setString(4, permissions);
        pstmt.execute();
    }

    public void deleteUser(String name) throws SQLException{
        connect();
        pstmt = conn.prepareStatement(deluserStatement);
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.execute();
    }

    public void addBillboard(String billboardName, String image) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(addbilbStatement);
        pstmt.setString(1, billboardName);
        pstmt.setString(2, image);
        pstmt.execute();
    }

    public void deleteBillboard(String billboardName) throws SQLException{
        connect();
        pstmt = conn.prepareStatement(delbilbStatement);
        pstmt.setString(1, billboardName);
        pstmt.execute();
    }

    public void addSchedule(String billboardName, LocalTime time) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId FROM billboards WHERE billboardName=\""+billboardName+"\"");
        rs.next();
        pstmt = conn.prepareStatement(addschedStatement);
        pstmt.setInt(1, rs.getInt(1));
        String schedTime = timeToDateTimeString(time);
        pstmt.setString(2, schedTime);
        pstmt.execute();
    }

    public void deleteSchedule(LocalTime time) throws SQLException{
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
