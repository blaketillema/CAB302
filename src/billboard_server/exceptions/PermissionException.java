package billboard_server.exceptions;

import billboard_server.Protocol;

public class PermissionException extends ServerException {
    public PermissionException(String user, int has, int needs) {
        super(String.format("user '%s' has permissions %s, but tried access permission level %s", user, Protocol.Permission.toString(has), Protocol.Permission.toString(needs)));
    }
}
