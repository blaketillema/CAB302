package connections.engines;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.lang.reflect.*;

import billboard_server.Database;

/****************
 * initialiser
 ****************/

class UserInfo {
    String userId;
    long createdAt;
}

public class Server {
    private int port;
    public static TreeMap<Long, UserInfo> sessionIds = null;
    public static final long ONE_DAY_MS = 86400000;
    public static Database database = null;

    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "network.props").toString();

    public Server() throws SQLException {
        this.port = 1234;
        sessionIds = new TreeMap<>();
        database = new Database();
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