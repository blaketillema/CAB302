package connections;

import connections.exceptions.HttpException;

public class ViewerMain
{
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        ClientServerInterface server = new ClientServerInterface();

        try {
            System.out.println(server.getCurrentBillboard());
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

}
