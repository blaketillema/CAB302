package connections;

import java.util.TreeMap;

public class controlPanelMain
{
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        clientRequest request = new clientRequest();
        request.type = "GET";
        request.path = "/cmd/newSessionId";
        request.params = new TreeMap<>();
        request.params.put("username", "max");
        request.params.put("password", "test");

        serverConnect server = new serverConnect("localHost", 1234, request);
        Thread thread = new Thread(server);
        thread.start();
        thread.join();
        serverResponse response = server.getResponse();
        response.print();
    }
}
