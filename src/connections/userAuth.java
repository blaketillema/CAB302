package connections;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.TreeMap;


class Server
{
    public String user = null;
    public String salt = null;
    public String hashed = null;
    public String sessionId = null;

    public Server(String user, String initialHash, String salt)
    {
        this.user = user;
        this.salt = salt;
        this.hashed = userAuth.hashAndSalt(initialHash, salt);
    }

    private String genSessionId()
    {
        return "wfwafwafafaf";
    }

    public serverResponse getSessionId(String username, String hashed)
    {
        serverResponse response = new serverResponse();

        if (username.equals(this.user))
        {
            hashed = userAuth.hashAndSalt(hashed, this.salt);

            if(hashed.equals(this.hashed)){
                response.status = "OK";
                response.data = new TreeMap<>();
                String sessionId = genSessionId();
                this.sessionId = sessionId;
                response.data.put("sessionId", sessionId);
            }
        }

        else
        {
            response.status = "invalid username or password";
        }

        return response;
    }
}

class Client
{
    public String user = null;
    public String salt = null;
    public String hashed = null;

    public Client(String user, String password, String salt)
    {
        this.user = user;
        this.salt = salt;
        this.hashed = userAuth.hashAndSalt(password, salt);
    }
}

public class userAuth
{
    private static Client client = null;
    private static Server server = null;

    public static void main(String[] args)
    {
        // get variables into databases initially (only run once)
        userAuthInit("max", "password123");

        serverResponse response = server.getSessionId(client.user, client.hashed);

        System.out.println(response.status + response.data);
    }

    private static void userAuthInit(String user, String password)
    {
        String salt = generateSalt();
        client = new Client(user, password, salt);
        server = new Server(client.user, client.hashed, client.salt);
    }

    public static String generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return new String(salt);
    }

    public static String hashAndSalt(String password, String strSalt)
    {
        MessageDigest md;
        try
        {
            byte[] salt = strSalt.getBytes(StandardCharsets.UTF_8);

            // Select the message digest for the hash computation -> SHA-256
            md = MessageDigest.getInstance("SHA-256");

            // Passing the salt to the digest for the computation
            md.update(salt);

            // Generate the salted hash
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword)
                sb.append(String.format("%02x", b));

            return sb.toString();

        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return "";
        }
    }
}