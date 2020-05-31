package billboard_server.engines;

import billboard_server.types.ClientRequest;
import billboard_server.exceptions.ServerException;
import billboard_server.types.ServerResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client side: Sends a request to the server, then receives a response back
 * @author Max Ferguson
 */
public class ServerClientConnection implements Runnable {
    private String ip = null;
    private int port;
    private ClientRequest request = null;
    private ServerResponse response = null;

    /**
     * Initialiser function to specify the IP, port and request to be sent
     * @param ip
     * @param port
     * @param request
     */
    public ServerClientConnection(String ip, int port, ClientRequest request) {
        this.ip = ip;
        this.port = port;
        this.request = request;
    }

    /**
     * Sends a request to the server and receives a response back, raising an exception if the response indicates failure
     * @param ip The IP address of the server to connect to
     * @param port The port to connect to
     * @param request The ClientRequest object to send to the server
     * @return ServerResponse The response from the server
     * @throws ServerException If no response was sent back, or if response.success == false
     */
    public static ServerResponse request(String ip, int port, ClientRequest request) throws ServerException {
        // create a new ServerClientConnection
        ServerClientConnection server = new ServerClientConnection(ip, port, request);

        // run the ServerClientConnection in a new thread, waiting for a response to be received
        Thread thread = new Thread(server);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get the response and raise a ServerException based on the status and success values
        ServerResponse response = server.getResponse();

        if (response == null) {
            throw new ServerException("request failed: server response is null");
        }

        if (!response.success) {
            if (response.status == null) {
                throw new ServerException("request failed: no error msg provided");
            } else {
                throw new ServerException("request failed: " + response.status);
            }
        }

        return response;
    }

    /**
     * Returns the server's response after the run() function finishes
     * @return ServerResponse
     */
    public ServerResponse getResponse() {
        return this.response;
    }

    /**
     * When a thread is created: connects to the server, sends a request over, receives a response and terminates the connection
     */
    @Override
    public void run() {
        try {
            // connect to the server's socket
            Socket socket = new Socket(this.ip, this.port);

            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

            // send the request to the server
            outStream.writeObject(this.request);

            // store the response of the server
            this.response = (ServerResponse) inStream.readObject();

            // close the connection
            socket.close();

        } catch (IOException e) {
            System.out.println("server disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
