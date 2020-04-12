package connections;

public class viewerMain
{
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {


        clientRequest request = new clientRequest();
        request.type = "GET";
        request.path = "/billboards/current";

        serverConnect viewer = new serverConnect("localHost", 1234, request);
        Thread thread = new Thread(viewer);
        thread.start();
        thread.join();
        serverResponse response = viewer.getResponse();

    }

}
