package connections.testing;

import connections.ClientServerInterface;
import connections.Protocol;
import connections.exceptions.ServerException;

import java.util.Map;
import java.util.TreeMap;

public class AdminModifyUsers {
    public static void main(String[] args) throws Exception {

        ClientServerInterface server = new ClientServerInterface();

        server.login("admin", "admin");

        modifyUsers(server);

        System.out.println(server.getAllUsers());

        System.out.println(server.getAllBillboards());
    }

    public static void modifyUsers(ClientServerInterface server) {

        String[] passwords = new String[]{"admin1", "test11", "test22", "test33"};
        String[] permissions = new String[]{
                Protocol.Permission.NONE,
                Protocol.Permission.NONE,
                Protocol.Permission.NONE,
                Protocol.Permission.NONE};

        TreeMap<String, TreeMap<String, String>> users;

        try {
            users = server.getAllUsers();
        } catch (ServerException e) {
            e.printStackTrace();
            return;
        }

        int i = 0;
        for (Map.Entry<String, TreeMap<String, String>> user : users.entrySet()) {

            try {
                // if user is editing himself, user can't remove the edit_users permission
                server.modifyUser(user.getKey(), "(edited by admin) " + user.getKey(), passwords[i], permissions[i]);
            } catch (ServerException e) {
                System.out.println(e.getMessage());
            }

            i++;
        }
    }
}
