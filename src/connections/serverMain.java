package connections;

public class serverMain
{
    public static void main(String[] args)
    {
        serverSocket server = new serverSocket(1234);

        server.run();
    }
}
