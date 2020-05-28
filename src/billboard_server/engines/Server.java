package billboard_server.engines;

import billboard_server.Database;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Random;
import java.util.TreeMap;

/****************
 * initialiser
 ****************/

class UserInfo {
    String userId;
    long createdAt;
}

/**
 * Creates a server at the port and IP specified in assets/network.props
 *
 * @author Max Ferguson
 */
public class Server {
    private int port;
    public static TreeMap<Long, UserInfo> sessionIds = null;
    public static final long ONE_DAY_MS = 86400000;
    public static Database database = null;

    public static void main(String[] args) throws SQLException {
        Server server = new Server();
        server.run();
    }

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "billboard_server", "assets", "network.props").toString();

    /**
     *
     * @throws SQLException
     */
    public Server() throws SQLException {
        this.port = 1234;
        sessionIds = new TreeMap<>();
        database = new Database();

        long sessionId = new Random().nextLong();

        UserInfo newUser = new UserInfo();
        newUser.userId = "b220a053-91f1-48ee-acea-d1a145376e57";
        newUser.createdAt = Long.MAX_VALUE - ONE_DAY_MS;

        sessionIds.put(sessionId, newUser);
    }

    public void run() {
        try {
            java.net.ServerSocket serverSocket = new java.net.ServerSocket(this.port);

            System.out.printf("server running @ localhost, port %d\n", this.port);

            while(true) {
                Socket socket = serverSocket.accept();

                ServerThread sThread = new ServerThread(socket);
                Thread thread = new Thread(sThread);
                thread.start();
            }

        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}