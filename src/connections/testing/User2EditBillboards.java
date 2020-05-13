package connections.testing;

import connections.ClientServerInterface;
import connections.exceptions.ServerException;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Deprecated
public class User2EditBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user2", "test2");

        editBillboards(server);

        System.out.println(server.getAllBillboards());
    }

    public static void editBillboards(ClientServerInterface server) {
        TreeMap<String, TreeMap<String, String>> oldData;

        try {
            oldData = server.getAllBillboards();
        } catch (ServerException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (Map.Entry<String, TreeMap<String, String>> billboard : oldData.entrySet()) {
            // change title:
            String newTitle = "(edit by user 2) " + billboard.getKey();
            oldData.get(billboard.getKey()).put("renameTo", newTitle);

            // change message
            oldData.get(billboard.getKey()).put("message", java.util.UUID.randomUUID().toString());
        }

        try {
            server.sendEditedBillboards(oldData);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }
}
