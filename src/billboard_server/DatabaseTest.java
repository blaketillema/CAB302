package billboard_server;

import java.sql.SQLException;
import java.time.*;

public class DatabaseTest {

    public static void main(String args[]) throws SQLException {
        Database db = new Database();
        String userId = java.util.UUID.randomUUID().toString();
        String billboardId = java.util.UUID.randomUUID().toString();
        OffsetDateTime date = OffsetDateTime.now();
        String scheduleId = java.util.UUID.randomUUID().toString();

        db.addUser(userId, "test", "test", "test", 8);
        db.addBillboard(billboardId, "test", userId, "msg", "info", "", "url", "bg", "msgCol", "infoCol");
        db.addSchedule(scheduleId, billboardId, date, 120, true, 3600);

        System.out.println(db.getUsers());
        System.out.println(db.getBillboards());
        System.out.println(db.getSchedules());

        db.deleteUser(userId);
        db.deleteBillboard(billboardId);
        db.deleteSchedule(scheduleId);

        System.out.println(db.getUsers());
        System.out.println(db.getBillboards());
        System.out.println(db.getSchedules());
    }
}