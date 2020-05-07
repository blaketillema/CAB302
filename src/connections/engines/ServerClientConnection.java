package connections.engines;

import connections.types.ClientRequest;
import connections.exceptions.ServerException;
import connections.types.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerClientConnection implements Runnable {
    private String ip = null;
    private int port;
    private ClientRequest request = null;
    private ServerResponse response = null;

    public static ServerResponse request(String ip, int port, ClientRequest request) throws ServerException {
        ServerClientConnection server = new ServerClientConnection(ip, port, request);

        Thread thread = new Thread(server);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ServerResponse response = server.getResponse();

        if (response.status == null || !response.status.equals("OK"))
        {
            throw new ServerException(response.status);
        }

        return response;
    }

    public ServerClientConnection(String ip, int port, ClientRequest request) {
        this.ip = ip;
        this.port = port;
        this.request = request;
    }

    public ServerResponse getResponse()
    {
        return this.response;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(this.ip, this.port);

            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

            outStream.writeObject(this.request);

            this.response = (ServerResponse) inStream.readObject();

            socket.close();

        } catch (IOException e) {
            System.out.println("server disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
