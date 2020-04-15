package connections;

public class ViewerMain
{
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        ClientRequest request = ClientRequest.buildRequest(
                Protocol.Type.GET,
                Protocol.Path.BILLBOARDS,
                null,
                new String[][] {{Protocol.Params.CURRENT_SCHEDULED, "true"}},
                null);

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
