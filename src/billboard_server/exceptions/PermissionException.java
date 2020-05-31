package billboard_server.exceptions;

import billboard_server.Protocol;

/**
 * Throws a ServerException. To be used for when permission is denied, creating a verbose error message.
 */
public class PermissionException extends ServerException {
    public PermissionException(String user, int has, int needs) {
        super(String.format("user '%s' has permissions %s, but tried access permission level %s", user, Protocol.Permission.toString(has), Protocol.Permission.toString(needs)));
    }
}
