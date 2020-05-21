package billboard_server;

import connections.Protocol;
import connections.tools.UserAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class Database {

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " +
            "userId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "userName VARCHAR(191)," +
            "hash VARCHAR(191) NOT NULL," +
            "salt VARCHAR(191) NOT NULL," +
            "permissions INT ) ";
    private static final String BILLBOARDS_TABLE = "CREATE TABLE IF NOT EXISTS billboards ( " +
            "billboardId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "billboardName VARCHAR(191)," +
            "billboardCreator VARCHAR(191)," +
            "billboardMessage VARCHAR(191)," +
            "billboardInfo VARCHAR(191)," +
            "billboardPictureData MEDIUMTEXT," +
            "billboardPictureUrl MEDIUMTEXT," +
            "billboardBg VARCHAR(191)," +
            "billboardMsgColour VARCHAR(191)," +
            "billboardInfoColour VARCHAR(191)," +
            "FOREIGN KEY (billboardCreator) REFERENCES users(userId) ON DELETE CASCADE )";
    // TODO: add support for startTime as datetime object
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( " +
            "scheduleId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "billboardId VARCHAR(191), " +
            "startTime DATETIME, " +
            "duration INT, " +
            "isRecurring BOOLEAN, " +
            "recurFreqInMins INT, " +
            "FOREIGN KEY (billboardId) REFERENCES billboards(billboardId) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userId, userName, hash, salt, permissions) VALUES (?, ?, ?, ?, ?)";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardId, billboardName, billboardCreator, billboardMessage, billboardInfo, billboardPictureData, billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String addschedStatement = "INSERT INTO schedules (scheduleId, billboardId, startTime, duration, isRecurring, recurFreqInMins) VALUES (?, ?, ?, ?, ?, ?)";

    private String url;
    private String schema;
    private String username;
    private String password;
    private Connection conn;
    private Statement statement;
    PreparedStatement pstmt;

    public Database() throws SQLException { // Reads the db.props file and sets the variables for the server to those props
        try {
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

    private void setup() throws SQLException { //Runs a setup SQL statement, creating the users table. This is run during construction TODO: actually make the right tables
        dropDb();

        String adminUserID = "b220a053-91f1-48ee-acea-d1a145376e57";
        String adminSalt = "2219d4ec595ce93cabfe7c7941d7e274";
        String adminHash = UserAuth.hashAndSalt("7ec582cb6dda5f00485d4f3026c1309ba7c3eb255cdfbdcb4a3fb3646d74953d", adminSalt);

        connect();
        statement.executeQuery(USERS_TABLE);
        statement.executeQuery(BILLBOARDS_TABLE);
        statement.executeQuery(SCHEDULE_TABLE);

        pstmt = conn.prepareStatement(adduserStatement);
        pstmt.setString(1, adminUserID);
        pstmt.setString(2, "admin");
        pstmt.setString(3, adminHash);
        pstmt.setString(4, adminSalt);
        pstmt.setInt(5, Protocol.Permission.ALL);
        pstmt.execute();

        conn.close();
    }

    public int getPermission(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT permissions FROM users WHERE userId=\"" + userId + "\"");
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    public boolean doesUserExist(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");
        return rs.next();
    }

    public String userNameToId(String userName) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT userId FROM users WHERE userName=\"" + userName + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public String billboardNameToId(String billboardName) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId FROM billboards WHERE billboardName=\"" + billboardName + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public String billboardToScheduleId(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT scheduleId FROM schedules WHERE billboardId=\"" + billboardId + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public boolean doesBillboardExist(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        return rs.next();
    }

    public boolean doesScheduleExist(String scheduleId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        return rs.next();
    }

    public String getHash(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT hash FROM users WHERE userId=\"" + userId + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public String getBillboardCreator(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardCreator FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public String getSalt(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT salt FROM users WHERE userId=\"" + userId + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public void addUser(String userId, String userName, String hash, String salt, Integer permissions) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(adduserStatement);

        pstmt.setString(1, userId);
        pstmt.setString(2, userName);
        pstmt.setString(3, hash);
        pstmt.setString(4, salt);
        pstmt.setInt(5, permissions);
        pstmt.execute();
        conn.close();
    }

    // TODO: same as addUser but if value provided is null, that value isnt touched in database
    public void editUser(String userId, String userName, String hash, String salt, Integer permissions) throws SQLException {
        connect();
        String editStatement = "UPDATE users SET ";
        if (userName != null) editStatement += "userName=\"" + userName + "\", ";
        if (hash != null) editStatement += "hash=\"" + hash + "\", ";
        if (salt != null) editStatement += "salt=\"" + salt + "\", ";
        if (permissions != null) editStatement += "permissions=\"" + permissions + "\", ";
        editStatement = editStatement.substring(0, editStatement.length() - 2);
        String endEditStatement = " WHERE userId=\"" + userId + "\"";
        editStatement += endEditStatement;
        //System.out.println(editStatement);
        statement.executeQuery(editStatement);
        conn.close();
    }

    public TreeMap<String, Object> getUser(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");

        TreeMap<String, Object> user = new TreeMap<>();

        if (rs.next()) {
            TreeMap<String, Object> body = new TreeMap<>();

            body.put("userName", rs.getString(2));
            // user.put("hash", rs.getString(3));
            // body.put("salt", rs.getString(4));
            body.put("permissions", rs.getInt(5));
            user.put(rs.getString(1), body);
        }

        conn.close();
        return user;
    }

    public TreeMap<String, Object> getUsers(ArrayList<String> userIds) throws SQLException {
        connect();

        TreeMap<String, Object> users = new TreeMap<>();

        for (String userId : userIds) {
            TreeMap<String, Object> body = new TreeMap<>();

            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");

            if (rs.next()) {
                body.put("userName", rs.getString(2));
                // body.put("hash", rs.getString(3));
                // body.put("salt", rs.getString(4));
                body.put("permissions", rs.getInt(5));

                users.put(rs.getString(1), body);
            }
        }

        conn.close();
        return users;
    }

    public TreeMap<String, Object> getUsers() throws SQLException {
        connect();
        TreeMap<String, Object> users = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM users");

        while (rs.next()) {
            TreeMap<String, Object> user = new TreeMap<>();
            user.put("userName", rs.getString(2));
            // user.put("hash", rs.getString(3));
            // user.put("salt", rs.getString(4));
            user.put("permissions", rs.getInt(5));

            users.put(rs.getString(1), user);
        }
        conn.close();
        return users;
    }

    public void deleteUser(String userId) throws SQLException {
        connect();
        statement.executeQuery("DELETE FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
    }

    public void deleteUsers(ArrayList<String> userIds) throws SQLException {
        connect();

        for (String userId : userIds) {
            statement.executeQuery("DELETE FROM users WHERE userId=\"" + userId + "\"");
        }
        conn.close();
    }

    public void addBillboard(String billboardId, String billboardName, String billboardCreator,
                             String billboardMessage, String billboardInfo, String billboardPictureData,
                             String billboardPictureUrl, String billboardBg, String billboardMsgColour,
                             String billboardInfoColour) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(addbilbStatement);
        pstmt.setString(1, billboardId);
        pstmt.setString(2, billboardName);
        pstmt.setString(3, billboardCreator);
        pstmt.setString(4, billboardMessage);
        pstmt.setString(5, billboardInfo);
        pstmt.setString(6, billboardPictureData);
        pstmt.setString(7, billboardPictureUrl);
        pstmt.setString(8, billboardBg);
        pstmt.setString(9, billboardMsgColour);
        pstmt.setString(10, billboardInfoColour);
        pstmt.execute();
        conn.close();
    }

    // TODO: same as addbillboards, but editing existing billboard and values provided can be null
    public void editBillboard(String billboardId, String billboardName, String billboardCreator,
                              String billboardMessage, String billboardInfo, String billboardPictureData,
                              String billboardPictureUrl, String billboardBg, String billboardMsgColour,
                              String billboardInfoColour) throws SQLException {
        connect();
        String editStatement = "UPDATE billboards SET ";
        if (billboardName != null) editStatement += "billboardName=\"" + billboardName + "\", ";
        if (billboardMessage != null) editStatement += "billboardMessage=\"" + billboardMessage + "\", ";
        if (billboardInfo != null) editStatement += "billboardInfo=\"" + billboardInfo + "\", ";
        if (billboardPictureData != null) editStatement += "billboardPictureData=\"" + billboardPictureData + "\", ";
        if (billboardPictureUrl != null) editStatement += "billboardPictureUrl=\"" + billboardPictureUrl + "\", ";
        if (billboardBg != null) editStatement += "billboardBg=\"" + billboardBg + "\", ";
        if (billboardMsgColour != null) editStatement += "billboardMsgColour=\"" + billboardMsgColour + "\", ";
        if (billboardInfoColour != null) editStatement += "billboardInfoColour=\"" + billboardInfoColour + "\", ";
        editStatement = editStatement.substring(0, editStatement.length() - 2);
        String endEditStatement = " WHERE billboardId=\"" + billboardId + "\"";
        editStatement += endEditStatement;
        //System.out.println(editStatement);
        statement.executeQuery(editStatement);
        conn.close();
    }

    public TreeMap<String, Object> getBillboards(ArrayList<String> billboardIds) throws SQLException {

        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();

        for (String billboardId : billboardIds) {
            ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + billboardId + "\"");

            if (rs.next()) {
                TreeMap<String, String> billboard = new TreeMap<>();
                billboard.put("billboardName", rs.getString(2));
                billboard.put("billboardCreator", rs.getString(3));
                billboard.put("message", rs.getString(4));
                billboard.put("info", rs.getString(5));
                billboard.put("pictureData", rs.getString(6));
                billboard.put("pictureUrl", rs.getString(7));
                billboard.put("billboardBackground", rs.getString(8));
                billboard.put("messageColour", rs.getString(9));
                billboard.put("informationColour", rs.getString(10));

                billboards.put(rs.getString(1), billboard);
            }
        }

        conn.close();
        return billboards;
    }

    public TreeMap<String, Object> getBillboards() throws SQLException {
        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM billboards");

        while (rs.next()) {
            TreeMap<String, String> billboard = new TreeMap<>();
            billboard.put("billboardName", rs.getString(2));
            billboard.put("billboardCreator", rs.getString(3));
            billboard.put("message", rs.getString(4));
            billboard.put("info", rs.getString(5));
            billboard.put("pictureData", rs.getString(6));
            billboard.put("pictureUrl", rs.getString(7));
            billboard.put("billboardBackground", rs.getString(8));
            billboard.put("messageColour", rs.getString(9));
            billboard.put("informationColour", rs.getString(10));

            billboards.put(rs.getString(1), billboard);
        }

        return billboards;
    }

    public void deleteBillboards(ArrayList<String> billboardIds) throws SQLException {
        connect();

        for (String billboardId : billboardIds) {
            statement.executeQuery("DELETE FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        }
        conn.close();
    }

    public void deleteBillboard(String billboardId) throws SQLException {
        connect();
        statement.executeQuery("DELETE FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
    }

    public void addSchedule(String scheduleId, String billboardId, OffsetDateTime startTime, Integer duration, Boolean isRecurring, Integer recurFreqInMins) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(addschedStatement);
        pstmt.setString(1, scheduleId);
        pstmt.setString(2, billboardId);
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.ofInstant(startTime.toInstant(), ZoneOffset.of("+10:00"))));
        pstmt.setInt(4, duration);
        pstmt.setBoolean(5, isRecurring);
        pstmt.setInt(6, recurFreqInMins);
        pstmt.execute();
        conn.close();
    }

    // TODO:
    public void editSchedule(String scheduleId, String billboardId, OffsetDateTime startTime, Integer duration, Boolean isRecurring, Integer recurFreqInMins) throws SQLException {
        connect();
        String editStatement = "UPDATE schedules SET ";
        if (billboardId != null) editStatement += "billboardId=\"" + billboardId + "\", ";
        if (startTime != null)
            editStatement += "startTime=\"" + Timestamp.valueOf(LocalDateTime.ofInstant(startTime.toInstant(), ZoneOffset.of("+10:00"))) + "\", ";
        if (duration != null) editStatement += "duration=\"" + duration + "\", ";
        if (isRecurring != null) editStatement += "isRecurring=" + isRecurring + ", ";
        if (recurFreqInMins != null) editStatement += "recurFreqInMins=\"" + recurFreqInMins + "\", ";
        editStatement = editStatement.substring(0, editStatement.length() - 2);
        String endEditStatement = " WHERE scheduleId=\"" + scheduleId + "\"";
        editStatement += endEditStatement;
        //System.out.println(editStatement);
        statement.executeQuery(editStatement);
        conn.close();
    }

    public TreeMap<String, Object> getSchedules() throws SQLException {
        connect();
        TreeMap<String, Object> schedules = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM schedules");

        while (rs.next()) {
            TreeMap<String, Object> schedule = new TreeMap<>();
            schedule.put("scheduleId", rs.getString(1));
            schedule.put("billboardId", rs.getString(2));
            schedule.put("startTime", OffsetDateTime.of(rs.getTimestamp(3).toLocalDateTime(), ZoneOffset.of("+10:00")));
            schedule.put("duration", rs.getInt(4));
            schedule.put("isRecurring", rs.getBoolean(5));
            schedule.put("recurFreqInMins", rs.getInt(6));
            schedules.put(rs.getString(1), schedule);
        }
        conn.close();
        return schedules;
    }

    public TreeMap<String, Object> getSchedules(ArrayList<String> scheduleIds) throws SQLException {
        connect();
        TreeMap<String, Object> schedules = new TreeMap<>();

        for (String scheduleId : scheduleIds) {
            ResultSet rs = statement.executeQuery("SELECT * FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");

            if (rs.next()) {
                TreeMap<String, Object> schedule = new TreeMap<>();
                schedule.put("scheduleId", rs.getString(1));
                schedule.put("billboardId", rs.getString(2));
                schedule.put("startTime", OffsetDateTime.of(rs.getTimestamp(3).toLocalDateTime(), ZoneOffset.of("+10:00")));
                schedule.put("duration", rs.getInt(4));
                schedule.put("isRecurring", rs.getBoolean(5));
                schedule.put("recurFreqInMins", rs.getInt(6));
                schedules.put(rs.getString(1), schedule);
            }
        }

        conn.close();
        return schedules;
    }

    public void deleteSchedule(String scheduleId) throws SQLException { // deletes a user
        connect();
        statement.executeQuery("DELETE FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        conn.close();
    }

    public void deleteSchedules(ArrayList<String> scheduleIds) throws SQLException { // deletes a user
        connect();

        for (String scheduleId : scheduleIds) {
            statement.executeQuery("DELETE FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        }
        conn.close();
    }


    public void dropDb() throws SQLException {
        connect();
        statement.executeQuery("DROP TABLE IF EXISTS schedules");
        statement.executeQuery("DROP TABLE IF EXISTS billboards");
        statement.executeQuery("DROP TABLE IF EXISTS users");
    }
}