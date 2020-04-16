package connections;

import java.util.TreeMap;

public class ViewerMain
{
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        ClientRequest request = new ClientRequest();
        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.BILLBOARDS;
        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.CURRENT_SCHEDULED, "true");

        ServerResponse response;
        try
        {
            response = ServerConnect.request(Protocol.LOCALHOST, 1234, request);
            response.print();
        } catch (HttpException e) {
            System.out.println(e.getMessage());
        }
    }

}
