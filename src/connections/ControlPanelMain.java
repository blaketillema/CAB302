package connections;

public class ControlPanelMain
{
    private static String password = "password1234";
    private static String salt = null;
    private static String sessionId = null;


    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        salt = UserAuth.generateSalt();
        password = UserAuth.hashAndSalt(password, salt);

        sendCredentials();

        getSessionId();

        addToDatabase();

        getCurrentBillboard();

    }

    public static void sendCredentials()
    {
        ClientRequest request = ClientRequest.buildRequest(
                Protocol.Type.POST,
                Protocol.Path.USERS,
                null,
                new String[][] {{Protocol.USER, "max"}, {Protocol.HASH, password}, {Protocol.SALT, salt},
                        {Protocol.PERMISSION, Protocol.Permission.EDIT_USERS}},
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

    public static void getSessionId()
    {
        ClientRequest request = ClientRequest.buildRequest(
                Protocol.Type.GET,
                Protocol.Path.NEW_SESSION_ID,
                null,
                new String[][] {{Protocol.USER, "max"}, {Protocol.HASH, password}},
                null);

        ServerResponse response;
        try
        {
            response = ServerConnect.request(Protocol.LOCALHOST, 1234, request);
            sessionId = response.data.get(Protocol.Params.SESSION_ID);
            response.print();
        } catch (HttpException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addToDatabase()
    {
        ClientRequest request = ClientRequest.buildRequest(
                Protocol.Type.POST,
                Protocol.Path.BILLBOARDS,
                null,
                new String[][] {{Protocol.Params.SESSION_ID, sessionId}},
                new String[][] {{"test", "test"}});

        ServerResponse response;
        try
        {
            response = ServerConnect.request(Protocol.LOCALHOST, 1234, request);
            response.print();
        } catch (HttpException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getCurrentBillboard()
    {
        ClientRequest request = ClientRequest.buildRequest(
                Protocol.Type.GET,
                Protocol.Path.BILLBOARDS,
                null,
                new String[][] {{Protocol.Params.SESSION_ID, sessionId}, {Protocol.Params.CURRENT_SCHEDULED, "true"}},
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
