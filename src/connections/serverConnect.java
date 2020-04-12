package connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class serverConnect implements Runnable
{
    private String ip = null;
    private int port;
    private clientRequest request = null;
    private serverResponse response = null;

    public serverConnect(String ip, int port, clientRequest request){
        this.ip = ip;
        this.port = port;
        this.request = request;
    }

    public serverResponse getResponse()
    {
        return this.response;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(this.ip, this.port);
            System.out.println("Connected");

            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

            outStream.writeObject(this.request);

            this.response = (serverResponse) inStream.readObject();

            socket.close();

        } catch (IOException e) {
            System.out.println("server disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
