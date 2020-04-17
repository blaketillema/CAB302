package connections.exceptions;

public class BillboardException extends ServerException {
    public BillboardException(String s) {
        super("billboard " + s + " already exists");
    }
}
