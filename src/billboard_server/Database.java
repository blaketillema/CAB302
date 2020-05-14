package billboard_server;

import connections.exceptions.*;
import connections.*;
import connections.tools.UserAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Database {

    private static final String USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ( " +
<<<<<<< HEAD
            "userId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "userName VARCHAR(191)," +
            "hash VARCHAR(191) NOT NULL," +
            "salt VARCHAR(191) NOT NULL," +
=======
            "userId VARCHAR(255) PRIMARY KEY NOT NULL,"+
            "userName VARCHAR(255)," +
            "hash VARCHAR(255) NOT NULL,"+
            "salt VARCHAR(255) NOT NULL,"+
>>>>>>> billboardServerDB
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
            "billboardInfoColour VARCHAR(191) )";
    // TODO: add support for startTime as datetime object
    private static final String SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS schedules ( " +
            "scheduleId VARCHAR(191) PRIMARY KEY NOT NULL," +
            "billboardId VARCHAR(191), " +
            "startTime VARCHAR(191), " +
            "duration INT, " +
            "isRecurring BOOLEAN, " +
            "recurFreqInMins INT, " +
            "FOREIGN KEY (billboardId) REFERENCES billboards(billboardId) ON DELETE CASCADE ) ";

    private static final String adduserStatement = "INSERT INTO users (userId, userName, hash, salt, permissions) VALUES (?, ?, ?, ?, ?)";
    private static final String deluserStatement = "DELETE FROM users WHERE userId=?";
    private static final String addbilbStatement = "INSERT INTO billboards (billboardId, billboardName, billboardCreator, billboardMessage, billboardInfo, billboardPictureData, billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String delbilbStatement = "DELETE FROM billboards WHERE billboardId=?";
    private static final String addschedStatement = "INSERT INTO schedules (scheduleId, billboardId, startTime, duration, isRecurring, recurFreqInMins) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String delschedStatement = "DELETE FROM schedules WHERE scheduleId=?";

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

<<<<<<< HEAD
        dropDb();

        String adminUserID = "b220a053-91f1-48ee-acea-d1a145376e57";
        String adminSalt = "2219d4ec595ce93cabfe7c7941d7e274";
        String adminHash = UserAuth.hashAndSalt("7ec582cb6dda5f00485d4f3026c1309ba7c3eb255cdfbdcb4a3fb3646d74953d", adminSalt);

=======
>>>>>>> billboardServerDB
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

    private int getPermission(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT permissions FROM users WHERE userId=\"" + userId + "\"");
<<<<<<< HEAD
        if (rs.next()) {
=======
        if(rs.next()){
>>>>>>> billboardServerDB
            return rs.getInt(1);
        } else {
            return 0;
        }
    }

    private void checkPermission(String userId, int permissionNeeded) throws SQLException, ServerException {
        if (userId == null) {
            throw new ServerException("tried to check permissions for null user");
        }
        if ((getPermission(userId) & permissionNeeded) == 0) {
            throw new PermissionException(userId, getPermission(userId), permissionNeeded);
        }
    }

    private boolean doesUserExist(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE userId=\"" + userId + "\"");
        return rs.next();
    }

    public String userNameToId(String userName) throws SQLException, ServerException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT userId FROM users WHERE userName=\"" + userName + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            throw new ServerException(userName + "does not exist in database");
        }
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

    public String getSalt(String userId) throws SQLException {
        connect();
        ResultSet rs = statement.executeQuery("SELECT salt FROM users WHERE userId=\"" + userId + "\"");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public void addUsers(String userId, TreeMap<String, Object> data) throws ServerException, SQLException {

        /* make sure user attempting to add new users has permissions */
        checkPermission(userId, Protocol.Permission.EDIT_USERS);

        /* loop through each user in the treemap */
        for (Map.Entry<String, Object> user : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each user */
            TreeMap<String, Object> userDetails = (TreeMap<String, Object>) user.getValue();

            /* get all of the relevant values and cast to types */
            String newUserId = user.getKey();
            String newUsername = (String) userDetails.get("userName");
            String newSalt = (String) userDetails.get("salt");
            String newHash = UserAuth.hashAndSalt((String) userDetails.get("hash"), newSalt);
            int newPermissions = -1;
            if (userDetails.containsKey("permissions"))
                newPermissions = (int) userDetails.get("permissions");

            /* check if the user to be added exists or not. */
            boolean userExists = doesUserExist(user.getKey());

            /* if user doesn't exist, make sure all user information has been provided and add it */
            if (!userExists) {
                if (newUsername == null || newHash == null || newSalt == null || newPermissions == -1) {
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

    public TreeMap<String, Object> getUsers() throws SQLException {
        connect();
        TreeMap<String, Object> users = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM users");

        while (rs.next()) {
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
        for (Map.Entry<String, Object> user : data.entrySet()) {
            pstmt = conn.prepareStatement(deluserStatement);
            pstmt.setString(1, user.getKey());
            pstmt.execute();
        }
    }

    public void addBillboards(String userId, TreeMap<String, Object> data) throws ServerException, SQLException {

        boolean addedAny = false;

        /* loop through each billboard in the treemap */
        for (Map.Entry<String, Object> billboard : data.entrySet()) {

            /* cast the value of the treemap entry and get the details of each billboard */
            TreeMap<String, Object> billboardDetails = (TreeMap<String, Object>) billboard.getValue();

            /* get all of the relevant values and cast to types */
            String newId = billboard.getKey();
            String newName = (String) billboardDetails.get(Protocol.BOARDNAME);
            String newCreator = null;
            String newMessage = (String) billboardDetails.get("message");
            String newInfo = (String) billboardDetails.get("information");
            String newPictureData = (String) billboardDetails.get("pictureData");
            String newPictureUrl = (String) billboardDetails.get("pictureUrl");
            String newBillboardBackground = (String) billboardDetails.get("billboardBackground");
            String newMessageColour = (String) billboardDetails.get("messageColour");
            String newInformationColour = (String) billboardDetails.get("informationColour");

            connect();
            ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + newId + "\"");

            boolean isAdding = false;
            boolean isEditing = false;

            try {
                // if billboard is already in database
                if (rs.next()) {
                    newCreator = (String) billboardDetails.get(Protocol.BOARDCREATOR);
                    // if userId provided matches userId of billboard, check if they can edit their own
                    if (rs.getString(3).equals(userId)) {
                        checkPermission(userId, Protocol.Permission.CREATE_BILLBOARDS);
                    }
                    // if userId doesn't match billboard userId, check if they have edit all permission
                    else {
                        checkPermission(userId, Protocol.Permission.EDIT_ALL_BILLBOARDS);
                    }
                    isEditing = true;
                    throw new SQLException("editing billboards not implemented yet");
                }
                // if billboard isn't in database, check if they can add billboards
                else {
                    checkPermission(userId, Protocol.Permission.CREATE_BILLBOARDS);
                    newCreator = userId;
                    isAdding = true;
                }
            } catch (ServerException e) {
                e.printStackTrace();
                continue;
            }

            /* prepare and execute the statement to add the billboard:
            billboardId, billboardMessage, billboardInfo, billboardPictureData,
            billboardPictureUrl, billboardBg, billboardMsgColour, billboardInfoColour */
            // TODO: add modify billboard statement
            if (isAdding) {
                pstmt = conn.prepareStatement(addbilbStatement);
                pstmt.setString(1, newId);
            } else if (isEditing) {
                // pstmt = conn.prepareStatement(modBilbStatment);
            }

            pstmt.setString(2, newName);
            pstmt.setString(3, newCreator);
            pstmt.setString(4, newMessage);
            pstmt.setString(5, newInfo);
            pstmt.setString(6, newPictureData);
            pstmt.setString(7, newPictureUrl);
            pstmt.setString(8, newBillboardBackground);
            pstmt.setString(9, newMessageColour);
            pstmt.setString(10, newInformationColour);
            pstmt.execute();

            addedAny = true;
        }

        if (!addedAny) {
            throw new ServerException("all billboards sent to the server weren't added (bad permissions)");
        }
    }

    public TreeMap<String, Object> getBillboards() throws SQLException {
        connect();
        TreeMap<String, Object> billboards = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM billboards");

        while (rs.next()) {
            TreeMap<String, String> billboard = new TreeMap<>();
            billboard.put(Protocol.BOARDNAME, rs.getString(2));
            billboard.put(Protocol.BOARDCREATOR, rs.getString(3));
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

    public void deleteBillboards(String userId, TreeMap<String, Object> data) throws SQLException, ServerException {
        connect();

        for (Map.Entry<String, Object> billboard : data.entrySet()) {

            ResultSet rs = statement.executeQuery("SELECT * FROM billboards WHERE billboardId=\"" + userId + "\"");

            try {
                // if billboard is in database
                if (rs.next()) {
                    // if userId provided matches userId of billboard, check if they can edit their own
                    if (rs.getString(3).equals(userId)) {
                        checkPermission(userId, Protocol.Permission.CREATE_BILLBOARDS);
                    }
                    // if userId doesn't match billboard userId, check if they have edit all permission
                    else {
                        checkPermission(userId, Protocol.Permission.EDIT_ALL_BILLBOARDS);
                    }
                }
            } catch (ServerException e) {
                e.printStackTrace();
                continue;
            }

            pstmt = conn.prepareStatement(delbilbStatement);
            pstmt.setString(1, billboard.getKey());
            pstmt.execute();
        }
    }

    public void addSchedule(String userId, TreeMap<String, Object> data) throws SQLException, ServerException {
        connect();
        checkPermission(userId, Protocol.Permission.SCHEDULE_BILLBOARDS);

        /*(scheduleId, billboardId, startTime, scheduleDuration, isRecurring, recurFreqInMins)*/
        for (Map.Entry<String, Object> schedule : data.entrySet()) {

            TreeMap<String, Object> scheduleDetails = (TreeMap<String, Object>) schedule.getValue();

            String scheduleId = schedule.getKey();
            String billboardId = (String) scheduleDetails.get("billboardId");
            String startTime = (String) scheduleDetails.get("startTime");
            int duration = -1;
            if (scheduleDetails.containsKey("duration")) {
                duration = (Integer) scheduleDetails.get("duration");
            }

            boolean containsIsRecurring = false;
            boolean isRecurring = false;
            if (scheduleDetails.containsKey("isRecurring")) {
                containsIsRecurring = true;
                isRecurring = (Boolean) scheduleDetails.get("isRecurring");
            }

            int recurFreqInMins = -1;
            if (scheduleDetails.containsKey("recurFreqInMins")) {
                recurFreqInMins = (Integer) scheduleDetails.get("recurFreqInMins");
            }

            boolean scheduleExists = false;

            if (!scheduleExists) {
                if (scheduleId == null || billboardId == null || startTime == null || duration == -1 || !containsIsRecurring || recurFreqInMins == -1) {
                    throw new ServerException("all schedule details not provided");
                }

                pstmt = conn.prepareStatement(addschedStatement);
                pstmt.setString(1, scheduleId);
                pstmt.setString(2, billboardId);
                pstmt.setString(3, startTime);
                pstmt.setInt(4, duration);
                pstmt.setBoolean(5, isRecurring);
                pstmt.setInt(6, recurFreqInMins);
                pstmt.execute();
            } else {

            }
        }
    }

    public TreeMap<String, Object> getSchedules() throws SQLException {
        connect();
        TreeMap<String, Object> schedules = new TreeMap<>();

        ResultSet rs = statement.executeQuery("SELECT * FROM schedules");

        while (rs.next()) {
            TreeMap<String, Object> schedule = new TreeMap<>();
            schedule.put("billboardId", rs.getString(2));
            schedule.put("startTime", rs.getString(3));
            schedule.put("duration", rs.getInt(4));
            schedule.put("isRecurring", rs.getBoolean(5));
            schedule.put("recurFreqInMins", rs.getInt(6));

            schedules.put(rs.getString(1), schedule);
        }

        return schedules;
    }

    public void deleteSchedules(String userId, TreeMap<String, Object> data) throws SQLException, ServerException { // deletes a user
        connect();
        for (Map.Entry<String, Object> schedule : data.entrySet()) {
            pstmt = conn.prepareStatement(delschedStatement);
            pstmt.setString(1, schedule.getKey());
            pstmt.execute();
        }
    }

    public void dropDb() throws SQLException {
        connect();
        statement.executeQuery("DROP TABLE IF EXISTS schedules");
        statement.executeQuery("DROP TABLE IF EXISTS billboards");
        statement.executeQuery("DROP TABLE IF EXISTS users");
    }
<<<<<<< HEAD
}
=======

    public void addTestAdmin() throws SQLException{
        connect();
        statement.executeQuery("INSERT INTO users (userId, userName, hash, salt, permissions) VALUES (\"admin12345\", \"adminname\", \"adminhash\", \"adminsalt\", 8)");
    }
}
>>>>>>> billboardServerDB
