package connections.testing;

import connections.ClientServerInterface;
import connections.Protocol;
import connections.Protocol.*;
import connections.exceptions.ServerException;

public class AdminAddUsers {
    public static void main(String[] args) throws Exception {

        ClientServerInterface server = new ClientServerInterface();

        server.addNewUser("admin", "admin", Protocol.Permission.ALL);

        server.login("admin", "admin");

        addUsers(server);

        System.out.println(server.getAllUsers());
    }

    // NOTE: this can only be called once, as it will be stored server and client side
    // tests adding users and skipping existing users
    public static void addUsers(ClientServerInterface server) {
        String[] users = new String[]{"user1", "user2", "user2", "user3"};
        String[] passwords = new String[]{"test1", "test2", "test2", "test3"};
        String[] permissions = new String[]{
                Permission.CREATE_BILLBOARDS,
                Permission.EDIT_ALL_BILLBOARDS,
                Permission.EDIT_ALL_BILLBOARDS,
                Permission.SCHEDULE_BILLBOARDS};

        for (int i = 0; i < users.length; i++) {
            try {
                server.addNewUser(users[i], passwords[i], permissions[i]);
            } catch (ServerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
