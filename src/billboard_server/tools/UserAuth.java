package billboard_server.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Simple class to generate salts and to hash and salt a password
 * @author Max Ferguson
 */
public class UserAuth
{
    /**
     * Generates a random salt of size 16 chars
     * @return String generated salt
     */
    public static String generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        StringBuilder sb = new StringBuilder();
        for (byte b : salt)
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    /**
     * Hashes and salts a password using SHA-256
     * @param password The password to be hashed (e.g. "password1" or "1234")
     * @param strSalt The randomly generated salt using the generateSalt() function
     * @return String The hashed and salted password to be sent over the network
     */
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}