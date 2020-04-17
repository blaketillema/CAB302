package connections.testing;

import connections.engines.Server;

public class ServerMain
{
    public static void main(String[] args)
    {
        Server server = new Server();

        server.run();
    }
}
