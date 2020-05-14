package connections.testing;

import connections.ClientServerInterface;
import connections.exceptions.ServerException;

@Deprecated
public class ViewerGetCurrent {
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        ClientServerInterface server = new ClientServerInterface();

        try {
            System.out.println(server.getCurrentBillboard());
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

}
