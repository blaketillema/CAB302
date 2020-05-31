package billboard_server.exceptions;

/**
 * Throws a ServerException. To be used when a billboard already exists.
 * @author Max Ferguson
 */
public class BillboardException extends ServerException {
    public BillboardException(String s) {
        super("billboard " + s + " already exists");
    }
}
