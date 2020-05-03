package connections.exceptions;

public class PermissionException extends ServerException {
    public PermissionException(String user, int has, int needs) {
        super(String.format("user '%s' has permissions %s, but tried access permission level %s", user, Integer.toBinaryString(has), Integer.toBinaryString(needs)));
    }
}
