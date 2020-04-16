package connections;

import java.util.TreeMap;
import connections.Protocol.*;
import connections.exceptions.HttpException;

public class ControlPanelMain
{

    // NOTE: this can only be called once, as it will be stored server and client side
    public static void testAddAdminToDatabases(ClientServerInterface server) throws Exception {
        String pw = "admin";

        try {
            server.addNewUser("admin", pw, "1111");
        } catch (HttpException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void testAdminLogin(ClientServerInterface server) throws Exception {
        String pw = "admin";
        // login as admin
        try {
            server.login("admin", pw);
        } catch (HttpException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // NOTE: this can only be called once, as it will be stored server and client side
    public static void testAddUsersToDatabases(ClientServerInterface server)
    {
        String[] users = new String[] {"joe", "bob", "jack"};
        String[] passwords = new String[] {"test1", "test2", "test3"};
        String[] permissions = new String[] {
                Permission.combine(Permission.EDIT_USERS, Permission.CREATE_BILLBOARDS),
                Permission.combine(Permission.CREATE_BILLBOARDS, Permission.SCHEDULE_BILLBOARDS),
                Permission.combine(Permission.EDIT_ALL_BILLBOARDS)};

        for(int i = 0; i < users.length; i++)
        {
            try {
                server.addNewUser(users[i], passwords[i], permissions[i]);
            } catch (HttpException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    public static void testAddBillboards(ClientServerInterface server)
    {
        String[] billboardNames = new String[] {"billboard1", "billboard2", "billboard3"};

        for(String billboard : billboardNames)
        {
            TreeMap<String, String> data = new TreeMap<>();
            data.put("message", java.util.UUID.randomUUID().toString());

            try {
                server.addBillboard(billboard, data);
            } catch (HttpException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

    public static void main(String[] args) throws Exception {

        ClientServerInterface server = new ClientServerInterface();

        testAddAdminToDatabases(server);

        testAdminLogin(server);

        testAddUsersToDatabases(server);

        testAddBillboards(server);

        System.out.println(server.getAllBillboards());

    }
}
