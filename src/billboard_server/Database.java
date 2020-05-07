package billboard_server;

import connections.exceptions.*;
import connections.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class Database {

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " +
            "userId VARCHAR(255) PRIMARY KEY NOT NULL,"+
            "userName VARCHAR(255)," +
            "hash VARCHAR(255) NOT NULL,"+
            "salt VARCHAR(255) NOT NULL,"+
            "permission INT ) ";
    private static final String BILLBOARDS_TABLE = "CREATE TABLE IF NOT EXISTS billboards ( " +
            "billboardId VARCHAR(255) PRIMARY KEY NOT NULL," +
            "billboardMessage VARCHAR(255)," +
            "billboardInfo VARCHAR(255)," +
            "billboardPictureData MEDIUMTEXT," +
            "billboardPictureUrl MEDIUMTEXT," +
            "billboardBg VARCHAR(255)," +
            "billboardMsgColour VARCHAR(255)," +
            "billboardInfoColour VARCHAR(255) )";
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( " +
            "scheduleId VARCHAR(255) PRIMARY KEY NOT NULL," +
            "billboardId VARCHAR(255), " +
            "startTime DATETIME, " +
            "duration INT, " +
            "isRecurring BOOLEAN, " +
            "recurFreqInMins INT, " +
            "FOREIGN KEY (billboardId) REFERENCES billboards(billboardId) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userId, userName, hash, salt, permission) VALUES (?, ?, ?, ?, ?)";
    private static final String deluserStatement = "DELETE FROM users WHERE userId=?";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardId, billboardMessage, billboardInfo, billboardPictureData, billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String delbilbStatement = "DELETE FROM billboards WHERE billboardId=?";
    private static final String addschedStatement = "INSERT INTO schedules (scheduleId, billboardId, startTime, scheduleDuration, isRecurring, recurFreqInMins) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String delschedStatement = "DELETE FROM schedules WHERE scheduleId=?";

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

    private int getPermission(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT permission FROM users WHERE userId=\"" + userId + "\"");
        if(rs.next()){
            return rs.getInt(1);
        }
        else{
            return 0;
        }
    }

    private void checkPermission(String userId, int permissionNeeded) throws SQLException, ServerException {
        if(userId == null){
            throw new ServerException("tried to check permissions for null user");
        }
        if((getPermission(userId) & permissionNeeded) == 0){
            throw new PermissionException(userId, getPermission(userId), permissionNeeded);
        }
    }

    private boolean doesUserExist(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\""+userId+"\"");
        if(rs.next()){
            return true;
        }
        else{
            return false;
        }
    }

    public String getHash(String userId) throws SQLException{
        connect();
        ResultSet rs = statement.executeQuery("SELECT hash FROM users WHERE userId=\""+userId+"\"");
        if(rs.next()){
            return rs.getString(1);
        }
        else{
            return null;
        }
    }

    public String getSalt(String userId) throws SQLException{
        connect();
        ResultSet rs = statement.executeQuery("SELECT salt FROM users WHERE userId=\""+userId+"\"");
        if(rs.next()){
            return rs.getString(1);
        }
        else{
            return null;
        }
    }

    public void addUsers(String userId, TreeMap<String, Object> data) throws ServerException, SQLException {

        /* make sure user attempting to add new users has permissions */
        checkPermission(userId, Protocol.Permission.EDIT_USERS);

        /* loop through each user in the treemap */
        for(Map.Entry<String, Object> user : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each user */
            TreeMap<String, Object> userDetails = (TreeMap<String, Object>) user.getValue();

            /* get all of the relevant values and cast to types */
            String newUserId = user.getKey();
            String newUsername = (String) userDetails.get("userName");
            String newHash = (String) userDetails.get("hash");
            String newSalt = (String) userDetails.get("salt");
            int newPermissions = -1;
            if(userDetails.containsKey("permissions"))
                newPermissions = (int) userDetails.get("permissions");

            /* check if the user to be added exists or not. */
            boolean userExists = doesUserExist(user.getKey());

            /* if user doesn't exist, make sure all user information has been provided and add it */
            if(!userExists) {
                if(newUsername == null || newHash == null || newSalt == null || newPermissions == -1) {
                    throw new ServerException("attempting to add user without all of the required information");
                }
                connect();
                pstmt = conn.prepareStatement(adduserStatement);
                pstmt.setString(1, newUserId);
                pstmt.setString(2, newUsername);
                pstmt.setString(3, newHash);
                pstmt.setString(4, newSalt);
                pstmt.setInt(5, newPermissions);
                pstmt.execute();

                /* else if user does exist, somehow modify the existing info for that userId */
            } else {

            }
        }
    }

    public TreeMap<String, Object> getUsers() throws SQLException{
        connect();
        TreeMap<String, Object> users = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM users");

        while(rs.next()){
            TreeMap<String, Object> user = new TreeMap<>();
            user.put("userName", rs.getString(2));
            user.put("hash", rs.getString(3));
            user.put("salt", rs.getString(4));
            user.put("permissions", rs.getInt(5));

            users.put(rs.getString(1), user);
        }

        return users;
    }

    public void deleteUsers(String userId, TreeMap<String, Object> data) throws SQLException, ServerException { // deletes a user
        connect();
        checkPermission(userId, Protocol.Permission.EDIT_USERS);
        for(Map.Entry<String, Object> user : data.entrySet()){
            pstmt = conn.prepareStatement(deluserStatement);
            pstmt.setString(1, user.getKey());
            pstmt.execute();
        }
    }

    public void addBillboards(String userId, TreeMap<String, Object> data) throws ServerException, SQLException {

        /* make sure user attempting to add new users has permissions */
        checkPermission(userId, Protocol.Permission.CREATE_BILLBOARDS);

        /* loop through each billboard in the treemap */
        for(Map.Entry<String, Object> billboard : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each billboard */
            TreeMap<String, Object> billboardDetails = (TreeMap<String, Object>) billboard.getValue();

            /* get all of the relevant values and cast to types */
            String newId = billboard.getKey();
            String newMessage = (String) billboardDetails.get("message");
            String newInfo = (String) billboardDetails.get("information");
            String newPictureData = (String) billboardDetails.get("pictureData");
            String newPictureUrl = (String) billboardDetails.get("pictureUrl");
            String newBillboardBackground = (String) billboardDetails.get("billboardBackground");
            String newMessageColour = (String) billboardDetails.get("messageColour");
            String newInformationColour = (String) billboardDetails.get("informationColour");

            /* prepare and execute the statement to add the billboard:
            billboardId, billboardMessage, billboardInfo, billboardPictureData,
            billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour */
            pstmt = conn.prepareStatement(addbilbStatement);
            pstmt.setString(1, newId);
            pstmt.setString(2, newMessage);
            pstmt.setString(3, newInfo);
            pstmt.setString(4, newPictureData);
            pstmt.setString(5, newPictureUrl);
            pstmt.setString(6, newBillboardBackground);
            pstmt.setString(7, newMessageColour);
            pstmt.setString(8, newInformationColour);
            pstmt.execute();

        }
    }

    public TreeMap<String, Object> getBillboards() throws SQLException{
        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM billboards");

        while(rs.next()){
            TreeMap<String, Object> billboard = new TreeMap<>();
            billboard.put("message", rs.getString(2));
            billboard.put("information", rs.getString(3));
            billboard.put("pictureData", rs.getString(4));
            billboard.put("pictureUrl", rs.getString(5));
            billboard.put("billboardBackground", rs.getString(6));
            billboard.put("messageColour", rs.getString(7));
            billboard.put("informationColour", rs.getString(8));

            billboards.put(rs.getString(1), billboard);
        }

        return billboards;
    }

    public void deleteBillboards(String userId, TreeMap<String, Object> data) throws SQLException, ServerException { // deletes a user
        connect();
        checkPermission(userId, Protocol.Permission.EDIT_ALL_BILLBOARDS);
        for(Map.Entry<String, Object> billboard : data.entrySet()){
            pstmt = conn.prepareStatement(delbilbStatement);
            pstmt.setString(1, billboard.getKey());
            pstmt.execute();
        }
    }

    public void addSchedule(String userId, TreeMap<String, Object> data) throws SQLException, ServerException {
        connect();
        checkPermission(userId, Protocol.Permission.SCHEDULE_BILLBOARDS);

        /*(scheduleId, billboardId, startTime, scheduleDuration, isRecurring, recurFreqInMins)*/
        for(Map.Entry<String, Object> schedule : data.entrySet()){

            TreeMap<String, Object> scheduleDetails = (TreeMap<String, Object>) schedule.getValue();

            String scheduleId = schedule.getKey();
            String billboardId = (String) scheduleDetails.get("billboardId");
            String startTime = (String) scheduleDetails.get("startTime");
            int duration = (Integer) scheduleDetails.get("scheduleDuration");
            boolean isRecurring = (Boolean) scheduleDetails.get("isRecurring");
            int recurFreqInMins = (Integer) scheduleDetails.get("recurFreqInMins");

            pstmt = conn.prepareStatement(addschedStatement);
            pstmt.setString(1, scheduleId);
            pstmt.setString(2, billboardId);
            pstmt.setString(3, startTime);
            pstmt.setInt(4, duration);
            pstmt.setBoolean(5, isRecurring);
            pstmt.setInt(6, recurFreqInMins);
            pstmt.execute();
        }
    }

    public TreeMap<String, Object> getSchedules() throws SQLException{
        connect();
        TreeMap<String, Object> schedules = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM schedules");

        while(rs.next()){
            TreeMap<String, Object> schedule = new TreeMap<>();
            schedule.put("billboardId", rs.getString(2));
            schedule.put("startTime", rs.getString(3));
            schedule.put("scheduleDuration", rs.getInt(4));
            schedule.put("isRecurring", rs.getBoolean(5));
            schedule.put("recurFreqInMins", rs.getInt(6));

            schedules.put(rs.getString(1), schedule);
        }

        return schedules;
    }

    public void deleteSchedules(String userId, TreeMap<String, Object> data) throws SQLException, ServerException { // deletes a user
        connect();
        for(Map.Entry<String, Object> schedule : data.entrySet()){
            pstmt = conn.prepareStatement(delschedStatement);
            pstmt.setString(1, schedule.getKey());
            pstmt.execute();
        }
    }

    public void dropDb() throws SQLException { //TODO: REMOVE LATER THIS IS JUST FOR TESTING
        connect();
        statement.executeQuery("DROP TABLE IF EXISTS schedules");
        statement.executeQuery("DROP TABLE IF EXISTS billboards");
        statement.executeQuery("DROP TABLE IF EXISTS users");
        conn.close();
    }

}
