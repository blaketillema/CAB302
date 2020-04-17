package connections.exceptions;

public class PermissionException extends ServerException {
    public PermissionException(String user, String has, String needs) {
        super(String.format("user '%s' has permissions %s, but tried access permission level %s", user, has, needs));
    }
}
