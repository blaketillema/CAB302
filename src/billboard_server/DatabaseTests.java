package billboard_server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class DatabaseTests {

    private Database db = new Database();

    public DatabaseTests() throws SQLException {
    }

    @BeforeEach
    public void createEmptyDB() throws SQLException {
        db.dropDb();
        db = new Database();
    }

    @Test
    public void addUser() throws SQLException{
        db.addUser("jeff", "hash", "salt", "perms");
        String[][] res = new String[1][4];
        res[0][0] = "jeff";
        res[0][1] = "hash";
        res[0][2] = "salt";
        res[0][3] = "perms";
        Assertions.assertArrayEquals(res, db.getUsers());
    }

    @Test
    public void addTwoUsers() throws SQLException{
        db.addUser("jeff", "hash", "salt", "perms");
        db.addUser("paul", "brown", "pepper", "none");
        String[][] res = new String[2][4];
        res[0][0] = "jeff";
        res[0][1] = "hash";
        res[0][2] = "salt";
        res[0][3] = "perms";
        res[1][0] = "paul";
        res[1][1] = "brown";
        res[1][2] = "pepper";
        res[1][3] = "none";
        Assertions.assertArrayEquals(res, db.getUsers());
    }

    @Test
    public void addBillboard() throws SQLException{
        db.addBillboard("bilb name", "1234abcd");
        String[][] res = new String[1][3];
        res[0][0] = "1";
        res[0][1] = "bilb name";
        res[0][2] = "1234abcd";
        Assertions.assertArrayEquals(res, db.getBillboards());
    }

    @Test
    public void addSchedule() throws SQLException{
        db.addBillboard("bilb", "");
        db.addSchedule("bilb", LocalTime.MIDNIGHT);
        String[][] res = new String[1][2];
        res[0][0] = "1";
        res[0][1] = LocalDate.now().toString() + " " + LocalTime.MIDNIGHT.toString() + ":00.0";
        Assertions.assertArrayEquals(res, db.getSchedule());
    }

}
