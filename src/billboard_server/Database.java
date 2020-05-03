package billboard_server;

import connections.ClientRequest;
import connections.types.ClientRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.time.LocalTime;
import java.util.TreeMap;

public class Database {

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " +
            "userName VARCHAR(255) PRIMARY KEY NOT NULL,"+
            "hash VARCHAR(255) NOT NULL,"+
            "salt VARCHAR(255) NOT NULL,"+
            "permission VARCHAR(255) ) ";
    private static final String BILLBOARDS_TABLE = "CREATE TABLE IF NOT EXISTS billboards ( " +
            "billboardId INT NOT NULL AUTO_INCREMENT,"+
            "billboardName VARCHAR(255),"+
            "billboardMessage VARCHAR(255)," +
            "billboardInfo VARCHAR(255)," +
            "billboardImg MEDIUMTEXT," +
            "billboardBg VARCHAR(255)," +
            "billboardMsgColour VARCHAR(255)," +
            "billboardInfoColour VARCHAR(255)," +
            "PRIMARY KEY(billboardId, billboardName))";
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( " +
            "billboardId INT, " +
            "billboardName VARCHAR(255)," +
            "startTime DATETIME, " +
            "duration INT, " +
            "isRecurring BOOLEAN, " +
            "recurFreqInMins INT, " +
            "FOREIGN KEY (billboardId, billboardName) REFERENCES billboards(billboardId, billboardName) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userName, hash, salt, permission) VALUES (?, ?, ?, ?)";
    private static final String deluserStatement = "DELETE FROM users WHERE userName=?";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardName, billboardMessage, billboardInfo, billboardImg, billboardBg, billboardMsgColour, billboardInfoColour)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String delbilbStatement = "DELETE FROM billboards WHERE billboardName=?";
    private static final String addschedStatement = "INSERT INTO schedules (billboardId, billboardName, startTime, duration, isRecurring, recurFreqInMins) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String delschedStatement = "DELETE FROM schedules WHERE startTime=? AND billboardName=?";

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
            FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/db.props");
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

        dropDb();
        connect();
        statement.executeQuery(USERS_TABLE);
        statement.executeQuery(BILLBOARDS_TABLE);
        statement.executeQuery(SCHEDULE_TABLE);
        conn.close();

    }

    public boolean processRequest(ClientRequest cr) throws SQLException{
        if(cr.type.equals("POST")){
            switch (cr.path) {
                case "users":
                    addUser(cr.params);
                    return true;
                case "billboards":
                    addBillboard(cr.params);
                    return true;
                case "schedules":
                    addSchedule(cr.params);
                    return true;
            }
        }
        else if(cr.type.equals("GET")){
            switch (cr.path) {
                case "users":
                    getUsers();
                    return true;
                case "billboards":
                    getBillboards();
                    return true;
                case "schedules":
                    getSchedule();
                    return true;
            }
        }
        else if(cr.type.equals("DEL")){
            switch (cr.path) {
                case "users":
                    //deleteUser()
                    return true;
                case "billboards":
                    //deleteBillboard()
                    return true;
                case "schedule":
                    //deleteSchedule
                    return true;
            }
        }
        return false;
    }

    public void addUser(TreeMap<String, String> user) throws SQLException { // adds a user to the DB
        connect();
        pstmt = conn.prepareStatement(adduserStatement);
        pstmt.clearParameters();
        pstmt.setString(1, user.get("userName"));
        pstmt.setString(2, user.get("hash"));
        pstmt.setString(3, user.get("salt"));
        pstmt.setString(4, user.get("permissions"));
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

    public void addBillboard(TreeMap<String, String> billboard) throws SQLException { // adds a billboard
        connect();
        pstmt = conn.prepareStatement(addbilbStatement);
        pstmt.setString(1, billboard.get("billboardName"));
        pstmt.setString(2, billboard.get("billboardMessage"));
        pstmt.setString(3, billboard.get("billboardInfo"));
        pstmt.setString(4, billboard.get("billboardImg"));
        pstmt.setString(5, billboard.get("billboardBg"));
        pstmt.setString(6, billboard.get("billboardMsgColour"));
        pstmt.setString(7, billboard.get("billboardInfoColour"));
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
            billboards[i][2] = rs.getString(5);
        }
        return billboards;
    }

    public void deleteBillboard(String billboardName) throws SQLException{ // deletes a billboard
        connect();
        pstmt = conn.prepareStatement(delbilbStatement);
        pstmt.setString(1, billboardName);
        pstmt.execute();
    }

    public void addSchedule(TreeMap<String, String> schedule) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId FROM billboards WHERE billboardName=\"" + schedule.get("billboardName") + "\"");
        rs.next();
        pstmt = conn.prepareStatement(addschedStatement);
        pstmt.setInt(1, rs.getInt(1));
        pstmt.setString(2, schedule.get("billboardName"));
        pstmt.setString(3, schedule.get("startDate") + ' ' + schedule.get("startTime"));
        pstmt.setInt(4, Integer.parseInt(schedule.get("duration")));
        pstmt.setBoolean(5, Boolean.parseBoolean(schedule.get("isRecurring")));
        pstmt.setInt(6, Integer.parseInt(schedule.get("recurFreqInMins")));
        pstmt.execute();
    }

    public String[][] getSchedule() throws SQLException { // returns a 2D array of [schedule][billboardId, billboardName, startTime, isRecurring, recurFreqInMins]
        connect();
        ResultSet rs = statement.executeQuery("SELECT * from schedules");
        int len = 0;
        while(rs.next()){
            len++;
        }
        rs.beforeFirst();
        String[][] schedules = new String[len][5];
        int i = 0;
        while(rs.next()){
            schedules[i][0] = rs.getString(2);
            schedules[i][1] = rs.getString(3);
            schedules[i][2] = rs.getString(4);
            schedules[i][3] = rs.getString(5);
            schedules[i][4] = rs.getString(6);
            i++;
        }
        return schedules;
    }

    public void deleteSchedule(TreeMap<String, String> schedule) throws SQLException{ // deletes a schedule based off time
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardName from schedules WHERE time=\""+schedule.get("startDate")+' '+schedule.get("startTime")+"\"");
        rs.next();
        pstmt = conn.prepareStatement(delschedStatement);
        pstmt.setString(1, schedule.get("startDate") +' '+ schedule.get("startTime"));
        pstmt.setString(2, schedule.get("billboardName"));
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
