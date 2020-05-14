package connections;

import connections.exceptions.ServerException;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

public class ClientMainTests {
    public static void main(String[] args) throws ServerException {
        Test1_LoginAdmin();

        Test2_LoginAdminAddUser();

        Test3_LoginJoeAddBillboard();

        Test4_GetBillboards();

        Test7_LoginBobScheduleBillboards();

        Test8_getSchedules();
    }

    static void Test1_LoginAdmin() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
    }

    static void Test2_LoginAdminAddUser() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
        server.addUser("joe", "1234", Protocol.Permission.CREATE_BILLBOARDS);
        server.addUser("bob", "2345", Protocol.Permission.SCHEDULE_BILLBOARDS);
        server.addUser("jack", "3456", Protocol.Permission.EDIT_ALL_BILLBOARDS);
    }

    static void Test3_LoginJoeAddBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("joe", "1234");
        server.addBillboard("joe's billboard", randomNewBillboard());
    }

    static void Test4_GetBillboards() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        TreeMap<String, Object> billboards = server.getBillboards();

        for (Map.Entry<String, Object> billboard : billboards.entrySet()) {
            TreeMap<String, String> data = (TreeMap<String, String>) billboard.getValue();
            System.out.println(billboard.getKey() + ": " + data);
        }
    }

    @Deprecated
    static void Test5_LoginJoeEditOwn() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("joe", "1234");

        TreeMap<String, Object> edited = new TreeMap<>();
        TreeMap<String, Object> billboards = server.getBillboards();

        for (Map.Entry<String, Object> billboard : billboards.entrySet()) {
            TreeMap<String, String> data = (TreeMap<String, String>) billboard.getValue();
            edited.put(billboard.getKey(), randomEditBillboard(data));
        }

        server.editBillboards(edited);
    }

    @Deprecated
    static void Test6_LoginAdminModifyJoe() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
    }

    static void Test7_LoginBobScheduleBillboards() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("bob", "2345");

        TreeMap<String, Object> schedule = new TreeMap<>();
        TreeMap<String, Object> billboards = server.getBillboards();

        for (Map.Entry<String, Object> billboard : billboards.entrySet()) {
            schedule.put(java.util.UUID.randomUUID().toString(), randomNewSchedule(billboard.getKey()));
        }

        server.addSchedules(schedule);
    }

    static void Test8_getSchedules() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        TreeMap<String, Object> schedules = server.getSchedules();

        for (Map.Entry<String, Object> billboard : schedules.entrySet()) {
            TreeMap<String, Object> data = (TreeMap<String, Object>) billboard.getValue();
            System.out.println(billboard.getKey() + ": ");

            System.out.println((String) data.get("billboardId"));
            System.out.println((String) data.get("startTime"));
            System.out.println((Integer) data.get("duration"));
            System.out.println((Boolean) data.get("isRecurring"));
            System.out.println((Integer) data.get("recurFreqInMins"));
        }
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
        body.put("startTime", "01/01/2000 01:01:01");
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
