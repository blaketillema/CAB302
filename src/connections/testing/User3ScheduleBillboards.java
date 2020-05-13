package connections.testing;

import connections.ClientServerInterface;
import connections.exceptions.ServerException;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Deprecated
public class User3ScheduleBillboards {
    public static void main(String[] args) throws Exception {
        ClientServerInterface server = new ClientServerInterface();

        server.login("user3", "test3");

        editSchedules(server);

        System.out.println(server.getAllBillboards());
    }

    public static void editSchedules(ClientServerInterface server) {
        long now = System.currentTimeMillis();

        String[] strSchedules = new String[3];

        for (int i = 0; i < strSchedules.length; i++) {
            long start = now;
            now += 3600000;
            long end = now - 1;

            strSchedules[i] = String.format("%d %d", start, end);
        }

        // error handling - same schedule
        strSchedules[2] = strSchedules[1];

        TreeMap<String, TreeMap<String, String>> data;
        TreeMap<String, TreeMap<String, String>> schedules = new TreeMap<>();

        try {
            data = server.getAllBillboards();
        } catch (ServerException e) {
            e.printStackTrace();
            return;
        }

        int i = 0;
        for (Map.Entry<String, TreeMap<String, String>> billboard : data.entrySet()) {
            TreeMap<String, String> body = new TreeMap<>();

            body.put("schedule", strSchedules[i]);
            schedules.put(billboard.getKey(), body);

            i++;
        }

        try {
            server.sendSchedules(schedules);
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }
}
