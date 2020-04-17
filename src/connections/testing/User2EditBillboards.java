package connections.testing;

import connections.ClientServerInterface;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class User2EditBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user2", "test2");

        editBillboards(server);

        System.out.println(server.getAllBillboards());
    }

    public static void editBillboards(ClientServerInterface server) throws Exception {
        TreeMap<String, TreeMap<String, String>> oldData = server.getAllBillboards();
        TreeMap<String, TreeMap<String, String>> oldNames = new TreeMap<>();
        TreeMap<String, TreeMap<String, String>> newData = new TreeMap<>();

        for (Map.Entry<String, TreeMap<String, String>> billboard : oldData.entrySet()) {
            // if changing title, add to oldName so server knows what to remove:
            oldNames.put(billboard.getKey(), null);

            // change title:
            String newTitle = "user 2 edited: " + billboard.getKey();
            newData.put(newTitle, new TreeMap<>());

            // change message
            newData.get(newTitle).put("message", java.util.UUID.randomUUID().toString());
        }

        server.removeBillboards(oldNames);
        server.addBillboards(newData);
    }
}
