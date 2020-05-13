package connections;

import connections.engines.Server;
import billboard_server.Database;

import java.sql.SQLException;

public class ServerMainTest {
    public static void main(String[] args) throws SQLException {

        Server server = new Server();

        server.run();
    }
}
