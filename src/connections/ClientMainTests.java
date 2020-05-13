package connections;

import connections.exceptions.ServerException;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ClientMainTests {
    public static void main(String[] args) throws ServerException {
        Test1_LoginAdmin();

        Test2_LoginAdminAddUser();

        Test3_LoginJoeAddBillboard();

        Test4_GetBillboards();
    }

    static void Test1_LoginAdmin() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
    }

    static void Test2_LoginAdminAddUser() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "cab203");
        server.addUser("joe", "1234", Protocol.Permission.EDIT_ALL_BILLBOARDS);
    }

    static void Test3_LoginJoeAddBillboard() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        server.login("joe", "1234");
        server.addBillboard("joe's billboard", randomBillboard());
    }

    static void Test4_GetBillboards() throws ServerException {
        ClientServerInterface server = new ClientServerInterface();

        TreeMap<String, Object> billboards = server.getBillboards();

        for (Map.Entry<String, Object> billboard : billboards.entrySet()) {
            TreeMap<String, String> data = (TreeMap<String, String>) billboard.getValue();
            System.out.println(billboard.getKey() + ": " + data);
        }
    }


    // helper functions
    static TreeMap<String, String> randomBillboard() {
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
}
