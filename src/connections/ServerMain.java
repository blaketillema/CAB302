package connections;

import connections.engines.ServerSocket;

public class ServerMain
{
    public static void main(String[] args)
    {
        ServerSocket server = new ServerSocket(1234);

        server.run();
    }
}
