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
        db.addBillboard(billboardId, "test", userId, "msg", "info", null, "url", "bg", null, "infoCol");
        db.addSchedule(scheduleId, billboardId, date, 120, true, 3600);

        System.out.println(db.getUsers());
        System.out.println(db.getBillboards());
        System.out.println(db.getSchedules());

        db.editUser(userId, "jeff", null, null, 0);
        db.editBillboard(billboardId, "updateTest", null, "hello", null, null, "google", null, null, null);
        db.editSchedule(scheduleId, null, null, 10, false, 0);

        System.out.println(db.getUsers());
        System.out.println(db.getBillboards());
        System.out.println(db.getSchedules());
    }
}
