package billboard_server.engines;

import billboard_server.Database;
import billboard_server.Scheduler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Random;
import java.util.TreeMap;

class UserInfo {
    String userId;
    long createdAt;
}

/**
 * Server side: Creates a server as specified by the IP and port in assets/network.props
 * @author Max Ferguson
 */
public class Server {
    private int port;
    public static TreeMap<Long, UserInfo> sessionIds = null;
    public static final long ONE_DAY_MS = 86400000;
    public static Database database = null;
    public static Scheduler scheduler = null;

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "network.props").toString();

    /**
     * Initialises the server, setting the port and adding a session ID for admin
     * @throws SQLException
     */
    public Server() throws SQLException {
        scheduler = new Scheduler();
        sessionIds = new TreeMap<>();
        database = new Database();

        this.port = 1234;

        // create a sessionId for admin that will expire at the maximum capacity of long integer
        long sessionId = new Random().nextLong();

        UserInfo newUser = new UserInfo();
        newUser.userId = "b220a053-91f1-48ee-acea-d1a145376e57";
        newUser.createdAt = Long.MAX_VALUE - ONE_DAY_MS;

        sessionIds.put(sessionId, newUser);
    }

    /**
     * Creates and runs the server, accepting new clients and handling them in seperate ServerThread's
     */
    public void run() {
        try {
            // create a server socket
            java.net.ServerSocket serverSocket = new java.net.ServerSocket(this.port);

            System.out.printf("server running @ localhost, port %d\n", this.port);

            // infinitely accept new clients and handle them in a seperate thread
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