package connections.testing;

import connections.ClientServerInterface;
import connections.exceptions.ServerException;

import java.util.TreeMap;

public class User1AddBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user1", "test1");

        addBillboards(server);

        System.out.println(server.getAllBillboards());
    }

    public static void addBillboards(ClientServerInterface server) {
        String[] billboardNames = new String[]{"user1's billboard1", "user1's billboard2",
                "user1's billboard2", "user1's billboard3"};

        for (String billboard : billboardNames) {
            TreeMap<String, String> data = new TreeMap<>();
            // TODO testing
            data.put("default", "Advertise Here!!!");
            data.put("message", java.util.UUID.randomUUID().toString());

            try {
                server.sendNewBillboard("user1", billboard, data);
            } catch (ServerException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
