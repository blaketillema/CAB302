package billboard_server.exceptions;

/**
 * ServerException to be thrown when a user request or server response contains invalid data, or if the request/response couldn't be satisifed
 */
public class ServerException extends Exception {
    public ServerException(String s) {
        super(s);
    }
}
