package connections.testing;

import connections.ClientServerInterface;

import java.util.TreeMap;

public class User1AddBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user1", "test1");

        addBillboards(server);

        System.out.println(server.getAllBillboards());
    }

    public static void addBillboards(ClientServerInterface server) throws Exception {
        String[] billboardNames = new String[]{"user1's billboard1", "user1's billboard2", "user1's billboard3"};

        for (String billboard : billboardNames) {
            TreeMap<String, String> data = new TreeMap<>();
            data.put("message", java.util.UUID.randomUUID().toString());

            server.addBillboard(billboard, data);
        }
    }
}
