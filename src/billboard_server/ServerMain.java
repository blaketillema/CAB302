package billboard_server;

import billboard_server.engines.Server;

import java.sql.SQLException;

/**
 * Simple class to run the server
 * @author Max Ferguson
 */
public class ServerMain {
    public static void main(String[] args) throws SQLException {
        Server server = new Server();
        server.run();
    }
}
