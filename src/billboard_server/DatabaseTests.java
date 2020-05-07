package billboard_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.TreeMap;

public class DatabaseTests {

    Database db;
    TreeMap<String, String> user;
    TreeMap<String, String> billboard;
    TreeMap<String, String> schedule;

    public DatabaseTests() throws SQLException {
    }

    @BeforeEach
    public void createDb() throws SQLException {
        db = new Database();
    }

    //TODO: write some tests

}
