package connections;

import connections.engines.Server;

public class ServerMainTest {
    public static void main(String[] args) {
        Server server = new Server();

        server.run();
    }
}
