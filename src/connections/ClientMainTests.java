package connections;

import connections.exceptions.ServerException;

import java.time.OffsetDateTime;
import java.util.*;

public class ClientMainTests {
    public static void main(String[] args) throws ServerException {
        Test1_LoginAdmin();

        // ALL PERMISSIONS: adding/editing/deleting users (as admin)
        Test2a_LoginAdminAddUsers();
        Test2b_LoginAdminEditJack();
        Test2c_LoginAdminDeleteHarry();

        // CREATE_BILLBOARDS: adding/editing/deleting own billboards (as joe)
        Test3a_LoginJoeAddBillboard();
        Test3b_LoginJoeEditBillboard();
        Test3c_LoginJoeDeleteBillboard();

        // EDIT_SCHEDULES: adding/editing/deleting schedules (as bob)
        Test4a_LoginBobScheduleBillboards();
        Test4b_LoginBobEditSchedules();
        Test4c_LoginBobDeleteSchedules();

        // EDIT_ALL_BILLBOARDS: editing/deleting other billboards (as jack)
        Test5a_LoginJackEditBillboard();
        Test5b_LoginJackDeleteBillboard();
    }

    static void Test1_LoginAdmin() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
    }

    static void Test2a_LoginAdminAddUsers() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
        server.addUser("joe", "1234", Protocol.Permission.CREATE_BILLBOARDS);
        server.addUser("bob", "2345", Protocol.Permission.SCHEDULE_BILLBOARDS);
        server.addUser("jack", "", Protocol.Permission.NONE);
        server.addUser("harry", "", Protocol.Permission.NONE);

        TreeMap<String, Object> confirmed = server.getUsers();
        System.out.println(confirmed);
    }

    static void Test2b_LoginAdminEditJack() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("admin", "cab203");

        String userId = server.getUserId("jack");
        server.editUser(userId, "jack", "3456", Protocol.Permission.EDIT_ALL_BILLBOARDS);

        TreeMap<String, Object> user = server.getUser(userId);
        System.out.println(user.toString());
    }

    static void Test2c_LoginAdminDeleteHarry() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("admin", "cab203");

        String userId = server.getUserId("harry");
        server.deleteUser(userId);

        TreeMap<String, Object> users = server.getUsers();
        System.out.println(users.toString());
    }


    static void Test3a_LoginJoeAddBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("joe", "1234");

        server.addBillboard("joe's billboard 1", randomNewBillboard());
        server.addBillboard("joe's billboard 2", randomNewBillboard());


        TreeMap<String, Object> confirmed = server.getBillboards();
        System.out.println(confirmed);
    }

    static void Test3b_LoginJoeEditBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("joe", "1234");

        String billboardId = server.getBillboardId("joe's billboard 1");
        TreeMap<String, String> billboard = server.getBillboard(billboardId);

        billboard.put("message", "new updated message");

        server.editBillboard(billboardId, billboard);


        TreeMap<String, String> editedBillboard = server.getBillboard(billboardId);
        System.out.println(editedBillboard);
    }

    static void Test3c_LoginJoeDeleteBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("joe", "1234");

        server.deleteBillboard(server.getBillboardId("joe's billboard 1"));


        TreeMap<String, Object> billboards = server.getBillboards();
        System.out.println(billboards);
    }

    static void Test4a_LoginBobScheduleBillboards() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("bob", "2345");

        String billboardId = server.getBillboardId("joe's billboard 2");
        server.addSchedule(randomNewSchedule(billboardId));

        String scheduleId = server.getScheduleId(billboardId);

        TreeMap<String, Object> confirmed = server.getSchedule(scheduleId);
        System.out.println(confirmed);
    }

    static void Test4b_LoginBobEditSchedules() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("bob", "2345");

        String billboardId = server.getBillboardId("joe's billboard 2");
        String scheduleId = server.getScheduleId(billboardId);
        server.editSchedule(scheduleId, randomNewSchedule(billboardId));


        TreeMap<String, Object> confirmed = server.getSchedule(scheduleId);
        System.out.println(confirmed);
    }

    static void Test4c_LoginBobDeleteSchedules() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("bob", "2345");

        String scheduleId = server.getScheduleId(server.getBillboardId("joe's billboard 2"));
        server.deleteSchedule(scheduleId);

        TreeMap<String, Object> confirmed = server.getSchedules();
        System.out.println(confirmed);
    }

    static void Test5a_LoginJackEditBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("jack", "3456");

        String billboardId = server.getBillboardId("joe's billboard 2");
        TreeMap<String, String> billboard = server.getBillboard(billboardId);
        billboard.replace("message", "updated by jack");
        server.editBillboard(billboardId, billboard);


        TreeMap<String, String> confirmed = server.getBillboard(billboardId);
        System.out.println(confirmed);
    }

    static void Test5b_LoginJackDeleteBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();
        server.login("jack", "3456");

        String billboardId = server.getBillboardId("joe's billboard 2");
        server.deleteBillboard(billboardId);


        TreeMap<String, Object> confirmed = server.getBillboards();
        System.out.println(confirmed);
    }


    // helper functions
    static TreeMap<String, String> randomNewBillboard() {
        Random r = new Random();

        TreeMap<String, String> body = new TreeMap<>();

        body.put("message", "" + (char) (r.nextInt(26) + 'a'));
        body.put("information", "" + (char) (r.nextInt(26) + 'a'));
        body.put("pictureData", "" + (char) (r.nextInt(26) + 'a'));
        body.put("pictureUrl", "" + (char) (r.nextInt(26) + 'a'));
        body.put("billboardBackground", "" + (char) (r.nextInt(26) + 'a'));
        body.put("messageColour", "" + (char) (r.nextInt(26) + 'a'));
        body.put("informationColour", "" + (char) (r.nextInt(26) + 'a'));

        return body;
    }

    static TreeMap<String, Object> randomNewSchedule(String billboardId) {
        Random r = new Random();

        TreeMap<String, Object> body = new TreeMap<>();

        body.put("billboardId", billboardId);
        body.put("startTime", OffsetDateTime.now());
        body.put("duration", r.nextInt());
        body.put("isRecurring", r.nextBoolean());
        body.put("recurFreqInMins", r.nextInt());

        return body;
    }

    // helper functions
    static TreeMap<String, String> randomEditBillboard(TreeMap<String, String> original) {
        Random r = new Random();

        original.put("message", "" + (char) (r.nextInt(26) + 'a'));
        original.put("information", "" + (char) (r.nextInt(26) + 'a'));
        original.put("pictureData", "" + (char) (r.nextInt(26) + 'a'));
        original.put("pictureUrl", "" + (char) (r.nextInt(26) + 'a'));
        original.put("billboardBackground", "" + (char) (r.nextInt(26) + 'a'));
        original.put("messageColour", "" + (char) (r.nextInt(26) + 'a'));
        original.put("informationColour", "" + (char) (r.nextInt(26) + 'a'));

        return original;
    }
}
