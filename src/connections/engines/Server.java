package connections.engines;

import connections.ServerDatabaseInterface;
import connections.exceptions.ServerException;
import connections.types.TestDatabase;
import connections.tools.Tools;
import connections.tools.UserAuth;
import connections.types.ClientRequest;
import connections.Protocol;
import connections.types.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TreeMap;

import static connections.engines.Server.*;

/****************
 * initialiser
 ****************/
public class Server {
    private int port;
    public static ServerDatabaseInterface database = null;
    public static TreeMap<String, String[]> sessionIds = null;
    public static long ONE_DAY_MS = 86400000;


    private static final String networkPath =
            Paths.get(System.getProperty("user.dir"), "src", "connections", "assets", "network.props").toString();

    public Server() {
        this.port = 1234;
        database = new ServerDatabaseInterface();
        sessionIds = new TreeMap<>();
    }

    public void run() {
        try {
            java.net.ServerSocket serverSocket = new java.net.ServerSocket(this.port);

            System.out.printf("server running @ localhost, port %d\n", this.port);

            while(true)
            {
                Socket socket = serverSocket.accept();

                ClientThread cThread = new ClientThread(socket);
                Thread thread = new Thread(cThread);
                thread.start();
            }

        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}