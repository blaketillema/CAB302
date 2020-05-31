package billboard_server;

import billboard_server.exceptions.ServerException;
import billboard_server.tools.UserAuth;

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
            "billboardMessage TEXT," +
            "billboardInfo TEXT," +
            "billboardPictureData MEDIUMTEXT," +
            "billboardPictureUrl MEDIUMTEXT," +
            "billboardBg VARCHAR(191)," +
            "billboardMsgColour VARCHAR(191)," +
            "billboardInfoColour VARCHAR(191)," +
            "FOREIGN KEY (billboardCreator) REFERENCES users(userId) ON DELETE CASCADE )";
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( " +
            "scheduleId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "billboardId VARCHAR(191), " +
            "startTime DATETIME, " +
            "scheduleCreationTime DATETIME, " +
            "duration INT, " +
            "isRecurring BOOLEAN, " +
            "recurFreqInMins INT, " +
            "FOREIGN KEY (billboardId) REFERENCES billboards(billboardId) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userId, userName, hash, salt, permissions) VALUES (?, ?, ?, ?, ?)";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardId, billboardName, billboardCreator, billboardMessage, billboardInfo, billboardPictureData, billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String addschedStatement = "INSERT INTO schedules (scheduleId, billboardId, startTime, scheduleCreationTime, duration, isRecurring, recurFreqInMins) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private String url;
    private String schema;
    private String username;
    private String password;
    private Connection conn;
    private Statement statement;
    PreparedStatement pstmt;

    /**
     * Database constructor. Creates tables in a database specified by a db.props file.
     * @throws SQLException
     */
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

    /**
     * Connects to the DB before a statement.
     * @throws SQLException
     */
    private void connect() throws SQLException{ // used to connect to the db before sending a query (this is mostly to cut down on repetitive code)
        conn = DriverManager.getConnection(url + "/" + schema, username, password);
        statement = conn.createStatement();
    }

    /**
     * Creates the tables in the database if they don't already exist, and creates a default admin user with full privileges.
     * @throws SQLException
     */
    private void setup() throws SQLException { //Runs a setup SQL statement, creating the users table. This is run during construction
        dropDb();

        // Default admin account
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

    /**
     * Queries the users table in the DB for permissions given a userId
     * @param userId A randomly generated string used to identify a user.
     * @return permissions
     * @throws SQLException
     */
    public int getPermission(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT permissions FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    /**
     * Queries the users table in the DB for a userId. Returns true if the user exists, false otherwise.
     * @param userId A randomly generated string used to identify a user.
     * @return boolean
     * @throws SQLException
     */
    public boolean doesUserExist(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
        return rs.next();
    }

    /**
     * Queries the users table in the DB for a userName given a userId.
     * @param userName A name for the user specified by the creator of the user.
     * @return userId
     * @throws SQLException
     */
    public String userNameToId(String userName) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT userId FROM users WHERE userName=\"" + userName + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the billboards table in the DB for a billboardId given a billboardName.
     * @param billboardName A name for the billboard specified by the creator.
     * @return billboardId
     * @throws SQLException
     */
    public String billboardNameToId(String billboardName) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardId FROM billboards WHERE billboardName=\"" + billboardName + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the billboards table in the DB for a billboardName given a billboardId.
     * @param billboardId A randomly generated string used to identify a billboard.
     * @return billboardName
     * @throws SQLException
     */
    public String billboardIdToName(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardName FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the schedules table in the DB for a scheduleId given a billboardId.
     * @param billboardId A randomly generated string used to identify a billboard.
     * @return scheduleId
     * @throws SQLException
     */
    public String billboardToScheduleId(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT scheduleId FROM schedules WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the billboards table in the DB for a billboard given a billboardId. Returns true if it exists, false otherwise.
     * @param billboardId A randomly generated string used to identify a billboard.
     * @return boolean
     * @throws SQLException
     */
    public boolean doesBillboardExist(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
        return rs.next();
    }

    /**
     * Queries the schedules table in the DB for a schedule given a scheduleId. Returns true if it exists, false otherwise.
     * @param scheduleId A randomly generated string used to identify a schedule.
     * @return boolean
     * @throws SQLException
     */
    public boolean doesScheduleExist(String scheduleId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        conn.close();
        return rs.next();
    }

    /**
     * Queries the users table in the DB for a hash given a userId.
     * @param userId A randomly generated string used to identify a user.
     * @return hash
     * @throws SQLException
     */
    public String getHash(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT hash FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the billboards table in the DB for a billboardCreator given a billboardId. billboardCreator is a foreign key for users.userId.
     * @param billboardId A randomly generated string used to identify a billboard.
     * @return userId
     * @throws SQLException
     */
    public String getBillboardCreator(String billboardId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT billboardCreator FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Queries the users table in the DB for a salt given a userId.
     * @param userId A randomly generated string used to identify a user.
     * @return salt
     * @throws SQLException
     */
    public String getSalt(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT salt FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    /**
     * Inserts a user into the users table in the DB.
     * @param userId A randomly generated string used to identify a user.
     * @param userName A name for the user specified by the creator of the user.
     * @param hash A hashed and salted password.
     * @param salt A salt used in the hashing of the password.
     * @param permissions The level of permission the user has.
     * @throws SQLException
     */
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

    /**
     * Updates a user in the users table in the DB. The user property arguments can be null.
     * @param userId A randomly generated string used to identify the user.
     * @param userName A name for the user specified by the creator of the user. Can be null for this function.
     * @param hash A hashed and salted password. Can be null for this function.
     * @param salt A salt used in the hashing of the password. Can be null for this function.
     * @param permissions The level of permission the user has. Can be null for this function.
     * @throws SQLException
     */
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
        statement.executeQuery(editStatement);
        conn.close();
    }

    /**
     * Queries the users table for a user given a userId.
     * @param userId A randomly generated string used to identify a user.
     * @return user
     * @throws SQLException
     */
    public TreeMap<String, Object> getUser(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");

        TreeMap<String, Object> user = new TreeMap<>();
        //treemap structure: {userId, {userName, permission}}
        if (rs.next()) {
            TreeMap<String, Object> body = new TreeMap<>();
            body.put("userName", rs.getString(2));
            body.put("permissions", rs.getInt(5));
            user.put(rs.getString(1), body);
        }

        conn.close();
        return user;
    }

    /**
     * Queries the users table for a list of users given a list of userIds.
     * @param userIds A list of ids used to identify users.
     * @return users
     * @throws SQLException
     */
    public TreeMap<String, Object> getUsers(ArrayList<String> userIds) throws SQLException {
        connect();

        TreeMap<String, Object> users = new TreeMap<>();
        //treemap structure: [{userId, {userName, permissions}}, {userId, {userName, permissions}} ... ]

        for (String userId : userIds) {
            TreeMap<String, Object> body = new TreeMap<>();

            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");

            if (rs.next()) {
                body.put("userName", rs.getString(2));
                body.put("permissions", rs.getInt(5));
                users.put(rs.getString(1), body);
            }
        }

        conn.close();
        return users;
    }

    /**
     * Queries the users table for all users in the table.
     * @return users
     * @throws SQLException
     */
    public TreeMap<String, Object> getUsers() throws SQLException {
        connect();
        TreeMap<String, Object> users = new TreeMap<>();
        //treemap structure: [{userId, {userName, permissions}}, {userId, {userName, permissions}} ... ]
        ResultSet rs = statement.executeQuery("SELECT * FROM users");

        while (rs.next()) {
            TreeMap<String, Object> user = new TreeMap<>();
            user.put("userName", rs.getString(2));
            user.put("permissions", rs.getInt(5));
            users.put(rs.getString(1), user);
        }
        conn.close();
        return users;
    }

    /**
     * Deletes an entry in the users table given a userId. Admin user cannot be deleted.
     * @param userId
     * @throws SQLException
     */
    public void deleteUser(String userId) throws SQLException {
        connect();
        if (userId.equals("b220a053-91f1-48ee-acea-d1a145376e57")) {
            conn.close();
            return;
        }
        statement.executeQuery("DELETE FROM users WHERE userId=\"" + userId + "\"");
        conn.close();
    }

    /**
     * Deletes a list of users from the users table given a list of userIds. Admin user cannot be deleted.
     * @param userIds
     * @throws SQLException
     */
    public void deleteUsers(ArrayList<String> userIds) throws SQLException {
        connect();

        for (String userId : userIds) {
            if (userId.equals("b220a053-91f1-48ee-acea-d1a145376e57")) {
                continue;
            }
            statement.executeQuery("DELETE FROM users WHERE userId=\"" + userId + "\"");
        }
        conn.close();
    }

    /**
     * Inserts a billboard into the billboards table in the DB.
     * @param billboardId A randomly generated string used to identify the billboard.
     * @param billboardName A name for the billboard specified by the billboard creator.
     * @param billboardCreator A foreign key used to link the billboard to a user in the users table.
     * @param billboardMessage A message to be displayed by the billboard specified by the billboard creator.
     * @param billboardInfo Information to be displayed by the billboard specified by the billboard creator.
     * @param billboardPictureData A base64 encoded image to be displayed by the billboard specified by the billboard creator.
     * @param billboardPictureUrl An image url to be displayed by the billboard specified by the billboard creator.
     * @param billboardBg A background colour to be displayed by the billboard specified by the billboard creator.
     * @param billboardMsgColour A message colour to be displayed by the billboard specified by the billboard creator.
     * @param billboardInfoColour An information colour to be displayed by the billboard specified by the billboard creator.
     * @throws SQLException
     */
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

    /**
     * Updates a row in the billboards table given a billboardId. Billboard property arguments can be null.
     * @param billboardId A randomly generated string used to identify the billboard.
     * @param billboardName A name for the billboard specified by the billboard creator. Can be null.
     * @param billboardMessage A message to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardInfo Information to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardPictureData A base64 encoded image to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardPictureUrl An image url to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardBg A background colour to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardMsgColour A message colour to be displayed by the billboard specified by the billboard creator. Can be null.
     * @param billboardInfoColour An information colour to be displayed by the billboard specified by the billboard creator. Can be null.
     * @throws SQLException
     */
    public void editBillboard(String billboardId, String billboardName, String billboardCreator,
                              String billboardMessage, String billboardInfo, String billboardPictureData,
                              String billboardPictureUrl, String billboardBg, String billboardMsgColour,
                              String billboardInfoColour) throws SQLException {
        connect();
        //absolute mess but it works. if an argument isn't null, it gets appended to the query
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
        statement.executeQuery(editStatement);
        conn.close();
    }

    /**
     * Queries the billboards table in the DB for a list of billboards given a list of billboardIds.
     * @param billboardIds
     * @return billboards
     * @throws SQLException
     */
    public TreeMap<String, Object> getBillboards(ArrayList<String> billboardIds) throws SQLException {

        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();
        //treemap structure: [{billboardId, {billboardName, ..., informationColour}}, ...,  {billboardId, {..., informationColour}}]

        for (String billboardId : billboardIds) {
            ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + billboardId + "\"");
            //rs: {billboardId, billboardName, billboardCreator, message, ... (as below) , informationColour}
            if (rs.next()) {
                TreeMap<String, String> billboard = new TreeMap<>();
                billboard.put("billboardName", rs.getString(2));
                billboard.put("billboardCreator", rs.getString(3));
                billboard.put("message", rs.getString(4));
                billboard.put("information", rs.getString(5));
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

    /**
     * Queries the billboards table in the DB for all billboards.
     * @return billboards
     * @throws SQLException
     */
    public TreeMap<String, Object> getBillboards() throws SQLException {
        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();
        //treemap structure: [{billboardId, {billboardName, ..., informationColour}}, ...,  {billboardId, {..., informationColour}}]
        ResultSet rs = statement.executeQuery("SELECT * FROM billboards");

        while (rs.next()) {
            TreeMap<String, String> billboard = new TreeMap<>();
            billboard.put("billboardName", rs.getString(2));
            billboard.put("billboardCreator", rs.getString(3));
            billboard.put("message", rs.getString(4));
            billboard.put("information", rs.getString(5));
            billboard.put("pictureData", rs.getString(6));
            billboard.put("pictureUrl", rs.getString(7));
            billboard.put("billboardBackground", rs.getString(8));
            billboard.put("messageColour", rs.getString(9));
            billboard.put("informationColour", rs.getString(10));

            billboards.put(rs.getString(1), billboard);
        }

        return billboards;
    }

    /**
     * Deletes a list of entries in the billboards table given a list of billboardIds.
     * @param billboardIds
     * @throws SQLException
     */
    public void deleteBillboards(ArrayList<String> billboardIds) throws SQLException {
        connect();

        for (String billboardId : billboardIds) {
            statement.executeQuery("DELETE FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        }
        conn.close();
    }

    /**
     * Deletes an entry in the billboards table given a billboardId.
     * @param billboardId
     * @throws SQLException
     */
    public void deleteBillboard(String billboardId) throws SQLException {
        connect();
        statement.executeQuery("DELETE FROM billboards WHERE billboardId=\"" + billboardId + "\"");
        conn.close();
    }

    /**
     * Inserts an entry into the schedules table in the DB.
     * @param scheduleId A randomly generated string used to identify the schedule.
     * @param billboardId A randomly generated string used to identify the billboard. Foreign key for billboards.billboardId.
     * @param startTime The time the billboard is scheduled to start displaying.
     * @param duration The duration the billboard will display for.
     * @param isRecurring Whether the billboard will display again after it's initial display.
     * @param recurFreqInMins Time between each display of the billboard. Cannot be less than the duration.
     * @throws SQLException
     */
    public void addSchedule(String scheduleId, String billboardId, OffsetDateTime startTime, Integer duration, Boolean isRecurring, Integer recurFreqInMins) throws SQLException {
        connect();
        pstmt = conn.prepareStatement(addschedStatement);
        pstmt.setString(1, scheduleId);
        pstmt.setString(2, billboardId);
        pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.ofInstant(startTime.toInstant(), ZoneOffset.of("+10:00"))));
        pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        pstmt.setInt(5, duration);
        pstmt.setBoolean(6, isRecurring);
        pstmt.setInt(7, recurFreqInMins);
        pstmt.execute();
        conn.close();
    }

    /**
     * Updates an entry in the schedules table given a scheduleId. Schedule property arguments can be null.
     * @param scheduleId A randomly generated string used to identify a schedule.
     * @param billboardId A randomly generated string used to identify a billboard. Foreign key for billboards.billboardId. Can be null in this function.
     * @param startTime The time the billboard is scheduled to start displaying. Can be null in this function.
     * @param duration The duration the billboard will display for. Can be null in this function.
     * @param isRecurring Whether the billboard will display again after it's initial display. Can be null in this function.
     * @param recurFreqInMins Time between each display of the billboard. Cannot be less than the duration. Can be null in this function.
     * @throws SQLException
     */
    public void editSchedule(String scheduleId, String billboardId, OffsetDateTime startTime, Integer duration, Boolean isRecurring, Integer recurFreqInMins) throws SQLException {
        connect();
        //absolute mess but like above, it works. appends any arguments that aren't null to the query.
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
        statement.executeQuery(editStatement);
        conn.close();
    }

    /**
     * Queries the schedules table in the DB for all schedules.
     * @return schedules
     * @throws SQLException
     */
    public TreeMap<String, Object> getSchedules() throws SQLException {
        connect();
        TreeMap<String, Object> schedules = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM schedules");

        while (rs.next()) {
            TreeMap<String, Object> schedule = new TreeMap<>();
            schedule.put("scheduleId", rs.getString(1));
            schedule.put("billboardId", rs.getString(2));
            schedule.put("startTime", OffsetDateTime.of(rs.getTimestamp(3).toLocalDateTime(), ZoneOffset.of("+10:00")));
            schedule.put("scheduleCreationTime", OffsetDateTime.of(rs.getTimestamp(4).toLocalDateTime(), ZoneOffset.of("+10:00")));
            schedule.put("duration", rs.getInt(5));
            schedule.put("isRecurring", rs.getBoolean(6));
            schedule.put("recurFreqInMins", rs.getInt(7));
            schedule.put("billboardName", billboardIdToName(rs.getString(2)));
            schedules.put(rs.getString(1), schedule);
        }
        conn.close();
        return schedules;
    }

    /**
     * Queries the schedules table in the DB for a list of schedules given a list of scheduleIds.
     * @param scheduleIds
     * @return schedules
     * @throws SQLException
     */
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
                schedule.put("scheduleCreationTime", OffsetDateTime.of(rs.getTimestamp(4).toLocalDateTime(), ZoneOffset.of("+10:00")));
                schedule.put("duration", rs.getInt(5));
                schedule.put("isRecurring", rs.getBoolean(6));
                schedule.put("recurFreqInMins", rs.getInt(7));
                schedule.put("billboardName", billboardIdToName(rs.getString(2)));
                schedules.put(rs.getString(1), schedule);
            }
        }

        conn.close();
        return schedules;
    }

    /**
     * Deletes an entry from the schedules table given a scheduleId.
     * @param scheduleId
     * @throws SQLException
     */
    public void deleteSchedule(String scheduleId) throws SQLException { // deletes a schedule
        connect();
        statement.executeQuery("DELETE FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        conn.close();
    }

    /**
     * Deletes a list of entries in the schedules table given a list of scheduleIds.
     * @param scheduleIds
     * @throws SQLException
     */
    public void deleteSchedules(ArrayList<String> scheduleIds) throws SQLException { // deletes schedules
        connect();

        for (String scheduleId : scheduleIds) {
            statement.executeQuery("DELETE FROM schedules WHERE scheduleId=\"" + scheduleId + "\"");
        }
        conn.close();
    }

    /**
     * Drops the DB tables. Mostly used in testing.
     * @throws SQLException
     */
    public void dropDb() throws SQLException {
        connect();
        statement.executeQuery("DROP TABLE IF EXISTS schedules");
        statement.executeQuery("DROP TABLE IF EXISTS billboards");
        statement.executeQuery("DROP TABLE IF EXISTS users");
    }
}