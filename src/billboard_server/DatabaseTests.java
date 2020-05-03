package billboard_server;

import com.sun.source.tree.Tree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

public class DatabaseTests {

    Database db = new Database();
    TreeMap<String, String> user;
    TreeMap<String, String> billboard;
    TreeMap<String, String> schedule;

    public DatabaseTests() throws SQLException {
    }

    @BeforeEach
    public void reset() throws SQLException {
        db.dropDb();
        db = new Database();

        user = new TreeMap<>();
        user.put("userName", "jeff");
        user.put("hash", "brown");
        user.put("salt", "pepper");
        user.put("permissions", "none");

        billboard = new TreeMap<>();
        billboard.put("billboardName", "grant mcdonald");
        billboard.put("billboardMessage", null);
        billboard.put("billboardInfo", null);
        billboard.put("billboardImg", "img64");
        billboard.put("billboardBg", null);
        billboard.put("billboardMsgColour", null);
        billboard.put("billboardInfoColour", null);

        schedule = new TreeMap<>();
        schedule.put("billboardName", "grant mcdonald");
        schedule.put("startDate", LocalDate.now().toString());
        schedule.put("startTime", LocalTime.now().toString());
        schedule.put("duration", "120");
        schedule.put("isRecurring", "false");
        schedule.put("recurFreqInMins", "0");
    }

    @Test
    public void addUser() throws SQLException{
        db.addUser(user);
        String[][] res = new String[1][4];
        res[0][0] = "jeff";
        res[0][1] = "brown";
        res[0][2] = "pepper";
        res[0][3] = "none";
        Assertions.assertArrayEquals(res, db.getUsers());
    }

    @Test
    public void addTwoUsers() throws SQLException{
        db.addUser(user);
        TreeMap<String, String> user2 = new TreeMap<>();
        user2.put("userName", "test");
        user2.put("hash", "test");
        user2.put("salt", "test");
        user2.put("permissions", "test");
        db.addUser(user2);
        String[][] res = new String[2][4];
        res[0][0] = "jeff";
        res[0][1] = "brown";
        res[0][2] = "pepper";
        res[0][3] = "none";
        res[1][0] = "test";
        res[1][1] = "test";
        res[1][2] = "test";
        res[1][3] = "test";
        Assertions.assertArrayEquals(res, db.getUsers());
    }

    @Test
    public void addBillboard() throws SQLException{
        db.addBillboard(billboard);
        String[][] res = new String[1][3];
        res[0][0] = "1";
        res[0][1] = "grant mcdonald";
        res[0][2] = "img64";
        Assertions.assertArrayEquals(res, db.getBillboards());
    }

    @Test
    public void addSchedule() throws SQLException{
        db.addBillboard(billboard);
        LocalTime testTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        db.addSchedule(schedule);
        String[][] res = new String[1][5];
        res[0][0] = "grant mcdonald";
        res[0][1] = LocalDate.now().toString() + ' ' + testTime.format(formatter) + ".0";
        res[0][2] = "120";
        res[0][3] = "0";
        res[0][4] = "0";
        Assertions.assertArrayEquals(res, db.getSchedule());
    }

}
