package connections.testing;

import connections.ClientServerInterface;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class User3ScheduleBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user3", "test3");

        editSchedules(server);

        System.out.println(server.getAllBillboards());
    }

    public static void editSchedules(ClientServerInterface server) throws Exception {
        long now = System.currentTimeMillis();

        String[] strSchedules = new String[3];

        for (int i = 0; i < strSchedules.length; i++) {
            long start = now;
            now += 3600000;
            long end = now - 1;

            strSchedules[i] = String.format("%d %d", start, end);
        }

        TreeMap<String, TreeMap<String, String>> data = server.getAllBillboards();

        int i = 0;
        for (Map.Entry<String, TreeMap<String, String>> billboard : data.entrySet()) {
            data.get(billboard.getKey()).put("schedule", strSchedules[i]);
        }

        server.addBillboards(data);
    }
}
