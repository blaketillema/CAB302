package connections;

import java.util.TreeMap;

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

        addBillboard();

        getAllBillboards();

    }

    public static void sendCredentials()
    {

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.USERS;
        request.data = new TreeMap<>();

        TreeMap<String, String> body = new TreeMap<>();
        body.put(Protocol.HASH, password);
        body.put(Protocol.SALT, salt);
        body.put(Protocol.PERMISSION, Protocol.Permission.EDIT_USERS);

        String username = "max";
        request.data.put(username, body);

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

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.NEW_SESSION_ID;
        request.params = new TreeMap<>();
        request.params.put(Protocol.USER, "max");
        request.params.put(Protocol.HASH, password);

        ServerResponse response;
        try
        {
            response = ServerConnect.request(Protocol.LOCALHOST, 1234, request);
            sessionId = response.data.get("data").get(Protocol.Params.SESSION_ID);
            response.print();
        } catch (HttpException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void addBillboard()
    {

        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.POST;
        request.path = Protocol.Path.BILLBOARDS;
        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.SESSION_ID, sessionId);

        request.data = new TreeMap<>();
        TreeMap<String, String> body = new TreeMap<>();
        body.put("xmlData", "xxxxxxxxxxxx");
        request.data.put("billboard_name_2", body);

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
        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.BILLBOARDS;
        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.SESSION_ID, sessionId);
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

    public static void getAllBillboards()
    {
        ClientRequest request = new ClientRequest();

        request.type = Protocol.Type.GET;
        request.path = Protocol.Path.BILLBOARDS;
        request.params = new TreeMap<>();
        request.params.put(Protocol.Params.SESSION_ID, sessionId);

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
