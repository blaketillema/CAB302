package billboard_server;

import connections.exceptions.ServerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.TreeMap;

public class DatabaseTests {

    Database db;
    String userId = "admin12345";

    public DatabaseTests() throws SQLException {
    }

    @BeforeEach
    public void createDb() throws SQLException {
        db = new Database();
        db.addTestAdmin();
    }

    @AfterEach
    public void dropDB() throws SQLException {
        db.dropDb();
    }

    @Test
    public void addUser() throws SQLException, ServerException {
        TreeMap<String, Object> users = new TreeMap<>();
        TreeMap<String, Object> user = new TreeMap<>();
        user.put("userId", "abc123");
        user.put("userName", "grant");
        user.put("hash", "brown");
        user.put("salt", "pepper");
        user.put("permissions", 5);
        users.put(user.get("userId").toString(), user);
        db.addUsers(userId, users);
    }

    @Test
    public void deleteUser() throws SQLException, ServerException {
        TreeMap<String, Object> users = new TreeMap<>();
        TreeMap<String, Object> user = new TreeMap<>();
        user.put("userId", "abc123");
        user.put("userName", "grant");
        user.put("hash", "brown");
        user.put("salt", "pepper");
        user.put("permissions", 5);
        users.put(user.get("userId").toString(), user);
        db.addUsers(userId, users);
        db.deleteUsers(userId, users);
    }

    //TODO: write some tests

}
